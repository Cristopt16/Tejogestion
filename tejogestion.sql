-- ==========================================================
-- SPRINT 1 - BASE DE DATOS: TejoGestion
-- Crea la BD y las tablas fundacionales: ROL, USUARIO, CANCHA,
-- CLIENTE, RESERVA.  Todas InnoDB + utf8mb4 para garantizar
-- compatibilidad de llaves foráneas con los módulos 02 y 03.
-- ==========================================================
CREATE DATABASE IF NOT EXISTS tejogestion
    CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE tejogestion;

-- 1. ROL
CREATE TABLE IF NOT EXISTS ROL (
    id_rol INT(11) NOT NULL AUTO_INCREMENT,
    nombre VARCHAR(50) NOT NULL,
    CONSTRAINT pk_rol PRIMARY KEY (id_rol),
    CONSTRAINT uk_rol_nombre UNIQUE (nombre)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 2. USUARIO
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

-- 3. CLIENTE
CREATE TABLE IF NOT EXISTS CLIENTE (
    id_cliente INT(11) NOT NULL AUTO_INCREMENT,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NULL,
    telefono VARCHAR(20) NOT NULL,
    correo VARCHAR(120) NULL,
    CONSTRAINT pk_cliente PRIMARY KEY (id_cliente)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 4. CANCHA
CREATE TABLE IF NOT EXISTS CANCHA (
    id_cancha INT(11) NOT NULL AUTO_INCREMENT,
    nombre VARCHAR(50) NOT NULL,
    estado VARCHAR(20) NOT NULL DEFAULT 'Disponible',
    CONSTRAINT pk_cancha PRIMARY KEY (id_cancha),
    CONSTRAINT uk_cancha_nombre UNIQUE (nombre)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 5. RESERVA
CREATE TABLE IF NOT EXISTS RESERVA (
    id_reserva INT(11) NOT NULL AUTO_INCREMENT,
    fecha DATE NOT NULL,
    hora_inicio TIME NOT NULL,
    hora_fin TIME NOT NULL,
    estado VARCHAR(20) NOT NULL DEFAULT 'Activa',
    id_cliente INT(11) NOT NULL,
    id_cancha INT(11) NOT NULL,
    CONSTRAINT pk_reserva PRIMARY KEY (id_reserva),
    CONSTRAINT fk_reserva_cliente FOREIGN KEY (id_cliente)
        REFERENCES CLIENTE(id_cliente) ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_reserva_cancha FOREIGN KEY (id_cancha)
        REFERENCES CANCHA(id_cancha) ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT chk_horario CHECK (hora_fin > hora_inicio)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
