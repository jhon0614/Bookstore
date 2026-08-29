package com.example.movil.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp

@Composable
fun LoginScreen(
    viewModel: AuthViewModel,
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val state by viewModel.authState.collectAsState()

    LaunchedEffect(state) {
        if (state is UiState.Success) {
            viewModel.resetState()
            onLoginSuccess()
        }
    }

    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = MaterialTheme.shapes.large
            ) {
                Icon(
                    Icons.Default.AutoStories,
                    contentDescription = null,
                    modifier = Modifier
                        .padding(18.dp)
                        .size(42.dp)
                )
            }
            Spacer(Modifier.height(16.dp))
            Text(
                text = "Bienvenido a BookStore+",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.headlineMedium
            )
            Text(
                "Inicia sesión para gestionar tu cuenta",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(24.dp))

            ElevatedCard(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Correo electrónico") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Contraseña") },
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    (state as? UiState.Error)?.let {
                        Text(it.message, color = MaterialTheme.colorScheme.error)
                    }
                    Button(
                        onClick = { viewModel.login(email.trim(), password) },
                        enabled = email.isNotBlank() && password.isNotBlank() && state !is UiState.Loading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 50.dp)
                    ) {
                        if (state is UiState.Loading) CircularProgressIndicator(
                            Modifier.size(22.dp),
                            strokeWidth = 2.dp
                        )
                        else {
                            Icon(Icons.AutoMirrored.Filled.Login, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text("Ingresar")
                        }
                    }
                }
            }
            TextButton(onClick = onNavigateToRegister) { Text("¿No tienes cuenta? Regístrate") }
        }
    }
}
