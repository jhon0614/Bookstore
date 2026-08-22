package com.example.movil.data.orders

import android.content.Context
import com.example.movil.data.cart.DataResult
import com.example.movil.data.cart.execute
import com.example.movil.data.remote.RetrofitClient

class OrdersRepository(
    context: Context,
    private val api: OrdersApiService = RetrofitClient.getOrdersApiService(context)
) {
    suspend fun checkout(method: PaymentMethod): DataResult<CheckoutResponse> =
        execute { api.checkout(CheckoutRequest(method.apiValue)) }

    suspend fun getOrders(): DataResult<OrdersResponse> =
        execute { api.getOrders() }

    suspend fun getOrder(orderId: Int): DataResult<OrderResponse> =
        execute { api.getOrder(orderId) }
}
