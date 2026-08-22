package com.example.movil.data.cart

import android.content.Context
import com.example.movil.data.remote.RetrofitClient
import com.example.movil.data.session.SessionManager
import org.json.JSONObject
import retrofit2.Response

class CartRepository(
    context: Context,
    private val api: CartApiService = RetrofitClient.getCartApiService(context)
) {
    private val session = SessionManager(context.applicationContext)
    suspend fun getCart(): DataResult<CartResponse> = execute { api.getCart(authorization()) }

    suspend fun addItem(bookId: Int, quantity: Int): DataResult<CartMutationResponse> {
        if (quantity <= 0) return DataResult.Error("La cantidad debe ser mayor que cero")
        return execute { api.addItem(authorization(), AddCartItemRequest(bookId, quantity)) }
    }

    suspend fun updateItem(itemId: Int, quantity: Int): DataResult<CartMutationResponse> {
        if (quantity <= 0) return DataResult.Error("La cantidad debe ser mayor que cero")
        return execute { api.updateItem(authorization(), itemId, UpdateCartItemRequest(quantity)) }
    }

    suspend fun removeItem(itemId: Int): DataResult<CartMutationResponse> =
        execute { api.removeItem(authorization(), itemId) }

    suspend fun clearCart(): DataResult<ApiMessage> = execute { api.clearCart(authorization()) }

    private suspend fun authorization(): String {
        val token = session.getTokenSync()?.takeIf { it.isNotBlank() }
            ?: throw IllegalStateException("No hay una sesión activa")
        return "Bearer $token"
    }
}

internal suspend fun <T> execute(block: suspend () -> Response<T>): DataResult<T> = try {
    val response = block()
    val body = response.body()
    if (response.isSuccessful && body != null) {
        DataResult.Success(body)
    } else {
        val raw = response.errorBody()?.string()
        val message = try {
            raw?.let { JSONObject(it).optString("error") }?.takeIf { it.isNotBlank() }
        } catch (_: Exception) {
            null
        } ?: "Error del servidor (${response.code()})"
        DataResult.Error(message, response.code())
    }
} catch (error: Exception) {
    DataResult.Error(error.message ?: "No fue posible conectar con el servidor")
}
