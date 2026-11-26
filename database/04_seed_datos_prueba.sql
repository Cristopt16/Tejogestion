-- ==========================================================
-- DATOS SEMILLA DE PRUEBA (TejoGestion)
-- Este script SOLO inserta datos semilla; NO crea tablas.
-- Prerrequisitos: ejecutar en orden tejogestion.sql,
--   02_mod2_inventario_ventas.sql, 03_mod3_torneos.sql.
-- ==========================================================
USE tejogestion;

-- Roles base
INSERT IGNORE INTO ROL (id_rol, nombre) VALUES
(1, 'Administrador'),
(2, 'Vendedor'),
(3, 'Cliente');

-- Usuarios de prueba para cada rol
INSERT IGNORE INTO USUARIO (id_usuario, ref_rol, nombre, usuario, contrasena, estado) VALUES
(1, 1, 'Administrador Principal', 'admin', 'admin123', 'Activo'),
(2, 2, 'Vendedor Turno', 'vendedor', 'vend123', 'Activo'),
(3, 3, 'Cliente Invitado', 'cliente', 'cli123', 'Activo');

-- Canchas base
INSERT IGNORE INTO CANCHA (id_cancha, nombre, estado) VALUES
(1, 'Cancha 1', 'Disponible'),
(2, 'Cancha 2', 'Disponible'),
(3, 'Cancha 3', 'Disponible');

-- Productos básicos de inventario y su stock inicial
INSERT IGNORE INTO PRODUCTO (id_producto, codigo, nombre, categoria, precio_venta, activo) VALUES
(1, 'BEB-010', 'Agua Cristal 500ml', 'Bebidas', 2500.00, TRUE),
(2, 'BEB-011', 'Gaseosa Postobon 400ml', 'Bebidas', 3500.00, TRUE),
(3, 'SNK-001', 'Papas Margarita Pollo', 'Snacks', 3000.00, TRUE),
(4, 'DEP-001', 'Mecha Tejo Unidad', 'Deportivo', 1500.00, TRUE);

INSERT IGNORE INTO STOCK (ref_producto, cantidad_actual, nivel_minimo, estado) VALUES
(1, 40, 10, 'Optimo'),
(2, 35, 10, 'Optimo'),
(3, 25, 5, 'Optimo'),
(4, 100, 20, 'Optimo');
