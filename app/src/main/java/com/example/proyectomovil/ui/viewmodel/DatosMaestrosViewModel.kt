
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

/**
 * ViewModel para la pantalla de Gestión de Datos Maestros.
 *
 * Este ViewModel maneja las 4 entidades de datos maestros de forma genérica:
 * - Marcas (Dior, Chanel, etc.)
 * - Categorías (Florales, Cítricas, etc.)
 * - Géneros (Masculino, Femenino, Unisex)
 * - Tipos de Producto (Eau de Parfum, Eau de Toilette, etc.)
 *
 * Responsabilidades:
 * - Cargar las listas de datos maestros desde el backend
 * - Crear nuevos registros
 * - Editar registros existentes
 * - Eliminar registros
 * - Mantener el estado de carga, error y éxito
 * - Gestionar el estado del formulario (agregar/editar)
 *
 * Patrón: MVVM con Repository Pattern
 */
class DatosMaestrosViewModel : ViewModel() {

    // Repositorio para acceso a datos
    private val repository = RinconRepository()

    /**
     * Estado interno mutable.
     * Solo este ViewModel puede modificarlo.
     */
    private val _state = MutableStateFlow(DatosMaestrosState())

    /**
     * Estado público de solo lectura para la UI.
     * La UI observa este StateFlow y se recompone automáticamente cuando cambia.
     */
    val state: StateFlow<DatosMaestrosState> = _state.asStateFlow()

    /**
     * Inicialización del ViewModel.
     * Se ejecuta automáticamente al crear el ViewModel.
     * Carga todos los datos maestros al iniciar.
     */
    init {
        cargarTodosDatos()
    }

    /**
     * Carga todas las entidades de datos maestros desde el backend.
     *
     * Este método se llama al iniciar el ViewModel y cuando se necesita
     * refrescar los datos después de una operación CRUD.
     */
    fun cargarTodosDatos() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            // Cargar todas las entidades en paralelo usando coroutines
            // Esto es más eficiente que cargarlas secuencialmente
            val marcasResult = repository.getMarcas()
            val categoriasResult = repository.getCategorias()
            val generosResult = repository.getGeneros()
            val tiposResult = repository.getTiposProducto()

            // Actualizar el estado con todos los resultados
            _state.update {
                it.copy(
                    marcas = marcasResult.getOrNull() ?: emptyList(),
                    categorias = categoriasResult.getOrNull() ?: emptyList(),
                    generos = generosResult.getOrNull() ?: emptyList(),
                    tiposProducto = tiposResult.getOrNull() ?: emptyList(),
                    isLoading = false,
                    errorMessage = null
                )
            }
        }
    }

    // ==================== OPERACIONES CRUD ====================

    /**
     * ==================== MARCAS ====================
     */

    /**
     * Crea una nueva marca en el backend.
     *
     * @param nombre Nombre de la marca (requerido)
     * @param descripcion Descripción de la marca (opcional)
     * @param paisOrigen País de origen de la marca (opcional)
     */
    fun crearMarca(nombre: String, descripcion: String?, paisOrigen: String?) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }

            val request = MarcaRequest(
                nombreMarca = nombre,
                descripcion = descripcion?.ifBlank { null },
                paisOrigen = paisOrigen?.ifBlank { null }
            )

            val result = repository.crearMarca(request)

            if (result.isSuccess) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        exitoMessage = "Marca creada exitosamente",
                        mostrarFormulario = false
                    )
                }
                cargarTodosDatos() // Recargar para reflejar el cambio
            } else {
                _state.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Error al crear marca: ${result.exceptionOrNull()?.message}"
                    )
                }
            }
        }
    }

    /**
     * Actualiza una marca existente.
     *
     * @param id ID de la marca a actualizar
     * @param nombre Nuevo nombre
     * @param descripcion Nueva descripción
     * @param paisOrigen Nuevo país de origen
     */
    fun actualizarMarca(id: Long, nombre: String, descripcion: String?, paisOrigen: String?) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }

            val request = MarcaRequest(
                nombreMarca = nombre,
                descripcion = descripcion?.ifBlank { null },
                paisOrigen = paisOrigen?.ifBlank { null }
            )

            val result = repository.actualizarMarca(id, request)

            if (result.isSuccess) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        exitoMessage = "Marca actualizada exitosamente",
                        mostrarFormulario = false,
                        itemEditando = null
                    )
                }
                cargarTodosDatos()
            } else {
                _state.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Error al actualizar marca: ${result.exceptionOrNull()?.message}"
                    )
                }
            }
        }
    }

    /**
     * Elimina una marca del sistema.
     *
     * @param id ID de la marca a eliminar
     */
    fun eliminarMarca(id: Long) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }

            val result = repository.eliminarMarca(id)

            if (result.isSuccess) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        exitoMessage = "Marca eliminada exitosamente"
                    )
                }
                cargarTodosDatos()
            } else {
                _state.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Error al eliminar marca: ${result.exceptionOrNull()?.message}"
                    )
                }
            }
        }
    }

    /**
     * ==================== CATEGORÍAS ====================
     */

    fun crearCategoria(nombre: String, descripcion: String?) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }

            val request = CategoriaRequest(
                nombreCategoria = nombre,
                descripcion = descripcion?.ifBlank { null }
            )

            val result = repository.crearCategoria(request)

            if (result.isSuccess) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        exitoMessage = "Categoría creada exitosamente",
                        mostrarFormulario = false
                    )
                }
                cargarTodosDatos()
            } else {
                _state.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Error al crear categoría: ${result.exceptionOrNull()?.message}"
                    )
                }
            }
        }
    }

    fun actualizarCategoria(id: Long, nombre: String, descripcion: String?) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }

            val request = CategoriaRequest(
                nombreCategoria = nombre,
                descripcion = descripcion?.ifBlank { null }
            )

            val result = repository.actualizarCategoria(id, request)

            if (result.isSuccess) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        exitoMessage = "Categoría actualizada exitosamente",
                        mostrarFormulario = false,
                        itemEditando = null
                    )
                }
                cargarTodosDatos()
            } else {
                _state.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Error al actualizar categoría: ${result.exceptionOrNull()?.message}"
                    )
                }
            }
        }
    }

    fun eliminarCategoria(id: Long) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }

            val result = repository.eliminarCategoria(id)

            if (result.isSuccess) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        exitoMessage = "Categoría eliminada exitosamente"
                    )
                }
                cargarTodosDatos()
            } else {
                _state.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Error al eliminar categoría: ${result.exceptionOrNull()?.message}"
                    )
                }
            }
        }
    }

    /**
     * ==================== GÉNEROS ====================
     */

    fun crearGenero(nombre: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }

            val request = GeneroRequest(nombreGenero = nombre)

            val result = repository.crearGenero(request)

            if (result.isSuccess) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        exitoMessage = "Género creado exitosamente",
                        mostrarFormulario = false
                    )
                }
                cargarTodosDatos()
            } else {
                _state.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Error al crear género: ${result.exceptionOrNull()?.message}"
                    )
                }
            }
        }
    }

    fun actualizarGenero(id: Long, nombre: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }

            val request = GeneroRequest(nombreGenero = nombre)

            val result = repository.actualizarGenero(id, request)

            if (result.isSuccess) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        exitoMessage = "Género actualizado exitosamente",
                        mostrarFormulario = false,
                        itemEditando = null
                    )
                }
                cargarTodosDatos()
            } else {
                _state.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Error al actualizar género: ${result.exceptionOrNull()?.message}"
                    )
                }
            }
        }
    }

    fun eliminarGenero(id: Long) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }

            val result = repository.eliminarGenero(id)

            if (result.isSuccess) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        exitoMessage = "Género eliminado exitosamente"
                    )
                }
                cargarTodosDatos()
            } else {
                _state.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Error al eliminar género: ${result.exceptionOrNull()?.message}"
                    )
                }
            }
        }
    }

    /**
     * ==================== TIPOS DE PRODUCTO ====================
     */

    fun crearTipoProducto(nombre: String, descripcion: String?) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }

            val request = TipoProductoRequest(
                nombreTipo = nombre,
                descripcion = descripcion?.ifBlank { null }
            )

            val result = repository.crearTipoProducto(request)

            if (result.isSuccess) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        exitoMessage = "Tipo de producto creado exitosamente",
                        mostrarFormulario = false
                    )
                }
                cargarTodosDatos()
            } else {
                _state.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Error al crear tipo: ${result.exceptionOrNull()?.message}"
                    )
                }
            }
        }
    }

    fun actualizarTipoProducto(id: Long, nombre: String, descripcion: String?) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }

            val request = TipoProductoRequest(
                nombreTipo = nombre,
                descripcion = descripcion?.ifBlank { null }
            )

            val result = repository.actualizarTipoProducto(id, request)

            if (result.isSuccess) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        exitoMessage = "Tipo de producto actualizado exitosamente",
                        mostrarFormulario = false,
                        itemEditando = null
                    )
                }
                cargarTodosDatos()
            } else {
                _state.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Error al actualizar tipo: ${result.exceptionOrNull()?.message}"
                    )
                }
            }
        }
    }

    fun eliminarTipoProducto(id: Long) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }

            val result = repository.eliminarTipoProducto(id)

            if (result.isSuccess) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        exitoMessage = "Tipo de producto eliminado exitosamente"
                    )
                }
                cargarTodosDatos()
            } else {
                _state.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Error al eliminar tipo: ${result.exceptionOrNull()?.message}"
                    )
                }
            }
        }
    }

    // ==================== GESTIÓN DE FORMULARIO ====================

    /**
     * Muestra el formulario para agregar un nuevo item.
     *
     * @param tipo Tipo de entidad a agregar (MARCA, CATEGORIA, etc.)
     */
    fun mostrarFormularioAgregar(tipo: TipoDatoMaestro) {
        _state.update {
            it.copy(
                mostrarFormulario = true,
                tipoSeleccionado = tipo,
                itemEditando = null
            )
        }
    }

    /**
     * Muestra el formulario para editar un item existente.
     *
     * @param tipo Tipo de entidad
     * @param item El item a editar (Marca, Categoria, Genero o TipoProducto)
     */
    fun mostrarFormularioEditar(tipo: TipoDatoMaestro, item: Any) {
        _state.update {
            it.copy(
                mostrarFormulario = true,
                tipoSeleccionado = tipo,
                itemEditando = item
            )
        }
    }

    /**
     * Oculta el formulario y limpia el estado de edición.
     */
    fun ocultarFormulario() {
        _state.update {
            it.copy(
                mostrarFormulario = false,
                itemEditando = null
            )
        }
    }

    /**
     * Limpia los mensajes de éxito y error.
     * Se llama después de mostrarlos por un tiempo.
     */
    fun limpiarMensajes() {
        _state.update {
            it.copy(
                exitoMessage = null,
                errorMessage = null
            )
        }
    }
}

/**
 * Data class que representa el estado completo de la pantalla
 * de Gestión de Datos Maestros.
 *
 * Uso de inmutabilidad (val) para garantizar que el estado solo
 * se modifique mediante el método copy().
 *
 * @property marcas Lista de todas las marcas
 * @property categorias Lista de todas las categorías
 * @property generos Lista de todos los géneros
 * @property tiposProducto Lista de todos los tipos de producto
 * @property isLoading Indica si hay una operación en curso
 * @property errorMessage Mensaje de error si algo falla (null si no hay error)
 * @property exitoMessage Mensaje de éxito después de una operación
 * @property mostrarFormulario Indica si el formulario está visible
 * @property tipoSeleccionado Tipo de entidad que se está editando
 * @property itemEditando Item siendo editado (null si es nuevo)
 */
data class DatosMaestrosState(
    val marcas: List<Marca> = emptyList(),
    val categorias: List<Categoria> = emptyList(),
    val generos: List<Genero> = emptyList(),
    val tiposProducto: List<TipoProducto> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val exitoMessage: String? = null,
    val mostrarFormulario: Boolean = false,
    val tipoSeleccionado: TipoDatoMaestro = TipoDatoMaestro.MARCA,
    val itemEditando: Any? = null
)

/**
 * Enum que identifica el tipo de dato maestro que se está gestionando.
 * Facilita el manejo genérico de las 4 entidades en la UI.
 */
enum class TipoDatoMaestro {
    MARCA,
    CATEGORIA,
    GENERO,
    TIPO_PRODUCTO
}
