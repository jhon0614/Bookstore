package com.example.movil.ui.profile

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.movil.ui.auth.UiState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack

@Composable
fun ChangePasswordScreen(
    viewModel: ProfileViewModel,
    onBack: () -> Unit
) {
    var currentPass by remember { mutableStateOf("") }
    var newPass by remember { mutableStateOf("") }
    val actionState by viewModel.actionState.collectAsState()

    LaunchedEffect(actionState) {
        if (actionState is UiState.Success) {
            viewModel.resetActionState()
            onBack()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver") }
            Text("Cambiar contraseña", style = MaterialTheme.typography.headlineMedium)
        }
        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = currentPass,
            onValueChange = { currentPass = it },
            label = { Text("Contraseña Actual") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = newPass,
            onValueChange = { newPass = it },
            label = { Text("Nueva Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            supportingText = { Text("Mínimo 6 caracteres") }
        )
        Spacer(Modifier.height(16.dp))

        if (actionState is UiState.Loading) {
            CircularProgressIndicator()
        } else {
            Button(
                onClick = { viewModel.changePassword(currentPass, newPass) },
                enabled = currentPass.isNotBlank() && newPass.length >= 6,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Actualizar Contraseña")
            }
        }

        if (actionState is UiState.Error) {
            Text((actionState as UiState.Error).message, color = MaterialTheme.colorScheme.error)
        }
    }
}
