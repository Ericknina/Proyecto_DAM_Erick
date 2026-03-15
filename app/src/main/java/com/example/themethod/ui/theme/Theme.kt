package com.example.themethod.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.material3.Typography

private val LightColorScheme = lightColorScheme(
    primary = GymAppAccent,
    background = GymAppBackground,
    surface = GymAppWhite,
    onBackground = GymAppTextPrimary,
    onSurface = GymAppTextPrimary,
    // ... otros colores si quieres
)

@Composable
fun TheMethodTheme( // El nombre puede variar según tu app
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // Forzamos el tema claro para que se parezca a la imagen
    val colorScheme = LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = androidx.compose.material3.Typography(), // Asegúrate de tener Typography por defecto
        content = content
    )
}