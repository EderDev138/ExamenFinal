# 📋 ROLES Y PERMISOS - RINCÓN PERFUMES

## 🎭 ROLES DEL SISTEMA

El sistema tiene 4 roles principales:

### 1. 👤 INVITADO (Usuario no logueado)
**Capacidades:**
- ✅ Ver catálogo de productos
- ✅ Buscar y filtrar productos
- ✅ Ver detalles de productos
- ❌ NO puede agregar al carrito
- ❌ NO puede hacer pedidos
- ❌ NO tiene acceso a funciones administrativas

**Cómo acceder:**
- Entrar directamente a la app sin hacer login
- El sistema asigna userId = 3 y rol = "INVITADO"

---

### 2. 🛒 CLIENTE (Usuario registrado)
**Capacidades:**
- ✅ Todo lo que puede hacer un INVITADO
- ✅ Agregar productos al carrito
- ✅ Realizar pedidos (checkout)
- ✅ Ver historial de pedidos (Mis Pedidos)
- ✅ Ver y editar perfil
- ✅ Cerrar sesión
- ❌ NO tiene acceso a funciones administrativas

**Opciones en el menú principal (HomeScreen):**
```
┌──────────────────────────────────┐
│  🏠 Rincón Perfumes    🛒  ⋮     │
└──────────────────────────────────┘
                          │
                          ├─ Mis Pedidos
                          ├─ Perfil
                          └─ Cerrar Sesión
```

**Cómo crear un usuario CLIENTE:**
1. Ir a la pantalla de Registro
2. Llenar el formulario con:
   - Nombre
   - Email
   - Contraseña
   - Dirección
   - Teléfono
3. El sistema automáticamente crea un Usuario y un Cliente vinculado
4. El usuario queda ACTIVO inmediatamente

---

### 3. 👨‍💼 ENCARGADO (Personal administrativo)
**Capacidades:**
- ✅ Ver catálogo de productos
- ✅ **Gestión de Productos (CRUD completo)**
  - Crear nuevos productos
  - Editar productos existentes
  - Eliminar productos
  - Ver lista completa de productos
- ✅ **Gestión de Datos Maestros (CRUD completo)**
  - Gestionar Marcas (Dior, Chanel, etc.)
  - Gestionar Categorías (Florales, Cítricas, etc.)
  - Gestionar Géneros (Masculino, Femenino, Unisex)
  - Gestionar Tipos de Producto (Eau de Parfum, Eau de Toilette, etc.)
- ❌ NO puede agregar al carrito (no es cliente)
- ❌ NO puede hacer pedidos
- ❌ NO puede gestionar usuarios (solo ADMIN)

**Opciones en el menú principal (HomeScreen):**
```
┌──────────────────────────────────┐
│  🏠 Rincón Perfumes          ⋮   │
└──────────────────────────────────┘
                               │
                               ├─ Gestión Productos
                               ├─ Gestión Datos Maestros
                               ├─ Perfil
                               └─ Cerrar Sesión
```

**Cómo crear un usuario ENCARGADO:**

⚠️ **IMPORTANTE:** Los usuarios ENCARGADO deben ser aprobados por un ADMIN.

**Opción 1 - Desde el frontend (recomendado para pruebas):**
1. Ir a la pantalla de Registro
2. Llenar el formulario
3. El usuario ENCARGADO se crea con estado PENDIENTE
4. Un usuario ADMIN debe ir a "Gestión Usuarios" y aprobar al ENCARGADO
5. Una vez aprobado, el ENCARGADO puede iniciar sesión

**Opción 2 - Desde el backend (más rápido para pruebas):**
```sql
-- Crear usuario ENCARGADO directamente en la BD
INSERT INTO usuarios (nombre, email, password, rol, activo)
VALUES ('Juan Pérez', 'encargado@test.com', 'password123', 'ENCARGADO', true);

-- O actualizar un usuario existente
UPDATE usuarios SET rol = 'ENCARGADO', activo = true WHERE id = 4;
```

**Opción 3 - Usar datos de prueba precargados (si existen):**
- Usuario: `encargado@test.com`
- Contraseña: `password123`

---

### 4. 👑 ADMIN (Administrador del sistema)
**Capacidades:**
- ✅ **Todo lo que puede hacer un ENCARGADO:**
  - Gestión de Productos
  - Gestión de Datos Maestros
- ✅ **Gestión de Usuarios (exclusivo de ADMIN)**
  - Ver usuarios ENCARGADO pendientes de aprobación
  - Aprobar usuarios ENCARGADO
  - Desactivar usuarios ENCARGADO
  - Ver lista de usuarios por estado
- ❌ NO puede agregar al carrito (no es cliente)
- ❌ NO puede hacer pedidos

**Opciones en el menú principal (HomeScreen):**
```
┌──────────────────────────────────┐
│  🏠 Rincón Perfumes          ⋮   │
└──────────────────────────────────┘
                               │
                               ├─ Gestión Productos
                               ├─ Gestión Datos Maestros
                               ├─ Gestión Usuarios ⭐
                               ├─ Perfil
                               └─ Cerrar Sesión
```

**Cómo crear un usuario ADMIN:**

**Opción 1 - Desde el backend (recomendado):**
```sql
-- Crear el primer usuario ADMIN directamente en la BD
INSERT INTO usuarios (nombre, email, password, rol, activo)
VALUES ('Admin Principal', 'admin@rinconperfumes.com', 'admin123', 'ADMIN', true);

-- O actualizar un usuario existente
UPDATE usuarios SET rol = 'ADMIN', activo = true WHERE id = 1;
```

**Opción 2 - Usar datos de prueba precargados (si existen):**
- Usuario: `admin@test.com`
- Contraseña: `admin123`

---

## 🔐 MATRIZ DE PERMISOS

| Función                          | INVITADO | CLIENTE | ENCARGADO | ADMIN |
|----------------------------------|----------|---------|-----------|-------|
| Ver catálogo                     | ✅       | ✅      | ✅        | ✅    |
| Buscar/Filtrar productos         | ✅       | ✅      | ✅        | ✅    |
| Ver detalle de producto          | ✅       | ✅      | ✅        | ✅    |
| Agregar al carrito               | ❌       | ✅      | ❌        | ❌    |
| Realizar pedidos                 | ❌       | ✅      | ❌        | ❌    |
| Ver mis pedidos                  | ❌       | ✅      | ❌        | ❌    |
| Gestión de productos (CRUD)      | ❌       | ❌      | ✅        | ✅    |
| Gestión de datos maestros (CRUD) | ❌       | ❌      | ✅        | ✅    |
| Gestión de usuarios              | ❌       | ❌      | ❌        | ✅    |
| Ver/Editar perfil                | ❌       | ✅      | ✅        | ✅    |

---

## 🧪 CÓMO PROBAR CADA ROL

### Probar como INVITADO:
1. Iniciar la app sin hacer login
2. Navegar por el catálogo
3. Intentar agregar al carrito → No debería aparecer el botón

### Probar como CLIENTE:
1. Registrarse o hacer login con usuario CLIENTE
2. Agregar productos al carrito
3. Ir a "Mis Pedidos" desde el menú
4. Realizar un pedido completo (checkout)
5. Verificar que NO aparecen opciones de gestión en el menú

### Probar como ENCARGADO:
1. Hacer login con usuario ENCARGADO (debe estar aprobado)
2. Ir al menú (⋮) → Verificar que aparece:
   - ✅ Gestión Productos
   - ✅ Gestión Datos Maestros
   - ❌ NO aparece Gestión Usuarios
3. Crear/Editar/Eliminar productos
4. Crear/Editar/Eliminar datos maestros (marcas, categorías, etc.)
5. Verificar que NO aparece el botón de carrito
6. Verificar que NO puede hacer pedidos

### Probar como ADMIN:
1. Hacer login con usuario ADMIN
2. Ir al menú (⋮) → Verificar que aparece:
   - ✅ Gestión Productos
   - ✅ Gestión Datos Maestros
   - ✅ Gestión Usuarios
3. Ir a Gestión Usuarios
4. Aprobar un usuario ENCARGADO pendiente
5. Desactivar un usuario ENCARGADO activo
6. Realizar todas las funciones de ENCARGADO

---

## 📝 NOTAS IMPORTANTES

### ⚠️ Diferencia CLIENTE vs ENCARGADO/ADMIN:
- **CLIENTE**: Es un comprador. Tiene un registro en la tabla `clientes` vinculado a su `usuario`. Puede hacer pedidos.
- **ENCARGADO/ADMIN**: Son empleados. NO tienen registro en `clientes`. NO pueden comprar, solo administran.

### ⚠️ Aprobación de ENCARGADO:
- Cuando un ENCARGADO se registra, su estado inicial es `activo = false`
- Solo un ADMIN puede cambiar `activo = true` desde "Gestión Usuarios"
- Un ENCARGADO con `activo = false` NO puede iniciar sesión

### ⚠️ Seguridad:
- El primer ADMIN del sistema debe crearse directamente en la base de datos
- No existe registro público de ADMIN (para evitar que cualquiera se registre como admin)
- Las contraseñas deberían estar hasheadas (verificar implementación del backend)

---

## 🐛 PROBLEMAS COMUNES Y SOLUCIONES

### Problema: "No puedo agregar al carrito"
**Causa:** El usuario no es CLIENTE o no está logueado
**Solución:**
- Verificar que estás logueado con un usuario CLIENTE
- Verificar que el clienteId es válido (> 0)

### Problema: "El carrito aparece vacío"
**Causa:** Error de clienteId o problema con el backend
**Solución:**
- Verificar que el clienteId se pasa correctamente en la navegación
- Verificar que el backend guarda correctamente en `/carrito`
- Verificar los logs del backend

### Problema: "ENCARGADO no puede iniciar sesión"
**Causa:** El usuario ENCARGADO no ha sido aprobado
**Solución:**
- Iniciar sesión como ADMIN
- Ir a Gestión Usuarios
- Aprobar al ENCARGADO desde la lista de pendientes

### Problema: "No aparece el menú de gestión"
**Causa:** El rol del usuario no es ENCARGADO o ADMIN
**Solución:**
- Verificar el rol en la base de datos: `SELECT rol FROM usuarios WHERE id = X`
- Asegurarse de que el login devuelve el rol correcto

---

## 📂 ARCHIVOS RELACIONADOS

- `HomeScreen.kt`: Define qué opciones ve cada rol en el menú
- `NavGraph.kt`: Define las rutas de navegación
- `LoginScreen.kt`: Maneja el login y asigna el rol
- `GestionProductosScreen.kt`: Pantalla de CRUD de productos (ENCARGADO/ADMIN)
- `GestionDatosMaestrosScreen.kt`: Pantalla de CRUD de datos maestros (ENCARGADO/ADMIN)
- `GestionUsuariosScreen.kt`: Pantalla de gestión de usuarios (solo ADMIN)
- `CarritoScreen.kt`: Pantalla del carrito (solo CLIENTE)
- `MisPedidosScreen.kt`: Pantalla de pedidos (solo CLIENTE)
