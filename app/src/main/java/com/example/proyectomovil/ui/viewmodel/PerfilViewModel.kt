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

/**
 * ViewModel para la pantalla de Perfil de Usuario.
 *
 * Responsabilidades:
 * - Cargar los datos del usuario desde el backend por su ID
 * - Mantener el estado de carga y error
 * - Proporcionar los datos del usuario a la UI
 *
 * Patrón de diseño: MVVM (Model-View-ViewModel)
 * - Separa la lógica de negocio de la UI
 * - Usa StateFlow para comunicación reactiva con la UI
 * - Maneja operaciones asíncronas con Coroutines
 */
class PerfilViewModel : ViewModel() {

    // Repositorio para acceder a los datos del backend
    private val repository = RinconRepository()

    /**
     * Estado interno mutable del perfil.
     * Solo este ViewModel puede modificarlo (private).
     */
    private val _state = MutableStateFlow(PerfilState())

    /**
     * Estado público de solo lectura para la UI.
     * La UI observa este StateFlow y se recompone cuando cambia.
     */
    val state: StateFlow<PerfilState> = _state.asStateFlow()

    /**
     * Carga los datos del usuario desde el backend.
     *
     * Flujo de ejecución:
     * 1. Marca isLoading = true para mostrar indicador de carga
     * 2. Llama al repositorio para obtener el usuario por ID
     * 3. Si es exitoso: actualiza el estado con el usuario
     * 4. Si falla: actualiza el estado con el mensaje de error
     * 5. Marca isLoading = false
     *
     * @param userId ID del usuario a cargar
     */
    fun cargarUsuario(userId: Long) {
        // Ejecutar en el scope del ViewModel (se cancela automáticamente si el ViewModel se destruye)
        viewModelScope.launch {
            // Paso 1: Mostrar indicador de carga
            _state.update { it.copy(isLoading = true, errorMessage = null) }

            // Paso 2: Llamar al repositorio (operación asíncrona)
            val result = repository.getUsuario(userId)

            // Paso 3 y 4: Actualizar estado según el resultado
            if (result.isSuccess) {
                // Éxito: tenemos el usuario
                val usuario = result.getOrNull()
                _state.update {
                    it.copy(
                        usuario = usuario,
                        isLoading = false,
                        errorMessage = null
                    )
                }
            } else {
                // Error: mostrar mensaje
                val error = result.exceptionOrNull()?.message ?: "Error desconocido"
                _state.update {
                    it.copy(
                        usuario = null,
                        isLoading = false,
                        errorMessage = "Error al cargar perfil: $error"
                    )
                }
            }
        }
    }

    /**
     * Limpia el mensaje de error.
     * Útil después de mostrar el error al usuario.
     */
    fun limpiarError() {
        _state.update { it.copy(errorMessage = null) }
    }
}

/**
 * Data class que representa el estado de la pantalla de Perfil.
 *
 * Uso de data class:
 * - Inmutable (val en lugar de var) para evitar modificaciones accidentales
 * - Método copy() automático para crear copias modificadas
 * - Facilita el testing al ser un POJO (Plain Old Java Object)
 *
 * @property usuario Usuario cargado (null si aún no se ha cargado o hubo error)
 * @property isLoading Indica si se está cargando información del backend
 * @property errorMessage Mensaje de error si algo falló (null si todo va bien)
 */
data class PerfilState(
    val usuario: Usuario? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
