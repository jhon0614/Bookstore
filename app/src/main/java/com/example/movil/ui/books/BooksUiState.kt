package com.example.movil.ui.books

import com.example.movil.data.books.Book
import com.example.movil.data.books.Category

/**
 * Valores actuales de los filtros de búsqueda. Los precios se guardan como
 * String para que el TextField los pueda editar libremente (incluyendo
 * vacío) y se convierten a Double solo al momento de llamar al backend.
 */
data class BooksFilters(
    val query: String = "",
    val author: String = "",
    val genre: String = "",
    val isbn: String = "",
    val categoryId: Int? = null,
    val minPrice: String = "",
    val maxPrice: String = "",
    val onlyAvailable: Boolean = false
)

/**
 * Estado único de la pantalla de catálogo (lista + búsqueda + filtros + paginación).
 */
data class BooksUiState(
    val books: List<Book> = emptyList(),
    val categories: List<Category> = emptyList(),
    val filters: BooksFilters = BooksFilters(),
    val page: Int = 1,
    val totalPages: Int = 1,
    val hasNext: Boolean = false,
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val isFiltersVisible: Boolean = false,
    val errorMessage: String? = null
) {
    val isEmptyResult: Boolean
        get() = !isLoading && errorMessage == null && books.isEmpty()
}

/**
 * Estado de la pantalla de detalle. Se modela como sealed class porque
 * el detalle no tiene un estado "parcial": o está cargando, o hay libro,
 * o hubo error.
 */
sealed class BookDetailUiState {
    data object Loading : BookDetailUiState()

    data class Success(
        val book: Book,
        val quantity: Int = 1
    ) : BookDetailUiState()

    data class Error(
        val message: String
    ) : BookDetailUiState()
}