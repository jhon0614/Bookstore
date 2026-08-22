package com.example.movil.data.cart

import retrofit2.Response
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
