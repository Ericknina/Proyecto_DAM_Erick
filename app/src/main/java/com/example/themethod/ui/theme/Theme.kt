package com.example.themethod.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.material3.Typography

// 1.La paleta de colores que usaremos para el tema claro
private val LightColorScheme = lightColorScheme(
    primary = GymAppAccent,
    background = GymAppBackground,
    surface = GymAppWhite,
    onBackground = GymAppTextPrimary,
    onSurface = GymAppTextPrimary,

)

// 2.Definimos el tema claro

// Esta función envuelve toda la aplicación para aplicarle los estilos
@Composable
fun TheMethodTheme(
    // Verifica si el telefono del usuario tiene activado el modo oscuro en sus ajustes
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // Forzamos el tema claro para que se pueda usar en toda la app
    val colorScheme = LightColorScheme

    // MaterialTheme es el motor nativo de Google que inyecta estos colores y fuentes
    MaterialTheme(
        colorScheme = colorScheme,
        typography = androidx.compose.material3.Typography(), // Asegúrate de tener Typography por defecto
        content = content
    )
}