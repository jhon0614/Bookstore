package com.example.movil.data.books

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Servicio Retrofit para el módulo de libros.
 *
 * Endpoints usados (los únicos disponibles según el backend):
 *  - GET /api/books/
 *  - GET /api/books/{id}
 *  - GET /api/categories/
 *
 * NOTA sobre GET /api/categories/:
 * El enunciado no especifica la forma exacta de la respuesta de este endpoint.
 * Aquí se asume que devuelve un arreglo JSON plano de objetos Category, ej:
 * [ { "id": 1, "name": "Ficción" }, { "id": 2, "name": "Ciencia" } ]
 * Si en tu backend viene envuelto (ej: { "categories": [...] }), avísame y
 * ajusto solo este método (una línea) sin tocar el resto del código.
 */
interface BooksApiService {

    @GET("api/books/")
    suspend fun getBooks(
        @Query("q") q: String? = null,
        @Query("author") author: String? = null,
        @Query("genre") genre: String? = null,
        @Query("isbn") isbn: String? = null,
        @Query("category_id") categoryId: Int? = null,
        @Query("min_price") minPrice: Double? = null,
        @Query("max_price") maxPrice: Double? = null,
        @Query("available") available: Boolean? = null,
        @Query("page") page: Int,
        @Query("per_page") perPage: Int
    ): BooksResponse

    @GET("api/books/{id}")
    suspend fun getBookById(
        @Path("id") id: Int
    ): Book

    @GET("api/categories/")
    suspend fun getCategories(): List<Category>
}