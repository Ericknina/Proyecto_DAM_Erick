package com.example.themethod.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [Rutina:: class, Ejercicio:: class, RutinaEjercicioCrossRef:: class,HistorialEntrenamiento::class],
    version = 3,
    exportSchema = false,

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
    private class FitnessDatabaseCallback : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            // Cuando la DB se crea por primera vez, lanzamos una corrutina
            INSTANCE?.let { database ->
                CoroutineScope(Dispatchers.IO).launch {
                    val dao = database.ejercicioDao()

                    // Recorremos la lista de nuestro archivo DatosIniciales y la guardamos
                    DatosIniciales.ejerciciosPredeterminados.forEach { ejercicio ->
                        // Verifica que tu EjercicioDao tenga la función insertEjercicio (o insert)
                        dao.insertEjercicio(ejercicio)
                    }
                }
            }
        }
    }
}