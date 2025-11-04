package com.example.proyectomovil.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectomovil.data.model.*
import com.example.proyectomovil.data.repository.RinconRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class GestionProductosUiState(
    val productos: List<Producto> = emptyList(),
    val marcas: List<Marca> = emptyList(),
    val categorias: List<Categoria> = emptyList(),
    val generos: List<Genero> = emptyList(),
    val tiposProducto: List<TipoProducto> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val productoEditando: Producto? = null,
    val exitoMessage: String? = null
)

class GestionProductosViewModel : ViewModel() {

    private val repository = RinconRepository()

    private val _state = MutableStateFlow(GestionProductosUiState())
    val state: StateFlow<GestionProductosUiState> = _state.asStateFlow()

    init {
        cargarDatos()
    }

    fun cargarDatos() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }

            try {
                val productosResult = repository.getProductos()
                val productos = productosResult.getOrNull() ?: emptyList()

                val marcasResult = repository.getMarcas()
                val marcas = marcasResult.getOrNull() ?: emptyList()

                val categoriasResult = repository.getCategorias()
                val categorias = categoriasResult.getOrNull() ?: emptyList()

                val generosResult = repository.getGeneros()
                val generos = generosResult.getOrNull() ?: emptyList()

                val tiposResult = repository.getTiposProducto()
                val tipos = tiposResult.getOrNull() ?: emptyList()

                _state.update {
                    it.copy(
                        productos = productos,
                        marcas = marcas,
                        categorias = categorias,
                        generos = generos,
                        tiposProducto = tipos,
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

    fun seleccionarProducto(producto: Producto) {
        _state.update { it.copy(productoEditando = producto) }
    }

    fun limpiarSeleccion() {
        _state.update { it.copy(productoEditando = null) }
    }

    fun crearProducto(request: ProductoRequest) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }

            val result = repository.crearProducto(request)

            if (result.isSuccess) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        exitoMessage = "Producto creado exitosamente",
                        productoEditando = null
                    )
                }
                cargarDatos()
            } else {
                _state.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Error al crear: ${result.exceptionOrNull()?.message}"
                    )
                }
            }
        }
    }

    fun actualizarProducto(id: Long, request: ProductoRequest) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }

            val result = repository.actualizarProducto(id, request)

            if (result.isSuccess) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        exitoMessage = "Producto actualizado exitosamente",
                        productoEditando = null
                    )
                }
                cargarDatos()
            } else {
                _state.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Error al actualizar: ${result.exceptionOrNull()?.message}"
                    )
                }
            }
        }
    }

    fun eliminarProducto(id: Long) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }

            val result = repository.eliminarProducto(id)

            if (result.isSuccess) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        exitoMessage = "Producto eliminado exitosamente"
                    )
                }
                cargarDatos()
            } else {
                _state.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Error al eliminar: ${result.exceptionOrNull()?.message}"
                    )
                }
            }
        }
    }

    fun limpiarMensajes() {
        _state.update { it.copy(errorMessage = null, exitoMessage = null) }
    }
}
