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

data class LoginUiState(
    val correo: String = "",
    val contrasena: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val usuario: Usuario? = null,
    val loginExitoso: Boolean = false
)

class LoginViewModel : ViewModel() {

    private val repository = RinconRepository()

    private val _state = MutableStateFlow(LoginUiState())
    val state: StateFlow<LoginUiState> = _state.asStateFlow()

    fun onCorreoChange(value: String) {
        _state.update { it.copy(correo = value, errorMessage = null) }
    }

    fun onContrasenaChange(value: String) {
        _state.update { it.copy(contrasena = value, errorMessage = null) }
    }

    fun login() {
        val s = _state.value
        if (s.correo.isBlank() || s.contrasena.isBlank()) {
            _state.update { it.copy(errorMessage = "Completa todos los campos") }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }

            // Paso 1: Login básico
            val loginResult = repository.login(s.correo, s.contrasena)

            if (loginResult.isSuccess) {
                val loginResponse = loginResult.getOrNull()

                if (loginResponse?.autenticado == true) {
                    // Paso 2: Obtener usuario completo con roles
                    val usuarioResult = repository.buscarUsuarioPorCorreo(s.correo)

                    if (usuarioResult.isSuccess) {
                        val usuario = usuarioResult.getOrNull()
                        if (usuario != null) {
                            _state.update {
                                it.copy(
                                    isLoading = false,
                                    usuario = usuario,
                                    loginExitoso = true
                                )
                            }
                        } else {
                            _state.update {
                                it.copy(
                                    isLoading = false,
                                    errorMessage = "Usuario no encontrado"
                                )
                            }
                        }
                    } else {
                        _state.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = "Error al obtener datos del usuario"
                            )
                        }
                    }
                } else {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = loginResponse?.mensaje ?: "Credenciales incorrectas"
                        )
                    }
                }
            } else {
                _state.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Error de conexión: ${loginResult.exceptionOrNull()?.message}"
                    )
                }
            }
        }
    }

    fun limpiarError() {
        _state.update { it.copy(errorMessage = null) }
    }

    fun logout() {
        _state.update { LoginUiState() }
    }
}