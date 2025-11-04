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

    /**
     * Carga el carrito del cliente desde el backend.
     *
     * ✅ FLUJO:
     * 1. Hace GET a /carrito/cliente/{clienteId}
     * 2. El backend devuelve todos los items activos del carrito
     * 3. Calcula subtotal, IVA y total
     * 4. Actualiza el estado para que la UI se recomponga
     *
     * @param clienteId ID del cliente (debe ser > 0, sino no hay carrito)
     */
    fun cargarCarrito(clienteId: Long) {
        viewModelScope.launch {
            // ✅ Validación: clienteId debe ser válido
            if (clienteId <= 0) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "ID de cliente inválido",
                        items = emptyList()
                    )
                }
                return@launch
            }

            _state.update { it.copy(isLoading = true, errorMessage = null) }

            val result = repository.getCarrito(clienteId)

            if (result.isSuccess) {
                val items = result.getOrNull() ?: emptyList()
                // ✅ Calcular totales basados en los items actuales
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

    /**
     * Agrega un producto al carrito del cliente.
     *
     * ✅ FLUJO:
     * 1. Valida que el producto tenga stock suficiente
     * 2. Verifica si el producto ya está en el carrito
     * 3. Si ya existe, lo elimina primero (para actualizar la cantidad)
     * 4. Hace POST a /carrito con los datos del item
     * 5. Recarga el carrito completo desde el backend
     *
     * 📌 IMPORTANTE: Este método NO actualiza el stock del producto.
     *    El stock se actualiza cuando se crea el pedido (checkout).
     *
     * @param productoId ID del producto a agregar
     * @param clienteId ID del cliente (debe estar logueado)
     * @param cantidad Cantidad de unidades a agregar (default 1)
     */
    fun agregarProducto(productoId: Long, clienteId: Long, cantidad: Int = 1) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }

            // ✅ PASO 1: Validar stock primero
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

            // ✅ PASO 2: Verificar si ya existe en el carrito
            val carritoActual = repository.getCarrito(clienteId).getOrNull() ?: emptyList()
            val itemExistente = carritoActual.find { it.producto?.idProducto == productoId }

            if (itemExistente != null) {
                // ✅ PASO 3: Eliminar y volver a agregar con nueva cantidad
                // Esto evita duplicados en el carrito
                repository.eliminarItemCarrito(itemExistente.id)
            }

            // ✅ PASO 4: Agregar al carrito
            val request = CarritoRequest(
                cliente = ClienteRef(clienteId),
                producto = ProductoRef(productoId),
                cantidad = cantidad
            )

            val result = repository.agregarAlCarrito(request)

            if (result.isSuccess) {
                // ✅ PASO 5: Recargar carrito desde el backend
                // Esto asegura que tenemos los datos más actualizados
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