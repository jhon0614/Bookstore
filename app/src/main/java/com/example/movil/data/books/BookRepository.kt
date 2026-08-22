package com.example.movil.data.books

import retrofit2.Retrofit

/**
 * Repositorio del módulo de libros. El ViewModel nunca llama a Retrofit
 * directamente; siempre pasa por aquí.
 */
class BooksRepository(
    private val api: BooksApiService
) {

    suspend fun searchBooks(
        query: String? = null,
        author: String? = null,
        genre: String? = null,
        isbn: String? = null,
        categoryId: Int? = null,
        minPrice: Double? = null,
        maxPrice: Double? = null,
        available: Boolean? = null,
        page: Int = 1,
        perPage: Int = 20
    ): BooksResponse {
        return api.getBooks(
            q = query?.trim()?.takeIf { it.isNotEmpty() },
            author = author?.trim()?.takeIf { it.isNotEmpty() },
            genre = genre?.trim()?.takeIf { it.isNotEmpty() },
            isbn = isbn?.trim()?.takeIf { it.isNotEmpty() },
            categoryId = categoryId,
            minPrice = minPrice,
            maxPrice = maxPrice,
            available = available,
            page = page,
            perPage = perPage
        )
    }

    suspend fun getBookDetail(bookId: Int): Book =
        api.getBookById(bookId)

    suspend fun getCategories(): List<Category> =
        api.getCategories()

    companion object {
        /**
         * Fábrica auxiliar para construir el repositorio a partir de un
         * Retrofit ya configurado (el que expone RetrofitClient.kt).
         *
         * Uso típico en el composable/NavHost (archivo que NO pertenece a
         * este módulo, lo arma quien conecte la navegación):
         *
         *   val repository = remember {
         *       BooksRepository.create(RetrofitClient.retrofit)
         *   }
         *
         * Reemplaza "RetrofitClient.retrofit" por el nombre real de la
         * propiedad que expone tu instancia de Retrofit. Ver sección
         * "Cambio requerido en RetrofitClient.kt" de la respuesta.
         */
        fun create(retrofit: Retrofit): BooksRepository {
            val api = retrofit.create(BooksApiService::class.java)
            return BooksRepository(api)
        }
    }
}