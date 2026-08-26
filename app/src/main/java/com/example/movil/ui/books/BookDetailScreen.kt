package com.example.movil.ui.books

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.movil.data.books.Book

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookDetailScreen(
    viewModel: BookDetailViewModel,
    onAddToCart: (bookId: Int, quantity: Int) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val success = uiState as? BookDetailUiState.Success

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Detalle del libro") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        },
        bottomBar = {
            success?.let { state ->
                Surface(shadowElevation = 10.dp, tonalElevation = 3.dp) {
                    Button(
                        onClick = { onAddToCart(state.book.id, state.quantity) },
                        enabled = state.book.stock > 0,
                        modifier = Modifier
                            .navigationBarsPadding()
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                            .fillMaxWidth()
                            .heightIn(min = 52.dp)
                    ) {
                        Icon(Icons.Default.ShoppingCart, contentDescription = null)
                        Spacer(Modifier.width(10.dp))
                        Text(if (state.book.stock > 0) "Agregar al carrito" else "Libro agotado")
                    }
                }
            }
        }
    ) { paddingValues ->
        Box(Modifier
            .fillMaxSize()
            .padding(paddingValues)) {
            when (val state = uiState) {
                BookDetailUiState.Loading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                is BookDetailUiState.Error -> ErrorDetail(state.message, viewModel::loadBook)
                is BookDetailUiState.Success -> BookDetailContent(
                    book = state.book,
                    quantity = state.quantity,
                    onIncrement = viewModel::incrementQuantity,
                    onDecrement = viewModel::decrementQuantity
                )
            }
        }
    }
}

@Composable
private fun ErrorDetail(message: String, retry: () -> Unit) {
    Column(
        Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(message, color = MaterialTheme.colorScheme.error)
        Spacer(Modifier.height(12.dp))
        Button(onClick = retry) { Text("Reintentar") }
    }
}

@Composable
private fun BookDetailContent(
    book: Book,
    quantity: Int,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(210.dp)
                .background(MaterialTheme.colorScheme.primaryContainer, MaterialTheme.shapes.large),
            contentAlignment = Alignment.Center
        ) { Text("📚", style = MaterialTheme.typography.displayLarge) }

        Spacer(Modifier.height(22.dp))
        Text(book.title, style = MaterialTheme.typography.headlineSmall)
        Text(
            book.author,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(12.dp))
        Text(
            formatPrice(book.price),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            if (book.stock > 0) "Disponible · ${book.stock} en inventario" else "Agotado",
            color = if (book.stock > 0) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.error,
            fontWeight = FontWeight.SemiBold
        )

        if (book.categories.isNotEmpty()) {
            Spacer(Modifier.height(8.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(book.categories, key = { it.id }) { category ->
                    AssistChip(onClick = {}, label = { Text(category.name) })
                }
            }
        }

        HorizontalDivider(Modifier.padding(vertical = 20.dp))
        Text("Información", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))
        Text("Género: ${book.genre}")
        Text("ISBN: ${book.isbn}")
        Spacer(Modifier.height(14.dp))
        Text(
            book.description ?: "Sin descripción disponible.",
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        if (book.stock > 0) {
            Spacer(Modifier.height(24.dp))
            Text("Cantidad", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(10.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                FilledTonalIconButton(
                    onClick = onDecrement,
                    enabled = quantity > 1,
                    shape = CircleShape
                ) {
                    Icon(Icons.Default.Remove, contentDescription = "Disminuir cantidad")
                }
                Text(
                    quantity.toString(),
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )
                FilledTonalIconButton(
                    onClick = onIncrement,
                    enabled = quantity < book.stock,
                    shape = CircleShape
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Aumentar cantidad")
                }
            }
        }
        Spacer(Modifier.height(16.dp))
    }
}
