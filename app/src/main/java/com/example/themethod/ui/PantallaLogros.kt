package com.example.themethod.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.themethod.data.HistorialEntrenamiento
import com.example.themethod.ui.theme.*
import com.example.themethod.viewmodel.RutinaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaLogros(
    viewModel: RutinaViewModel,
    onVolver: () -> Unit
) {
    val historial by viewModel.historialCompleto.collectAsState(initial = emptyList())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis Logros 🏆", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = GymAppAccent)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = GymAppBackground)
            )
        },
        containerColor = GymAppBackground
    ) { padding ->
        if (historial.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Aún no has terminado ningún entrenamiento.\n¡A darle caña!", color = Color.Gray)
            }
        } else {
            LazyColumn(
                modifier = Modifier.padding(padding).fillMaxSize(),
                contentPadding = PaddingValues(16.dp)
            ) {
                items(historial) { registro ->
                    TarjetaHistorial(registro)
                }
            }
        }
    }
}

@Composable
fun TarjetaHistorial(registro: HistorialEntrenamiento) {
    Surface(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(text = registro.nombreRutina, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = GymAppTextPrimary)
                Text(text = registro.fecha, fontSize = 14.sp, color = GymAppTextSecondary)
            }
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = GymAppAccent.copy(alpha = 0.1f)
            ) {
                Text(
                    text = "⏱️ ${registro.duracion}",
                    modifier = Modifier.padding(8.dp),
                    color = GymAppAccent,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}