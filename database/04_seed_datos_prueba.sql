-- ==========================================================
-- DATOS SEMILLA DE PRUEBA (TejoGestion)
-- ==========================================================
CREATE DATABASE IF NOT EXISTS tejogestion CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE tejogestion;

-- Tablas base del Sprint 1 (creación condicional para ejecución idempotente)
CREATE TABLE IF NOT EXISTS ROL (
    id_rol INT(11) NOT NULL AUTO_INCREMENT,
    nombre VARCHAR(50) NOT NULL,
    CONSTRAINT pk_rol PRIMARY KEY (id_rol),
    CONSTRAINT uk_rol_nombre UNIQUE (nombre)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS USUARIO (
    id_usuario INT(11) NOT NULL AUTO_INCREMENT,
    ref_rol INT(11) NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    usuario VARCHAR(50) NOT NULL,
    contrasena VARCHAR(100) NOT NULL,
    estado VARCHAR(20) NOT NULL DEFAULT 'Activo',
    CONSTRAINT pk_usuario PRIMARY KEY (id_usuario),
    CONSTRAINT uk_usuario_login UNIQUE (usuario),
    CONSTRAINT fk_usuario_rol FOREIGN KEY (ref_rol)
        REFERENCES ROL(id_rol) ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS CANCHA (
    id_cancha INT(11) NOT NULL AUTO_INCREMENT,
    nombre VARCHAR(50) NOT NULL,
    estado VARCHAR(20) NOT NULL DEFAULT 'Disponible',
    CONSTRAINT pk_cancha PRIMARY KEY (id_cancha),
    CONSTRAINT uk_cancha_nombre UNIQUE (nombre)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS CLIENTE (
    id_cliente INT(11) NOT NULL AUTO_INCREMENT,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NULL,
    telefono VARCHAR(20) NOT NULL,
    correo VARCHAR(120) NULL,
    CONSTRAINT pk_cliente PRIMARY KEY (id_cliente)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

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
