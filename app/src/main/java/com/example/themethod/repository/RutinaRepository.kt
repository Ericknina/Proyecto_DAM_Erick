package com.example.themethod.repository

import com.example.themethod.data.EjercicioConDetalles
import com.example.themethod.data.EjercicioDao
import com.example.themethod.data.Rutina
import com.example.themethod.data.RutinaDao
import com.example.themethod.data.RutinaEjercicioCrossRef
import kotlinx.coroutines.flow.Flow




    class RutinaRepository(private val rutinaDao: RutinaDao, private val ejercicioDao: EjercicioDao) {

        // 1. Leer datos
        // Creamos una variable que contiene el flow(Tuberia de datos)
        // que vienen directamente del DAO

        val todaslasRutinas: Flow<List<Rutina>> = rutinaDao.ObtenerRutinas()

        //Llamamos al DAO para obetner los ejercicios que pertenecen a una rutina en concreto
        fun obtenerEjerciciosDeRutina(rutinaId: Int): Flow<List<EjercicioConDetalles>> {
            return rutinaDao. obtenerEjerciciosDeRutina(rutinaId)
        }

        //. 2, Escribir los datos:
        // Creamos la funcion suspend que se encarga de llamar a la funcion del DAO

        suspend fun insertarRutina(rutina: Rutina) {
            rutinaDao.insertRutina(rutina)
        }

        // 3. BORRAR DATOS (Si añadiste esta función en el DAO antes):
        // suspend fun borrarRutina(idRutina: Int) {
        //     rutinaDao.borrarRutina(idRutina)
        // }


        // Guardar el "Post-it" con las series, repeticiones y peso

        suspend fun insertRutinaEjercicio(crossRef: RutinaEjercicioCrossRef) {
            rutinaDao.insertRutinaEjercicio(crossRef)
        }


        // Traemos el catálogo de ejercicios
        val todosLosEjercicios: Flow<List<com.example.themethod.data.Ejercicio>> = ejercicioDao.obtenerTodosLosEjercicios()

        // Guardar un ejercicio nuevo en el catálogo general
        suspend fun insertarEjercicio(ejercicio: com.example.themethod.data.Ejercicio) {
            ejercicioDao.insertEjercicio(ejercicio)
        }

        // Elimina un ejercicio creado del catalogo
        suspend fun eliminarRutinaEjercicio(relacion: RutinaEjercicioCrossRef) {
            rutinaDao.eliminarRutinaEjercicio(relacion)
        }

        // Elimina una rutina en especifico
        suspend fun eliminarRutinaCompleta(rutinaId: Int) {
            // Primero limpiamos los ejercicios de esa rutina, luego borramos la rutina
            rutinaDao.limpiarEjerciciosDeRutina(rutinaId)
            rutinaDao.eliminarRutinaPorId(rutinaId)
        }

    }

