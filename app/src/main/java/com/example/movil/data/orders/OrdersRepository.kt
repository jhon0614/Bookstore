package com.example.movil.data.orders

import com.example.movil.data.cart.BookstoreApiFactory
import com.example.movil.data.cart.DataResult
import com.example.movil.data.cart.execute
import com.example.movil.data.session.Session

class OrdersRepository(
    private val api: OrdersApiService = BookstoreApiFactory.create(OrdersApiService::class.java),
    private val tokenProvider: () -> String? = { Session.token }
) {
    suspend fun checkout(method: PaymentMethod): DataResult<CheckoutResponse> =
        execute { api.checkout(authorization(), CheckoutRequest(method.apiValue)) }

    suspend fun getOrders(): DataResult<OrdersResponse> =
        execute { api.getOrders(authorization()) }

    suspend fun getOrder(orderId: Int): DataResult<OrderResponse> =
        execute { api.getOrder(authorization(), orderId) }

    private fun authorization(): String {
        val token = tokenProvider()?.takeIf { it.isNotBlank() }
            ?: throw IllegalStateException("No hay una sesión activa")
        return "Bearer $token"
    }
}
