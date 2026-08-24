package com.example.movil.ui.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.movil.ui.auth.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UsersScreen(viewModel: AdminViewModel, onCreateAdmin: () -> Unit, onBack: () -> Unit) {
    var search by remember { mutableStateOf("") }
    val state by viewModel.usersState.collectAsState()
    LaunchedEffect(Unit) { viewModel.getUsers() }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Gestión de usuarios") }, navigationIcon = {
                IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver") }
            })
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(onClick = onCreateAdmin, icon = { Icon(Icons.Default.PersonAdd, null) }, text = { Text("Nuevo admin") })
        }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp)) {
            OutlinedTextField(
                value = search,
                onValueChange = { search = it; viewModel.getUsers(it) },
                placeholder = { Text("Buscar por nombre o correo") },
                leadingIcon = { Icon(Icons.Default.Search, null) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(14.dp))
            when (val currentState = state) {
                is UiState.Loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
                is UiState.Empty -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("No se encontraron usuarios") }
                is UiState.Success -> LazyColumn(
                    contentPadding = PaddingValues(bottom = 96.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(currentState.data) { user ->
                        ElevatedCard(Modifier.fillMaxWidth()) {
                            Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                Column(Modifier.weight(1f)) {
                                    Text(user.userName ?: "Sin nombre", style = MaterialTheme.typography.titleMedium)
                                    Text(user.email ?: "", color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                IconButton(onClick = { user.id?.let(viewModel::deleteUser) }) {
                                    Icon(Icons.Default.Delete, "Eliminar usuario", tint = MaterialTheme.colorScheme.error)
                                }
                            }
                        }
                    }
                }
                is UiState.Error -> Text(currentState.message, color = MaterialTheme.colorScheme.error)
                else -> Unit
            }
        }
    }
}
