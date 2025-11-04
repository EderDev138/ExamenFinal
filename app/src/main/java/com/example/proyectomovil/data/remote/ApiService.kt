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
    @GET("/api/productos")
    suspend fun getProductos(): Response<List<Producto>>

    @GET("/api/productos/{id}")
    suspend fun getProducto(@Path("id") id: Long): Response<Producto>

    @POST("/api/productos")
    suspend fun crearProducto(@Body producto: ProductoRequest): Response<Producto>

    @PUT("/api/productos/{id}")
    suspend fun actualizarProducto(
        @Path("id") id: Long,
        @Body producto: ProductoRequest
    ): Response<Producto>

    @DELETE("/api/productos/{id}")
    suspend fun eliminarProducto(@Path("id") id: Long): Response<Unit>

    // ==================== DATOS MAESTROS ====================

    // --- MARCAS ---
    @GET("/api/marcas")
    suspend fun getMarcas(): Response<List<Marca>>

    @GET("/api/marcas/{id}")
    suspend fun getMarca(@Path("id") id: Long): Response<Marca>

    @POST("/api/marcas")
    suspend fun crearMarca(@Body marca: MarcaRequest): Response<Marca>

    @PUT("/api/marcas/{id}")
    suspend fun actualizarMarca(
        @Path("id") id: Long,
        @Body marca: MarcaRequest
    ): Response<Marca>

    @DELETE("/api/marcas/{id}")
    suspend fun eliminarMarca(@Path("id") id: Long): Response<Unit>

    // --- CATEGORÍAS ---
    @GET("/api/categorias")
    suspend fun getCategorias(): Response<List<Categoria>>

    @GET("/api/categorias/{id}")
    suspend fun getCategoria(@Path("id") id: Long): Response<Categoria>

    @POST("/api/categorias")
    suspend fun crearCategoria(@Body categoria: CategoriaRequest): Response<Categoria>

    @PUT("/api/categorias/{id}")
    suspend fun actualizarCategoria(
        @Path("id") id: Long,
        @Body categoria: CategoriaRequest
    ): Response<Categoria>

    @DELETE("/api/categorias/{id}")
    suspend fun eliminarCategoria(@Path("id") id: Long): Response<Unit>

    // --- GÉNEROS ---
    @GET("/api/generos")
    suspend fun getGeneros(): Response<List<Genero>>

    @GET("/api/generos/{id}")
    suspend fun getGenero(@Path("id") id: Long): Response<Genero>

    @POST("/api/generos")
    suspend fun crearGenero(@Body genero: GeneroRequest): Response<Genero>

    @PUT("/api/generos/{id}")
    suspend fun actualizarGenero(
        @Path("id") id: Long,
        @Body genero: GeneroRequest
    ): Response<Genero>

    @DELETE("/api/generos/{id}")
    suspend fun eliminarGenero(@Path("id") id: Long): Response<Unit>

    // --- TIPOS DE PRODUCTO ---
    @GET("/api/tipos-producto")
    suspend fun getTiposProducto(): Response<List<TipoProducto>>

    @GET("/api/tipos-producto/{id}")
    suspend fun getTipoProducto(@Path("id") id: Long): Response<TipoProducto>

    @POST("/api/tipos-producto")
    suspend fun crearTipoProducto(@Body tipo: TipoProductoRequest): Response<TipoProducto>

    @PUT("/api/tipos-producto/{id}")
    suspend fun actualizarTipoProducto(
        @Path("id") id: Long,
        @Body tipo: TipoProductoRequest
    ): Response<TipoProducto>

    @DELETE("/api/tipos-producto/{id}")
    suspend fun eliminarTipoProducto(@Path("id") id: Long): Response<Unit>

    // ==================== CARRITO ====================
    @GET("/api/carrito/cliente/{clienteId}")
    suspend fun getCarrito(@Path("clienteId") clienteId: Long): Response<List<Carrito>>

    @POST("/api/carrito")
    suspend fun agregarAlCarrito(@Body request: CarritoRequest): Response<Carrito>

    @DELETE("/api/carrito/{id}")
    suspend fun eliminarItemCarrito(@Path("id") id: Long): Response<Unit>

    @DELETE("/api/carrito/vaciar/{clienteId}")
    suspend fun vaciarCarrito(@Path("clienteId") clienteId: Long): Response<Unit>

    // ==================== PEDIDOS ====================
    @GET("/api/pedidos/cliente/{clienteId}")
    suspend fun getPedidosCliente(@Path("clienteId") clienteId: Long): Response<List<Pedido>>

    @GET("/api/pedidos/{id}")
    suspend fun getPedido(@Path("id") id: Long): Response<Pedido>

    @POST("/api/pedidos")
    suspend fun crearPedido(@Body pedido: PedidoRequest): Response<Pedido>

    @PUT("/api/pedidos/{id}")
    suspend fun actualizarPedido(
        @Path("id") id: Long,
        @Body pedido: PedidoRequest
    ): Response<Pedido>

    // ==================== DETALLES PEDIDO ====================
    @GET("/api/detalles-pedido/pedido/{pedidoId}")
    suspend fun getDetallesPedido(@Path("pedidoId") pedidoId: Long): Response<List<DetallePedido>>

    @POST("/api/detalles-pedido")
    suspend fun crearDetallePedido(@Body detalle: DetallePedidoRequest): Response<DetallePedido>
}