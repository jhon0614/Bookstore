package com.example.movil.data.orders

import android.content.Context
import com.example.movil.data.cart.DataResult
import com.example.movil.data.cart.execute
import com.example.movil.data.remote.RetrofitClient
import com.example.movil.data.session.SessionManager

class OrdersRepository(
    context: Context,
    private val api: OrdersApiService = RetrofitClient.getOrdersApiService(context)
) {
    private val session = SessionManager(context.applicationContext)
    suspend fun checkout(method: PaymentMethod): DataResult<CheckoutResponse> =
        execute { api.checkout(authorization(), CheckoutRequest(method.apiValue)) }

    suspend fun getOrders(): DataResult<OrdersResponse> =
        execute { api.getOrders(authorization()) }

    suspend fun getOrder(orderId: Int): DataResult<OrderResponse> =
        execute { api.getOrder(authorization(), orderId) }

    private suspend fun authorization(): String {
        val token = session.getTokenSync()?.takeIf { it.isNotBlank() }
            ?: throw IllegalStateException("No hay una sesión activa")
        return "Bearer $token"
    }
}
