// LoginScreen.kt — pantalla de inicio de sesión con Jetpack Compose.
// Ahora guarda la sesión y navega al menú al iniciar sesión correctamente.

package com.example.movil.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.movil.data.model.LoginRequest
import com.example.movil.data.remote.RetrofitClient
import com.example.movil.data.session.Session
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(navController: NavController) {

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var result by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Iniciar sesión", fontSize = 26.sp)
        Spacer(Modifier.height(24.dp))

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
        Spacer(Modifier.height(24.dp))

        Button(
            onClick = {
                if (email.isBlank() || password.isBlank()) {
                    result = "Escribe email y contraseña"
                    return@Button
                }
                loading = true
                result = ""
                scope.launch {
                    try {
                        val resp = RetrofitClient.api.login(
                            LoginRequest(Email = email, PasswoRDkey = password)
                        )
                        if (resp.isSuccessful) {
                            val body = resp.body()
                            val user = body?.user

                            // --- Guardamos la sesión ---
                            val token = body?.token
                            if (token.isNullOrBlank() || user == null) {
                                result = "❌ El servidor devolvió una sesión incompleta"
                                return@launch
                            }
                            Session.saveToken(context, token)
                            Session.userName = user?.UserName
                            Session.isAdmin =
                                user?.roles?.any { it.TypeRole == "Administrador" } == true

                            // --- Navegamos al menú y borramos login del historial ---
                            navController.navigate("home") {
                                popUpTo("login") { inclusive = true }
                            }
                        } else {
                            result = "❌ Credenciales inválidas (código ${resp.code()})"
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
                Text("Entrando...")
            } else {
                Text("Entrar")
            }
        }

        if (result.isNotEmpty()) {
            Spacer(Modifier.height(24.dp))
            Text(result)
        }
        Spacer(Modifier.height(12.dp))

        // Botón de Registro
        Button(
            onClick = { navController.navigate("register") },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Green)
        ) {
            Text("Registrarse")
        }
    }
}
