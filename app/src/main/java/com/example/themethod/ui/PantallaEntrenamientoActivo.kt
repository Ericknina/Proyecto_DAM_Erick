package com.example.themethod.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.themethod.ui.theme.GymAppAccent
import com.example.themethod.ui.theme.GymAppBackground
import com.example.themethod.viewmodel.RutinaViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaEntrenamientoActivo(
    rutinaId: Int,
    nombreRutina: String,
    viewModel: RutinaViewModel,
    onTerminarEntrenamiento: () -> Unit
) {
    // 1. CARGA DE EJERCICIOS Y ESTADOS
    val ejercicios by viewModel.obtenerEjercicios(rutinaId).collectAsState(initial = emptyList())
    val haptic = LocalHapticFeedback.current

    // 2. CRONÓMETRO GENERAL (Tiempo total)
    var tiempoEnSegundos by remember { mutableIntStateOf(0) }
    LaunchedEffect(Unit) {
        while(true) {
            delay(1000L)
            tiempoEnSegundos++
        }
    }

    val minutosTotales = tiempoEnSegundos / 60
    val segundosTotales = tiempoEnSegundos % 60
    val tiempoFormateado = "${minutosTotales.toString().padStart(2, '0')}:${segundosTotales.toString().padStart(2, '0')}"

    // 3. ESTADO DEL TEMPORIZADOR DE DESCANSO
    var tiempoDescanso by remember { mutableIntStateOf(0) }
    var temporizadorActivo by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = temporizadorActivo, key2 = tiempoDescanso) {
        if (temporizadorActivo && tiempoDescanso > 0) {
            delay(1000L)
            tiempoDescanso--
        } else if (tiempoDescanso == 0) {
            temporizadorActivo = false
        }
    }

    // Formato para el reloj de descanso (ej: 01:30)
    val minDescanso = tiempoDescanso / 60
    val segDescanso = tiempoDescanso % 60
    val textoDescanso = "${minDescanso.toString().padStart(2, '0')}:${segDescanso.toString().padStart(2, '0')}"

    // USAMOS UN BOX PARA QUE EL RELOJ FLOTE SOBRE EL SCAFFOLD
    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Column {
                            Text(nombreRutina, fontSize = 18.sp)
                            Text("⏱️ $tiempoFormateado", style = MaterialTheme.typography.labelLarge, color = GymAppAccent)
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = onTerminarEntrenamiento) {
                            Icon(Icons.Default.Close, contentDescription = "Cerrar")
                        }
                    }
                )
            },
            floatingActionButton = {
                ExtendedFloatingActionButton(
                    onClick = {
                        viewModel.finalizarYGuardarEntrenamiento(nombreRutina, tiempoFormateado)
                        onTerminarEntrenamiento()
                    },
                    containerColor = GymAppAccent,
                    contentColor = Color.White
                ) {
                    Text("Finalizar Entrenamiento")
                }
            },
            containerColor = GymAppBackground
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp)
            ) {
                items(ejercicios, key = { it.idEjercicio }) { ejercicio ->
                    TarjetaEjercicioEntrenamiento(
                        ejercicio = ejercicio,
                        onSerieCompletada = { estaCompletada ->
                            if (estaCompletada) {
                                tiempoDescanso = 90 // 1 minuto y medio
                                temporizadorActivo = true
                            }
                        }
                    )
                }
                item { Spacer(modifier = Modifier.height(120.dp)) }
            }
        }

        // 4. EL TEMPORIZADOR VISUAL (FLOTANTE)
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 90.dp) // Lo elevamos para que no tape el botón de finalizar
        ) {
            AnimatedVisibility(
                visible = temporizadorActivo,
                enter = slideInVertically(initialOffsetY = { it }),
                exit = slideOutVertically(targetOffsetY = { it })
            ) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = Color.Black,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(100.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Notifications,
                            contentDescription = null,
                            tint = GymAppAccent,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Descanso: $textoDescanso",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        IconButton(
                            onClick = { temporizadorActivo = false },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Cerrar", tint = Color.Gray)
                        }
                    }
                }
            }
        }
    }
}