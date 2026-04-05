package com.example.themethod.ui // Comprueba que este es tu paquete correcto

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.themethod.data.Ejercicio
import com.example.themethod.ui.theme.*
import com.example.themethod.viewmodel.RutinaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaDetalleRutina(
    rutinaId: Int,
    nombreRutina: String,
    viewModel: RutinaViewModel,
    onVolver: () -> Unit
) {
    //   Escuchamos los ejercicios de esta rutina en directo
    val ejercicios by viewModel.obtenerEjercicios(rutinaId).collectAsState(initial = emptyList())

    // Controla si el menu está abierto o no
    var mostrarBottomSheet by remember{ mutableStateOf(false)}

    // Scaffold es el que nos permite poner el botón flotante (FloatingActionButton)
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                   mostrarBottomSheet = true // Aqui se pone en marcha el boton

                },
                containerColor = GymAppAccent,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Añadir Ejercicio")
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
            // Botón de volver
            IconButton(onClick = { onVolver() }, modifier = Modifier.offset(x = (-12).dp)) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = GymAppAccent)
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Título de la rutina
            Text(
                text = nombreRutina,
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold,
                color = GymAppTextPrimary
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Exercises",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = GymAppTextSecondary,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // LISTA DE EJERCICIOS
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 80.dp) // Espacio para que el botón no tape el último ejercicio
            ) {
                if (ejercicios.isEmpty()) {
                    item {
                        Text(
                            text = "No exercises yet. Add some!",
                            color = GymAppTextSecondary,
                            modifier = Modifier.padding(top = 20.dp)
                        )
                    }
                } else {
                    items(ejercicios ,  {it.nombre}) { ejercicio ->
                        // Crfeamos el estado del desplazamiento
                    val dismissState = rememberSwipeToDismissBoxState  (
                        confirmValueChange = { state ->
                            if (state == SwipeToDismissBoxValue.EndToStart){
                                //Borramos los datos en la base de datos
                                viewModel.eliminarEjercicioDeRutina(rutinaId, ejercicio.idEjercicio)
                                true
                            }else{
                                false
                            }
                        }
                    )
                        // 2. Contenedor Visual

                        SwipeToDismissBox(
                            state = dismissState,
                            enableDismissFromStartToEnd = false,
                            backgroundContent = {
                                val color = if (dismissState.targetValue == SwipeToDismissBoxValue.EndToStart){
                                    Color.Red.copy(alpha = 0.8f)
                                }else{
                                    Color.Transparent
                                }

                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(vertical = 8.dp)
                                        .background(color, shape = RoundedCornerShape(16.dp)),
                                    contentAlignment = Alignment.CenterEnd
                                ){
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Borrar",
                                        tint = Color.White,
                                        modifier = Modifier.padding(end = 16.dp)
                                    )
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
    // --------------------------------------------------------
    // MENU DE OPCIONES DESLIZABLE (BottomSheet)
    // --------------------------------------------------------


    if (mostrarBottomSheet) {
        // 1. Escuchamos el catálogo de ejercicios que viene de la base de datos
        val catalogoEjercicios by viewModel.todosLosEjercicios.collectAsState(initial = emptyList())

        var seriesInput by remember { mutableStateOf("") }
        var repeticionesInput by remember { mutableStateOf("") }
        var pesoInput by remember { mutableStateOf("") }

        // Variables para el menú desplegable
        var menuExpandido by remember { mutableStateOf(false) }
        var ejercicioSeleccionado by remember { mutableStateOf<com.example.themethod.data.Ejercicio?>(null) }

        ModalBottomSheet(
            onDismissRequest = { mostrarBottomSheet = false },
            containerColor = Color.White
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                    .padding(bottom = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Añadir Ejercicio", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))

                // 2. EL MENÚ DESPLEGABLE (Dropdown)
                @OptIn(ExperimentalMaterial3Api::class)
                ExposedDropdownMenuBox(
                    expanded = menuExpandido,
                    onExpandedChange = { menuExpandido = !menuExpandido }
                ) {
                    OutlinedTextField(
                        value = ejercicioSeleccionado?.nombre ?: "Selecciona un ejercicio...",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Ejercicio") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = menuExpandido) },
                        colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )

                    ExposedDropdownMenu(
                        expanded = menuExpandido,
                        onDismissRequest = { menuExpandido = false }
                    ) {
                        if (catalogoEjercicios.isEmpty()) {
                            DropdownMenuItem(
                                text = { Text("No hay ejercicios en el catálogo") },
                                onClick = { menuExpandido = false }
                            )
                        } else {
                            catalogoEjercicios.forEach { ejercicio ->
                                DropdownMenuItem(
                                    text = { Text(ejercicio.nombre) },
                                    onClick = {
                                        ejercicioSeleccionado = ejercicio
                                        menuExpandido = false
                                    }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Campos de texto (Series, Repeticiones, Peso)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = seriesInput,
                        onValueChange = { seriesInput = it },
                        label = { Text("Series") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = repeticionesInput,
                        onValueChange = { repeticionesInput = it },
                        label = { Text("Reps") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = pesoInput,
                        onValueChange = { pesoInput = it },
                        label = { Text("Kg") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 3. EL BOTÓN DE GUARDAR
                Button(
                    onClick = {
                        // Verificamos que todo esté lleno antes de guardar
                        if (ejercicioSeleccionado != null && seriesInput.isNotEmpty() && repeticionesInput.isNotEmpty() && pesoInput.isNotEmpty()) {
                            viewModel.insertarEjercicioRutina(
                                rutinaId = rutinaId,
                                ejercicioId = ejercicioSeleccionado!!.idEjercicio, // Asegúrate de que tu ID se llame idEjercicio en la clase Ejercicio
                                series = seriesInput.toIntOrNull() ?: 0,
                                repeticiones = repeticionesInput.toIntOrNull() ?: 0,
                                pesoKgs = pesoInput.toDoubleOrNull() ?: 0.0
                            )
                            mostrarBottomSheet = false // Cerramos el menú
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GymAppAccent)
                ) {
                    Text("Guardar", fontSize = 16.sp)
                }
            }
        }
    }
}








// --------------------------------------------------------
// COMPONENTE: TARJETA DE EJERCICIO
// --------------------------------------------------------
@Composable
fun EjercicioCard(
    nombreEjercicio: String,
    series: Int,
    repeticiones: Int,
    peso: String
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = nombreEjercicio,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = GymAppTextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "$series Series x $repeticiones Reps",
                    fontSize = 14.sp,
                    color = GymAppTextSecondary
                )
            }

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = GymAppBackground,
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Text(
                    text = peso,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = GymAppAccent,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }
    }

}