// RegisterScreen.kt — pantalla de registro con Jetpack Compose
package com.example.movil.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.movil.data.model.RegisterRequest
import com.example.movil.data.remote.RetrofitClient
import kotlinx.coroutines.launch
import org.json.JSONObject
import androidx.navigation.NavController
import kotlinx.coroutines.delay


@Composable
fun RegisterScreen(navController: NavController) {
    var email by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var result by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Crear cuenta", fontSize = 26.sp)
        Spacer(Modifier.height(24.dp))

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Usuario") },
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
        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(12.dp))

        Button(
            onClick = {
                if (username.isBlank() || email.isBlank() || password.isBlank()) {
                    result = "Completa todos los campos"
                    return@Button
                }
                if (!email.contains("@")) {
                    result = "El correo debe contener '@'"
                    return@Button
                }

                loading = true
                result = ""
                scope.launch {
                    try {
                        val response = RetrofitClient.api.register(
                            RegisterRequest(
                                email = email,
                                password = password,
                                username = username
                            )
                        )
                        if (response.isSuccessful) {
                            val body = response.body()
                            result = "✅ Registro exitoso: ${body?.message}"
                            delay(2000)
                            // 🔹 Regresar al LoginScreen
                            navController.navigate("login") {
                                popUpTo("register") { inclusive = true } // elimina la pantalla de registro del stack
                            }
                        } else {
                            val errorMsg = try {
                                val rawError = response.errorBody()?.string()
                                rawError?.let { JSONObject(it).optString("error") }
                            } catch (e: Exception) {
                                null
                            }
                            result = "❌ Error ${response.code()}: ${errorMsg ?: "Respuesta desconocida"}"
                        }
                    } catch (e: Exception) {
                        result = "⚠️ Error de conexión: ${e.message}"
                    } finally {
                        loading = false
                    }
                }
            },
            enabled = !loading,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (loading) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Registrando...")
                }
            } else {
                Text("Registrarse")
            }
        }

        if (result.isNotEmpty()) {
            Spacer(Modifier.height(24.dp))
            Text(result)
        }
    }
}
