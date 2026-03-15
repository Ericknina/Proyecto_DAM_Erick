package com.example.themethod.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.themethod.ui.theme.GymAppAccent
import com.example.themethod.ui.theme.GymAppBackground
import com.example.themethod.ui.theme.GymAppTextPrimary
import com.example.themethod.ui.theme.GymAppTextSecondary


@Composable

fun PantallaDetalleRutina(
    nombreRutina: String,
    onVolver: () -> Unit
){
    Column (modifier = Modifier
        .fillMaxWidth()
        .background(GymAppBackground)
        .padding(24.dp)
        .statusBarsPadding()
    ){
        // Boton de Volver
        // Ejecuta la accion de volver cuando se presiona
        IconButton( onClick = { onVolver()}, modifier = Modifier.offset(x = (-12).dp))

        // El icono de fecha hacia atras por defecto para android
        {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription ="Volver", tint = GymAppAccent)
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Nombre de la rutina y sus estilos

        Text(
            text = nombreRutina,
            fontSize = 40.sp,
            fontWeight = FontWeight.Bold,
            color = GymAppTextPrimary
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Subtitulo de ejercicios y sus estilos

        Text(
            text = "Exercises",
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            color = GymAppTextSecondary
        )

    }

}