package com.example.themethod.ui

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.themethod.ui.theme.GymAppAccent
import com.example.themethod.ui.theme.GymAppBackground
import com.example.themethod.ui.theme.GymAppTextPrimary
import com.example.themethod.ui.theme.GymAppTextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaPerfil(
    emailUsuario: String, // Ahora solo recibimos el correo
    onVolver: () -> Unit,
    onCambiarContrasena: (String) -> Unit, // Ahora recibe la nueva contraseña
    onCerrarSesion: () -> Unit
) {
    // 1. EXTRAEMOS EL NOMBRE DE USUARIO DEL CORREO
    // Si es "juan@gmail.com", se queda con "juan"
    val nombreUsuario = emailUsuario.substringBefore("@").replaceFirstChar { it.uppercase() }

    var mostrarDialogoPassword by remember { mutableStateOf(false) }
    var nuevaPassword by remember { mutableStateOf("") }
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mi Perfil", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = GymAppBackground,
                    titleContentColor = GymAppTextPrimary,
                    navigationIconContentColor = GymAppTextPrimary
                )
            )
        },
        containerColor = GymAppBackground
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(GymAppAccent.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = nombreUsuario.take(1).uppercase(), // La inicial en grande
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold,
                    color = GymAppAccent
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(text = "@$nombreUsuario", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = GymAppTextPrimary)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = emailUsuario, fontSize = 16.sp, color = GymAppTextSecondary)

            Spacer(modifier = Modifier.height(48.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    TextButton(
                        onClick = { mostrarDialogoPassword = true },
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(vertical = 16.dp)
                    ) {
                        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = GymAppTextSecondary)
                            Spacer(modifier = Modifier.width(16.dp))
                            Text("Cambiar Contraseña", color = GymAppTextPrimary, fontSize = 16.sp, modifier = Modifier.weight(1f))
                        }
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Color.LightGray.copy(alpha = 0.5f))

                    TextButton(
                        onClick = onCerrarSesion,
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(vertical = 16.dp)
                    ) {
                        Text("Cerrar Sesión", color = Color.Red, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // --- 2. DIÁLOGO PARA CAMBIAR CONTRASEÑA ---
        if (mostrarDialogoPassword) {
            AlertDialog(
                onDismissRequest = { mostrarDialogoPassword = false },
                title = { Text("Actualizar Contraseña", fontWeight = FontWeight.Bold) },
                text = {
                    Column {
                        Text("Introduce tu nueva contraseña. Debe tener al menos 6 caracteres.", fontSize = 14.sp, color = GymAppTextSecondary)
                        Spacer(modifier = Modifier.height(16.dp))
                        OutlinedTextField(
                            value = nuevaPassword,
                            onValueChange = { nuevaPassword = it },
                            label = { Text("Nueva contraseña") },
                            singleLine = true,
                            visualTransformation = PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (nuevaPassword.length >= 6) {
                                onCambiarContrasena(nuevaPassword)
                                mostrarDialogoPassword = false
                                nuevaPassword = ""
                            } else {
                                Toast.makeText(context, "La contraseña es muy corta", Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = GymAppAccent)
                    ) {
                        Text("Guardar")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { mostrarDialogoPassword = false }) {
                        Text("Cancelar", color = Color.Gray)
                    }
                }
            )
        }
    }
}