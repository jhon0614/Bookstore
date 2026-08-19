// ApiService.kt — lista completa de servicios que ofrece el backend.
// Retrofit genera automáticamente el código HTTP a partir de estas anotaciones.

package com.example.movil.data.remote

import com.example.movil.data.model.*

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET      // petición HTTP GET (leer datos)
import retrofit2.http.Header   // inserta un encabezado en la petición (ej. token)
import retrofit2.http.POST     // petición HTTP POST (crear datos)
import retrofit2.http.PUT      // petición HTTP PUT (actualizar datos)
import retrofit2.http.Path // reemplaza un segmento variable de la ruta, como {id}
import retrofit2.http.Query

interface ApiService {

    // ---------- Autenticación ----------
    @POST("api/auth/login")
    suspend fun login(@Body body: LoginRequest): Response<LoginResponse>

    @POST("api/auth/register")
    suspend fun register(@Body body: RegisterRequest): Response<RegisterResponse>

    // ---------- Perfil ----------
    // Obtener el perfil del usuario actual. Requiere token en el encabezado.
    @GET("api/users/profile")
    suspend fun getProfile(
        @Header("Authorization") auth: String
    ): Response<ProfileResponse>

    // Actualizar perfil (nombre y correo). Requiere token + datos.
    @PUT("api/users/profile")
    suspend fun updateProfile(
        @Header("Authorization") auth: String,
        @Body body: UpdateProfileRequest
    ): Response<ProfileResponse>

    // ---------- Cambio de contraseña ----------
    @PUT("api/auth/change-password")
    suspend fun changePassword(
        @Header("Authorization") auth: String,
        @Body body: ChangePasswordRequest
    ): Response<MessageResponse>

    // Listar todos los usuarios. Solo funciona con token de administrador (si no, 403).
    @GET("api/users/")
    suspend fun getUsers(
        @Header("Authorization") auth: String
    ): Response<UsersListResponse>

    // Cambiar los roles de un usuario. {id} se reemplaza con @Path. Requiere admin.
    @PUT("api/users/{id}/roles")
    suspend fun updateRoles(
        @Header("Authorization") auth: String,
        @Path("id") id: Int, // reemplaza el {id} de la ruta
        @Body body: UpdateRolesRequest
    ): Response<MessageResponse>

    @GET("api/users/search")
    suspend fun searchUsers(
        @Header("Authorization") auth: String,
        @Query("q") query: String
    ): Response<UsersListResponse>

    @DELETE("api/users/{id}")
    suspend fun deleteUser(
        @Header("Authorization") auth: String,
        @Path("id") id: Int
    ): Response<MessageResponse>

    @GET("api/users/stats")
    suspend fun getUsersStats(
        @Header("Authorization") auth: String
    ): Response<UsersStatsResponse>


}
