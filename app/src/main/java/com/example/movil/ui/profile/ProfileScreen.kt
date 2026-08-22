package com.example.movil.ui.profile

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.movil.ui.auth.UiState

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    onEditProfile: () -> Unit,
    onChangePassword: () -> Unit,
    onLogout: () -> Unit
) {
    val state by viewModel.profileState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadProfile()
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Perfil de Usuario", style = MaterialTheme.typography.headlineLarge)
        Spacer(Modifier.height(24.dp))

        when (state) {
            is UiState.Loading -> CircularProgressIndicator()
            is UiState.Success -> {
                val user = (state as UiState.Success).data
                Text("Usuario: ${user.userName ?: "N/A"}", style = MaterialTheme.typography.bodyLarge)
                Spacer(Modifier.height(8.dp))
                Text("Email: ${user.email ?: "N/A"}", style = MaterialTheme.typography.bodyLarge)
                Spacer(Modifier.height(24.dp))

                Button(onClick = onEditProfile, modifier = Modifier.fillMaxWidth()) {
                    Text("Editar Perfil")
                }
                Spacer(Modifier.height(8.dp))
                Button(onClick = onChangePassword, modifier = Modifier.fillMaxWidth()) {
                    Text("Cambiar Contraseña")
                }
                Spacer(Modifier.height(16.dp))
                OutlinedButton(
                    onClick = { viewModel.logout(onLogout) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Cerrar Sesión")
                }
            }
            is UiState.Error -> {
                Text((state as UiState.Error).message, color = MaterialTheme.colorScheme.error)
                Spacer(Modifier.height(8.dp))
                Button(onClick = { viewModel.loadProfile() }) { Text("Reintentar") }
            }
            else -> {}
        }
    }
}
