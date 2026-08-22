package com.example.movil.ui.profile

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.movil.ui.auth.UiState

@Composable
fun EditProfileScreen(
    viewModel: ProfileViewModel,
    onBack: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    val profileState by viewModel.profileState.collectAsState()
    val actionState by viewModel.actionState.collectAsState()

    LaunchedEffect(profileState) {
        val profile = (profileState as? UiState.Success)?.data
        if (profile != null) {
            name = profile.userName
            email = profile.email
        }
    }

    LaunchedEffect(Unit) {
        if (profileState !is UiState.Success) viewModel.loadProfile()
    }

    LaunchedEffect(actionState) {
        if (actionState is UiState.Success) {
            viewModel.resetActionState()
            onBack()
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Editar Perfil", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Nuevo Nombre") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Nuevo Email") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(16.dp))

        if (actionState is UiState.Loading) {
            CircularProgressIndicator()
        } else {
            Button(
                onClick = { if (name.isNotBlank() && email.isNotBlank()) viewModel.updateProfile(name, email) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Guardar Cambios")
            }
        }

        if (actionState is UiState.Error) {
            Text((actionState as UiState.Error).message, color = MaterialTheme.colorScheme.error)
        }
    }
}
