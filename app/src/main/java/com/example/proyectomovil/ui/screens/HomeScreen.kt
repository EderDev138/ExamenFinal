package com.example.proyectomovil.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyectomovil.data.model.Producto
import com.example.proyectomovil.ui.viewmodel.ProductosViewModel
import coil.compose.AsyncImage


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    rol: String,
    onProductoClick: (Long) -> Unit,
    onCarritoClick: () -> Unit,
    onPerfilClick: () -> Unit,
    onMisPedidosClick: () -> Unit,
    onGestionProductosClick: () -> Unit,
    onGestionUsuariosClick: () -> Unit,
    onGestionDatosMaestrosClick: () -> Unit,
    viewModel: ProductosViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()
    var mostrarFiltros by remember { mutableStateOf(false) }
    var mostrarMenu by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Rincón Perfumes",
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                actions = {
                    // Botón Carrito (solo para clientes)
                    if (rol == "CLIENTE") {
                        IconButton(onClick = onCarritoClick) {
                            Icon(Icons.Default.ShoppingCart, "Carrito")
                        }
                    }

                    // Menú de opciones
                    Box {
                        IconButton(onClick = { mostrarMenu = true }) {
                            Icon(Icons.Default.MoreVert, "Menú")
                        }
                        DropdownMenu(
                            expanded = mostrarMenu,
                            onDismissRequest = { mostrarMenu = false }
                        ) {
                            if (rol == "CLIENTE") {
                                DropdownMenuItem(
                                    text = { Text("Mis Pedidos") },
                                    onClick = {
                                        mostrarMenu = false
                                        onMisPedidosClick()
                                    },
                                    leadingIcon = {
                                        Icon(Icons.Default.Receipt, null)
                                    }
                                )
                            }

                            if (rol == "ENCARGADO" || rol == "ADMIN") {
                                DropdownMenuItem(
                                    text = { Text("Gestión Productos") },
                                    onClick = {
                                        mostrarMenu = false
                                        onGestionProductosClick()
                                    },
                                    leadingIcon = {
                                        Icon(Icons.Default.Add, null)
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Gestión Datos Maestros") },
                                    onClick = {
                                        mostrarMenu = false
                                        onGestionDatosMaestrosClick()
                                    },
                                    leadingIcon = {
                                        Icon(Icons.Default.Category, null)
                                    }
                                )
                            }

                            if (rol == "ADMIN") {
                                DropdownMenuItem(
                                    text = { Text("Gestión Usuarios") },
                                    onClick = {
                                        mostrarMenu = false
                                        onGestionUsuariosClick()
                                    },
                                    leadingIcon = {
                                        Icon(Icons.Default.AccountCircle, null)
                                    }
                                )
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Barra de búsqueda
            SearchBarSection(
                busqueda = state.busqueda,
                onBusquedaChange = { viewModel.onBusquedaChange(it) },
                onFilterClick = { mostrarFiltros = !mostrarFiltros }
            )

            // Filtros
            if (mostrarFiltros) {
                FiltrosSection(
                    categorias = state.categorias,
                    marcas = state.marcas,
                    generos = state.generos,
                    categoriaSeleccionada = state.categoriaSeleccionada,
                    marcaSeleccionada = state.marcaSeleccionada,
                    generoSeleccionado = state.generoSeleccionado,
                    onCategoriaClick = { viewModel.seleccionarCategoria(it) },
                    onMarcaClick = { viewModel.seleccionarMarca(it) },
                    onGeneroClick = { viewModel.seleccionarGenero(it) },
                    onLimpiarClick = { viewModel.limpiarFiltros() }
                )
            }

            // Lista de productos
            if (state.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (state.errorMessage != null) {
                ErrorMessage(
                    mensaje = state.errorMessage ?: "",
                    onRetry = { viewModel.cargarDatos() }
                )
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(state.productos, key = { it.idProducto }) { producto ->
                        ProductoCard(
                            producto = producto,
                            onClick = { onProductoClick(producto.idProducto) }
                        )
                    }

                    if (state.productos.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "No se encontraron productos",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SearchBarSection(
    busqueda: String,
    onBusquedaChange: (String) -> Unit,
    onFilterClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedTextField(
            value = busqueda,
            onValueChange = onBusquedaChange,
            modifier = Modifier.weight(1f),
            placeholder = { Text("Buscar perfume...") },
            leadingIcon = { Icon(Icons.Default.Search, "Buscar") },
            singleLine = true
        )
        IconButton(
            onClick = onFilterClick,
            modifier = Modifier.size(48.dp)
        ) {
            Icon(Icons.Default.FilterList, "Filtros")
        }
    }
}

@Composable
fun FiltrosSection(
    categorias: List<com.example.proyectomovil.data.model.Categoria>,
    marcas: List<com.example.proyectomovil.data.model.Marca>,
    generos: List<com.example.proyectomovil.data.model.Genero>,
    categoriaSeleccionada: Long?,
    marcaSeleccionada: Long?,
    generoSeleccionado: Long?,
    onCategoriaClick: (Long?) -> Unit,
    onMarcaClick: (Long?) -> Unit,
    onGeneroClick: (Long?) -> Unit,
    onLimpiarClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Filtros", style = MaterialTheme.typography.titleSmall)
                TextButton(onClick = onLimpiarClick) {
                    Text("Limpiar")
                }
            }

            // Categorías
            if (categorias.isNotEmpty()) {
                Text("Categorías", style = MaterialTheme.typography.labelMedium)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(categorias) { cat ->
                        FilterChip(
                            selected = cat.idCategoria == categoriaSeleccionada,
                            onClick = {
                                onCategoriaClick(
                                    if (cat.idCategoria == categoriaSeleccionada) null
                                    else cat.idCategoria
                                )
                            },
                            label = { Text(cat.nombreCategoria) }
                        )
                    }
                }
            }

            // Marcas
            if (marcas.isNotEmpty()) {
                Text("Marcas", style = MaterialTheme.typography.labelMedium)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(marcas) { marca ->
                        FilterChip(
                            selected = marca.idMarca == marcaSeleccionada,
                            onClick = {
                                onMarcaClick(
                                    if (marca.idMarca == marcaSeleccionada) null
                                    else marca.idMarca
                                )
                            },
                            label = { Text(marca.nombreMarca) }
                        )
                    }
                }
            }

            // Géneros
            if (generos.isNotEmpty()) {
                Text("Para", style = MaterialTheme.typography.labelMedium)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(generos) { gen ->
                        FilterChip(
                            selected = gen.idGenero == generoSeleccionado,
                            onClick = {
                                onGeneroClick(
                                    if (gen.idGenero == generoSeleccionado) null
                                    else gen.idGenero
                                )
                            },
                            label = { Text(gen.nombreGenero) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ProductoCard(
    producto: Producto,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(modifier = Modifier.padding(12.dp)) {
            // Imagen del producto
            AsyncImage(
                model = producto.imagenUrl ?: "",
                contentDescription = producto.nombreProducto,
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = producto.nombreProducto,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = producto.marca?.nombreMarca ?: "",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Text(
                    text = producto.descripcion ?: "",
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.weight(1f))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "$${String.format("%.0f", producto.precio)}",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "${producto.volumenML}ml",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            text = "Stock: ${producto.stock}",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (producto.stock > 10)
                                MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ErrorMessage(mensaje: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = mensaje,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.error
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onRetry) {
            Text("Reintentar")
        }
    }
}