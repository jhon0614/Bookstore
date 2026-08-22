package com.example.movil.data.cart

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface CartApiService {
    @GET("api/orders/cart")
    suspend fun getCart(): Response<CartResponse>

    @POST("api/orders/cart/items")
    suspend fun addItem(
        @Body request: AddCartItemRequest
    ): Response<CartMutationResponse>

    @PUT("api/orders/cart/items/{itemId}")
    suspend fun updateItem(
        @Path("itemId") itemId: Int,
        @Body request: UpdateCartItemRequest
    ): Response<CartMutationResponse>

    @DELETE("api/orders/cart/items/{itemId}")
    suspend fun removeItem(
        @Path("itemId") itemId: Int
    ): Response<CartMutationResponse>

    @DELETE("api/orders/cart")
    suspend fun clearCart(): Response<ApiMessage>
}
