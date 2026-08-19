// Models.kt — versión COMBINADA y AMPLIADA
// Contiene las clases de datos que representan lo que ENVIAMOS y RECIBIMOS del backend.

package com.example.movil.data.model
import com.google.gson.annotations.SerializedName

// ---------- Login ----------
data class LoginRequest(
    val Email: String,        // correo que escribe el usuario
    val PasswoRDkey: String   // contraseña que escribe el usuario
)

// ---------- Registro ----------
data class RegisterRequest(
    @SerializedName("UserName") val username: String,
    @SerializedName("Email") val email: String,
    @SerializedName("PasswoRDkey") val password: String
)

data class RegisterResponse(
    val message: String?,     // mensaje del servidor, ej. "Registro exitoso"
    val token: String?,       // token de sesión
    val user: User?           // datos del usuario registrado
)

// ---------- Roles ----------
data class Role(
    val iDRole: Int,          // 1 = Administrador, 2 = Usuario, 3 = Vendedor, 4 = Cliente
    val TypeRole: String      // nombre del rol, ej. "Administrador"
)

// ---------- Usuario ----------
data class User(
    val iD_User: Int,         // identificador único
    val UserName: String,     // nombre de usuario
    val Email: String,        // correo del usuario
    val roles: List<Role>? = null // lista de roles (puede venir nula)
)

// ---------- Respuestas de login ----------
data class LoginResponse(
    val message: String?,     // mensaje del servidor
    val token: String?,       // token de sesión
    val user: User?           // datos del usuario
)

// ---------- Perfil ----------
data class ProfileResponse(
    val message: String?,
    val user: User?
)

data class UpdateProfileRequest(
    val UserName: String,
    val Email: String
)

// ---------- Cambio de contraseña ----------
data class ChangePasswordRequest(
    val current_password: String,
    val new_password: String
)

// ---------- Respuesta genérica ----------
data class MessageResponse(
    val message: String?,
    val error: String?
)

// ---------- Listar usuarios (solo admin) ----------
// El backend responde { "users": [ ... ], "pagination": {...} }. Solo nos interesa users.
data class UsersListResponse(
    val users: List<User>?    // lista de usuarios, puede venir nula
)

// Para asignar roles: enviamos la lista de ids de rol. Administrador = 1.
data class UpdateRolesRequest(
    val role_ids: List<Int>
)

data class UsersStatsResponse(
    val total_users: Int = 0,
    val roles_distribution: List<RoleDistribution> = emptyList(),
    val top_buyers: List<UserStats> = emptyList()
)

data class RoleDistribution(
    val role: Role,
    val user_count: Int
)

data class UserStats(
    val iD_User: Int,
    val UserName: String,
    val Email: String,
    val sales_count: Int
)
