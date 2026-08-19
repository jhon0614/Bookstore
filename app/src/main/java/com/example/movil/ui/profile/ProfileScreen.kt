// ProfileScreen.kt — muestra los datos del usuario en modo lectura.
package com.example.movil.ui.profile

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.movil.data.model.User
import com.example.movil.data.remote.RetrofitClient
import com.example.movil.data.session.Session
import kotlinx.coroutines.launch

@Composable
fun ProfileScreen(navController: NavController) {
    var user by remember { mutableStateOf<User?>(null) }
    var loading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        try {
            val resp = RetrofitClient.api.getProfile(Session.bearer())
            if (resp.isSuccessful) user = resp.body()?.user
        } finally { loading = false }
    }

    Column(Modifier.fillMaxSize().padding(24.dp)) {
        Text("Mi perfil", fontSize = 24.sp)
        Spacer(Modifier.height(16.dp))

        if (loading) {
            CircularProgressIndicator()
        } else {
            Text("Nombre: ${user?.UserName ?: "-"}")
            Text("Email: ${user?.Email ?: "-"}")
            Text("Roles: ${user?.roles?.joinToString { it.TypeRole }}")
        }

        Spacer(Modifier.height(24.dp))
        TextButton(onClick = { navController.popBackStack() }) { Text("Volver") }
    }
}
