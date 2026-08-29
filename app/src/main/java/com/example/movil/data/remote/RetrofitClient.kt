package com.example.movil.data.remote

import android.content.Context
import com.example.movil.data.books.BooksApiService
import com.example.movil.data.cart.CartApiService
import com.example.movil.data.orders.OrdersApiService
import com.example.movil.data.session.SessionManager
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    // Emulador Android. Para celular físico, usar aquí la IP local del computador.
    private const val BASE_URL = "https://bookstore-api-fyh9.onrender.com"

    @Volatile private var retrofitInstance: Retrofit? = null

    private fun retrofit(context: Context): Retrofit {
        return retrofitInstance ?: synchronized(this) {
            retrofitInstance ?: buildRetrofit(context.applicationContext).also { retrofitInstance = it }
        }
    }

    private fun buildRetrofit(context: Context): Retrofit {
        val session = SessionManager(context)
        val logging = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BASIC }
        val client = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val token = runBlocking { session.getTokenSync() }
                val guestId = runBlocking { session.getOrCreateGuestId() }
                val request = chain.request().newBuilder().apply {
                    if (!token.isNullOrBlank()) header("Authorization", "Bearer $token")
                    header("X-Guest-Id", guestId)
                }.build()
                chain.proceed(request).also { response ->
                    if (response.code == 401) runBlocking { session.clearSession() }
                }
            }
            .addInterceptor(logging)
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    fun getApiService(context: Context): ApiService = retrofit(context).create(ApiService::class.java)
    fun getBooksApiService(context: Context): BooksApiService = retrofit(context).create(BooksApiService::class.java)
    fun getCartApiService(context: Context): CartApiService = retrofit(context).create(CartApiService::class.java)
    fun getOrdersApiService(context: Context): OrdersApiService = retrofit(context).create(OrdersApiService::class.java)
}
