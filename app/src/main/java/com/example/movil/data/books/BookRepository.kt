package com.example.movil.data.books

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
        api.getBookById(bookId).book

    suspend fun getCategories(): List<Category> =
        api.getCategories().categories
}
