package com.example.themethod.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.themethod.data.EjercicioConDetalles
import com.example.themethod.ui.theme.*
import com.example.themethod.viewmodel.RutinaViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaDetalleRutina(
    rutinaId: Int,
    nombreRutina: String,
    viewModel: RutinaViewModel,
    onVolver: () -> Unit,
    onEmpezarEntrenamiento: () -> Unit
) {
    val ejercicios by viewModel.obtenerEjercicios(rutinaId).collectAsState(initial = emptyList())
    var mostrarBottomSheet by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            Column(horizontalAlignment = Alignment.End) {
                // Botón para iniciar el modo entrenamiento
                if (ejercicios.isNotEmpty()) {
                    ExtendedFloatingActionButton(
                        onClick = onEmpezarEntrenamiento,
                        containerColor = GymAppAccent,
                        contentColor = Color.White,
                        icon = { Icon(Icons.Default.Add, null) },
                        text = { Text("Entrenar") },
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }

                FloatingActionButton(
                    onClick = { mostrarBottomSheet = true },
                    containerColor = GymAppAccent,
                    contentColor = Color.White,
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Añadir Ejercicio")
                }
            }
        },
        containerColor = GymAppBackground
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp)
                .padding(top = 24.dp)
        ) {
            IconButton(onClick = onVolver, modifier = Modifier.offset(x = (-12).dp)) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = GymAppAccent)
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(text = nombreRutina, fontSize = 40.sp, fontWeight = FontWeight.Bold, color = GymAppTextPrimary)

            Spacer(modifier = Modifier.height(24.dp))

            Text(text = "Ejercicios", fontSize = 20.sp, fontWeight = FontWeight.SemiBold, color = GymAppTextSecondary)

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 100.dp)
            ) {
                if (ejercicios.isEmpty()) {
                    item {
                        Text("No hay ejercicios. ¡Añade uno!", color = GymAppTextSecondary, modifier = Modifier.padding(top = 20.dp))
                    }
                } else {
                    items(ejercicios, key = { it.idEjercicio }) { ejercicio ->
                        val dismissState = rememberSwipeToDismissBoxState(
                            confirmValueChange = { state ->
                                if (state == SwipeToDismissBoxValue.EndToStart) {
                                    viewModel.eliminarEjercicioDeRutina(rutinaId, ejercicio.idEjercicio)
                                    true
                                } else false
                            }
                        )

                        SwipeToDismissBox(
                            state = dismissState,
                            enableDismissFromStartToEnd = false,
                            backgroundContent = {
                                val color = if (dismissState.targetValue == SwipeToDismissBoxValue.EndToStart) Color.Red.copy(alpha = 0.8f) else Color.Transparent
                                Box(
                                    modifier = Modifier.fillMaxSize().padding(vertical = 8.dp).background(color, shape = RoundedCornerShape(16.dp)),
                                    contentAlignment = Alignment.CenterEnd
                                ) {
                                    Icon(Icons.Default.Delete, "Borrar", tint = Color.White, modifier = Modifier.padding(end = 16.dp))
                                }
                            },
                            content = {
                                EjercicioCard(
                                    nombreEjercicio = ejercicio.nombre,
                                    series = ejercicio.series,
                                    repeticiones = ejercicio.repeticiones,
                                    peso = "${ejercicio.pesoKgs} kg"
                                )
                            }
                        )
                    }
                }
            }
        }
    }

    if (mostrarBottomSheet) {
        val catalogoEjercicios by viewModel.todosLosEjercicios.collectAsState(initial = emptyList())
        var seriesInput by remember { mutableStateOf("") }
        var repeticionesInput by remember { mutableStateOf("") }
        var pesoInput by remember { mutableStateOf("") }
        var menuExpandido by remember { mutableStateOf(false) }
        var ejercicioSeleccionado by remember { mutableStateOf<com.example.themethod.data.Ejercicio?>(null) }

        // --- NUEVOS ESTADOS PARA FILTROS Y EJERCICIO PERSONALIZADO ---
        var grupoSeleccionado by remember { mutableStateOf("Todos") }
        var mostrarDialogoPersonalizado by remember { mutableStateOf(false) }

        // Lógica de filtrado dinámico
        val gruposMusculares = listOf("Todos") + catalogoEjercicios.map { it.grupoMuscular }.distinct().sorted()
        val ejerciciosFiltrados = if (grupoSeleccionado == "Todos") {
            catalogoEjercicios
        } else {
            catalogoEjercicios.filter { it.grupoMuscular == grupoSeleccionado }
        }

        ModalBottomSheet(onDismissRequest = { mostrarBottomSheet = false }, containerColor = Color.White) {
            Column(modifier = Modifier.fillMaxWidth().padding(24.dp).padding(bottom = 32.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Añadir Ejercicio", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))

                // 1. CHIPS DE FILTRO POR GRUPO MUSCULAR


                        LazyRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(gruposMusculares) { grupo ->
                                FilterChip(
                                    selected = grupoSeleccionado == grupo,
                                    onClick = {
                                        grupoSeleccionado = grupo
                                        ejercicioSeleccionado = null // Resetea el seleccionable al cambiar de filtro
                                    },
                                    label = { Text(grupo) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = GymAppAccent.copy(alpha = 0.2f),
                                        selectedLabelColor = GymAppAccent
                                    )
                                )
                            }
                        }

                Spacer(modifier = Modifier.height(12.dp))

                // 2. DESPLEGABLE FILTRADO
                ExposedDropdownMenuBox(expanded = menuExpandido, onExpandedChange = { menuExpandido = !menuExpandido }) {
                    OutlinedTextField(
                        value = ejercicioSeleccionado?.nombre ?: "Selecciona...",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Ejercicio") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = menuExpandido) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(expanded = menuExpandido, onDismissRequest = { menuExpandido = false }) {
                        if (ejerciciosFiltrados.isEmpty()) {
                            DropdownMenuItem(text = { Text("No hay ejercicios aquí") }, onClick = { })
                        } else {
                            ejerciciosFiltrados.forEach { ej ->
                                DropdownMenuItem(
                                    text = { Text(ej.nombre) },
                                    onClick = { ejercicioSeleccionado = ej; menuExpandido = false }
                                )
                            }
                        }
                    }
                }

                // 3. BOTÓN DE CREAR PERSONALIZADO
                TextButton(
                    onClick = { mostrarDialogoPersonalizado = true },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("+ Crear ejercicio personalizado", color = GymAppAccent)
                }

                Spacer(modifier = Modifier.height(4.dp))

                // 4. INPUTS DE SERIES, REPS Y PESO
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = seriesInput, onValueChange = { seriesInput = it }, label = { Text("Series") }, modifier = Modifier.weight(1f), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                    OutlinedTextField(value = repeticionesInput, onValueChange = { repeticionesInput = it }, label = { Text("Reps") }, modifier = Modifier.weight(1f), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                    OutlinedTextField(value = pesoInput, onValueChange = { pesoInput = it }, label = { Text("Kg") }, modifier = Modifier.weight(1f), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal))
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        if (ejercicioSeleccionado != null && seriesInput.isNotEmpty()) {
                            viewModel.insertarEjercicioRutina(rutinaId, ejercicioSeleccionado!!.idEjercicio, seriesInput.toIntOrNull() ?: 0, repeticionesInput.toIntOrNull() ?: 0, pesoInput.toDoubleOrNull() ?: 0.0)
                            mostrarBottomSheet = false
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GymAppAccent)
                ) { Text("Guardar en Rutina") }
            }
        }

        // --- DIÁLOGO PARA CREAR EJERCICIO NUEVO ---
        if (mostrarDialogoPersonalizado) {
            var nuevoNombre by remember { mutableStateOf("") }
            var nuevoGrupo by remember { mutableStateOf("") }

            AlertDialog(
                onDismissRequest = { mostrarDialogoPersonalizado = false },
                title = { Text("Nuevo Ejercicio") },
                text = {
                    Column {
                        OutlinedTextField(
                            value = nuevoNombre,
                            onValueChange = { nuevoNombre = it },
                            label = { Text("Nombre del ejercicio") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = nuevoGrupo,
                            onValueChange = { nuevoGrupo = it },
                            label = { Text("Grupo muscular (Ej. Pecho)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (nuevoNombre.isNotBlank()) {
                                val nuevoEj = com.example.themethod.data.Ejercicio(
                                    nombre = nuevoNombre,
                                    grupoMuscular = nuevoGrupo.ifBlank { "Personalizado" }
                                )
                                // Guardamos en la base de datos
                                viewModel.crearEjercicioPersonalizado(nuevoEj)
                                mostrarDialogoPersonalizado = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = GymAppAccent)
                    ) {
                        Text("Crear")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { mostrarDialogoPersonalizado = false }) {
                        Text("Cancelar", color = Color.Gray)
                    }
                }
            )
        }
    }
}

@Composable
fun EjercicioCard(nombreEjercicio: String, series: Int, repeticiones: Int, peso: String) {
    Surface(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), shape = RoundedCornerShape(16.dp), color = Color.White, shadowElevation = 1.dp) {
        Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column {
                Text(nombreEjercicio, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = GymAppTextPrimary)
                Text("$series Series x $repeticiones Reps", fontSize = 14.sp, color = GymAppTextSecondary)
            }
            Surface(shape = RoundedCornerShape(8.dp), color = GymAppBackground) {
                Text(peso, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = GymAppAccent, modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp))
            }
        }
    }
}

@Composable
fun TarjetaEjercicioActivo(ejercicio: EjercicioConDetalles, modifier: Modifier = Modifier) {
    val seriesCompletadas = remember { mutableStateListOf<Boolean>().apply { repeat(ejercicio.series) { add(false) } } }
    Card(modifier = modifier.fillMaxWidth().padding(vertical = 8.dp), elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(ejercicio.nombre, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text("${ejercicio.pesoKgs} kg", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.primary)
            }
            Text("Objetivo: ${ejercicio.series} x ${ejercicio.repeticiones}", style = MaterialTheme.typography.bodyMedium)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                for (i in 0 until ejercicio.series) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("${i + 1}", style = MaterialTheme.typography.labelSmall)
                        Checkbox(checked = seriesCompletadas[i], onCheckedChange = { seriesCompletadas[i] = it })
                    }
                }
            }
        }
    }
}


@Composable
fun TarjetaEjercicioEntrenamiento(ejercicio: EjercicioConDetalles, onSerieCompletada: (Boolean) -> Unit = {}) {

    val haptic = LocalHapticFeedback.current
    // Estado de los checkboxes (una lista de booleanos según el número de series)
    val seriesCompletadas = remember {
        mutableStateListOf<Boolean>().apply { repeat(ejercicio.series) { add(false) } }
    }

    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(ejercicio.nombre, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = GymAppTextPrimary)
                Text("${ejercicio.pesoKgs} kg", fontWeight = FontWeight.Bold, color = GymAppAccent)
            }

            Text("Objetivo: ${ejercicio.series} x ${ejercicio.repeticiones}", color = GymAppTextSecondary, fontSize = 14.sp)

            Spacer(modifier = Modifier.height(12.dp))

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start, // Alinea al principio
                maxItemsInEachRow = 5 // Máximo 5 casillas por fila para que no se vea apretado
            ) {
                for (i in 0 until ejercicio.series) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(4.dp) // Espacio entre casillas
                    ) {
                        Text(text = "${i + 1}", style = MaterialTheme.typography.labelSmall)
                        Checkbox(
                            checked = seriesCompletadas[i],
                            onCheckedChange = { nuevoEstado ->
                                seriesCompletadas[i] = nuevoEstado
                                onSerieCompletada(nuevoEstado)
                                // 2. SI EL USUARIO MARCA LA SERIE, VIBRAMOS
                                if (nuevoEstado) {
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                }
                            },
                            colors = CheckboxDefaults.colors(checkedColor = GymAppAccent)
                        )
                    }
                }
            }

        }
    }
}