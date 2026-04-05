package com.example.themethod.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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
import kotlinx.coroutines.launch


@Composable
fun PantallaPrincipal(
    viewModel: RutinaViewModel,
    onIrACrear: () -> Unit,
    onRutinaClick: (Int, String) -> Unit,
    onIrABiblioteca: () -> Unit,
    onCerrarSesion: () -> Unit
) {
    // ---------------------------------------------
    // 1. ZONA DE BASE DE DATOS Y ESTADO
    // ----------------------------------------------
    val rutinas by viewModel.todasLasRutinas.collectAsState(initial = emptyList())
    val snackbarHostState = remember { androidx.compose.material3.SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var mostrarMenu by remember { mutableStateOf(false) }

    Scaffold(
        // 1. EL MENÚ SUPERIOR
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, end = 24.dp, bottom = 8.dp), // Márgenes para que no se pegue
                horizontalArrangement = Arrangement.End
            ) {
                Box {
                    IconButton(onClick = { mostrarMenu = true }) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "Menú",
                            modifier = Modifier.size(28.dp),
                            tint = GymAppAccent
                        )
                    }
                    DropdownMenu(
                        expanded = mostrarMenu,
                        onDismissRequest = { mostrarMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Cerrar Sesión") },
                            onClick = {
                                mostrarMenu = false
                                onCerrarSesion()
                            }
                        )
                    }
                }
            }
        },
        snackbarHost = { androidx.compose.material3.SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onIrACrear() },
                containerColor = GymAppAccent,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(text = "+", fontSize = 24.sp, modifier = Modifier.padding(horizontal = 16.dp))
            }
        },
        containerColor = GymAppBackground
    ) { paddingValues ->

        // 2. TODA LA INTERFAZ DE LA PANTALLA EN UNA ÚNICA COLUMNA
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues) // Esto es lo que empuja el contenido hacia abajo
                .padding(horizontal = 24.dp)
        ) {
            // Título Principal
            Text(
                text = "Choose a\nWorkout",
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 40.sp,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(30.dp))

            // Subtítulo
            Text(
                text = "Your Routines",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Botón acceso a la biblioteca de ejercicios
            Button(
                onClick = { onIrABiblioteca() },
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GymAppAccent)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Catálogo", modifier = Modifier.padding(end = 8.dp))
                Text("Biblioteca de Ejercicios", fontSize = 16.sp)
            }



            // ----------------------------------------
            // 2. ZONA DE LA LISTA
            // -----------------------------------------
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
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
                        val dismissState = rememberSwipeToDismissBoxState(
                            confirmValueChange = { state ->
                                if (state == SwipeToDismissBoxValue.EndToStart) {
                                    viewModel.eliminarRutina(rutina.idRutina)
                                    scope.launch {
                                        snackbarHostState.showSnackbar("Routine deleted")
                                    }
                                    true
                                } else {
                                    false
                                }
                            }
                        )

                        SwipeToDismissBox(
                            state = dismissState,
                            enableDismissFromStartToEnd = false,
                            backgroundContent = {
                                val color = if (dismissState.targetValue == SwipeToDismissBoxValue.EndToStart) {
                                    Color.Red.copy(alpha = 0.8f)
                                } else {
                                    Color.Transparent
                                }

                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(vertical = 10.dp)
                                        .background(color, shape = RoundedCornerShape(24.dp)),
                                    contentAlignment = Alignment.CenterEnd
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Borrar",
                                        tint = Color.White,
                                        modifier = Modifier.padding(end = 24.dp)
                                    )
                                }
                            },
                            content = {
                                RutinaCard(
                                    rutina = rutina,
                                    onClick = { onRutinaClick(rutina.idRutina, rutina.nombreRutina) }
                                )
                            }
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
            .clickable { onClick() },
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
                    imageVector  = Icons.Default.PlayArrow,
                    contentDescription = "Empezar",
                    tint = Color.White,
                    modifier = Modifier.padding(10.dp)
                )
            }
        }
    }
}