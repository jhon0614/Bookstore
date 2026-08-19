// HomeScreen.kt — el MENÚ que se ve después de iniciar sesión.
// Incluye confirmación al cerrar sesión, acceso al perfil en modo lectura
// y limpieza de sesión en memoria + DataStore.

package com.example.movil.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.movil.data.session.Session
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(navController: NavController) {
    var showDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Saludo usando el nombre guardado en la sesión.
        Text("Hola, ${Session.userName ?: "usuario"}", fontSize = 24.sp)
        Text(if (Session.isAdmin) "Rol: Administrador" else "Rol: Usuario")
        Spacer(Modifier.height(32.dp))

        // Botón para editar perfil
        Button(
            onClick = { navController.navigate("editProfile") },
            modifier = Modifier.fillMaxWidth()
        ) { Text("Editar mis datos") }

        Spacer(Modifier.height(12.dp))

        // Botón para cambiar contraseña
        Button(
            onClick = { navController.navigate("changePassword") },
            modifier = Modifier.fillMaxWidth()
        ) { Text("Cambiar contraseña") }

        Spacer(Modifier.height(12.dp))

        // Solo para administradores (Session.isAdmin se calculó en el login).
        if (Session.isAdmin) {
            Spacer(Modifier.height(12.dp))
            Button(onClick = { navController.navigate("users") },
                modifier = Modifier.fillMaxWidth()) { Text("Administrar usuarios") }
        }

        Spacer(Modifier.height(12.dp))

        // Botón para ver perfil en modo lectura
        Button(
            onClick = { navController.navigate("profile") },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
        ) { Text("Ver perfil") }

        Spacer(Modifier.height(32.dp))

        // Botón de cerrar sesión con confirmación
        OutlinedButton(
            onClick = { showDialog = true },
            modifier = Modifier.fillMaxWidth()
        ) { Text("Cerrar sesión") }

        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                confirmButton = {
                    TextButton(onClick = {
                        scope.launch {
                            // ✅ Limpiamos sesión en memoria + DataStore
                            Session.clear(context)
                            navController.navigate("login") { popUpTo(0) }
                        }
                        showDialog = false
                    }) { Text("Sí, salir") }
                },
                dismissButton = {
                    TextButton(onClick = { showDialog = false }) { Text("Cancelar") }
                },
                title = { Text("Confirmación") },
                text = { Text("¿Seguro que deseas salir?") }
            )
        }
    }
}
