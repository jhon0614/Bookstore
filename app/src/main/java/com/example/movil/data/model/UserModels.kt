package com.example.movil.data.model

import com.google.gson.annotations.SerializedName

data class LoginRequest(
    @SerializedName("Email") val email: String,
    @SerializedName("PasswoRDkey") val passwordKey: String
)

data class RegisterRequest(
    @SerializedName("UserName") val userName: String,
    @SerializedName("Email") val email: String,
    @SerializedName("PasswoRDkey") val passwordKey: String
)

data class CreateAdminRequest(
    @SerializedName("UserName") val userName: String,
    @SerializedName("Email") val email: String,
    @SerializedName("PasswoRDkey") val passwordKey: String
)

data class ChangePasswordRequest(
    @SerializedName("current_password") val currentPassword: String,
    @SerializedName("new_password") val newPassword: String
)

data class AuthResponse(
    @SerializedName("token") val token: String,
    @SerializedName("role") val role: String? = "client",
    @SerializedName("message") val message: String? = null
)

data class UserProfile(
    @SerializedName("id") val id: String?,
    @SerializedName("UserName") val userName: String?,
    @SerializedName("Email") val email: String?,
    @SerializedName("role") val role: String? = "client"
)

data class UserPageResponse(
    @SerializedName("users") val users: List<UserProfile>,
    @SerializedName("total") val total: Int,
    @SerializedName("page") val page: Int
)

data class ApiMessageResponse(
    @SerializedName("message") val message: String
)
