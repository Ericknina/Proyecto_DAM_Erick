package com.example.themethod.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface EjercicioDao {

    //1. LEE LOS EJERCICIOS DEL EL CATÁLOGO: Nos devuelve la lista de todos los ejercicios disponibles
    @Query("SELECT * FROM tabla_ejercicio")
    fun obtenerTodosLosEjercicios(): Flow<List<Ejercicio>>


    // 2. AÑADIR UN EJERCICIO AL CATÁLOGO: Por si el usuario quiere crear un ejercicio nuevo que no exista
    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertEjercicio(ejercicio: Ejercicio)




}