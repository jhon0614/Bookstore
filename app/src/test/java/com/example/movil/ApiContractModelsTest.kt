package com.example.movil

import com.example.movil.data.books.BooksResponse
import com.example.movil.data.books.CategoriesResponse
import com.example.movil.data.cart.CartResponse
import com.example.movil.data.model.AuthResponse
import com.example.movil.data.model.ProfileResponse
import com.example.movil.data.model.UserPageResponse
import com.example.movil.data.orders.CheckoutResponse
import com.example.movil.data.orders.OrdersResponse
import com.google.gson.Gson
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ApiContractModelsTest {
    private val gson = Gson()

    @Test
    fun userResponsesMatchBackendContract() {
        val user = """{"iD_User":1,"UserName":"Admin","Email":"admin@bookstore.com","roles":[{"iDRole":1,"TypeRole":"Administrador"}]}"""
        val auth = gson.fromJson("""{"token":"jwt","user":$user,"message":"Login exitoso"}""", AuthResponse::class.java)
        val profile = gson.fromJson("""{"user":$user}""", ProfileResponse::class.java)
        val users = gson.fromJson("""{"users":[$user],"pagination":{"page":1,"pages":1,"per_page":20,"total":1}}""", UserPageResponse::class.java)

        assertEquals("jwt", auth.token)
        assertEquals("Administrador", auth.user.roles.single().name)
        assertEquals(1, profile.user.id)
        assertEquals(1, users.pagination?.total)
    }

    @Test
    fun bookstoreResponsesMatchBackendContract() {
        val book = """{"id":1,"title":"Cien años de soledad","author":"Gabriel García Márquez","isbn":"9780307474728","genre":"Realismo mágico","description":null,"price":59000.0,"stock":12,"available":true,"cover_url":null,"categories":[{"id":1,"name":"Ficción"}]}"""
        val books = gson.fromJson("""{"books":[$book],"pagination":{"page":1,"pages":1,"per_page":20,"total":1,"has_next":false,"has_prev":false}}""", BooksResponse::class.java)
        val categories = gson.fromJson("""{"categories":[{"id":1,"name":"Ficción"}],"count":1}""", CategoriesResponse::class.java)
        val cart = gson.fromJson("""{"items":[{"id":2,"book":$book,"quantity":2,"subtotal":118000.0}],"count":2,"total":118000.0}""", CartResponse::class.java)
        val order = """{"id":3,"user_id":2,"status":"confirmado","payment_method":"tarjeta","total":118000.0,"created_at":"2026-08-18T20:00:00","items":[{"id":4,"book_id":1,"title":"Cien años de soledad","quantity":2,"unit_price":59000.0,"subtotal":118000.0}]}"""
        val checkout = gson.fromJson("""{"message":"Pedido confirmado","order":$order}""", CheckoutResponse::class.java)
        val orders = gson.fromJson("""{"orders":[$order]}""", OrdersResponse::class.java)

        assertEquals("Cien años de soledad", books.books.single().title)
        assertEquals("Ficción", categories.categories.single().name)
        assertEquals(2, cart.count)
        assertEquals(3, checkout.order.id)
        assertTrue(orders.orders.single().items.isNotEmpty())
    }
}
