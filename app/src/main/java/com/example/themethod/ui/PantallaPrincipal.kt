package com.example.themethod.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.themethod.data.Rutina
import com.example.themethod.ui.theme.GymAppAccent
import com.example.themethod.ui.theme.GymAppBackground
import com.example.themethod.ui.theme.GymAppTextPrimary
import com.example.themethod.ui.theme.GymAppTextSecondary
import com.example.themethod.ui.theme.GymAppWhite
import com.example.themethod.viewmodel.RutinaViewModel


@Composable
fun PantallaPrincipal(
    viewModel: RutinaViewModel,
    onIrACrear: () -> Unit, //  nos permite movernos a la otra pantalla de crear
    onRutinaClick: (String) -> Unit // Nos permite navegar a la pantalla de ejercicios
) {

    // ---------------------------------------------
    // 1. ZONA DE BASE DE DATOS
    // ----------------------------------------------
     val rutinas by viewModel.todasLasRutinas.collectAsState(initial = emptyList())


    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onIrACrear() }, // Al pulsar el +, usamos la "llave" para navegar
                containerColor = GymAppAccent,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(text = "+", fontSize = 24.sp, modifier = Modifier.padding(horizontal = 16.dp))
            }
        },
        containerColor = GymAppBackground // Tu fondo gris clarito
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp, vertical = 16.dp)
        ) {

            // Icono de menú arriba a la derecha
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Menú",
                    modifier = Modifier.size(28.dp),
                    tint = GymAppAccent
                )
            }

            Spacer(modifier = Modifier.height(30.dp))

            // Título Principal (Forzado a negro para que no desaparezca)
            Text(
                text = "Choose a\nWorkout",
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 40.sp,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Subtítulo (Forzado a negro)
            Text(
                text = "Your Routines",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // ----------------------------------------
            // 2. ZONA DE LA LISTA (AHORA MISMO SIMULADA)
            // -----------------------------------------
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 80.dp) // Para que el botón + no tape el último elemento
            ) {



                // Si NO está vacía, mostramos las tarjetas con las rutinas

                if (rutinas.isEmpty()) {
                    item {
                        Text(
                            text = "No routines yet. Create one!",
                            fontSize = 16.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(top = 20.dp)
                        )
                    }
                } else {
                    items(rutinas) { rutina ->
                        RutinaCard(
                            rutina = rutina,
                            onClick = {onRutinaClick(rutina.nombreRutina)}


                        )
                    }
                }

            }
        }
    }
}

@Composable
fun RutinaCard(rutina: Rutina, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 10.dp)
            .clickable { onClick() }, // Ahora podremos tocar toda la tarjeta
        shape = RoundedCornerShape(24.dp),
        color = GymAppWhite,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 24.dp, vertical = 20.dp)
                .fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = rutina.nombreRutina,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = GymAppTextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Start today!",
                    style = MaterialTheme.typography.bodyMedium,
                    color = GymAppTextSecondary
                )
            }

            Surface(
                shape = RoundedCornerShape(50),
                color = GymAppAccent,
                modifier = Modifier.size(45.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Empezar",
                    tint = Color.White,
                    modifier = Modifier.padding(10.dp)
                )
            }
        }
    }
}