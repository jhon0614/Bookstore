package com.example.movil.data.books

import com.google.gson.annotations.SerializedName

/**
 * Representa un libro tal como lo devuelve GET /api/books/ y GET /api/books/{id}
 */
data class Book(
    val id: Int,
    val title: String,
    val author: String,
    val isbn: String,
    val genre: String,
    val description: String?,
    val price: Double,
    val stock: Int,
    val available: Boolean,
    @SerializedName("cover_url")
    val coverUrl: String?,
    val categories: List<Category> = emptyList()
)

/**
 * Categoría anidada dentro de "categories" en cada libro,
 * y también el tipo devuelto por GET /api/categories/
 */
data class Category(
    val id: Int,
    val name: String
)

/**
 * Bloque "pagination" del listado de libros.
 */
data class Pagination(
    val page: Int,
    val pages: Int,
    @SerializedName("per_page")
    val perPage: Int,
    val total: Int,
    @SerializedName("has_next")
    val hasNext: Boolean,
    @SerializedName("has_prev")
    val hasPrev: Boolean
)

/**
 * Respuesta completa de GET /api/books/
 */
data class BooksResponse(
    val books: List<Book>,
    val pagination: Pagination
)