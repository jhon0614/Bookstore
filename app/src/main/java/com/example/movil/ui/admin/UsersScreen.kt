package com.example.movil.ui.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.movil.ui.auth.UiState

@Composable
fun UsersScreen(
    viewModel: AdminViewModel,
    onCreateAdmin: () -> Unit
) {
    var search by remember { mutableStateOf("") }
    val state by viewModel.usersState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.getUsers()
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Gestión de Usuarios", style = MaterialTheme.typography.headlineMedium)
            Button(onClick = onCreateAdmin) { Text("+ Admin") }
        }
        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = search,
            onValueChange = {
                search = it
                viewModel.getUsers(it)
            },
            label = { Text("Buscar usuarios...") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(16.dp))

        when (state) {
            is UiState.Loading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            is UiState.Empty -> Text("No se encontraron usuarios.", modifier = Modifier.align(Alignment.CenterHorizontally))
            is UiState.Success -> {
                val users = (state as UiState.Success).data
                LazyColumn {
                    items(users) { u ->
                        Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                            Row(
                                modifier = Modifier.padding(16.dp).fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(u.userName ?: "Sin nombre", style = MaterialTheme.typography.bodyLarge)
                                    Text(u.email ?: "", style = MaterialTheme.typography.bodySmall)
                                }
                                TextButton(onClick = { u.id?.let { viewModel.deleteUser(it) } }) {
                                    Text("Eliminar", color = MaterialTheme.colorScheme.error)
                                }
                            }
                        }
                    }
                }
            }
            is UiState.Error -> Text((state as UiState.Error).message, color = MaterialTheme.colorScheme.error)
            else -> {}
        }
    }
}
