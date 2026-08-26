package com.example.movil.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp

@Composable
fun RegisterScreen(
    viewModel: AuthViewModel,
    onRegisterSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val state by viewModel.authState.collectAsState()

    LaunchedEffect(state) {
        if (state is UiState.Success) {
            viewModel.resetState()
            onRegisterSuccess()
        }
    }

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Surface(
            color = MaterialTheme.colorScheme.secondaryContainer,
            shape = MaterialTheme.shapes.large
        ) {
            Icon(
                Icons.Default.PersonAdd,
                contentDescription = null,
                modifier = Modifier
                    .padding(18.dp)
                    .size(42.dp)
            )
        }
        Spacer(Modifier.height(16.dp))
        Text("Crea tu cuenta", style = MaterialTheme.typography.headlineMedium)
        Text(
            "Guarda tus datos y continúa leyendo",
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(24.dp))

        ElevatedCard(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                OutlinedTextField(
                    name,
                    { name = it },
                    label = { Text("Nombre") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    email, { email = it }, label = { Text("Correo electrónico") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    singleLine = true, modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    password, { password = it }, label = { Text("Contraseña") },
                    supportingText = { Text("Mínimo 6 caracteres") },
                    visualTransformation = PasswordVisualTransformation(), singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                (state as? UiState.Error)?.let {
                    Text(
                        it.message,
                        color = MaterialTheme.colorScheme.error
                    )
                }
                Button(
                    onClick = { viewModel.register(name.trim(), email.trim(), password) },
                    enabled = name.isNotBlank() && email.isNotBlank() && password.length >= 6 && state !is UiState.Loading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 50.dp)
                ) {
                    if (state is UiState.Loading) CircularProgressIndicator(
                        Modifier.size(22.dp),
                        strokeWidth = 2.dp
                    )
                    else Text("Crear cuenta")
                }
            }
        }
        TextButton(onClick = onNavigateToLogin) { Text("¿Ya tienes cuenta? Inicia sesión") }
    }
}
