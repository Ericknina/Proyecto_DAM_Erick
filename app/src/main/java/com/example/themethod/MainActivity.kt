package com.example.themethod

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.themethod.ui.PantallaCrearRutina
import com.example.themethod.ui.PantallaDetalleRutina
import com.example.themethod.ui.PantallaPrincipal
import com.example.themethod.ui.theme.TheMethodTheme
import com.example.themethod.viewmodel.RutinaViewModel



class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)

        // 1. Obtenemos el repositorio de nuestro "interruptor general" (FitnessApp)

        val app = application as FitnessApp
        val factory = RutinaViewModel.RutinaViewModelFactory(app.repository)

        setContent {
            TheMethodTheme {
                val viewModel: RutinaViewModel = viewModel(factory = factory)
                val navController = rememberNavController() // <-- EL GPS

                NavHost(navController = navController, startDestination = "home") {

                    // 1. PANTALLA PRINCIPAL
                    composable("home") {
                        PantallaPrincipal(
                            viewModel = viewModel,
                            onIrACrear = {
                               navController.navigate("Crear")},
                            onRutinaClick = {
                                nombreDeLaRutina ->
                                navController.navigate("detalle/$nombreDeLaRutina")
                            }

                        )
                    }

                    // 2.  PANTALLA DE CREAR
                    composable("crear") {
                        PantallaCrearRutina(
                            viewModel = viewModel,
                            onVolver = { navController.popBackStack() }
                        )
                    }


                    // 3. PANTALLA DE DETALLE

                    composable ("detalle/{nombreDeLaRutina}" ) { backStackEntry ->
                        // Extraemos el nombre que viene en la RUTA

                        val nombre = backStackEntry.arguments?.getString("nombreDeLaRutina") ?: "Rutina"
                        PantallaDetalleRutina(
                            nombreRutina = nombre,
                            onVolver = { navController.popBackStack() }
                        )
                    }
                }
            }

        }
    }




}

