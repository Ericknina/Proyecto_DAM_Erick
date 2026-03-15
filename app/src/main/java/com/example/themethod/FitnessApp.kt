package com.example.themethod

import android.app.Application
import com.example.themethod.data.FitnessDatabase
import com.example.themethod.repository.RutinaRepository

class FitnessApp : Application() {

    // Utilizamos "lazy" para que la base de datos solo se cree cuando sea necesaria
    // volviendo mas rapido el arranque

    val database by lazy { FitnessDatabase.getDatabase(this) }

    val repository by lazy { RutinaRepository(database.rutinaDao()) }









}