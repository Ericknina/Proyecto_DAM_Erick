package com.example.themethod.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("tabla_rutinas")

data class Rutina(
    @PrimaryKey(autoGenerate = true)
    val idRutina: Int = 0,
    val nombreRutina: String,
    val fechaCreacion: Long = System.currentTimeMillis()
)