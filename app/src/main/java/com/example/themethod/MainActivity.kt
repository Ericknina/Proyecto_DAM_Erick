package com.example.themethod

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.themethod.ui.PantallaBibliotecaEjercicios
import com.example.themethod.ui.PantallaCrearRutina
import com.example.themethod.ui.PantallaDetalleRutina
import com.example.themethod.ui.PantallaEntrenamientoActivo
import com.example.themethod.ui.PantallaLogin
import com.example.themethod.ui.PantallaLogros
import com.example.themethod.ui.PantallaPerfil
import com.example.themethod.ui.PantallaPrincipal
import com.example.themethod.ui.PantallaSplash
import com.example.themethod.ui.theme.TheMethodTheme
import com.example.themethod.viewmodel.RutinaViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.themethod.ui.PantallaRegistro

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?){
        installSplashScreen()
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

                NavHost(navController = navController, startDestination = "Splash",
                    // Animaciones
                    enterTransition = { slideInHorizontally(tween(400)) { 1000 } + fadeIn(tween(400)) },
                    exitTransition = { slideOutHorizontally(tween(400)) { -1000 } + fadeOut(tween(400)) },
                    popEnterTransition = { slideInHorizontally(tween(400)) { -1000 } + fadeIn(tween(400)) },
                    popExitTransition = { slideOutHorizontally(tween(400)) { 1000 } + fadeOut(tween(400)) }





                ) {
                    // CAMBIO 2: Nueva ruta para el Splash
                    composable("splash") {
                        PantallaSplash(onNavigateNext = { ruta ->
                            navController.navigate(ruta) {
                                // Borramos el splash del historial para que no se pueda volver atrás
                                popUpTo("splash") { inclusive = true }
                            }
                        })
                    }

                    // 1. PANTALLA DE LOGIN
                    // 1. RUTA DE LOGIN
                    composable("login") {
                        val scope = rememberCoroutineScope()
                        PantallaLogin(
                            onLoginClick = { email, password, setLoading, setError ->

                                auth.signInWithEmailAndPassword(email, password)
                                    .addOnCompleteListener { task ->
                                        setLoading(false) // Apagamos el circulito

                                        if (task.isSuccessful) {
                                            val uid = auth.currentUser?.uid
                                            if (uid != null) {
                                                scope.launch {
                                                    viewModel.descargarDatosDeNube(uid)
                                                    navController.navigate("home") {
                                                        popUpTo("login") { inclusive = true }
                                                    }
                                                }
                                            }
                                        } else {
                                            val mensajeError = task.exception?.localizedMessage ?: "Error al iniciar sesión"
                                            setError(mensajeError) // Mostramos el mensaje en pantalla
                                        }
                                    }
                            },
                            onRegisterClick = {
                                navController.navigate("registro")
                            }
                        )
                    }

// 2.  RUTA DE REGISTRO
                    composable("registro") {
                        val scope = rememberCoroutineScope()
                        PantallaRegistro(
                            onRegistroClick = { nombre, email, password ->
                                if (email.isNotEmpty() && password.isNotEmpty()) {
                                    // AQUÍ es donde realmente llamamos a Firebase para crear la cuenta
                                    auth.createUserWithEmailAndPassword(email, password)
                                        .addOnCompleteListener { task ->
                                            if (task.isSuccessful) {
                                                // Si quieres guardar el nombre del usuario en tu base de datos
                                                // podrías hacerlo aquí antes de navegar.
                                                navController.navigate("home") {
                                                    popUpTo("login") { inclusive = true }
                                                }
                                            } else {
                                                println("Error al registrar: ${task.exception?.message}")
                                            }
                                        }
                                }
                            },
                            onVolverAlLogin = {
                                navController.popBackStack() // Simplemente vuelve atrás
                            }
                        )
                    }

                    //Logros
                    composable("logros") {
                        PantallaLogros(
                            viewModel = viewModel,
                            onVolver = { navController.popBackStack() }
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
                            onIrALogros = { navController.navigate("logros") },
                            onIrAPerfil = { navController.navigate("perfil") },
                            onCerrarSesion = {
                                viewModel.cerrarSesion {
                                    // USAMOS LA FUNCIÓN DEL VIEWMODEL (que limpia Room y Firebase)
                                    navController.navigate("login") {
                                        // Borramos "home" del historial para que no se pueda volver atrás
                                        popUpTo("home") { inclusive = true }
                                    }
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

                    // Pantalla Perfil
                    composable("perfil") {
                        // Obtenemos el usuario actual de Firebase
                        val usuarioActual = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser
                        val correoUsuario = usuarioActual?.email ?: "usuario@desconocido.com"
                        val context = LocalContext.current // Necesario para los Toasts

                        PantallaPerfil(
                            emailUsuario = correoUsuario,
                            onVolver = { navController.popBackStack() },
                            onCambiarContrasena = { nuevaPass ->
                                // Llamamos a la función del ViewModel
                                viewModel.cambiarContrasenaEnFirebase(nuevaPass) { exito, mensaje ->
                                    // Mostramos un mensajito en la pantalla (Toast)
                                    android.widget.Toast.makeText(context, mensaje, android.widget.Toast.LENGTH_LONG).show()
                                }
                            },
                            onCerrarSesion = {
                                viewModel.cerrarSesion {
                                    navController.navigate("login") {
                                        popUpTo("home") { inclusive = true }
                                    }
                                }
                            }
                        )
                    }

                    // . PANTALLA DE DETALLE

                    composable ("detalle/{rutinaId}/{nombreDeLaRutina}" ) { backStackEntry ->
                        // Extraemos el nombre que viene en la RUTA
                        val rutinaIdString = backStackEntry.arguments?.getString("rutinaId") ?: "0"
                        val rutinaId = backStackEntry.arguments?.getString("rutinaId")?.toIntOrNull() ?: 0
                        val nombre = backStackEntry.arguments?.getString("nombreDeLaRutina") ?: "Rutina"
                        PantallaDetalleRutina(
                            rutinaId = rutinaId, // <-- Le pasamos el ID
                            nombreRutina = nombre,
                            viewModel = viewModel, // <-- Le pasamos el cerebro
                            onVolver = { navController.popBackStack() },
                            onEmpezarEntrenamiento = {
                                // Le decimos que navegue a la pantalla de entrenamiento activo pasando los datos
                                navController.navigate("entrenamiento_activo/$rutinaId/$nombre")
                            }
                        )
                    }

                    // . Pantalla de BIBLIOTECA DE EJERCICIOS
                    composable("biblioteca"){
                        PantallaBibliotecaEjercicios(
                            viewModel = viewModel,
                            onVolver = { navController.popBackStack() } // popBackStack permite dar un paso atras
                        )
                    }

                    // Asegúrate de que los argumentos de la ruta coincidan con lo que pide la pantalla
                    composable(
                        route = "entrenamiento_activo/{rutinaId}/{nombreRutina}",
                        arguments = listOf(
                            navArgument("rutinaId") { type = NavType.IntType },
                            navArgument("nombreRutina") { type = NavType.StringType }
                        )
                    ) { backStackEntry ->
                        val id = backStackEntry.arguments?.getInt("rutinaId") ?: -1
                        val nombre = backStackEntry.arguments?.getString("nombreRutina") ?: "Entrenamiento"

                        PantallaEntrenamientoActivo(
                            rutinaId = id,
                            nombreRutina = nombre,
                            viewModel = viewModel,
                            onTerminarEntrenamiento = {
                                // Volvemos atrás cuando el usuario termine
                                navController.popBackStack()
                            }
                        )
                    }
                }
            }

        }
    }




}

