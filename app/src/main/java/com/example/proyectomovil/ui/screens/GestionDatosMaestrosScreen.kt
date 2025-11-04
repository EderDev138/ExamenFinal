package com.example.proyectomovil.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyectomovil.data.model.*
import com.example.proyectomovil.ui.viewmodel.DatosMaestrosViewModel
import com.example.proyectomovil.ui.viewmodel.TipoDatoMaestro

/**
 * Pantalla de Gestión de Datos Maestros.
 *
 * Esta pantalla unificada permite a los usuarios con rol ADMIN o ENCARGADO
 * gestionar los 4 tipos de datos maestros del sistema:
 * - Marcas (Dior, Chanel, etc.)
 * - Categorías (Florales, Cítricas, etc.)
 * - Géneros (Masculino, Femenino, Unisex)
 * - Tipos de Producto (Eau de Parfum, Eau de Toilette, etc.)
 *
 * Funcionalidades:
 * - Ver lista de items de cada entidad en tabs separados
 * - Agregar nuevos items (botón FAB)
 * - Editar items existentes
 * - Eliminar items
 * - Validaciones de campos requeridos
 * - Mensajes de éxito y error
 *
 * Diseño:
 * - TopAppBar con título y botón volver
 * - 4 Tabs para cambiar entre entidades
 * - LazyColumn con lista de items
 * - FAB flotante para agregar
 * - Dialog modal con formulario para crear/editar
 *
 * @param onVolverClick Callback al presionar el botón volver
 * @param viewModel ViewModel que maneja la lógica de negocio
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GestionDatosMaestrosScreen(
    onVolverClick: () -> Unit,
    viewModel: DatosMaestrosViewModel = viewModel()
) {
    // Observar el estado del ViewModel
    // collectAsState() convierte el StateFlow en State de Compose
    // La UI se recompone automáticamente cuando el estado cambia
    val state by viewModel.state.collectAsState()

    // Estado local para el tab seleccionado (0 = Marcas, 1 = Categorías, etc.)
    var tabSeleccionado by remember { mutableStateOf(0) }

    // Lista de nombres de los tabs
    val tabs = listOf("Marcas", "Categorías", "Géneros", "Tipos")

    // Scaffold proporciona la estructura básica de Material Design
    // con TopBar, FAB, etc.
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gestión de Datos Maestros") },
                navigationIcon = {
                    IconButton(onClick = onVolverClick) {
                        Icon(Icons.Default.ArrowBack, "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        },
        // FAB (Floating Action Button) para agregar nuevos items
        // Solo se muestra si no hay un formulario abierto
        floatingActionButton = {
            if (!state.mostrarFormulario) {
                FloatingActionButton(
                    onClick = {
                        // Determinar qué tipo de entidad agregar según el tab activo
                        val tipo = when (tabSeleccionado) {
                            0 -> TipoDatoMaestro.MARCA
                            1 -> TipoDatoMaestro.CATEGORIA
                            2 -> TipoDatoMaestro.GENERO
                            else -> TipoDatoMaestro.TIPO_PRODUCTO
                        }
                        viewModel.mostrarFormularioAgregar(tipo)
                    }
                ) {
                    Icon(Icons.Default.Add, "Agregar")
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // TabRow para cambiar entre las 4 entidades
            TabRow(selectedTabIndex = tabSeleccionado) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = tabSeleccionado == index,
                        onClick = { tabSeleccionado = index },
                        text = {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(title)
                                // Badge con el count de items
                                Badge {
                                    Text(
                                        when (index) {
                                            0 -> state.marcas.size.toString()
                                            1 -> state.categorias.size.toString()
                                            2 -> state.generos.size.toString()
                                            else -> state.tiposProducto.size.toString()
                                        }
                                    )
                                }
                            }
                        }
                    )
                }
            }

            // Contenido según el tab seleccionado
            when {
                // Estado de carga: Mostrar indicador circular
                state.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                // Mostrar el contenido del tab activo
                else -> {
                    when (tabSeleccionado) {
                        0 -> MarcasTab(
                            marcas = state.marcas,
                            onEditar = { viewModel.mostrarFormularioEditar(TipoDatoMaestro.MARCA, it) },
                            onEliminar = { viewModel.eliminarMarca(it.idMarca) }
                        )
                        1 -> CategoriasTab(
                            categorias = state.categorias,
                            onEditar = { viewModel.mostrarFormularioEditar(TipoDatoMaestro.CATEGORIA, it) },
                            onEliminar = { viewModel.eliminarCategoria(it.idCategoria) }
                        )
                        2 -> GenerosTab(
                            generos = state.generos,
                            onEditar = { viewModel.mostrarFormularioEditar(TipoDatoMaestro.GENERO, it) },
                            onEliminar = { viewModel.eliminarGenero(it.idGenero) }
                        )
                        3 -> TiposProductoTab(
                            tipos = state.tiposProducto,
                            onEditar = { viewModel.mostrarFormularioEditar(TipoDatoMaestro.TIPO_PRODUCTO, it) },
                            onEliminar = { viewModel.eliminarTipoProducto(it.idTipoProducto) }
                        )
                    }
                }
            }
        }

        // Dialog modal con formulario para agregar/editar
        if (state.mostrarFormulario) {
            FormularioDatoMaestro(
                tipo = state.tipoSeleccionado,
                itemEditando = state.itemEditando,
                onGuardar = { tipo, nombre, descripcion, paisOrigen ->
                    // Llamar al método correspondiente según el tipo
                    when (tipo) {
                        TipoDatoMaestro.MARCA -> {
                            val item = state.itemEditando as? Marca
                            if (item != null) {
                                viewModel.actualizarMarca(item.idMarca, nombre, descripcion, paisOrigen)
                            } else {
                                viewModel.crearMarca(nombre, descripcion, paisOrigen)
                            }
                        }
                        TipoDatoMaestro.CATEGORIA -> {
                            val item = state.itemEditando as? Categoria
                            if (item != null) {
                                viewModel.actualizarCategoria(item.idCategoria, nombre, descripcion)
                            } else {
                                viewModel.crearCategoria(nombre, descripcion)
                            }
                        }
                        TipoDatoMaestro.GENERO -> {
                            val item = state.itemEditando as? Genero
                            if (item != null) {
                                viewModel.actualizarGenero(item.idGenero, nombre)
                            } else {
                                viewModel.crearGenero(nombre)
                            }
                        }
                        TipoDatoMaestro.TIPO_PRODUCTO -> {
                            val item = state.itemEditando as? TipoProducto
                            if (item != null) {
                                viewModel.actualizarTipoProducto(item.idTipoProducto, nombre, descripcion)
                            } else {
                                viewModel.crearTipoProducto(nombre, descripcion)
                            }
                        }
                    }
                },
                onCancelar = { viewModel.ocultarFormulario() }
            )
        }

        // Snackbar para mensajes de éxito
        state.exitoMessage?.let { mensaje ->
            // LaunchedEffect se ejecuta cuando cambia el mensaje
            // Después de 2 segundos, limpia el mensaje
            LaunchedEffect(mensaje) {
                kotlinx.coroutines.delay(2000)
                viewModel.limpiarMensajes()
            }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.BottomCenter
            ) {
                Snackbar {
                    Text(mensaje)
                }
            }
        }

        // Snackbar para mensajes de error
        state.errorMessage?.let { error ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.BottomCenter
            ) {
                Snackbar(
                    action = {
                        TextButton(onClick = { viewModel.limpiarMensajes() }) {
                            Text("OK")
                        }
                    }
                ) {
                    Text(error)
                }
            }
        }
    }
}

// ==================== TABS PARA CADA ENTIDAD ====================

/**
 * Tab que muestra la lista de Marcas.
 * Cada marca se muestra en un Card con botones para editar y eliminar.
 *
 * @param marcas Lista de marcas a mostrar
 * @param onEditar Callback al presionar el botón editar
 * @param onEliminar Callback al presionar el botón eliminar
 */
@Composable
private fun MarcasTab(
    marcas: List<Marca>,
    onEditar: (Marca) -> Unit,
    onEliminar: (Marca) -> Unit
) {
    if (marcas.isEmpty()) {
        // Mensaje cuando no hay datos
        EmptyState(mensaje = "No hay marcas registradas")
    } else {
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(marcas, key = { it.idMarca }) { marca ->
                MarcaCard(
                    marca = marca,
                    onEditar = { onEditar(marca) },
                    onEliminar = { onEliminar(marca) }
                )
            }
        }
    }
}

/**
 * Tab que muestra la lista de Categorías.
 */
@Composable
private fun CategoriasTab(
    categorias: List<Categoria>,
    onEditar: (Categoria) -> Unit,
    onEliminar: (Categoria) -> Unit
) {
    if (categorias.isEmpty()) {
        EmptyState(mensaje = "No hay categorías registradas")
    } else {
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(categorias, key = { it.idCategoria }) { categoria ->
                CategoriaCard(
                    categoria = categoria,
                    onEditar = { onEditar(categoria) },
                    onEliminar = { onEliminar(categoria) }
                )
            }
        }
    }
}

/**
 * Tab que muestra la lista de Géneros.
 */
@Composable
private fun GenerosTab(
    generos: List<Genero>,
    onEditar: (Genero) -> Unit,
    onEliminar: (Genero) -> Unit
) {
    if (generos.isEmpty()) {
        EmptyState(mensaje = "No hay géneros registrados")
    } else {
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(generos, key = { it.idGenero }) { genero ->
                GeneroCard(
                    genero = genero,
                    onEditar = { onEditar(genero) },
                    onEliminar = { onEliminar(genero) }
                )
            }
        }
    }
}

/**
 * Tab que muestra la lista de Tipos de Producto.
 */
@Composable
private fun TiposProductoTab(
    tipos: List<TipoProducto>,
    onEditar: (TipoProducto) -> Unit,
    onEliminar: (TipoProducto) -> Unit
) {
    if (tipos.isEmpty()) {
        EmptyState(mensaje = "No hay tipos de producto registrados")
    } else {
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(tipos, key = { it.idTipoProducto }) { tipo ->
                TipoProductoCard(
                    tipo = tipo,
                    onEditar = { onEditar(tipo) },
                    onEliminar = { onEliminar(tipo) }
                )
            }
        }
    }
}

// ==================== CARDS PARA MOSTRAR ITEMS ====================

/**
 * Card que muestra una Marca con sus datos y botones de acción.
 *
 * Muestra:
 * - Nombre de la marca (destacado)
 * - País de origen (si existe)
 * - Descripción (si existe)
 * - Botones: Editar y Eliminar
 */
@Composable
private fun MarcaCard(
    marca: Marca,
    onEditar: () -> Unit,
    onEliminar: () -> Unit
) {
    var mostrarDialogoEliminar by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icono decorativo
            Icon(
                Icons.Default.Star,
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Información de la marca
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = marca.nombreMarca,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                marca.paisOrigen?.let {
                    Text(
                        text = "País: $it",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                marca.descripcion?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2
                    )
                }
            }

            // Botones de acción
            Row {
                IconButton(onClick = onEditar) {
                    Icon(Icons.Default.Edit, "Editar")
                }
                IconButton(onClick = { mostrarDialogoEliminar = true }) {
                    Icon(
                        Icons.Default.Delete,
                        "Eliminar",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }

    // Dialog de confirmación para eliminar
    if (mostrarDialogoEliminar) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoEliminar = false },
            title = { Text("Eliminar Marca") },
            text = { Text("¿Estás seguro de eliminar '${marca.nombreMarca}'?") },
            confirmButton = {
                Button(
                    onClick = {
                        onEliminar()
                        mostrarDialogoEliminar = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogoEliminar = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

/**
 * Card que muestra una Categoría.
 * Similar a MarcaCard pero sin el campo país de origen.
 */
@Composable
private fun CategoriaCard(
    categoria: Categoria,
    onEditar: () -> Unit,
    onEliminar: () -> Unit
) {
    var mostrarDialogoEliminar by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Category,
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = categoria.nombreCategoria,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                categoria.descripcion?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2
                    )
                }
            }

            Row {
                IconButton(onClick = onEditar) {
                    Icon(Icons.Default.Edit, "Editar")
                }
                IconButton(onClick = { mostrarDialogoEliminar = true }) {
                    Icon(
                        Icons.Default.Delete,
                        "Eliminar",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }

    if (mostrarDialogoEliminar) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoEliminar = false },
            title = { Text("Eliminar Categoría") },
            text = { Text("¿Estás seguro de eliminar '${categoria.nombreCategoria}'?") },
            confirmButton = {
                Button(
                    onClick = {
                        onEliminar()
                        mostrarDialogoEliminar = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogoEliminar = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

/**
 * Card que muestra un Género.
 * Más simple ya que solo tiene nombre.
 */
@Composable
private fun GeneroCard(
    genero: Genero,
    onEditar: () -> Unit,
    onEliminar: () -> Unit
) {
    var mostrarDialogoEliminar by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Person,
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = genero.nombreGenero,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )

            Row {
                IconButton(onClick = onEditar) {
                    Icon(Icons.Default.Edit, "Editar")
                }
                IconButton(onClick = { mostrarDialogoEliminar = true }) {
                    Icon(
                        Icons.Default.Delete,
                        "Eliminar",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }

    if (mostrarDialogoEliminar) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoEliminar = false },
            title = { Text("Eliminar Género") },
            text = { Text("¿Estás seguro de eliminar '${genero.nombreGenero}'?") },
            confirmButton = {
                Button(
                    onClick = {
                        onEliminar()
                        mostrarDialogoEliminar = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogoEliminar = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

/**
 * Card que muestra un Tipo de Producto.
 */
@Composable
private fun TipoProductoCard(
    tipo: TipoProducto,
    onEditar: () -> Unit,
    onEliminar: () -> Unit
) {
    var mostrarDialogoEliminar by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.LocalOffer,
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = tipo.nombreTipo,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                tipo.descripcion?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2
                    )
                }
            }

            Row {
                IconButton(onClick = onEditar) {
                    Icon(Icons.Default.Edit, "Editar")
                }
                IconButton(onClick = { mostrarDialogoEliminar = true }) {
                    Icon(
                        Icons.Default.Delete,
                        "Eliminar",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }

    if (mostrarDialogoEliminar) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoEliminar = false },
            title = { Text("Eliminar Tipo de Producto") },
            text = { Text("¿Estás seguro de eliminar '${tipo.nombreTipo}'?") },
            confirmButton = {
                Button(
                    onClick = {
                        onEliminar()
                        mostrarDialogoEliminar = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogoEliminar = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

// ==================== FORMULARIO PARA CREAR/EDITAR ====================

/**
 * Dialog con formulario para crear o editar datos maestros.
 *
 * El formulario se adapta dinámicamente según el tipo de entidad:
 * - MARCA: nombre, descripción, país de origen
 * - CATEGORIA: nombre, descripción
 * - GENERO: solo nombre
 * - TIPO_PRODUCTO: nombre, descripción
 *
 * @param tipo Tipo de entidad que se está editando
 * @param itemEditando Item a editar (null si es nuevo)
 * @param onGuardar Callback al guardar con los datos del formulario
 * @param onCancelar Callback al cancelar
 */
@Composable
private fun FormularioDatoMaestro(
    tipo: TipoDatoMaestro,
    itemEditando: Any?,
    onGuardar: (TipoDatoMaestro, String, String?, String?) -> Unit,
    onCancelar: () -> Unit
) {
    // Estados locales para los campos del formulario
    // Se inicializan con los datos del item si se está editando
    var nombre by remember {
        mutableStateOf(
            when (itemEditando) {
                is Marca -> itemEditando.nombreMarca
                is Categoria -> itemEditando.nombreCategoria
                is Genero -> itemEditando.nombreGenero
                is TipoProducto -> itemEditando.nombreTipo
                else -> ""
            }
        )
    }

    var descripcion by remember {
        mutableStateOf(
            when (itemEditando) {
                is Marca -> itemEditando.descripcion ?: ""
                is Categoria -> itemEditando.descripcion ?: ""
                is TipoProducto -> itemEditando.descripcion ?: ""
                else -> ""
            }
        )
    }

    var paisOrigen by remember {
        mutableStateOf(
            when (itemEditando) {
                is Marca -> itemEditando.paisOrigen ?: ""
                else -> ""
            }
        )
    }

    // Determinar el título del dialog según el contexto
    val titulo = if (itemEditando != null) {
        "Editar ${
            when (tipo) {
                TipoDatoMaestro.MARCA -> "Marca"
                TipoDatoMaestro.CATEGORIA -> "Categoría"
                TipoDatoMaestro.GENERO -> "Género"
                TipoDatoMaestro.TIPO_PRODUCTO -> "Tipo de Producto"
            }
        }"
    } else {
        "Nueva ${
            when (tipo) {
                TipoDatoMaestro.MARCA -> "Marca"
                TipoDatoMaestro.CATEGORIA -> "Categoría"
                TipoDatoMaestro.GENERO -> "Género"
                TipoDatoMaestro.TIPO_PRODUCTO -> "Tipo de Producto"
            }
        }"
    }

    AlertDialog(
        onDismissRequest = onCancelar,
        title = { Text(titulo) },
        text = {
            // Formulario con scroll para campos largos
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Campo nombre (requerido para todos)
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = {
                        Text(
                            when (tipo) {
                                TipoDatoMaestro.MARCA -> "Nombre de la Marca *"
                                TipoDatoMaestro.CATEGORIA -> "Nombre de la Categoría *"
                                TipoDatoMaestro.GENERO -> "Nombre del Género *"
                                TipoDatoMaestro.TIPO_PRODUCTO -> "Nombre del Tipo *"
                            }
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                // Campo descripción (solo para Marca, Categoría y Tipo)
                if (tipo != TipoDatoMaestro.GENERO) {
                    OutlinedTextField(
                        value = descripcion,
                        onValueChange = { descripcion = it },
                        label = { Text("Descripción") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2,
                        maxLines = 3
                    )
                }

                // Campo país de origen (solo para Marca)
                if (tipo == TipoDatoMaestro.MARCA) {
                    OutlinedTextField(
                        value = paisOrigen,
                        onValueChange = { paisOrigen = it },
                        label = { Text("País de Origen") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onGuardar(
                        tipo,
                        nombre,
                        descripcion.ifBlank { null },
                        paisOrigen.ifBlank { null }
                    )
                },
                // Deshabilitar si el nombre está vacío (campo requerido)
                enabled = nombre.isNotBlank()
            ) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onCancelar) {
                Text("Cancelar")
            }
        }
    )
}

/**
 * Componente que muestra un estado vacío cuando no hay datos.
 * Se usa en cada tab cuando la lista está vacía.
 *
 * @param mensaje Mensaje a mostrar
 */
@Composable
private fun EmptyState(mensaje: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                Icons.Default.Inbox,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = mensaje,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "Toca el botón + para agregar",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
