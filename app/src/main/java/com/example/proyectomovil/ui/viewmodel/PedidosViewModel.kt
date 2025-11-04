package com.example.proyectomovil.ui.viewmodel



import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectomovil.data.model.Pedido
import com.example.proyectomovil.data.repository.RinconRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PedidosUiState(
    val pedidos: List<Pedido> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class PedidosViewModel : ViewModel() {

    private val repository = RinconRepository()

    private val _state = MutableStateFlow(PedidosUiState())
    val state: StateFlow<PedidosUiState> = _state.asStateFlow()

    fun cargarPedidos(clienteId: Long) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }

            val result = repository.getPedidosCliente(clienteId)

            if (result.isSuccess) {
                val pedidos = result.getOrNull() ?: emptyList()
                _state.update {
                    it.copy(
                        pedidos = pedidos.sortedByDescending { pedido -> pedido.fechaPedido },
                        isLoading = false
                    )
                }
            } else {
                _state.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Error al cargar pedidos: ${result.exceptionOrNull()?.message}"
                    )
                }
            }
        }
    }

    fun limpiarError() {
        _state.update { it.copy(errorMessage = null) }
    }
}