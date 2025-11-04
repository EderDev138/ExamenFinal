package com.example.proyectomovil.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectomovil.data.model.Usuario
import com.example.proyectomovil.data.repository.RinconRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class GestionUsuariosUiState(
    val usuarios: List<Usuario> = emptyList(),
    val usuariosPendientes: List<Usuario> = emptyList(),
    val usuariosActivos: List<Usuario> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val exitoMessage: String? = null
)

class GestionUsuariosViewModel : ViewModel() {

    private val repository = RinconRepository()

    private val _state = MutableStateFlow(GestionUsuariosUiState())
    val state: StateFlow<GestionUsuariosUiState> = _state.asStateFlow()

    init {
        cargarUsuarios()
    }

    fun cargarUsuarios() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }

            val result = repository.getUsuarios()

            if (result.isSuccess) {
                val usuarios = result.getOrNull() ?: emptyList()

                // Filtrar usuarios pendientes (inactivos con rol ENCARGADO)
                val pendientes = usuarios.filter { usuario ->
                    !usuario.activo && usuario.roles.any { it.nombreRol == "ENCARGADO" }
                }

                // Filtrar usuarios activos
                val activos = usuarios.filter { it.activo }

                _state.update {
                    it.copy(
                        usuarios = usuarios,
                        usuariosPendientes = pendientes,
                        usuariosActivos = activos,
                        isLoading = false
                    )
                }
            } else {
                _state.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Error al cargar usuarios: ${result.exceptionOrNull()?.message}"
                    )
                }
            }
        }
    }

    fun aprobarUsuario(usuario: Usuario) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }

            val usuarioActualizado = usuario.copy(activo = true)
            val result = repository.actualizarUsuario(usuario.idUsuario, usuarioActualizado)

            if (result.isSuccess) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        exitoMessage = "Usuario aprobado exitosamente"
                    )
                }
                cargarUsuarios()
            } else {
                _state.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Error al aprobar: ${result.exceptionOrNull()?.message}"
                    )
                }
            }
        }
    }

    fun desactivarUsuario(usuario: Usuario) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }

            val usuarioActualizado = usuario.copy(activo = false)
            val result = repository.actualizarUsuario(usuario.idUsuario, usuarioActualizado)

            if (result.isSuccess) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        exitoMessage = "Usuario desactivado"
                    )
                }
                cargarUsuarios()
            } else {
                _state.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Error al desactivar: ${result.exceptionOrNull()?.message}"
                    )
                }
            }
        }
    }

    fun limpiarMensajes() {
        _state.update { it.copy(errorMessage = null, exitoMessage = null) }
    }
}