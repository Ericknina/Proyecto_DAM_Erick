package com.example.themethod.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.themethod.data.Rutina
import com.example.themethod.repository.RutinaRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class RutinaViewModel(private val repository: RutinaRepository) : ViewModel() {

    // 1. LEER DATOS:
    // Exponemos la tubería de datos (Flow) para que la pantalla la observe.
    val todasLasRutinas: Flow<List<Rutina>> = repository.todaslasRutinas

    // 2. ESCRIBIR DATOS:
    // La pantalla llamará a esta función cuando el usuario pulse el botón "Guardar"
    fun insertarRutina(rutina: Rutina) {
        // viewModelScope.launch crea un hilo secundario para no congelar la pantalla
        viewModelScope.launch {
            repository.insertarRutina(rutina)
        }
    }

    // --- FACTORY ---
// Como nuestro ViewModel necesita un parámetro (el Repositorio), Android no sabe crearlo solo.
// Necesitamos esta "Fábrica" para enseñarle a Android cómo construir nuestro ViewModel.
    class RutinaViewModelFactory(private val repository: RutinaRepository) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(RutinaViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return RutinaViewModel(repository) as T
            }
            throw IllegalArgumentException("Clase ViewModel desconocida")
        }
    }
}



