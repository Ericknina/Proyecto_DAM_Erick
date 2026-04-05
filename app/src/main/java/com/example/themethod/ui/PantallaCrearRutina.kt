package com.example.themethod.ui



import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.example.themethod.data.Rutina
import com.example.themethod.viewmodel.RutinaViewModel
import com.example.themethod.ui.theme.*


// ----- Etiqueta que avisa a Android que esta función es una pantalla -----
// Ademas de avisar a Android que estamos usando componentes visuales modernos
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaCrearRutina(

    // Recibimos el viewModel para poder usarlo y guardar los datos
    viewModel: RutinaViewModel,

    // Recibimos la función que nos permitira volver atras
    onVolver: () -> Unit
) {

    // ------- Aqui guardaremos los datos del usuario que escriba -------
    // "remember { mutableStateOf("")" crea una memoria permamente

    var nombreInput by remember { mutableStateOf("") }


    // Con Column ordenamos de arriba hacia abajo los elementos
    Column(
        modifier = Modifier // con modifier indicaremos como se veran los elementos
            .fillMaxSize()
            .background(GymAppBackground) // Mismo fondo gris de la app
            .padding(24.dp)
            .statusBarsPadding()
    ) {
        // Botón para volver atrás
        IconButton(onClick = { onVolver() }, modifier = Modifier.offset(x = (-12).dp)) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = GymAppAccent)
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Título
        Text(
            text = "Create New\nWorkout",
            fontSize = 36.sp,
            fontWeight = FontWeight.Bold,
            lineHeight = 40.sp,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(40.dp))

        // Campo de texto donde escribiremos el nombre de la rutina
        OutlinedTextField(
            value = nombreInput,
            onValueChange = { nombreInput = it },
            label = { Text("Workout Name (e.g. Leg Day)", color = Color.Gray) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                unfocusedBorderColor = Color.Transparent,
                focusedBorderColor = GymAppAccent
            )
        )

        Spacer(modifier = Modifier.height(40.dp))

        // Botón de Guardar
        Button(
            onClick = {
                // Se comprueba primero que no se haya dejado la caja en blanco
                if (nombreInput.isNotBlank()) {
                    // Si no esta en blanco, se crea la rutina
                    val nuevaRutina = Rutina(nombreRutina = nombreInput)
                    // Se inserta en la base de datos
                    viewModel.insertarRutina(nuevaRutina) // Guarda en la base de datos
                    onVolver() // Vuelve a la pantalla principal
                }
            },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = GymAppAccent)
        ) {
            // Texto del botón de Guardar rutina
            Text("Save Workout", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    }
}