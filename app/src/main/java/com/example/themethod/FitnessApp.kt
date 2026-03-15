package com.example.themethod

import android.app.Application
import com.example.themethod.data.FitnessDatabase
import com.example.themethod.repository.RutinaRepository

class FitnessApp : Application() {

    // Utilizamos "lazy" para que la base de datos solo se cree cuando sea necesaria
    // volviendo mas rapido el arranque
    val database by lazy { FitnessDatabase.getDatabase(this) }

    // También inicializamos el Repositorio con la base de datos
    // las herramientas de la base de datos (el DAO). Por eso le pasamos "database.rutinaDao()"
    val repository by lazy { RutinaRepository(database.rutinaDao()) }








}