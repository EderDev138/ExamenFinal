-- ====================================================================
-- 🧪 SCRIPT DE USUARIOS DE PRUEBA - RINCÓN PERFUMES
-- ====================================================================
-- Este script crea usuarios de prueba para todos los roles del sistema.
-- Ejecutar este script en tu base de datos para facilitar las pruebas.
-- ====================================================================

-- ==================== LIMPIAR DATOS ANTERIORES (OPCIONAL) ====================
-- ⚠️ DESCOMENTAR SOLO SI QUIERES ELIMINAR LOS USUARIOS DE PRUEBA EXISTENTES
-- DELETE FROM usuarios WHERE email IN ('admin@rincon.com', 'encargado@test.com', 'cliente@test.com');
-- DELETE FROM clientes WHERE email IN ('admin@rincon.com', 'encargado@test.com', 'cliente@test.com');

-- ==================== CREAR ROLES (SI NO EXISTEN) ====================
-- Verificar que existen los roles necesarios
-- INSERT INTO roles (nombre_rol) VALUES ('ADMIN') ON CONFLICT DO NOTHING;
-- INSERT INTO roles (nombre_rol) VALUES ('ENCARGADO') ON CONFLICT DO NOTHING;
-- INSERT INTO roles (nombre_rol) VALUES ('CLIENTE') ON CONFLICT DO NOTHING;

-- ==================== USUARIO 1: ADMIN ====================
-- 👑 Usuario administrador con acceso completo al sistema
-- Email: admin@rincon.com
-- Contraseña: admin123
-- Capacidades:
--   ✅ Gestionar productos
--   ✅ Gestionar datos maestros (marcas, categorías, géneros, tipos)
--   ✅ Gestionar usuarios (aprobar/desactivar ENCARGADOS)
--   ❌ NO puede comprar (no es cliente)

INSERT INTO usuarios (id_usuario, nombre, email, password, rol, activo, fecha_registro)
VALUES (1, 'Admin Principal', 'admin@rincon.com', 'admin123', 'ADMIN', true, CURRENT_TIMESTAMP)
ON CONFLICT (id_usuario) DO UPDATE SET
    nombre = 'Admin Principal',
    email = 'admin@rincon.com',
    password = 'admin123',
    rol = 'ADMIN',
    activo = true;

-- Nota: El ADMIN NO necesita un registro en la tabla clientes
-- porque no hace pedidos

-- ==================== USUARIO 2: ENCARGADO ====================
-- 👨‍💼 Usuario encargado con permisos administrativos limitados
-- Email: encargado@test.com
-- Contraseña: encargado123
-- Capacidades:
--   ✅ Gestionar productos
--   ✅ Gestionar datos maestros (marcas, categorías, géneros, tipos)
--   ❌ NO puede gestionar usuarios
--   ❌ NO puede comprar (no es cliente)

INSERT INTO usuarios (id_usuario, nombre, email, password, rol, activo, fecha_registro)
VALUES (2, 'Juan Pérez', 'encargado@test.com', 'encargado123', 'ENCARGADO', true, CURRENT_TIMESTAMP)
ON CONFLICT (id_usuario) DO UPDATE SET
    nombre = 'Juan Pérez',
    email = 'encargado@test.com',
    password = 'encargado123',
    rol = 'ENCARGADO',
    activo = true;  -- ⚠️ IMPORTANTE: activo = true para que pueda iniciar sesión

-- Nota: El ENCARGADO NO necesita un registro en la tabla clientes
-- porque no hace pedidos

-- ==================== USUARIO 3: CLIENTE ====================
-- 🛒 Usuario cliente que puede realizar compras
-- Email: cliente@test.com
-- Contraseña: cliente123
-- Capacidades:
--   ✅ Ver catálogo de productos
--   ✅ Agregar productos al carrito
--   ✅ Realizar pedidos (checkout)
--   ✅ Ver historial de pedidos
--   ❌ NO tiene acceso a funciones administrativas

-- Paso 1: Crear el usuario
INSERT INTO usuarios (id_usuario, nombre, email, password, rol, activo, fecha_registro)
VALUES (3, 'María López', 'cliente@test.com', 'cliente123', 'CLIENTE', true, CURRENT_TIMESTAMP)
ON CONFLICT (id_usuario) DO UPDATE SET
    nombre = 'María López',
    email = 'cliente@test.com',
    password = 'cliente123',
    rol = 'CLIENTE',
    activo = true;

-- Paso 2: Crear el registro de cliente vinculado
-- ⚠️ IMPORTANTE: Los CLIENTES necesitan un registro en la tabla clientes
INSERT INTO clientes (id_cliente, id_usuario, nombre, email, direccion, telefono, fecha_registro)
VALUES (1, 3, 'María López', 'cliente@test.com', 'Calle Falsa 123', '555-1234', CURRENT_TIMESTAMP)
ON CONFLICT (id_cliente) DO UPDATE SET
    id_usuario = 3,
    nombre = 'María López',
    email = 'cliente@test.com',
    direccion = 'Calle Falsa 123',
    telefono = '555-1234';

-- ==================== USUARIO 4: CLIENTE ADICIONAL ====================
-- 🛒 Segundo usuario cliente para pruebas adicionales
-- Email: cliente2@test.com
-- Contraseña: cliente123

-- Paso 1: Crear el usuario
INSERT INTO usuarios (id_usuario, nombre, email, password, rol, activo, fecha_registro)
VALUES (4, 'Carlos Ramírez', 'cliente2@test.com', 'cliente123', 'CLIENTE', true, CURRENT_TIMESTAMP)
ON CONFLICT (id_usuario) DO UPDATE SET
    nombre = 'Carlos Ramírez',
    email = 'cliente2@test.com',
    password = 'cliente123',
    rol = 'CLIENTE',
    activo = true;

-- Paso 2: Crear el registro de cliente vinculado
INSERT INTO clientes (id_cliente, id_usuario, nombre, email, direccion, telefono, fecha_registro)
VALUES (2, 4, 'Carlos Ramírez', 'cliente2@test.com', 'Avenida Siempre Viva 742', '555-5678', CURRENT_TIMESTAMP)
ON CONFLICT (id_cliente) DO UPDATE SET
    id_usuario = 4,
    nombre = 'Carlos Ramírez',
    email = 'cliente2@test.com',
    direccion = 'Avenida Siempre Viva 742',
    telefono = '555-5678';

-- ==================== USUARIO 5: ENCARGADO PENDIENTE ====================
-- 👨‍💼 Usuario encargado SIN aprobar (para probar el flujo de aprobación)
-- Email: encargado.pendiente@test.com
-- Contraseña: encargado123
-- Estado: activo = false (NO puede iniciar sesión hasta que un ADMIN lo apruebe)

INSERT INTO usuarios (id_usuario, nombre, email, password, rol, activo, fecha_registro)
VALUES (5, 'Ana Torres', 'encargado.pendiente@test.com', 'encargado123', 'ENCARGADO', false, CURRENT_TIMESTAMP)
ON CONFLICT (id_usuario) DO UPDATE SET
    nombre = 'Ana Torres',
    email = 'encargado.pendiente@test.com',
    password = 'encargado123',
    rol = 'ENCARGADO',
    activo = false;  -- ⚠️ Pendiente de aprobación

-- ====================================================================
-- 📝 NOTAS IMPORTANTES
-- ====================================================================

/*
1. CONTRASEÑAS:
   - Las contraseñas aquí están en texto plano para facilitar las pruebas
   - En producción, SIEMPRE deben estar hasheadas (BCrypt, SHA-256, etc.)
   - Verificar que el backend hashea las contraseñas antes de guardarlas

2. IDs FIJOS:
   - Este script usa IDs fijos (1, 2, 3, 4, 5) para facilitar las pruebas
   - Si tu tabla tiene auto-increment, ajusta los IDs según sea necesario
   - Usa ON CONFLICT para evitar errores si los usuarios ya existen

3. RELACIÓN USUARIO-CLIENTE:
   - ADMIN y ENCARGADO: Solo tienen registro en 'usuarios'
   - CLIENTE: Tiene registro en 'usuarios' Y en 'clientes'
   - El id_usuario en 'clientes' debe coincidir con el id_usuario en 'usuarios'

4. ESTADO ACTIVO:
   - ADMIN: activo = true (siempre)
   - ENCARGADO aprobado: activo = true
   - ENCARGADO pendiente: activo = false (no puede iniciar sesión)
   - CLIENTE: activo = true

5. PROBAR EL FLUJO DE APROBACIÓN:
   - Crear un usuario ENCARGADO con activo = false
   - Iniciar sesión como ADMIN
   - Ir a Gestión Usuarios
   - Aprobar al ENCARGADO (cambia activo a true)
   - Ahora el ENCARGADO puede iniciar sesión

6. VERIFICAR LOS DATOS:
   -- Ver todos los usuarios
   SELECT * FROM usuarios;

   -- Ver todos los clientes
   SELECT * FROM clientes;

   -- Ver usuarios por rol
   SELECT id_usuario, nombre, email, rol, activo FROM usuarios ORDER BY rol;

   -- Ver relación usuario-cliente
   SELECT u.id_usuario, u.nombre, u.rol, c.id_cliente, c.direccion
   FROM usuarios u
   LEFT JOIN clientes c ON u.id_usuario = c.id_usuario;
*/

-- ====================================================================
-- ✅ VERIFICACIÓN FINAL
-- ====================================================================

-- Mostrar todos los usuarios de prueba creados
SELECT
    id_usuario,
    nombre,
    email,
    rol,
    activo,
    CASE
        WHEN activo = true THEN '✅ Activo'
        ELSE '❌ Pendiente'
    END as estado
FROM usuarios
WHERE email IN (
    'admin@rincon.com',
    'encargado@test.com',
    'cliente@test.com',
    'cliente2@test.com',
    'encargado.pendiente@test.com'
)
ORDER BY
    CASE rol
        WHEN 'ADMIN' THEN 1
        WHEN 'ENCARGADO' THEN 2
        WHEN 'CLIENTE' THEN 3
    END;

-- ====================================================================
-- 🎉 ¡LISTO! Ahora puedes usar estos usuarios para probar la app
-- ====================================================================
