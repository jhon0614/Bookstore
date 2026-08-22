package com.example.movil.data.remote

import android.content.Context
import com.example.movil.data.session.SessionManager
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "https://your-backend-api-url.com/" // Cambiar según backend

    fun getApiService(context: Context): ApiService {
        val sessionManager = SessionManager(context)

        val client = OkHttpClient.Builder().addInterceptor(Interceptor { chain ->
            val token = runBlocking { sessionManager.getTokenSync() }
            val requestBuilder = chain.request().newBuilder()

            if (!token.isNull_or_Empty()) {
                requestBuilder.addHeader("Authorization", "Bearer $token")
            }

            val response = chain.proceed(requestBuilder.build())

            if (response.code == 401) {
                runBlocking { sessionManager.clearSession() }
            }

            response
        }).build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}

private fun String?.isNull_or_Empty(): Boolean = this == null || this.isEmpty()
