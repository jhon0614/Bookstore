package com.example.movil.data.books

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Servicio Retrofit para el módulo de libros.
 *
 * Endpoints usados:
 *  - GET /api/books/
 *  - GET /api/books/{id}
 *  - GET /api/categories/
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
    ): BookResponse

    @GET("api/categories/")
    suspend fun getCategories(): CategoriesResponse
}
