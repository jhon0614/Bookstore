package com.example.movil.ui.books

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.movil.data.books.BooksRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val SEARCH_DEBOUNCE_MS = 400L
private const val DEFAULT_PER_PAGE = 20

/**
 * ViewModel del catálogo: búsqueda, filtros y paginación.
 */
class BooksViewModel(
    private val repository: BooksRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(BooksUiState())
    val uiState: StateFlow<BooksUiState> = _uiState.asStateFlow()

    private var searchJob: Job? = null

    init {
        loadCategories()
        search(resetPage = true)
    }

    /** Se llama desde el TextField de búsqueda en cada cambio de texto. */
    fun onQueryChange(newQuery: String) {
        _uiState.update { it.copy(filters = it.filters.copy(query = newQuery)) }
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(SEARCH_DEBOUNCE_MS)
            search(resetPage = true)
        }
    }

    fun onAuthorChange(value: String) =
        _uiState.update { it.copy(filters = it.filters.copy(author = value)) }

    fun onGenreChange(value: String) =
        _uiState.update { it.copy(filters = it.filters.copy(genre = value)) }

    fun onIsbnChange(value: String) =
        _uiState.update { it.copy(filters = it.filters.copy(isbn = value)) }

    fun onCategorySelected(categoryId: Int?) =
        _uiState.update { it.copy(filters = it.filters.copy(categoryId = categoryId)) }

    fun onMinPriceChange(value: String) =
        _uiState.update { it.copy(filters = it.filters.copy(minPrice = value)) }

    fun onMaxPriceChange(value: String) =
        _uiState.update { it.copy(filters = it.filters.copy(maxPrice = value)) }

    fun onOnlyAvailableChange(value: Boolean) =
        _uiState.update { it.copy(filters = it.filters.copy(onlyAvailable = value)) }

    fun toggleFiltersVisible(visible: Boolean) =
        _uiState.update { it.copy(isFiltersVisible = visible) }

    /** Botón "Aplicar filtros" del BookFiltersSheet. */
    fun applyFilters() {
        toggleFiltersVisible(false)
        search(resetPage = true)
    }

    /** Botón "Limpiar filtros" del BookFiltersSheet. Conserva el texto de búsqueda libre. */
    fun clearFilters() {
        _uiState.update { it.copy(filters = BooksFilters(query = it.filters.query)) }
        search(resetPage = true)
    }

    /** Llamado cuando el LazyColumn llega cerca del final de la lista. */
    fun loadNextPage() {
        val state = _uiState.value
        if (state.isLoading || state.isLoadingMore) return
        if (!state.hasNext) return
        search(resetPage = false)
    }

    /** Botón "Reintentar" del estado de error. */
    fun retry() {
        search(resetPage = _uiState.value.books.isEmpty())
    }

    private fun loadCategories() {
        viewModelScope.launch {
            runCatching { repository.getCategories() }
                .onSuccess { categories ->
                    _uiState.update { it.copy(categories = categories) }
                }
            // Si falla, simplemente no se muestran categorías en el filtro;
            // no bloqueamos el catálogo por esto.
        }
    }

    private fun search(resetPage: Boolean) {
        val current = _uiState.value
        val pageToLoad = if (resetPage) 1 else current.page + 1

        viewModelScope.launch {
            _uiState.update {
                if (resetPage) it.copy(isLoading = true, errorMessage = null)
                else it.copy(isLoadingMore = true, errorMessage = null)
            }

            val filters = current.filters
            val minPrice = filters.minPrice.toDoubleOrNull()
            val maxPrice = filters.maxPrice.toDoubleOrNull()

            val result = runCatching {
                repository.searchBooks(
                    query = filters.query,
                    author = filters.author,
                    genre = filters.genre,
                    isbn = filters.isbn,
                    categoryId = filters.categoryId,
                    minPrice = minPrice,
                    maxPrice = maxPrice,
                    available = if (filters.onlyAvailable) true else null,
                    page = pageToLoad,
                    perPage = DEFAULT_PER_PAGE
                )
            }

            result.onSuccess { response ->
                _uiState.update {
                    val newBooks = if (resetPage) {
                        response.books
                    } else {
                        // Evita duplicados si el usuario dispara loadNextPage() dos veces
                        (it.books + response.books).distinctBy { book -> book.id }
                    }
                    it.copy(
                        books = newBooks,
                        page = response.pagination.page,
                        totalPages = response.pagination.pages,
                        hasNext = response.pagination.hasNext,
                        isLoading = false,
                        isLoadingMore = false,
                        errorMessage = null
                    )
                }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isLoadingMore = false,
                        errorMessage = error.message ?: "No se pudo cargar el catálogo"
                    )
                }
            }
        }
    }

    companion object {
        fun provideFactory(repository: BooksRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return BooksViewModel(repository) as T
                }
            }
    }
}

/**
 * ViewModel del detalle de un libro. Maneja carga y selector de cantidad
 * (acotado entre 1 y el stock disponible). NO implementa el carrito: la
 * pantalla invoca el callback onAddToCart(bookId, quantity) que le llega
 * desde afuera (lo conecta la persona encargada del carrito).
 */
class BookDetailViewModel(
    private val repository: BooksRepository,
    private val bookId: Int
) : ViewModel() {

    private val _uiState = MutableStateFlow<BookDetailUiState>(BookDetailUiState.Loading)
    val uiState: StateFlow<BookDetailUiState> = _uiState.asStateFlow()

    init {
        loadBook()
    }

    fun loadBook() {
        viewModelScope.launch {
            _uiState.update { BookDetailUiState.Loading }
            runCatching { repository.getBookDetail(bookId) }
                .onSuccess { book ->
                    _uiState.update {
                        BookDetailUiState.Success(
                            book = book,
                            quantity = if (book.stock > 0) 1 else 0
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        BookDetailUiState.Error(error.message ?: "No se pudo cargar el libro")
                    }
                }
        }
    }

    fun incrementQuantity() {
        val state = _uiState.value
        if (state is BookDetailUiState.Success) {
            val newQuantity = (state.quantity + 1).coerceAtMost(state.book.stock)
            _uiState.update { state.copy(quantity = newQuantity) }
        }
    }

    fun decrementQuantity() {
        val state = _uiState.value
        if (state is BookDetailUiState.Success) {
            val newQuantity = (state.quantity - 1).coerceAtLeast(1)
            _uiState.update { state.copy(quantity = newQuantity) }
        }
    }

    companion object {
        fun provideFactory(repository: BooksRepository, bookId: Int): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return BookDetailViewModel(repository, bookId) as T
                }
            }
    }
}