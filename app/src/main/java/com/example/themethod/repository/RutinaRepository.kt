package com.example.themethod.repository

import com.example.themethod.data.Rutina
import com.example.themethod.data.RutinaDao
import kotlinx.coroutines.flow.Flow




    class RutinaRepository(private val rutinaDao: RutinaDao) {

        // 1. Leer datos
        // Creamos una variable que contiene el flow(Tuberia de datos)
        // que vienen directamente del DAO

        val todaslasRutinas: Flow<List<Rutina>> = rutinaDao.obtenerRutinas()


        //. 2, Escribir los datos:
        // Creamos la funcion suspend que se encarga de llamar a la funcion del DAO

        suspend fun insertarRutina(rutina: Rutina) {
            rutinaDao.insertRutina(rutina)
        }

        // 3. BORRAR DATOS (Si añadiste esta función en el DAO antes):
        // suspend fun borrarRutina(idRutina: Int) {
        //     rutinaDao.borrarRutina(idRutina)
        // }
    }

