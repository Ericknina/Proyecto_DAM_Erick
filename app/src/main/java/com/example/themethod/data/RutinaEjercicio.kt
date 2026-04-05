package com.example.themethod.data

import androidx.room.Entity

@Entity("tabla_rutina_ejercicio", primaryKeys = ["idRutina", "idEjercicio"])

data class RutinaEjercicioCrossRef(
    val idRutina: Int,
    val idEjercicio: Int,
    val series: Int,
    val repeticiones: Int,
    val pesoKgs: Double
)