package com.example.themethod.ui

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.draw.shadow
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
    onIrALogros: () -> Unit,
    onCerrarSesion: () -> Unit,
    onIrAPerfil: () -> Unit,
) {
    // ---------------------------------------------
    // 1. ZONA DE BASE DE DATOS Y ESTADO
    // ----------------------------------------------
    val rutinas by viewModel.todasLasRutinas.collectAsState(initial = emptyList())
    val historial by viewModel.historialCompleto.collectAsState(initial = emptyList())
    val snackbarHostState = remember { androidx.compose.material3.SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var mostrarMenu by remember { mutableStateOf(false) }

    Scaffold(
        // 1. EL MENÚ SUPERIOR
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, end = 16.dp),
                horizontalArrangement = Arrangement.End
            ) {
                Box {
                    IconButton(
                        onClick = { mostrarMenu = true },
                        modifier = Modifier
                            .background(Color.White, CircleShape)
                            .size(40.dp)
                            .shadow(1.dp, CircleShape)
                    ) {
                        Icon(Icons.Default.Menu, "Menú", tint = GymAppAccent)
                    }
                    DropdownMenu(expanded = mostrarMenu, onDismissRequest = { mostrarMenu = false }) {

                    // NUEVA OPCIÓN: Mi Perfil
                    DropdownMenuItem(
                        text = { Text("Mi Perfil") },
                        onClick = {
                            mostrarMenu = false
                            // Llama a una nueva función de navegación que pasaremos por parámetro
                            onIrAPerfil()
                        }
                    )

                    // OPCIÓN EXISTENTE: Cerrar Sesión
                    DropdownMenuItem(
                        text = { Text("Cerrar Sesión", color = Color.Red) },
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
                onClick = onIrACrear,
                containerColor = GymAppAccent,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp)
            ) {
                // Mantenemos tu icono de "+" con estilo limpio
                Icon(Icons.Default.Add, contentDescription = "Crear", modifier = Modifier.size(28.dp))
            }
        },
        containerColor = GymAppBackground
    ) { paddingValues ->

        // 2. TODA LA INTERFAZ DE LA PANTALLA EN UNA ÚNICA COLUMNA
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(GymAppBackground)
                .padding(horizontal = 24.dp)
        ) {
            // Título Principal (Mejorado con ExtraBold)
            Text(
                text = "Elige una \nRutina",
                fontSize = 40.sp,
                fontWeight = FontWeight.ExtraBold,
                lineHeight = 44.sp,
                color = GymAppTextPrimary
            )

            Spacer(modifier = Modifier.height(24.dp))

            // --- TARJETA DE MOTIVACIÓN
            if (historial.isNotEmpty()) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    color = GymAppAccent.copy(alpha = 0.1f)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "🔥", fontSize = 24.sp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Llevas ${historial.size} entrenamientos completados. ¡Sigue así!",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = GymAppAccent
                        )
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }

            // Subtítulo (Your Routines)
            Text(
                text = "Mis Rutinas",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = GymAppTextPrimary
            )

            Spacer(modifier = Modifier.height(16.dp))

            // --- BOTONES DE ACCIÓN (Biblioteca y Logros) ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Botón acceso a la biblioteca de ejercicios (Ahora como primario)
                Button(
                    onClick = onIrABiblioteca,
                    modifier = Modifier.weight(1f).height(54.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("Biblioteca", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }

                // Boton de logros (Ahora como secundario estilizado)
                OutlinedButton(
                    onClick = onIrALogros,
                    modifier = Modifier.weight(1f).height(54.dp),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.5.dp, Color.LightGray)
                ) {
                    Text("🏆 Logros", fontSize = 14.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                }
            }

            // ----------------------------------------
            // 2. ZONA DE LA LISTA (Con SwipeToDismiss)
            // -----------------------------------------
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(top = 16.dp, bottom = 100.dp)
            ) {
                if (rutinas.isEmpty()) {
                    item {
                        Text(
                            text = "Aún no tienes rutinas. ¡Crea una!",
                            fontSize = 16.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(top = 20.dp)
                        )
                    }
                } else {
                    items(rutinas, key = { it.idRutina }) { rutina ->
                        val dismissState = rememberSwipeToDismissBoxState(
                            confirmValueChange = { state ->
                                if (state == SwipeToDismissBoxValue.EndToStart) {
                                    viewModel.eliminarRutina(rutina.idRutina)
                                    scope.launch {
                                        snackbarHostState.showSnackbar("Routine deleted")
                                    }
                                    true
                                } else false
                            }
                        )

                        SwipeToDismissBox(
                            state = dismissState,
                            enableDismissFromStartToEnd = false,
                            backgroundContent = {
                                val color = if (dismissState.targetValue == SwipeToDismissBoxValue.EndToStart)
                                    Color.Red.copy(alpha = 0.8f)
                                else Color.Transparent

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
            .fillMaxWidth() // Cambiado a fillMaxWidth para consistencia en Row
            .padding(vertical = 8.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        color = Color.White,
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier.padding(24.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = rutina.nombreRutina.uppercase(),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = GymAppTextPrimary,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Empieza a entrenar",
                    fontSize = 13.sp,
                    color = GymAppAccent,
                    fontWeight = FontWeight.Medium
                )
            }

            // Botón de Play minimalista
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(GymAppAccent, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Empezar",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}