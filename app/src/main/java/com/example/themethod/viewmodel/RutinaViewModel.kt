package com.example.themethod.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.themethod.data.EjercicioConDetalles
import com.example.themethod.data.HistorialEntrenamiento
import com.example.themethod.data.Rutina
import com.example.themethod.data.RutinaEjercicioCrossRef
import com.example.themethod.repository.RutinaRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await

/**
 * ViewModel de Rutinas.
 * Actúa como el "Cerebro" o puente entre la Interfaz de Usuario (las pantallas)
 * y los Datos (Room local y Firebase en la nube).
 */
class RutinaViewModel(private val repository: RutinaRepository) : ViewModel() {

    // ========================================================================
    // 1. ESTADOS DE LA UI (VARIABLES OBSERVABLES)
    // ========================================================================

    // Tuberías de datos constantes: La UI se "suscribe" a ellas y se actualiza sola si hay cambios.
    val todasLasRutinas: Flow<List<Rutina>> = repository.todaslasRutinas
    val todosLosEjercicios: Flow<List<com.example.themethod.data.Ejercicio>> = repository.todosLosEjercicios

    // Variable para controlar si mostramos la "ruedecita de carga" en la pantalla de Login.
    // Usamos _estadoDescarga (privado) para modificarlo aquí dentro, y estadoDescarga (público) para que la UI lo lea.
    private val _estadoDescarga = MutableStateFlow(false)
    val estadoDescarga: StateFlow<Boolean> = _estadoDescarga.asStateFlow()

    // El bloque init se ejecuta automáticamente en el instante en que se crea este ViewModel.
    init {
        precargarEjercicios()
        verificarYDescargarSiLogueado()
    }

    // ========================================================================
    // 2. FUNCIONES DE ARRANQUE (BOOT)
    // ========================================================================

    //Comprueba si el usuario ya tenía la sesión abierta de antes.
    //Si es así, lanza la descarga de datos automáticamente sin esperar a que pulse "Iniciar Sesión".

    private fun verificarYDescargarSiLogueado() {
        val usuarioActual = FirebaseAuth.getInstance().currentUser
        if (usuarioActual != null) {
            viewModelScope.launch {
                // OPCIONAL: Comprobar si ya tenemos rutinas locales para no saturar
                val rutinasLocales = todasLasRutinas.first()
                if (rutinasLocales.isEmpty()) {
                    descargarDatosDeNube(usuarioActual.uid)
                }
            }
        }
    }

    //Llena el catálogo de ejercicios la primera vez que se abre la app.

    private fun precargarEjercicios() {
        viewModelScope.launch {
            // .first() lee la base de datos una sola vez (como una foto) en lugar de quedarse escuchando
            val ejerciciosActuales = todosLosEjercicios.first()

            if (ejerciciosActuales.isEmpty()) {
               val ejerciciosBase = com.example.themethod.data.DatosIniciales.ejerciciosPredeterminados
                ejerciciosBase.forEach { ejercicio ->
                    repository.insertarEjercicio(ejercicio)
                }
            }
        }
    }

    // ========================================================================
    // 3. FUNCIONES DE SUBIDA / GUARDADO (ROOM -> FIREBASE)
    // ========================================================================

    //Crea una rutina nueva. Primero la guarda en el móvil y luego sube una copia exacta a Firebase.
    fun insertarRutina(rutina: Rutina) {
        viewModelScope.launch {
            // 1. GUARDADO LOCAL: Guardamos en Room y pedimos que nos devuelva qué ID numérico le ha asignado
            val idGeneradoLocalmente = repository.insertarRutina(rutina)

            // 2. GUARDADO EN LA NUBE
            val db = FirebaseFirestore.getInstance()
            val auth = FirebaseAuth.getInstance()
            val usuarioActual = auth.currentUser

            if (usuarioActual != null) {
                // Preparamos el "paquete" de datos para Firebase
                // NO mandamos el ID por dentro, porque el ID será el nombre de la carpeta
                val rutinaParaNube = hashMapOf(
                    "nombreRutina" to rutina.nombreRutina
                )

                // IMPORTANTE: Construimos la ruta exacta: usuarios -> (Mi ID) -> rutinas -> (ID de Room)
                // Usar .set() con el ID de Room evita que Firebase invente nombres aleatorios.
                db.collection("usuarios").document(usuarioActual.uid)
                    .collection("rutinas").document(idGeneradoLocalmente.toString())
                    .set(rutinaParaNube)
                    .addOnSuccessListener {
                        println("LOG_UPLOAD: Éxito. Rutina '${rutina.nombreRutina}' guardada en Firebase con ID: $idGeneradoLocalmente")
                    }
                    .addOnFailureListener {
                        println("LOG_UPLOAD: Error al subir a Firebase")
                    }
            } else {
                println("LOG_UPLOAD: No hay usuario logueado, guardado solo en local.")
            }
        }
    }

    //Añade un ejercicio específico a una rutina (Ej: "Añadir Sentadilla a Piernas").

    fun insertarEjercicioRutina(rutinaId: Int, ejercicioId: Int, series: Int, repeticiones: Int, pesoKgs: Double ){
        viewModelScope.launch {
            // 1. GUARDADO LOCAL: Creamos el "enlace" (CrossRef) entre la rutina y el ejercicio
            val nuevoEjercicio = RutinaEjercicioCrossRef(
                idRutina = rutinaId, idEjercicio = ejercicioId, series = series, repeticiones = repeticiones, pesoKgs = pesoKgs
            )
            repository.insertRutinaEjercicio(nuevoEjercicio)

            // 2. GUARDADO EN LA NUBE
            val db = FirebaseFirestore.getInstance()
            val auth = FirebaseAuth.getInstance()
            val usuarioActual = auth.currentUser

            if(usuarioActual != null){
                val ejercicioParaNube = hashMapOf(
                    "idEjercicioLocal" to ejercicioId,
                    "series" to series,
                    "repeticiones" to repeticiones,
                    "pesoKgs" to pesoKgs
                )

                // Lo guardamos como una "sub-colección" dentro de la rutina correspondiente
                db.collection("usuarios").document(usuarioActual.uid)
                    .collection("rutinas").document(rutinaId.toString())
                    .collection("ejercicios").document(ejercicioId.toString())
                    .set(ejercicioParaNube)
                    .addOnSuccessListener {
                        println("LOG_UPLOAD: ÉXITO! Ejercicio $ejercicioId subido a rutina $rutinaId")
                    }
                    .addOnFailureListener { e ->
                        println("LOG_UPLOAD: Error al subir ejercicio: $e")
                    }
            }
        }
    }

    // ========================================================================
    // 4. FUNCIONES DE BORRADO Y LECTURA LOCAL
    // ========================================================================

    fun obtenerEjercicios(rutinaId: Int): Flow<List<EjercicioConDetalles>> {
        return repository.obtenerEjerciciosDeRutina(rutinaId)
    }

    fun eliminarEjercicioDeRutina(rutinaId: Int, ejercicioId: Int) {
        viewModelScope.launch {
            // Borrado local
            val relacionABorrar = com.example.themethod.data.RutinaEjercicioCrossRef(
                idRutina = rutinaId, idEjercicio = ejercicioId, series = 0, repeticiones = 0, pesoKgs = 0.0
            )
            repository.eliminarRutinaEjercicio(relacionABorrar)

            // Borrado en la nube
            val db = FirebaseFirestore.getInstance()
            val auth = FirebaseAuth.getInstance()
            val usuarioActual = auth.currentUser

            if (usuarioActual != null){
                db.collection("usuarios").document(usuarioActual.uid)
                    .collection("rutinas").document(rutinaId.toString())
                    .collection("ejercicios").document(ejercicioId.toString())
                    .delete()
            }
        }
    }

    fun crearEjerciciosPersonalizados(nombre: String, grupoMuscular: String) {
        viewModelScope.launch {
            repository.insertarEjercicio(com.example.themethod.data.Ejercicio(nombre = nombre, grupoMuscular = grupoMuscular))
        }
    }

    fun eliminarRutina(rutinaId: Int){
        viewModelScope.launch {
            repository.eliminarRutinaCompleta(rutinaId)
            // (Opcional): Faltaría añadir aquí el borrado de la rutina entera en Firebase si el usuario la borra.
            // 2. BORRADO EN LA NUBE: Buscamos la ruta del usuario
            val auth = FirebaseAuth.getInstance()
            val usuarioActual = auth.currentUser
            val db = FirebaseFirestore.getInstance()

            if (usuarioActual != null) {
                try {
                    // Guardamos la ruta exacta a la rutina en una variable para no repetir código
                    val rutinaRef = db.collection("usuarios").document(usuarioActual.uid)
                        .collection("rutinas").document(rutinaId.toString())

                    // PASO A: Entramos a la sub-colección y bajamos la lista de todos los ejercicios
                    val ejerciciosSnapshot = rutinaRef.collection("ejercicios").get().await()

                    // PASO B: Recorremos la lista y borramos cada ejercicio uno por uno
                    for (docEj in ejerciciosSnapshot.documents) {
                        docEj.reference.delete().await()
                        println("LOG_DELETE: Ejercicio ${docEj.id} borrado de la nube.")
                    }

                    // PASO C: Una vez vacía la sub-colección, borramos el documento de la rutina
                    rutinaRef.delete().await()
                    println("LOG_DELETE: Rutina $rutinaId eliminada por completo de Firebase. ¡Cero basura!")

                } catch (e: Exception) {
                    println("LOG_DELETE: Error durante la limpieza: ${e.message}")
                }
            }
        }
    }

    // ========================================================================
    // 5. FUNCIÓN DE DESCARGA MAESTRA (FIREBASE -> ROOM)
    // ========================================================================

    /**
     * Descarga todo el perfil del usuario de la nube.
     * Usamos 'suspend' para obligar a Kotlin a esperar a que termine antes de cambiar de pantalla.
     */
    suspend fun descargarDatosDeNube(uid: String) {
        val db = FirebaseFirestore.getInstance()
        println("LOG_DEBUG: 🔍 1. Iniciando para UID: $uid")

        // Activamos la pantalla de carga
        _estadoDescarga.value = true

        try {
            //  Usamos Source.SERVER para obligar a Firebase a ignorar su memoria caché (que suele fallar)
            // y conectarse directamente a la base de datos real.
            val snapshot = db.collection("usuarios").document(uid).collection("rutinas")
                .get(com.google.firebase.firestore.Source.SERVER)
                .await() // .await() pausa esta línea hasta que lleguen los datos

            if (snapshot.isEmpty) {
                println("LOG_DEBUG: ⚠ La colección 'rutinas' está VACÍA en Firebase.")
            }

            // Recorremos las rutinas una a una
            for (doc in snapshot.documents) {
                val nombre = doc.getString("nombreRutina") ?: "Sin nombre"
                val idRutina = doc.id.toIntOrNull() ?: 0

                println("LOG_DEBUG:  2. Procesando Rutina: $nombre (ID: ${doc.id})")

                // Guardamos la rutina en el móvil
                repository.insertarRutina(Rutina(idRutina = idRutina, nombreRutina = nombre))

                // Ahora entramos en la sub-colección para bajar los ejercicios de ESTA rutina
                val ejerciciosSnapshot = doc.reference.collection("ejercicios")
                    .get(com.google.firebase.firestore.Source.SERVER)
                    .await()

                for (docEj in ejerciciosSnapshot.documents) {
                    val idEj = docEj.getLong("idEjercicioLocal")?.toInt()
                    val series = docEj.getLong("series")?.toInt() ?: 0
                    val reps = docEj.getLong("repeticiones")?.toInt() ?: 0
                    val peso = docEj.getDouble("pesoKgs") ?: 0.0

                    if (idEj != null) {
                        println("LOG_DEBUG: ✅ 5. Guardando Ejercicio $idEj en Rutina $idRutina")

                        // Guardamos el ejercicio en el móvil
                        repository.insertRutinaEjercicio(
                            RutinaEjercicioCrossRef(idRutina, idEj, series, reps, peso)
                        )
                    }
                }
            }
        } catch (e: Exception) {
            println("LOG_DEBUG: 💥 ERROR CRÍTICO: ${e.message}")
        } finally {
            // Pase lo que pase (éxito o error), apagamos la pantalla de carga.
            _estadoDescarga.value = false
            println("LOG_DEBUG: 🏁 Sincronización finalizada.")
        }
    }

    // ========================================================================
    // 6. FACTORY
    // ========================================================================

    // Necesario porque nuestro ViewModel necesita recibir el Repositorio por parámetro.
    class RutinaViewModelFactory(private val repository: RutinaRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(RutinaViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return RutinaViewModel(repository) as T
            }
            throw IllegalArgumentException("Clase ViewModel desconocida")
        }
    }

    // Funcion para cerrar sesion y limpiar todo
    fun cerrarSesion(onNavigatoToLogin: () -> Unit) {
        viewModelScope.launch {
            // 1. Cerramos en Firebase
            FirebaseAuth.getInstance().signOut()

            // 2. Limpiamos Room para que el siguiente usuario no vea mis datos
            repository.limpiarBaseDeDatosLocal()

            // 3. Volvemos al Login
            onNavigatoToLogin()
        }
    }

    // Guardamos el entremiento con esta funcion
    fun guardarEntrenamiento(nombre: String, duracion: String) {
        viewModelScope.launch {
            val fechaActual = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault()).format(java.util.Date())
            val nuevoRegistro = HistorialEntrenamiento(
                nombreRutina = nombre,
                fecha = fechaActual,
                duracion = duracion
            )
            repository.insertarHistorial(nuevoRegistro)
        }
    }

    //
    fun finalizarYGuardarEntrenamiento(nombre: String, duracion: String) {
        viewModelScope.launch {
            val formatoFecha = java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault())
            val fechaHoy = formatoFecha.format(java.util.Date())

            val registro = HistorialEntrenamiento(
                nombreRutina = nombre,
                fecha = fechaHoy,
                duracion = duracion
            )

            repository.insertarHistorial(registro)
            println("LOG_HISTORY: Entrenamiento de $nombre guardado con éxito.")
        }
    }

    // Para mostrarlo en una futura pantalla de logros
    val historialCompleto: Flow<List<HistorialEntrenamiento>> = repository.elHistorial

    // Funcion para crear ejercicio personalizado
    fun crearEjercicioPersonalizado(ejercicio: com.example.themethod.data.Ejercicio) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertarEjercicio(ejercicio)
        }
    }

    // Funcion para cambiar contrasena de usuario
    fun cambiarContrasenaEnFirebase(nuevaContrasena: String, onResult: (Boolean, String) -> Unit) {
        val usuarioActual = FirebaseAuth.getInstance().currentUser

        usuarioActual?.updatePassword(nuevaContrasena)
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onResult(true, "¡Contraseña actualizada con éxito!")
                } else {
                    // Firebase puede fallar si la sesión lleva mucho tiempo abierta
                    onResult(false, task.exception?.message ?: "Error al actualizar")
                }
            }
    }

}