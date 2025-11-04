package com.example.proyectomovil.data.repository

import com.example.proyectomovil.data.model.*
import com.example.proyectomovil.data.remote.ApiService
import com.example.proyectomovil.data.remote.RetrofitProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RinconRepository {

    private val api: ApiService = RetrofitProvider.create()

    // ==================== AUTH ====================
    suspend fun login(correo: String, contrasena: String): Result<LoginResponse> =
        withContext(Dispatchers.IO) {
            try {
                val response = api.login(LoginRequest(correo, contrasena))
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception("Error: ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    // ==================== USUARIOS ====================
    suspend fun getUsuarios(): Result<List<Usuario>> = withContext(Dispatchers.IO) {
        try {
            val response = api.getUsuarios()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun buscarUsuarioPorCorreo(correo: String): Result<Usuario?> =
        withContext(Dispatchers.IO) {
            try {
                val response = api.getUsuarios()
                if (response.isSuccessful && response.body() != null) {
                    val usuario = response.body()!!.find { it.correo == correo }
                    Result.success(usuario)
                } else {
                    Result.failure(Exception("Error: ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    /**
     * Obtiene un usuario específico por su ID.
     *
     * Hace una petición GET al endpoint /usuarios/{id} del backend.
     * Este método es útil para cargar información actualizada de un usuario
     * específico, por ejemplo en la pantalla de perfil.
     *
     * @param id ID del usuario a buscar
     * @return Result<Usuario> - Éxito con el usuario o fallo con excepción
     *
     * Flujo:
     * 1. Ejecuta en IO dispatcher (hilo de fondo para operaciones de red)
     * 2. Llama al endpoint del backend vía Retrofit
     * 3. Si la respuesta es exitosa (200-299) y tiene cuerpo, retorna el usuario
     * 4. Si falla, retorna una excepción con el código de error
     *
     * Ejemplo de uso:
     * ```
     * val result = repository.getUsuario(123)
     * if (result.isSuccess) {
     *     val usuario = result.getOrNull()
     *     // usar el usuario
     * }
     * ```
     */
    suspend fun getUsuario(id: Long): Result<Usuario> = withContext(Dispatchers.IO) {
        try {
            val response = api.getUsuario(id)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error al obtener usuario: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun crearUsuario(usuario: Usuario): Result<Usuario> = withContext(Dispatchers.IO) {
        try {
            val response = api.crearUsuario(usuario)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun actualizarUsuario(id: Long, usuario: Usuario): Result<Usuario> =
        withContext(Dispatchers.IO) {
            try {
                val response = api.actualizarUsuario(id, usuario)
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception("Error: ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    // ==================== CLIENTES ====================
    suspend fun getClientes(): Result<List<Cliente>> = withContext(Dispatchers.IO) {
        try {
            val response = api.getClientes()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun buscarClientePorCorreo(correo: String): Result<Cliente?> =
        withContext(Dispatchers.IO) {
            try {
                val response = api.getClientes()
                if (response.isSuccessful && response.body() != null) {
                    val cliente =
                        response.body()!!.find { it.usuario?.correo == correo }
                    Result.success(cliente)
                } else {
                    Result.failure(Exception("Error: ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    suspend fun registrarCliente(cliente: ClienteRequest): Result<Cliente> =
        withContext(Dispatchers.IO) {
            try {
                val response = api.registrarCliente(cliente)
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception("Error: ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    // ==================== PRODUCTOS ====================
    suspend fun getProductos(): Result<List<Producto>> = withContext(Dispatchers.IO) {
        try {
            val response = api.getProductos()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getProducto(id: Long): Result<Producto> = withContext(Dispatchers.IO) {
        try {
            val response = api.getProducto(id)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun crearProducto(producto: ProductoRequest): Result<Producto> =
        withContext(Dispatchers.IO) {
            try {
                val response = api.crearProducto(producto)
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception("Error: ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    suspend fun actualizarProducto(id: Long, producto: ProductoRequest): Result<Producto> =
        withContext(Dispatchers.IO) {
            try {
                val response = api.actualizarProducto(id, producto)
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception("Error: ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    suspend fun eliminarProducto(id: Long): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val response = api.eliminarProducto(id)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ==================== DATOS MAESTROS ====================
    suspend fun getMarcas(): Result<List<Marca>> = withContext(Dispatchers.IO) {
        try {
            val response = api.getMarcas()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getCategorias(): Result<List<Categoria>> = withContext(Dispatchers.IO) {
        try {
            val response = api.getCategorias()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getGeneros(): Result<List<Genero>> = withContext(Dispatchers.IO) {
        try {
            val response = api.getGeneros()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getTiposProducto(): Result<List<TipoProducto>> = withContext(Dispatchers.IO) {
        try {
            val response = api.getTiposProducto()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ==================== DATOS MAESTROS - CRUD COMPLETO ====================

    /**
     * ==================== MARCAS ====================
     * Métodos para gestionar marcas (Dior, Chanel, etc.)
     */

    /**
     * Crea una nueva marca en el backend.
     *
     * @param marca Datos de la marca a crear (nombre, descripción, país)
     * @return Result con la marca creada o error
     */
    suspend fun crearMarca(marca: MarcaRequest): Result<Marca> = withContext(Dispatchers.IO) {
        try {
            val response = api.crearMarca(marca)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error al crear marca: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Actualiza una marca existente.
     *
     * @param id ID de la marca a actualizar
     * @param marca Nuevos datos de la marca
     * @return Result con la marca actualizada o error
     */
    suspend fun actualizarMarca(id: Long, marca: MarcaRequest): Result<Marca> =
        withContext(Dispatchers.IO) {
            try {
                val response = api.actualizarMarca(id, marca)
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception("Error al actualizar marca: ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    /**
     * Elimina una marca del sistema.
     *
     * @param id ID de la marca a eliminar
     * @return Result indicando éxito o error
     */
    suspend fun eliminarMarca(id: Long): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val response = api.eliminarMarca(id)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Error al eliminar marca: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * ==================== CATEGORÍAS ====================
     * Métodos para gestionar categorías (Florales, Cítricas, etc.)
     */

    /**
     * Crea una nueva categoría en el backend.
     *
     * @param categoria Datos de la categoría a crear
     * @return Result con la categoría creada o error
     */
    suspend fun crearCategoria(categoria: CategoriaRequest): Result<Categoria> =
        withContext(Dispatchers.IO) {
            try {
                val response = api.crearCategoria(categoria)
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception("Error al crear categoría: ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    /**
     * Actualiza una categoría existente.
     *
     * @param id ID de la categoría a actualizar
     * @param categoria Nuevos datos de la categoría
     * @return Result con la categoría actualizada o error
     */
    suspend fun actualizarCategoria(id: Long, categoria: CategoriaRequest): Result<Categoria> =
        withContext(Dispatchers.IO) {
            try {
                val response = api.actualizarCategoria(id, categoria)
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception("Error al actualizar categoría: ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    /**
     * Elimina una categoría del sistema.
     *
     * @param id ID de la categoría a eliminar
     * @return Result indicando éxito o error
     */
    suspend fun eliminarCategoria(id: Long): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val response = api.eliminarCategoria(id)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Error al eliminar categoría: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * ==================== GÉNEROS ====================
     * Métodos para gestionar géneros (Masculino, Femenino, Unisex)
     */

    /**
     * Crea un nuevo género en el backend.
     *
     * @param genero Datos del género a crear
     * @return Result con el género creado o error
     */
    suspend fun crearGenero(genero: GeneroRequest): Result<Genero> =
        withContext(Dispatchers.IO) {
            try {
                val response = api.crearGenero(genero)
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception("Error al crear género: ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    /**
     * Actualiza un género existente.
     *
     * @param id ID del género a actualizar
     * @param genero Nuevos datos del género
     * @return Result con el género actualizado o error
     */
    suspend fun actualizarGenero(id: Long, genero: GeneroRequest): Result<Genero> =
        withContext(Dispatchers.IO) {
            try {
                val response = api.actualizarGenero(id, genero)
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception("Error al actualizar género: ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    /**
     * Elimina un género del sistema.
     *
     * @param id ID del género a eliminar
     * @return Result indicando éxito o error
     */
    suspend fun eliminarGenero(id: Long): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val response = api.eliminarGenero(id)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Error al eliminar género: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * ==================== TIPOS DE PRODUCTO ====================
     * Métodos para gestionar tipos de producto (Eau de Parfum, Eau de Toilette, etc.)
     */

    /**
     * Crea un nuevo tipo de producto en el backend.
     *
     * @param tipo Datos del tipo de producto a crear
     * @return Result con el tipo creado o error
     */
    suspend fun crearTipoProducto(tipo: TipoProductoRequest): Result<TipoProducto> =
        withContext(Dispatchers.IO) {
            try {
                val response = api.crearTipoProducto(tipo)
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception("Error al crear tipo de producto: ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    /**
     * Actualiza un tipo de producto existente.
     *
     * @param id ID del tipo de producto a actualizar
     * @param tipo Nuevos datos del tipo de producto
     * @return Result con el tipo actualizado o error
     */
    suspend fun actualizarTipoProducto(id: Long, tipo: TipoProductoRequest): Result<TipoProducto> =
        withContext(Dispatchers.IO) {
            try {
                val response = api.actualizarTipoProducto(id, tipo)
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception("Error al actualizar tipo de producto: ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    /**
     * Elimina un tipo de producto del sistema.
     *
     * @param id ID del tipo de producto a eliminar
     * @return Result indicando éxito o error
     */
    suspend fun eliminarTipoProducto(id: Long): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val response = api.eliminarTipoProducto(id)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Error al eliminar tipo de producto: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ==================== CARRITO ====================
    suspend fun getCarrito(clienteId: Long): Result<List<Carrito>> =
        withContext(Dispatchers.IO) {
            try {
                val response = api.getCarrito(clienteId)
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception("Error: ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    suspend fun agregarAlCarrito(request: CarritoRequest): Result<Carrito> =
        withContext(Dispatchers.IO) {
            try {
                val response = api.agregarAlCarrito(request)
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception("Error: ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    suspend fun eliminarItemCarrito(id: Long): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val response = api.eliminarItemCarrito(id)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun vaciarCarrito(clienteId: Long): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val response = api.vaciarCarrito(clienteId)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ==================== PEDIDOS ====================
    suspend fun getPedidosCliente(clienteId: Long): Result<List<Pedido>> =
        withContext(Dispatchers.IO) {
            try {
                val response = api.getPedidosCliente(clienteId)
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception("Error: ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    suspend fun crearPedido(pedido: PedidoRequest): Result<Pedido> =
        withContext(Dispatchers.IO) {
            try {
                val response = api.crearPedido(pedido)
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception("Error: ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    suspend fun crearDetallePedido(detalle: DetallePedidoRequest): Result<DetallePedido> =
        withContext(Dispatchers.IO) {
            try {
                val response = api.crearDetallePedido(detalle)
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception("Error: ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
}