package com.example.themethod.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao

interface RutinaDao {

    // Guarda la rutina nueva en la base de datos

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertRutina(rutina: Rutina) : Long


    // Lee todas las rutinas. Usamos Flow para que la pantalla se actualize sola si
    // Agregamos una nueva

    @Query("SELECT * FROM tabla_rutinas")
    fun obtenerRutinas(): Flow<List<Rutina>>



}