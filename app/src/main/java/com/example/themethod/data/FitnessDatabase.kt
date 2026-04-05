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
            return INSTANCE ?: synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FitnessDatabase::class.java,
                    "fitness_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}