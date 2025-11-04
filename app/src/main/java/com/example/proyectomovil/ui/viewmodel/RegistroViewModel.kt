package com.example.proyectomovil.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectomovil.data.model.Rol
import com.example.proyectomovil.data.model.Usuario
import com.example.proyectomovil.data.repository.RinconRepository
import com.example.proyectomovil.utils.validarEmail
import com.example.proyectomovil.utils.*
import com.example.proyectomovil.data.model.ClienteRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter

data class RegistroUiState(
    val rut: String = "",
    val primerNombre: String = "",
    val segundoNombre: String = "",
    val primerApellido: String = "",
    val segundoApellido: String = "",
    val email: String = "",
    val contrasena: String = "",
    val confirmarContrasena: String = "",
    val fechaNacimiento: String = "",
    val telefono: String = "",
    val direccion: String = "",
    val comuna: String = "",
    val region: String = "Metropolitana",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val registroExitoso: Boolean = false
)

class RegistroViewModel : ViewModel() {

    private val repository = RinconRepository()

    private val _state = MutableStateFlow(RegistroUiState())
    val state: StateFlow<RegistroUiState> = _state.asStateFlow()

    fun onRutChange(value: String) {
        _state.update { it.copy(rut = value, errorMessage = null) }
    }

    fun onPrimerNombreChange(value: String) {
        _state.update { it.copy(primerNombre = value, errorMessage = null) }
    }

    fun onSegundoNombreChange(value: String) {
        _state.update { it.copy(segundoNombre = value, errorMessage = null) }
    }

    fun onPrimerApellidoChange(value: String) {
        _state.update { it.copy(primerApellido = value, errorMessage = null) }
    }

    fun onSegundoApellidoChange(value: String) {
        _state.update { it.copy(segundoApellido = value, errorMessage = null) }
    }

    fun onEmailChange(value: String) {
        _state.update { it.copy(email = value, errorMessage = null) }
    }

    fun onContrasenaChange(value: String) {
        _state.update { it.copy(contrasena = value, errorMessage = null) }
    }

    fun onConfirmarContrasenaChange(value: String) {
        _state.update { it.copy(confirmarContrasena = value, errorMessage = null) }
    }

    fun onFechaNacimientoChange(value: String) {
        _state.update { it.copy(fechaNacimiento = value, errorMessage = null) }
    }

    fun onTelefonoChange(value: String) {
        _state.update { it.copy(telefono = value, errorMessage = null) }
    }

    fun onDireccionChange(value: String) {
        _state.update { it.copy(direccion = value, errorMessage = null) }
    }

    fun onComunaChange(value: String) {
        _state.update { it.copy(comuna = value, errorMessage = null) }
    }

    fun onRegionChange(value: String) {
        _state.update { it.copy(region = value, errorMessage = null) }
    }

    fun registrar() {
        val s = _state.value

        // Validaciones
        if (s.rut.isBlank() || s.primerNombre.isBlank() || s.primerApellido.isBlank() ||
            s.email.isBlank() || s.contrasena.isBlank() || s.fechaNacimiento.isBlank() ||
            s.telefono.isBlank() || s.direccion.isBlank() || s.comuna.isBlank()
        ) {
            _state.update { it.copy(errorMessage = "Completa todos los campos obligatorios") }
            return
        }

        if (!validarRut(s.rut)) {
            _state.update { it.copy(errorMessage = "RUT inválido") }
            return
        }

        if (!validarEmail(s.email)) {
            _state.update { it.copy(errorMessage = "Email inválido") }
            return
        }

        if (s.contrasena.length < 6) {
            _state.update { it.copy(errorMessage = "La contraseña debe tener al menos 6 caracteres") }
            return
        }

        if (s.contrasena != s.confirmarContrasena) {
            _state.update { it.copy(errorMessage = "Las contraseñas no coinciden") }
            return
        }

        // Validar edad >= 18
        try {
            val fechaNac = LocalDate.parse(s.fechaNacimiento, DateTimeFormatter.ISO_LOCAL_DATE)
            val edad = Period.between(fechaNac, LocalDate.now()).years
            if (edad < 18) {
                _state.update { it.copy(errorMessage = "Debes ser mayor de 18 años") }
                return
            }
        } catch (e: Exception) {
            _state.update { it.copy(errorMessage = "Fecha de nacimiento inválida") }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }

            val clienteRequest = ClienteRequest(
                usuario = Usuario(
                    nombre = s.primerNombre,
                    apellido = s.primerApellido,
                    correo = s.email,
                    contrasena = s.contrasena,
                    activo = true,
                    roles = listOf(Rol(idRol = 3, nombreRol = "CLIENTE"))
                ),
                rut = s.rut,
                primerNombre = s.primerNombre,
                segundoNombre = s.segundoNombre.ifBlank { null },
                primerApellido = s.primerApellido,
                segundoApellido = s.segundoApellido.ifBlank { null },
                fechaNacimiento = s.fechaNacimiento,
                direccion = s.direccion,
                comuna = s.comuna,
                region = s.region,
                telefono = s.telefono
            )

            val result = repository.registrarCliente(clienteRequest)

            if (result.isSuccess) {
                _state.update { it.copy(isLoading = false, registroExitoso = true) }
            } else {
                _state.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Error al registrar: ${result.exceptionOrNull()?.message}"
                    )
                }
            }
        }
    }

    fun limpiarError() {
        _state.update { it.copy(errorMessage = null) }
    }
}