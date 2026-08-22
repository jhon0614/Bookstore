package com.example.movil.data.model

import com.google.gson.annotations.SerializedName

data class LoginRequest(@SerializedName("Email") val email: String, @SerializedName("PasswoRDkey") val passwordKey: String)
data class RegisterRequest(@SerializedName("UserName") val userName: String, @SerializedName("Email") val email: String, @SerializedName("PasswoRDkey") val passwordKey: String)
data class CreateAdminRequest(@SerializedName("UserName") val userName: String, @SerializedName("Email") val email: String, @SerializedName("PasswoRDkey") val passwordKey: String)
data class ChangePasswordRequest(@SerializedName("current_password") val currentPassword: String, @SerializedName("new_password") val newPassword: String)
data class UpdateProfileRequest(@SerializedName("UserName") val userName: String, @SerializedName("Email") val email: String)

data class RoleModel(@SerializedName("iDRole") val id: Int, @SerializedName("TypeRole") val name: String)

data class UserProfile(
    @SerializedName("iD_User") val id: Int,
    @SerializedName("UserName") val userName: String,
    @SerializedName("Email") val email: String,
    val roles: List<RoleModel> = emptyList()
)

data class AuthResponse(val token: String, val user: UserProfile, val message: String? = null)
data class ProfileResponse(val user: UserProfile, val message: String? = null)
data class PaginationResponse(val page: Int, val pages: Int, @SerializedName("per_page") val perPage: Int, val total: Int)
data class UserPageResponse(val users: List<UserProfile> = emptyList(), val pagination: PaginationResponse? = null, val count: Int? = null)
data class ApiMessageResponse(val message: String? = null, val error: String? = null, val valid: Boolean? = null, val user: UserProfile? = null)
