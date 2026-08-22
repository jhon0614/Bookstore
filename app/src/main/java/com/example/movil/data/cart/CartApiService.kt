package com.example.movil.data.cart

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface CartApiService {
    @GET("api/orders/cart")
    suspend fun getCart(@Header("Authorization") authorization: String): Response<CartResponse>

    @POST("api/orders/cart/items")
    suspend fun addItem(
        @Header("Authorization") authorization: String, @Body request: AddCartItemRequest
    ): Response<CartMutationResponse>

    @PUT("api/orders/cart/items/{itemId}")
    suspend fun updateItem(
        @Header("Authorization") authorization: String,
        @Path("itemId") itemId: Int,
        @Body request: UpdateCartItemRequest
    ): Response<CartMutationResponse>

    @DELETE("api/orders/cart/items/{itemId}")
    suspend fun removeItem(
        @Header("Authorization") authorization: String, @Path("itemId") itemId: Int
    ): Response<CartMutationResponse>

    @DELETE("api/orders/cart")
    suspend fun clearCart(@Header("Authorization") authorization: String): Response<ApiMessage>
}

/** Factoría temporal del módulo. Persona 1 puede moverla a RetrofitClient al integrar. */
object BookstoreApiFactory {
    private const val BASE_URL = "http://192.168.1.10:5050/"

    private val retrofit: Retrofit by lazy {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        val client = OkHttpClient.Builder().addInterceptor(logging).build()
        Retrofit.Builder().baseUrl(BASE_URL).client(client)
            .addConverterFactory(GsonConverterFactory.create()).build()
    }

    val cartApi: CartApiService by lazy { retrofit.create(CartApiService::class.java) }
    fun <T> create(service: Class<T>): T = retrofit.create(service)
}
