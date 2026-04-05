package com.example.themethod

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.themethod.ui.PantallaBibliotecaEjercicios
import com.example.themethod.ui.PantallaCrearRutina
import com.example.themethod.ui.PantallaDetalleRutina
import com.example.themethod.ui.PantallaLogin
import com.example.themethod.ui.PantallaPrincipal
import com.example.themethod.ui.theme.TheMethodTheme
import com.example.themethod.viewmodel.RutinaViewModel
import com.google.firebase.auth.FirebaseAuth


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)

        // 1. Obtenemos el repositorio de nuestro "interruptor general" (FitnessApp)

        val app = application as FitnessApp
        val factory = RutinaViewModel.RutinaViewModelFactory(app.repository)

        // Llamamos al servicio de autenticacion de Firebase
        val  auth = FirebaseAuth.getInstance()

        setContent {
            TheMethodTheme {
                val viewModel: RutinaViewModel = viewModel(factory = factory)
                val navController = rememberNavController() // <-- EL GPS

                NavHost(navController = navController, startDestination = "login") {

                    // 1. PANTALLA DE LOGIN
                    composable("login") {
                        PantallaLogin(
                            onLoginClick = { email, password ->
                                if (email.isNotEmpty() && password.isNotEmpty()){
                                    //Pedimos a Firebase que inicie sesion
                                    auth.signInWithEmailAndPassword(email, password)
                                        .addOnCompleteListener { task ->
                                            if (task.isSuccessful){
                                                // Nos novemos a la pantalla principal
                                                navController.navigate("home"){
                                                popUpTo("login"){inclusive = true}
                                                    }
                                            }else{
                                                // Muestra el error real en la pantalla del móvil
                                                println(" Error al entrar: ${task.exception?.message}")
                                            }
                                        }
                                }
                            },
                            onRegisterClick = { email, password ->
                                if (email.isNotEmpty() && password.isNotEmpty()){
                                    auth.createUserWithEmailAndPassword(email, password)
                                        .addOnCompleteListener { task ->
                                            if (task.isSuccessful) {
                                                // Nos novemos a la pantalla principal
                                                navController.navigate("home") {
                                                    popUpTo("login") { inclusive = true }
                                                }
                                            } else {
                                                // Muestra el error real en la pantalla del móvil
                                                println(" Error al registrar: ${task.exception?.message}")
                                            }
                                        }
                                 }
                            }

                        )
                    }




                    // . PANTALLA PRINCIPAL
                    composable("home") {
                        PantallaPrincipal(
                            viewModel = viewModel,
                            onIrACrear = {
                               navController.navigate("Crear")},
                            onRutinaClick = {
                                id, nombre ->
                                navController.navigate("detalle/$id/$nombre")
                            },
                            onIrABiblioteca = {navController.navigate("biblioteca")},
                            onCerrarSesion = {
                                auth.signOut()
                                navController.navigate("login"){
                                    popUpTo("home"){inclusive = true}
                                }
                            }

                        )
                    }

                    // .  PANTALLA DE CREAR
                    composable("crear") {
                        PantallaCrearRutina(
                            viewModel = viewModel,
                            onVolver = { navController.popBackStack() }
                        )
                    }


                    // . PANTALLA DE DETALLE

                    composable ("detalle/{rutinaId}/{nombreDeLaRutina}" ) { backStackEntry ->
                        // Extraemos el nombre que viene en la RUTA

                        val rutinaId = backStackEntry.arguments?.getString("rutinaId")?.toIntOrNull() ?: 0
                        val nombre = backStackEntry.arguments?.getString("nombreDeLaRutina") ?: "Rutina"
                        PantallaDetalleRutina(
                            rutinaId = rutinaId, // <-- Le pasamos el ID
                            nombreRutina = nombre,
                            viewModel = viewModel, // <-- Le pasamos el cerebro
                            onVolver = { navController.popBackStack() }
                        )
                    }

                    // . Pantalla de BIBLIOTECA DE EJERCICIOS
                    composable("biblioteca"){
                        PantallaBibliotecaEjercicios(
                            viewModel = viewModel,
                            onVolver = { navController.popBackStack() } // popBackStack permite dar un paso atras
                        )
                    }
                }
            }

        }
    }




}

