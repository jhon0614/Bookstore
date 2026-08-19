// EditProfileScreen.kt — permite ver y editar el nombre y correo del usuario.
// Al abrir, CARGA los datos actuales con GET; al guardar, los actualiza con PUT.
package com.example.movil.ui.profile

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.movil.data.model.UpdateProfileRequest
import com.example.movil.data.remote.RetrofitClient
import com.example.movil.data.session.Session
import kotlinx.coroutines.launch

@Composable
fun EditProfileScreen(navController: NavController) {
    var userName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var result by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    // Se ejecuta una vez al abrir la pantalla para precargar datos
    LaunchedEffect(Unit) {
        try {
            val resp = RetrofitClient.api.getProfile(Session.bearer()) // envía el token
            if (resp.isSuccessful) {
                userName = resp.body()?.user?.UserName ?: ""
                email = resp.body()?.user?.Email ?: ""
            }
        } catch (_: Exception) { /* si falla, dejamos los campos vacíos */ }
    }

    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        Text("Editar mis datos", fontSize = 24.sp)
        Spacer(Modifier.height(24.dp))

        OutlinedTextField(
            value = userName,
            onValueChange = { userName = it },
            label = { Text("Nombre") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(24.dp))

        // Botón con indicador de carga
        Button(
            onClick = {
                loading = true; result = ""
                scope.launch {
                    try {
                        val resp = RetrofitClient.api.updateProfile(
                            Session.bearer(), UpdateProfileRequest(userName, email)
                        )
                        result = when {
                            resp.isSuccessful -> {
                                // Actualizamos también el nombre guardado en la sesión.
                                Session.userName = resp.body()?.user?.UserName
                                "✅ ${resp.body()?.message}"
                            }
                            resp.code() == 409 -> "❌ Ese email ya está en uso"
                            else -> "❌ Error (código ${resp.code()})"
                        }
                    } catch (e: Exception) {
                        result = "⚠️ ${e.message}"
                    } finally { loading = false }
                }
            },
            enabled = !loading,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (loading) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp,
                    modifier = Modifier.size(20.dp)
                )
            } else {
                Text("Guardar")
            }
        }

        if (result.isNotEmpty()) {
            Spacer(Modifier.height(16.dp))
            Text(result)
        }

        // popBackStack() regresa a la pantalla anterior (el menú).
        TextButton(onClick = { navController.popBackStack() }) { Text("Volver") }
    }
}
