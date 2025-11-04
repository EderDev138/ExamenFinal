package com.example.proyectomovil.data.remote

import com.example.proyectomovil.data.model.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // ==================== AUTENTICACIÓN ====================
    @POST("/api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    // ==================== USUARIOS ====================
    @GET("/api/usuarios")
    suspend fun getUsuarios(): Response<List<Usuario>>

    @GET("/usuarios/{id}")
    suspend fun getUsuario(@Path("id") id: Long): Response<Usuario>

    @POST("/usuarios")
    suspend fun crearUsuario(@Body usuario: Usuario): Response<Usuario>

    @PUT("/api/usuarios/{id}")
    suspend fun actualizarUsuario(
        @Path("id") id: Long,
        @Body usuario: Usuario
    ): Response<Usuario>

    @DELETE("/usuarios/{id}")
    suspend fun eliminarUsuario(@Path("id") id: Long): Response<Unit>

    // ==================== CLIENTES ====================
    @GET("/api/clientes")
    suspend fun getClientes(): Response<List<Cliente>>

    @GET("/api/clientes/{id}")
    suspend fun getCliente(@Path("id") id: Long): Response<Cliente>

    @POST("/api/clientes")
    suspend fun registrarCliente(@Body cliente: ClienteRequest): Response<Cliente>

    @PUT("/api/clientes/{id}")
    suspend fun actualizarCliente(
        @Path("id") id: Long,
        @Body cliente: ClienteRequest
    ): Response<Cliente>

    // ==================== PRODUCTOS ====================
    @GET("/productos")
    suspend fun getProductos(): Response<List<Producto>>

    @GET("/productos/{id}")
    suspend fun getProducto(@Path("id") id: Long): Response<Producto>

    @POST("/productos")
    suspend fun crearProducto(@Body producto: ProductoRequest): Response<Producto>

    @PUT("/productos/{id}")
    suspend fun actualizarProducto(
        @Path("id") id: Long,
        @Body producto: ProductoRequest
    ): Response<Producto>

    @DELETE("/productos/{id}")
    suspend fun eliminarProducto(@Path("id") id: Long): Response<Unit>

    // ==================== DATOS MAESTROS ====================

    // --- MARCAS ---
    @GET("marcas")
    suspend fun getMarcas(): Response<List<Marca>>

    @GET("/marcas/{id}")
    suspend fun getMarca(@Path("id") id: Long): Response<Marca>

    @POST("/marcas")
    suspend fun crearMarca(@Body marca: MarcaRequest): Response<Marca>

    @PUT("/marcas/{id}")
    suspend fun actualizarMarca(
        @Path("id") id: Long,
        @Body marca: MarcaRequest
    ): Response<Marca>

    @DELETE("/marcas/{id}")
    suspend fun eliminarMarca(@Path("id") id: Long): Response<Unit>

    // --- CATEGORÍAS ---
    @GET("/categorias")
    suspend fun getCategorias(): Response<List<Categoria>>

    @GET("/categorias/{id}")
    suspend fun getCategoria(@Path("id") id: Long): Response<Categoria>

    @POST("/categorias")
    suspend fun crearCategoria(@Body categoria: CategoriaRequest): Response<Categoria>

    @PUT("/categorias/{id}")
    suspend fun actualizarCategoria(
        @Path("id") id: Long,
        @Body categoria: CategoriaRequest
    ): Response<Categoria>

    @DELETE("/categorias/{id}")
    suspend fun eliminarCategoria(@Path("id") id: Long): Response<Unit>

    // --- GÉNEROS ---
    @GET("/generos")
    suspend fun getGeneros(): Response<List<Genero>>

    @GET("/generos/{id}")
    suspend fun getGenero(@Path("id") id: Long): Response<Genero>

    @POST("/generos")
    suspend fun crearGenero(@Body genero: GeneroRequest): Response<Genero>

    @PUT("/generos/{id}")
    suspend fun actualizarGenero(
        @Path("id") id: Long,
        @Body genero: GeneroRequest
    ): Response<Genero>

    @DELETE("/generos/{id}")
    suspend fun eliminarGenero(@Path("id") id: Long): Response<Unit>

    // --- TIPOS DE PRODUCTO ---
    @GET("/tipos-producto")
    suspend fun getTiposProducto(): Response<List<TipoProducto>>

    @GET("/tipos-producto/{id}")
    suspend fun getTipoProducto(@Path("id") id: Long): Response<TipoProducto>

    @POST("/tipos-producto")
    suspend fun crearTipoProducto(@Body tipo: TipoProductoRequest): Response<TipoProducto>

    @PUT("/tipos-producto/{id}")
    suspend fun actualizarTipoProducto(
        @Path("id") id: Long,
        @Body tipo: TipoProductoRequest
    ): Response<TipoProducto>

    @DELETE("/tipos-producto/{id}")
    suspend fun eliminarTipoProducto(@Path("id") id: Long): Response<Unit>

    // ==================== CARRITO ====================
    @GET("/carrito/cliente/{clienteId}")
    suspend fun getCarrito(@Path("clienteId") clienteId: Long): Response<List<Carrito>>

    @POST("/carrito")
    suspend fun agregarAlCarrito(@Body request: CarritoRequest): Response<Carrito>

    @DELETE("/carrito/{id}")
    suspend fun eliminarItemCarrito(@Path("id") id: Long): Response<Unit>

    @DELETE("/carrito/vaciar/{clienteId}")
    suspend fun vaciarCarrito(@Path("clienteId") clienteId: Long): Response<Unit>

    // ==================== PEDIDOS ====================
    @GET("/pedidos/cliente/{clienteId}")
    suspend fun getPedidosCliente(@Path("clienteId") clienteId: Long): Response<List<Pedido>>

    @GET("/pedidos/{id}")
    suspend fun getPedido(@Path("id") id: Long): Response<Pedido>

    @POST("/pedidos")
    suspend fun crearPedido(@Body pedido: PedidoRequest): Response<Pedido>

    @PUT("/pedidos/{id}")
    suspend fun actualizarPedido(
        @Path("id") id: Long,
        @Body pedido: PedidoRequest
    ): Response<Pedido>

    // ==================== DETALLES PEDIDO ====================
    @GET("/detalles-pedido/pedido/{pedidoId}")
    suspend fun getDetallesPedido(@Path("pedidoId") pedidoId: Long): Response<List<DetallePedido>>

    @POST("/detalles-pedido")
    suspend fun crearDetallePedido(@Body detalle: DetallePedidoRequest): Response<DetallePedido>
}