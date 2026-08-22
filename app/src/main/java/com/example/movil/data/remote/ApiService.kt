package com.example.movil.data.remote

import com.example.movil.data.model.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>

    @POST("api/auth/verify-token")
    suspend fun verifyToken(): Response<ApiMessageResponse>

    @PUT("api/auth/change-password")
    suspend fun changePassword(@Body request: ChangePasswordRequest): Response<ApiMessageResponse>

    @GET("api/users/profile")
    suspend fun getProfile(): Response<ProfileResponse>

    @PUT("api/users/profile")
    suspend fun updateProfile(@Body profile: UpdateProfileRequest): Response<ProfileResponse>

    @GET("api/users/")
    suspend fun getUsers(
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 20,
        @Query("search") search: String = ""
    ): Response<UserPageResponse>

    @GET("api/users/search")
    suspend fun searchUsers(@Query("q") query: String): Response<UserPageResponse>

    @POST("api/users/admins")
    suspend fun createAdmin(@Body request: CreateAdminRequest): Response<ApiMessageResponse>

    @DELETE("api/users/{id}")
    suspend fun deleteUser(@Path("id") id: Int): Response<ApiMessageResponse>
}
