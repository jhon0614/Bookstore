package com.example.movil.data.orders

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface OrdersApiService {
    @POST("api/orders/checkout")
    suspend fun checkout(
        @Body request: CheckoutRequest
    ): Response<CheckoutResponse>

    @GET("api/orders/")
    suspend fun getOrders(): Response<OrdersResponse>

    @GET("api/orders/{id}")
    suspend fun getOrder(
        @Path("id") orderId: Int
    ): Response<OrderResponse>
}
