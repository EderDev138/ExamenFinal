package com.example.proyectomovil.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectomovil.data.model.Categoria
import com.example.proyectomovil.data.model.Genero
import com.example.proyectomovil.data.model.Marca
import com.example.proyectomovil.data.model.Producto
import com.example.proyectomovil.data.repository.RinconRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ProductosUiState(
    val productos: List<Producto> = emptyList(),
    val productosOriginales: List<Producto> = emptyList(),
    val categorias: List<Categoria> = emptyList(),
    val marcas: List<Marca> = emptyList(),
    val generos: List<Genero> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val busqueda: String = "",
    val categoriaSeleccionada: Long? = null,
    val marcaSeleccionada: Long? = null,
    val generoSeleccionado: Long? = null
)

class ProductosViewModel : ViewModel() {

    private val repository = RinconRepository()

    private val _state = MutableStateFlow(ProductosUiState())
    val state: StateFlow<ProductosUiState> = _state.asStateFlow()

    init {
        cargarDatos()
    }

    fun cargarDatos() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }

            try {
                // Cargar productos
                val productosResult = repository.getProductos()
                val productos = if (productosResult.isSuccess) {
                    productosResult.getOrNull()?.filter { it.activo && it.stock > 0 }
                        ?: emptyList()
                } else emptyList()

                // Cargar datos maestros
                val categoriasResult = repository.getCategorias()
                val categorias = categoriasResult.getOrNull() ?: emptyList()

                val marcasResult = repository.getMarcas()
                val marcas = marcasResult.getOrNull() ?: emptyList()

                val generosResult = repository.getGeneros()
                val generos = generosResult.getOrNull() ?: emptyList()

                _state.update {
                    it.copy(
                        productos = productos,
                        productosOriginales = productos,
                        categorias = categorias,
                        marcas = marcas,
                        generos = generos,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Error al cargar datos: ${e.message}"
                    )
                }
            }
        }
    }

    fun onBusquedaChange(value: String) {
        _state.update { it.copy(busqueda = value) }
        filtrarProductos()
    }

    fun seleccionarCategoria(id: Long?) {
        _state.update { it.copy(categoriaSeleccionada = id) }
        filtrarProductos()
    }

    fun seleccionarMarca(id: Long?) {
        _state.update { it.copy(marcaSeleccionada = id) }
        filtrarProductos()
    }

    fun seleccionarGenero(id: Long?) {
        _state.update { it.copy(generoSeleccionado = id) }
        filtrarProductos()
    }

    private fun filtrarProductos() {
        val s = _state.value
        var productosFiltrados = s.productosOriginales

        // Filtrar por búsqueda
        if (s.busqueda.isNotBlank()) {
            productosFiltrados = productosFiltrados.filter {
                it.nombreProducto.contains(s.busqueda, ignoreCase = true) ||
                        it.descripcion?.contains(s.busqueda, ignoreCase = true) == true
            }
        }

        // Filtrar por categoría
        if (s.categoriaSeleccionada != null) {
            productosFiltrados = productosFiltrados.filter {
                it.categoria?.idCategoria == s.categoriaSeleccionada
            }
        }

        // Filtrar por marca
        if (s.marcaSeleccionada != null) {
            productosFiltrados = productosFiltrados.filter {
                it.marca?.idMarca == s.marcaSeleccionada
            }
        }

        // Filtrar por género
        if (s.generoSeleccionado != null) {
            productosFiltrados = productosFiltrados.filter {
                it.genero?.idGenero == s.generoSeleccionado
            }
        }

        _state.update { it.copy(productos = productosFiltrados) }
    }

    fun limpiarFiltros() {
        _state.update {
            it.copy(
                busqueda = "",
                categoriaSeleccionada = null,
                marcaSeleccionada = null,
                generoSeleccionado = null,
                productos = it.productosOriginales
            )
        }
    }

    fun limpiarError() {
        _state.update { it.copy(errorMessage = null) }
    }
}