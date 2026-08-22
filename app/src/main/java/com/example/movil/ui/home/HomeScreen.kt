package com.example.movil.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun HomeScreen(
    isAdmin: Boolean,
    onNavigateToProfile: () -> Unit,
    onNavigateToUsers: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Bookstore App", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(16.dp))

        Button(onClick = onNavigateToProfile, modifier = Modifier.fillMaxWidth()) {
            Text("Ver mi perfil")
        }

        if (isAdmin) {
            Spacer(Modifier.height(8.dp))
            Button(onClick = onNavigateToUsers, modifier = Modifier.fillMaxWidth()) {
                Text("Panel Administrador: Usuarios")
            }
        }
    }
}
