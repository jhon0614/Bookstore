// ChangePasswordScreen.kt — cambia la contraseña del usuario autenticado.
package com.example.movil.ui.profile

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.movil.data.model.ChangePasswordRequest
import com.example.movil.data.remote.RetrofitClient
import com.example.movil.data.session.Session
import kotlinx.coroutines.launch

@Composable
fun ChangePasswordScreen(navController: NavController) {
    var actual by remember { mutableStateOf("") } // contraseña actual
    var nueva by remember { mutableStateOf("") }  // contraseña nueva
    var result by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        Text("Cambiar contraseña", fontSize = 24.sp)
        Spacer(Modifier.height(24.dp))

        OutlinedTextField(
            value = actual,
            onValueChange = { actual = it },
            label = { Text("Contraseña actual") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = nueva,
            onValueChange = { nueva = it },
            label = { Text("Nueva contraseña") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(24.dp))

        Button(
            onClick = {
                if (actual.isBlank() || nueva.isBlank()) {
                    result = "Completa los dos campos"
                    return@Button
                }
                loading = true
                result = ""
                scope.launch {
                    try {
                        val resp = RetrofitClient.api.changePassword(
                            Session.bearer(), ChangePasswordRequest(actual, nueva)
                        )
                        result = when {
                            resp.isSuccessful -> "✅ Contraseña cambiada"
                            resp.code() == 400 -> "❌ La contraseña actual es incorrecta"
                            else -> "❌ Error (código ${resp.code()})"
                        }
                    } catch (e: Exception) {
                        result = "⚠️ ${e.message}"
                    } finally {
                        loading = false
                    }
                }
            },
            enabled = !loading,
            modifier = Modifier.fillMaxWidth()
        ) { Text("Actualizar") }

        if (result.isNotEmpty()) {
            Spacer(Modifier.height(16.dp))
            Text(result)
        }

        TextButton(onClick = { navController.popBackStack() }) { Text("Volver") }
    }
}
