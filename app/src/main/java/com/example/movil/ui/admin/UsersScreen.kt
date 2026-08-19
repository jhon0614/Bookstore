// UsersScreen.kt — pantalla SOLO para administradores.
// Lista todos los usuarios y permite crear un nuevo usuario con rol elegido.
// Crear usuario son 2 pasos: registrar (nace como Cliente) y luego asignarle el rol seleccionado.
package com.example.movil.ui.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.movil.data.model.RegisterRequest
import com.example.movil.data.model.UpdateRolesRequest
import com.example.movil.data.model.User
import com.example.movil.data.model.UsersStatsResponse
import com.example.movil.data.remote.RetrofitClient
import com.example.movil.data.session.Session
import kotlinx.coroutines.launch

@Composable
fun UsersScreen(navController: NavController) {
    var users by remember { mutableStateOf<List<User>>(emptyList()) }
    var stats by remember { mutableStateOf<UsersStatsResponse?>(null) }
    var result by remember { mutableStateOf("") }

    // Campos del formulario "crear usuario".
    var newName by remember { mutableStateOf("") }
    var newEmail by remember { mutableStateOf("") }
    var newPass by remember { mutableStateOf("") }

    // Campo de búsqueda
    var searchQuery by remember { mutableStateOf("") }

    // Roles disponibles
    val roles = listOf(
        1 to "Administrador",
        3 to "Vendedor",
        4 to "Cliente"
    )
    var expanded by remember { mutableStateOf(false) }
    var selectedRole by remember { mutableStateOf(roles[0]) }

    val scope = rememberCoroutineScope()

    suspend fun cargarUsuarios() {
        val resp = RetrofitClient.api.getUsers(Session.bearer())
        if (resp.isSuccessful) users = resp.body()?.users ?: emptyList()
    }

    suspend fun cargarStats() {
        try {
            val resp = RetrofitClient.api.getUsersStats(Session.bearer())
            if (resp.isSuccessful) stats = resp.body()
            else stats = UsersStatsResponse(total_users = 0, roles_distribution = emptyList(), top_buyers = emptyList())
        } catch (e: Exception) {
            stats = UsersStatsResponse(total_users = 0, roles_distribution = emptyList(), top_buyers = emptyList())
            result = "⚠️ Error al cargar estadísticas: ${e.message}"
        }
    }

    LaunchedEffect(Unit) {
        try {
            cargarUsuarios()
            cargarStats()
        } catch (_: Exception) { }
    }

    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        Text("Usuarios del sistema", fontSize = 22.sp)
        Spacer(Modifier.height(12.dp))

        // Estadísticas
        stats?.let {
            Card(modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("📊 Estadísticas", fontSize = 18.sp)
                    Spacer(Modifier.height(6.dp))
                    Text("Total usuarios: ${it.total_users}")
                    it.roles_distribution.forEach { rd ->
                        Text("${rd.role.TypeRole}: ${rd.user_count}")
                    }

                    if (it.top_buyers.isNotEmpty()) {
                        Spacer(Modifier.height(12.dp))
                        Text("🏆 Top compradores", fontSize = 18.sp)
                        it.top_buyers.forEach { buyer ->
                            Text("${buyer.UserName} (${buyer.Email}) — Ventas: ${buyer.sales_count}")
                        }
                    }
                }
            }
        }

        // Campo de búsqueda
        OutlinedTextField(
            value = searchQuery,
            onValueChange = {
                searchQuery = it
                scope.launch {
                    try {
                        if (searchQuery.isBlank()) {
                            cargarUsuarios()
                        } else {
                            val resp = RetrofitClient.api.searchUsers(Session.bearer(), searchQuery)
                            if (resp.isSuccessful) {
                                users = resp.body()?.users ?: emptyList()
                            } else {
                                result = "❌ Error en búsqueda (${resp.code()})"
                            }
                        }
                    } catch (e: Exception) {
                        result = "⚠️ ${e.message}"
                    }
                }
            },
            label = { Text("Buscar usuario") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        // 👇 Espacio extra para separar búsqueda de la lista
        Spacer(Modifier.height(16.dp))

        // Lista de usuarios ocupa todo el espacio disponible
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(bottom = 16.dp) // 👈 espacio extra antes de la sección de creación
        ) {
            items(users) { u ->
                val esAdmin = u.roles?.any { it.TypeRole == "Administrador" } == true
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("• ${u.UserName ?: "(sin nombre)"} (${u.Email ?: "sin email"})" + if (esAdmin) " — Admin" else "")
                    IconButton(onClick = {
                        scope.launch {
                            try {
                                val resp = RetrofitClient.api.deleteUser(Session.bearer(), u.iD_User)
                                if (resp.isSuccessful) {
                                    result = "✅ Usuario eliminado"
                                    cargarUsuarios()
                                    cargarStats()
                                } else {
                                    result = if (resp.code() == 400)
                                        "❌ No puedes auto-eliminarte"
                                    else
                                        "❌ Error al eliminar (${resp.code()})"
                                }
                            } catch (e: Exception) {
                                result = "⚠️ ${e.message}"
                            }
                        }
                    }) {
                        Icon(Icons.Default.Delete, contentDescription = "Eliminar")
                    }
                }
            }
        }

        // 👇 Separador claro entre lista y creación
        HorizontalDivider(Modifier.padding(vertical = 12.dp))

        Text("Crear nuevo usuario", fontSize = 18.sp)
        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = newName,
            onValueChange = { newName = it },
            label = { Text("Nombre") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = newEmail,
            onValueChange = { newEmail = it },
            label = { Text("Email") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = newPass,
            onValueChange = { newPass = it },
            label = { Text("Contraseña") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))

        // Selector de rol
        Box {
            OutlinedButton(onClick = { expanded = true }, modifier = Modifier.fillMaxWidth()) {
                Text("Rol: ${selectedRole.second}")
            }
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                roles.forEach { role ->
                    DropdownMenuItem(
                        text = { Text(role.second) },
                        onClick = {
                            selectedRole = role
                            expanded = false
                        }
                    )
                }
            }
        }
        Spacer(Modifier.height(12.dp))

        Button(
            onClick = {
                result = ""
                scope.launch {
                    try {
                        val reg = RetrofitClient.api.register(
                            RegisterRequest(newName, newEmail, newPass)
                        )
                        if (!reg.isSuccessful) {
                            result = if (reg.code() == 409) "❌ Ese email ya existe"
                            else "❌ Error al crear (código ${reg.code()})"
                            return@launch
                        }

                        val nuevoId = reg.body()?.user?.iD_User
                        if (nuevoId == null) {
                            result = "❌ No se obtuvo el ID"
                            return@launch
                        }

                        val rolesResp = RetrofitClient.api.updateRoles(
                            Session.bearer(),
                            nuevoId,
                            UpdateRolesRequest(role_ids = listOf(selectedRole.first))
                        )
                        result = if (rolesResp.isSuccessful) "✅ Usuario creado con rol ${selectedRole.second}"
                        else "❌ Usuario creado, pero falló el rol (${rolesResp.code()})"

                        newName = ""; newEmail = ""; newPass = ""
                        cargarUsuarios()
                        cargarStats()

                    } catch (e: Exception) {
                        result = "⚠️ ${e.message}"
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) { Text("Crear usuario") }

        if (result.isNotEmpty()) {
            Spacer(Modifier.height(12.dp))
            Text(result)
        }

        TextButton(onClick = { navController.popBackStack() }) { Text("Volver") }
    }
}
