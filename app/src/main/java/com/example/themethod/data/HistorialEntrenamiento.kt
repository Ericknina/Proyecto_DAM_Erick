package com.example.themethod.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tabla_historial")
data class HistorialEntrenamiento(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nombreRutina: String,
    val fecha: String,
    val duracion: String
)