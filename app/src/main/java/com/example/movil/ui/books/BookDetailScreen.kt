package com.example.movil.ui.books


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.movil.data.books.Book

/**
 * Pantalla de detalle. No implementa el carrito: al presionar "Agregar al
 * carrito" simplemente invoca onAddToCart(bookId, quantity), que es
 * conectado desde afuera (persona encargada del módulo de carrito).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookDetailScreen(
    viewModel: BookDetailViewModel,
    onAddToCart: (bookId: Int, quantity: Int) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Detalle del libro") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors()
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val state = uiState) {
                is BookDetailUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                is BookDetailUiState.Error -> {
                    Column(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = state.message)
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(onClick = viewModel::loadBook) {
                            Text("Reintentar")
                        }
                    }
                }

                is BookDetailUiState.Success -> {
                    BookDetailContent(
                        book = state.book,
                        quantity = state.quantity,
                        onIncrement = viewModel::incrementQuantity,
                        onDecrement = viewModel::decrementQuantity,
                        onAddToCart = { onAddToCart(state.book.id, state.quantity) }
                    )
                }
            }
        }
    }
}

@Composable
private fun BookDetailContent(
    book: Book,
    quantity: Int,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    onAddToCart: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp),
            contentAlignment = Alignment.Center
        ) { Text("📖", style = MaterialTheme.typography.displayLarge) }

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = book.title, style = MaterialTheme.typography.headlineSmall)
        Text(text = book.author, style = MaterialTheme.typography.titleMedium)

        Spacer(modifier = Modifier.height(8.dp))

        Text(text = formatPrice(book.price), style = MaterialTheme.typography.headlineSmall)

        Spacer(modifier = Modifier.height(4.dp))

        if (book.stock > 0) {
            Text(
                text = "Disponible · Stock: ${book.stock}",
                color = MaterialTheme.colorScheme.primary
            )
        } else {
            Text(
                text = "Agotado",
                color = MaterialTheme.colorScheme.error
            )
        }

        if (book.categories.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow {
                items(book.categories, key = { it.id }) { category ->
                    AssistChip(
                        onClick = {},
                        label = { Text(category.name) },
                        modifier = Modifier.padding(end = 8.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider()
        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Género: ${book.genre}", style = MaterialTheme.typography.bodyMedium)
        Text(text = "ISBN: ${book.isbn}", style = MaterialTheme.typography.bodyMedium)

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = book.description ?: "Sin descripción disponible.",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(24.dp))

        if (book.stock > 0) {
            Text(text = "Cantidad", style = MaterialTheme.typography.titleSmall)
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedButton(
                    onClick = onDecrement,
                    enabled = quantity > 1
                ) {
                    Icon(Icons.Default.Remove, contentDescription = "Disminuir cantidad")
                }
                Text(
                    text = quantity.toString(),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier
                        .padding(horizontal = 24.dp)
                )
                OutlinedButton(
                    onClick = onIncrement,
                    enabled = quantity < book.stock
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Aumentar cantidad")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onAddToCart,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Agregar al carrito")
            }
        } else {
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = {},
                enabled = false,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Agotado")
            }
        }
    }
}
