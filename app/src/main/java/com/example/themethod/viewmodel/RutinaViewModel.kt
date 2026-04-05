package com.example.themethod.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.themethod.data.EjercicioConDetalles
import com.example.themethod.data.EjercicioDao
import com.example.themethod.data.Rutina
import com.example.themethod.data.RutinaEjercicioCrossRef
import com.example.themethod.repository.RutinaRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import com.google.firebase.firestore.FirebaseFirestore

class RutinaViewModel(private val repository: RutinaRepository) : ViewModel() {

    // 1. LEER DATOS:
    // Exponemos la tubería de datos (Flow) para que la pantalla la observe.
    val todasLasRutinas: Flow<List<Rutina>> = repository.todaslasRutinas

    // Tuberia para el Catalogo de ejercicios
    val todosLosEjercicios: Flow<List<com.example.themethod.data.Ejercicio>> = repository.todosLosEjercicios

    init {
        precargarEjercicios()
    }

    private fun precargarEjercicios() {
        viewModelScope.launch {
            // Leemos el catálogo actual (solo la primera vez, sin quedarnos escuchando)

            val ejerciciosActuales = todosLosEjercicios.first()

            // Si está vacío, inyectamos los datos de prueba
            if (ejerciciosActuales.isEmpty()) {
                val ejerciciosBase = listOf(
                    com.example.themethod.data.Ejercicio(
                        nombre = "Press de Banca",
                        grupoMuscular = "Pecho"
                    ),
                    com.example.themethod.data.Ejercicio(
                        nombre = "Sentadilla",
                        grupoMuscular = "Piernas"
                    ),
                    com.example.themethod.data.Ejercicio(
                        nombre = "Peso Muerto",
                        grupoMuscular = "Espalda"
                    ),
                    com.example.themethod.data.Ejercicio(
                        nombre = "Dominadas",
                        grupoMuscular = "Espalda"
                    ),
                    com.example.themethod.data.Ejercicio(
                        nombre = "Curl de Bíceps",
                        grupoMuscular = "Brazos"
                    )
                )

                // Guardamos uno por uno
                ejerciciosBase.forEach { ejercicio ->
                    repository.insertarEjercicio(ejercicio)
                }
            }
        }
    }

    // 2. ESCRIBIR DATOS:
    // La pantalla llamará a esta función cuando el usuario pulse el botón "Guardar"
    fun insertarRutina(rutina: Rutina) {
        // viewModelScope.launch crea un hilo secundario para no congelar la pantalla
        viewModelScope.launch {
            // GUARDADO LOCAL (ROOM)
            repository.insertarRutina(rutina)

        // GUARDADO EN LA NUBE (FIREBASE)
        val db = FirebaseFirestore.getInstance()
        // GUARDAMOS LA AUTENTICACION
        val auth = FirebaseAuth.getInstance()
            // OBTENEMOS EL USUARIO ACTUAL
        val usuarioActual = auth.currentUser


        // SI HAY ALGUIEN LOGUEADO, LE PONEMOS SU ID A LA RUTINA PARA DIFERENCIARLA
            if (usuarioActual != null) {
        //Empaquetamos los datos reales de la rutina
            val rutinaParaNube = hashMapOf(
                "nombreRutina" to rutina.nombreRutina,
                "userId" to usuarioActual.uid

            )
            db.collection("rutinas")
                .add(rutinaParaNube)
                .addOnSuccessListener {
                    println("Exito Rutina guardada a nombre de ${usuarioActual.email}")
                }
                .addOnFailureListener {
                    println("Error al subir a la Firebase: ")
                }
                } else {
                println("No hay usuario logueado")
            }
        }
    }

    // Muestra la lista de ejercicios de una rutina para que la interfaz grafica pueda escucharla
    // y la dibuje en pantalla
    fun obtenerEjercicios(rutinaId: Int): Flow<List<EjercicioConDetalles>> {
        return repository.obtenerEjerciciosDeRutina(rutinaId)
    }

    // --- FACTORY ---
// Como nuestro ViewModel necesita un parámetro (el Repositorio), Android no sabe crearlo solo.
// Necesitamos esta "Fábrica" para enseñarle a Android cómo construir nuestro ViewModel.
    class RutinaViewModelFactory(private val repository: RutinaRepository) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(RutinaViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return RutinaViewModel(repository) as T
            }
            throw IllegalArgumentException("Clase ViewModel desconocida")
        }
    }


    // Funcion para guardar el ejercio en la rutina actual

    fun insertarEjercicioRutina(rutinaId: Int, ejercicioId: Int, series: Int, repeticiones: Int, pesoKgs: Double ){
        viewModelScope.launch {
            // CREAMOS EL "POST-IT" CON TODOS LOS DATOS
            val nuevoEjercicio = RutinaEjercicioCrossRef(
                idRutina = rutinaId,
                idEjercicio = ejercicioId,
                series = series,
                repeticiones = repeticiones,
                pesoKgs = pesoKgs
            )
            // LLAMAMOS AL REPOSITORIO PARA GUARDARLO EN LA BASE DE DATOS
            repository.insertRutinaEjercicio(nuevoEjercicio)
        }
    }
    // Funcion para eliminar un ejercicio creado
    fun eliminarEjercicioDeRutina(rutinaId: Int, ejercicioId: Int) {
        viewModelScope.launch {
            // Para borrar, solo necesitamos que coincidan los IDs de la relación
            val relacionABorrar = com.example.themethod.data.RutinaEjercicioCrossRef(
                idRutina = rutinaId,
                idEjercicio = ejercicioId,
                series = 0, repeticiones = 0, pesoKgs = 0.0 // Estos valores no importan para borrar
            )
            repository.eliminarRutinaEjercicio(relacionABorrar)
        }
    }

   // Funcion para agregar un ejercicio personalizado al catalogo

    fun crearEjerciciosPersonalizados(nombre: String, grupoMuscular: String) {
        viewModelScope.launch {
            val nuevoEjercicio = com.example.themethod.data.Ejercicio(
                nombre = nombre,
                grupoMuscular = grupoMuscular
            )
            repository.insertarEjercicio(nuevoEjercicio)

        }
    }

    // Funcion para eliminar una rutina en especifico
    fun eliminarRutina(rutinaId: Int){
        viewModelScope.launch {
            repository.eliminarRutinaCompleta(rutinaId)
        }
    }




}



