package com.example.themethod.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface EjercicioDao {

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertEjercicio(ejercicio: Ejercicio)

    @Query("SELECT * FROM tabla_ejercicio")

    fun obtenerTodosLosEjercicios(): Flow<List<Ejercicio>>


}