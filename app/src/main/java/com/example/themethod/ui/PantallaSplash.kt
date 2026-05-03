package com.example.themethod.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.themethod.ui.theme.GymAppAccent
import com.example.themethod.ui.theme.GymAppBackground
import com.example.themethod.ui.theme.GymAppTextSecondary
import kotlinx.coroutines.delay

@Composable
fun PantallaSplash(onNavigateNext: (String) -> Unit) {
    // 1. Duración del Splash (2 segundos)
    LaunchedEffect(Unit) {
        delay(2000L)
        val auth = com.google.firebase.auth.FirebaseAuth.getInstance()
        // 2. Decisión inteligente: ¿Está logueado?
        if (auth.currentUser != null) {
            onNavigateNext("home")
        } else {
            onNavigateNext("login")
        }
    }

    // 3. Diseño Visual
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(GymAppBackground), // Tu color oscuro de fondo
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // Aquí podrías poner un Icono o Imagen de tu logo
            Text(
                text = "THE METHOD",
                fontSize = 40.sp,
                fontWeight = FontWeight.ExtraBold,
                color = GymAppAccent,
                letterSpacing = 4.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "TRAIN HARDER",
                fontSize = 14.sp,
                color = GymAppTextSecondary,
                letterSpacing = 2.sp
            )
        }
    }
}