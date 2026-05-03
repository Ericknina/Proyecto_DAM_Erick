package com.example.themethod.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.themethod.viewmodel.RutinaViewModel

// 🎨 Importamos tus variables de colores personalizadas
import com.example.themethod.ui.theme.* @Composable
fun PantallaBibliotecaEjercicios(
    viewModel: RutinaViewModel,
    onVolver: () -> Unit // La acción para regresar a la pantalla anterior
) {
    //  todos los ejercicios de la base de datos en tiempo real.

    val ejercicios by viewModel.todosLosEjercicios.collectAsState(initial = emptyList())

    //  Controla si la ventana para crear un ejercicio se ve (true) o no (false).
    // 'remember' hace que la pantalla no olvide este estado aunque se redibuje.
    var mostrarDialogo by remember { mutableStateOf(false) }

    //  Nos permite poner un fondo y un botón flotante fácilmente.
    Scaffold(
        // Configuración del botón flotante (el '+' abajo a la derecha)
        floatingActionButton = {
            FloatingActionButton(
                onClick = { mostrarDialogo = true },
                containerColor = GymAppAccent,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp) // Bordes redondeados
            ) {
                Icon(Icons.Default.Add, contentDescription = "Agregar Ejercicio")
            }
        },
        containerColor = GymAppBackground // Pintamos el fondo de toda la pantalla con tu gris clarito
    ) { paddingValues ->

        //  Organiza los elementos de arriba a abajo
        Column(
            modifier = Modifier
                .fillMaxSize() // Ocupa toda la pantalla
                .padding(paddingValues) // Respeta el espacio del Scaffold
                .padding(24.dp) // Deja un margen de 24dp a los lados para que no pegue a los bordes
        ) {
            //  BOTÓN DE VOLVER
            IconButton(onClick = { onVolver() }, modifier = Modifier.offset(x = (-12).dp)) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = GymAppAccent)
            }

            Spacer(modifier = Modifier.height(10.dp)) // Espacio en blanco

            // (Títulos)
            Text("Mis Ejercicios", fontSize = 40.sp, fontWeight = FontWeight.Bold, color = GymAppTextPrimary)
            Text("Your exercises", fontSize = 20.sp, color = GymAppTextSecondary, modifier = Modifier.padding(bottom = 16.dp))

            //  Aquí se dibujan los ejercicios solo carga los que ves en pantalla para no saturar el móvil.
            LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(bottom = 80.dp)) {

                // Iteramos sobre nuestra antena de ejercicios
                items(ejercicios) { ejercicio ->

                    //  TARJETA DE CADA EJERCICIO
                    Surface(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                        shape = RoundedCornerShape(12.dp),
                        color = Color.White,
                        shadowElevation = 1.dp
                    ) {
                        // Ponemos el Nombre y el Grupo Muscular en fila, separados a los extremos (SpaceBetween)
                        Row(
                            modifier = Modifier.padding(16.dp).fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = ejercicio.nombre, fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = GymAppTextPrimary)
                            Text(text = ejercicio.grupoMuscular, fontSize = 14.sp, color = GymAppTextSecondary)
                        }
                    }
                }
            }
        }
    }

    // Solo se dibuja si el interruptor está en 'true'
    if (mostrarDialogo) {
        // Variables temporales para guardar lo que el usuario escribe en las cajas de texto
        var nombreInput by remember { mutableStateOf("") }
        var grupoInput by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { mostrarDialogo = false },
            title = { Text("Nuevo Ejercicio") },
            text = {
                // Las dos cajas para escribir
                Column {
                    OutlinedTextField(
                        value = nombreInput,
                        onValueChange = { nombreInput = it },
                        label = { Text("Nombre(e.g. Leg Press)") }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = grupoInput,
                        onValueChange = { grupoInput = it },
                        label = { Text("Grupo Muscular(e.g. Espalda)") }
                    )
                }
            },
            // Botón de GUARDAR
            confirmButton = {
                TextButton(
                    onClick = {
                        // Verificamos que no haya dejado nada en blanco
                        if (nombreInput.isNotBlank() && grupoInput.isNotBlank()) {
                            // Le decimos al cerebro que guarde el nuevo ejercicio
                            viewModel.crearEjerciciosPersonalizados(nombreInput, grupoInput)
                            // Apagamos el interruptor para que la ventana desaparezca
                            mostrarDialogo = false
                        }
                    }
                ) { Text("Guardar", color = GymAppAccent) }
            },
            // Botón de CANCELAR
            dismissButton = {
                TextButton(
                    onClick = { mostrarDialogo = false }
                ) { Text("Cancelar", color = Color.Gray) }
            }
        )
    }
}