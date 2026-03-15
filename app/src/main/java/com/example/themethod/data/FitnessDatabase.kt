package com.example.themethod.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [Rutina:: class, Ejercicio:: class, RutinaEjercicioCrossRef:: class],
    version = 1,
    exportSchema = false
)

abstract  class FitnessDatabase : RoomDatabase(){

    // Conectamos los daos que hemos creado
    abstract fun rutinaDao(): RutinaDao
    abstract fun ejercicioDao(): EjercicioDao

    // Con lo siguiente se evita que la base de datos se abra mas de una vez y colapse
    // la memoria del dispositivo.

    companion object{
        @Volatile
        private var INSTANCE : FitnessDatabase? = null

        fun getDatabase(context: Context) : FitnessDatabase{
            // Devuelve la BD si ya existe si no, usa synchronized para que solo un hilo pueda crearla
            return INSTANCE ?: synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext, // Contexto global de la aplicacion
                    FitnessDatabase::class.java, //   Clase de la base de datos
                    "fitness_database" // Nombre de la base de datos
                ).build()
                INSTANCE = instance // Guarda la instancia creada
                instance // Devuelve la base de datos lista
            }
        }
    }
}