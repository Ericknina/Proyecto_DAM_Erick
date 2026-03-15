package com.example.themethod.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(  "tabla_ejercicio")

data class Ejercicio(
    @PrimaryKey(autoGenerate = true)
    val idEjercicio: Int = 0,
    val nombre: String,
    val grupoMuscular: String
)