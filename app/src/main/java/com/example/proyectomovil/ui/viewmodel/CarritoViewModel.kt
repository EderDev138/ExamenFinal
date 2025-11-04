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

data class CarritoUiState(
    val items: List<Carrito> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val subtotal: Double = 0.0,
    val iva: Double = 0.0,
    val total: Double = 0.0
)

class CarritoViewModel : ViewModel() {

    private val repository = RinconRepository()

    private val _state = MutableStateFlow(CarritoUiState())
    val state: StateFlow<CarritoUiState> = _state.asStateFlow()

    fun cargarCarrito(clienteId: Long) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }

            val result = repository.getCarrito(clienteId)

            if (result.isSuccess) {
                val items = result.getOrNull() ?: emptyList()
                val subtotal = items.sumOf { (it.producto?.precio ?: 0.0) * it.cantidad }
                val iva = subtotal * 0.19
                val total = subtotal + iva

                _state.update {
                    it.copy(
                        items = items,
                        subtotal = subtotal,
                        iva = iva,
                        total = total,
                        isLoading = false
                    )
                }
            } else {
                _state.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Error al cargar carrito: ${result.exceptionOrNull()?.message}"
                    )
                }
            }
        }
    }

    fun agregarProducto(productoId: Long, clienteId: Long, cantidad: Int = 1) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }

            // Validar stock primero
            val productoResult = repository.getProducto(productoId)
            if (productoResult.isFailure) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Error al validar stock"
                    )
                }
                return@launch
            }

            val producto = productoResult.getOrNull()
            if (producto == null || producto.stock < cantidad) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Stock insuficiente"
                    )
                }
                return@launch
            }

            // Verificar si ya existe en el carrito
            val carritoActual = repository.getCarrito(clienteId).getOrNull() ?: emptyList()
            val itemExistente = carritoActual.find { it.producto?.idProducto == productoId }

            if (itemExistente != null) {
                // Eliminar y volver a agregar con nueva cantidad
                repository.eliminarItemCarrito(itemExistente.id)
            }

            // Agregar al carrito
            val request = CarritoRequest(
                cliente = ClienteRef(clienteId),
                producto = ProductoRef(productoId),
                cantidad = cantidad
            )

            val result = repository.agregarAlCarrito(request)

            if (result.isSuccess) {
                cargarCarrito(clienteId)
            } else {
                _state.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Error al agregar: ${result.exceptionOrNull()?.message}"
                    )
                }
            }
        }
    }

    fun eliminarItem(itemId: Long, clienteId: Long) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }

            val result = repository.eliminarItemCarrito(itemId)

            if (result.isSuccess) {
                cargarCarrito(clienteId)
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

    fun limpiarError() {
        _state.update { it.copy(errorMessage = null) }
    }
}