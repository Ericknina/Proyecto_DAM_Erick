package com.example.themethod.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.themethod.ui.theme.GymAppAccent
import com.example.themethod.ui.theme.GymAppBackground
import com.example.themethod.ui.theme.GymAppTextPrimary
import com.example.themethod.ui.theme.GymAppTextSecondary


@Composable
fun PantallaLogin (
    onLoginClick: (String, String) -> Unit, // Accion al pulsar Log in
    onRegisterClick: (String, String) -> Unit // Accion al pulsar Register

){
    // Vriables para guardar los datos que escribe el usuario
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column (
        modifier = Modifier
            .fillMaxSize()
            .background(GymAppBackground)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ){
        // TITULO PRINCIPAL

       Text(
           text = "The Method",
           fontSize = 40.sp,
           fontWeight = FontWeight.Bold,
           color = GymAppTextPrimary
       )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Welcome back, Athlete!",
            fontSize = 20.sp,
            color = GymAppTextSecondary

        )

        Spacer(modifier = Modifier.height(48.dp))

        // Compo Email
        OutlinedTextField(
            value = email,
            onValueChange = {email = it},
            label = { Text("Email")},
            leadingIcon = { Icon(Icons.Default.Email, contentDescription = "Email")},
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            singleLine = true,

        )

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Password") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            singleLine = true,
            // Oculta el texto con asteriscos/puntos
            visualTransformation = PasswordVisualTransformation()
        )

        Spacer(modifier = Modifier.height(32.dp))

        // 4. BOTÓN DE INICIO DE SESIÓN
        Button(
            onClick = { onLoginClick(email, password) },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = GymAppAccent),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Log In", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(24.dp))


        // 5. TEXTO PARA REGISTRARSE
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Don't have an account? ", color = GymAppTextSecondary)
            Text(
                text = "Sign Up",
                color = GymAppAccent,
                fontWeight = FontWeight.Bold,
                // Hace que el texto sea "pulsable"
                modifier = Modifier.clickable { onRegisterClick(email, password) }
            )
        }
    }


}


