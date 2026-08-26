package com.example.movil.ui.profile

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.movil.ui.auth.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    onEditProfile: () -> Unit,
    onChangePassword: () -> Unit,
    onLogout: () -> Unit,
    onBack: () -> Unit
) {
    val state by viewModel.profileState.collectAsState()
    LaunchedEffect(Unit) { viewModel.loadProfile() }

    Scaffold(topBar = {
        TopAppBar(title = { Text("Mi perfil") }, navigationIcon = {
            IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver") }
        })
    }) { padding ->
        Box(Modifier
            .fillMaxSize()
            .padding(padding)
            .padding(20.dp)) {
            when (state) {
                is UiState.Loading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                is UiState.Success -> {
                    val user = (state as UiState.Success).data
                    Column(
                        Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Surface(
                            color = MaterialTheme.colorScheme.primaryContainer,
                            shape = MaterialTheme.shapes.large
                        ) {
                            Icon(Icons.Default.Person, null, Modifier
                                .padding(20.dp)
                                .size(48.dp))
                        }
                        Spacer(Modifier.height(14.dp))
                        Text(
                            user.userName ?: "Usuario",
                            style = MaterialTheme.typography.headlineSmall
                        )
                        Text(user.email ?: "", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(Modifier.height(28.dp))
                        ElevatedCard(Modifier.fillMaxWidth()) {
                            Column(
                                Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Button(
                                    onClick = onEditProfile,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Icon(
                                        Icons.Default.Edit,
                                        null
                                    ); Spacer(Modifier.width(8.dp)); Text("Editar datos")
                                }
                                FilledTonalButton(
                                    onClick = onChangePassword,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Icon(
                                        Icons.Default.Lock,
                                        null
                                    ); Spacer(Modifier.width(8.dp)); Text("Cambiar contraseña")
                                }
                                OutlinedButton(
                                    onClick = { viewModel.logout(onLogout) },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Icon(
                                        Icons.AutoMirrored.Filled.Logout,
                                        null
                                    ); Spacer(Modifier.width(8.dp)); Text("Cerrar sesión")
                                }
                            }
                        }
                    }
                }

                is UiState.Error -> Column(
                    Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text((state as UiState.Error).message, color = MaterialTheme.colorScheme.error)
                    Button(onClick = viewModel::loadProfile) { Text("Reintentar") }
                }

                else -> Unit
            }
        }
    }
}
