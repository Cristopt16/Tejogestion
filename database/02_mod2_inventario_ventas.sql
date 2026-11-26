-- ==========================================================
-- SPRINT 2 - MÓDULO 02: INVENTARIO Y VENTAS (TejoGestion)
-- Responsable: Persona 1
-- Fecha: 2026-11-26
-- Prerrequisitos: Las tablas USUARIO, ROL, CANCHA y CLIENTE
--                 ya deben existir del Sprint 1.
-- ==========================================================
USE tejogestion;

-- 1. PRODUCTO
CREATE TABLE IF NOT EXISTS PRODUCTO (
    id_producto INT(11) NOT NULL AUTO_INCREMENT,
    codigo VARCHAR(30) NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    categoria VARCHAR(50) NOT NULL,
    precio_venta DECIMAL(10,2) NOT NULL,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT pk_producto PRIMARY KEY (id_producto),
    CONSTRAINT uk_producto_codigo UNIQUE (codigo),
    CONSTRAINT uk_producto_nombre UNIQUE (nombre),
    CONSTRAINT chk_producto_precio CHECK (precio_venta > 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 2. STOCK
CREATE TABLE IF NOT EXISTS STOCK (
    id_stock INT(11) NOT NULL AUTO_INCREMENT,
    ref_producto INT(11) NOT NULL,
    cantidad_actual INT(11) NOT NULL DEFAULT 0,
    nivel_minimo INT(11) NOT NULL DEFAULT 5,
    estado VARCHAR(20) NOT NULL DEFAULT 'Optimo',
    CONSTRAINT pk_stock PRIMARY KEY (id_stock),
    CONSTRAINT uk_stock_producto UNIQUE (ref_producto),
    CONSTRAINT fk_stock_producto FOREIGN KEY (ref_producto)
        REFERENCES PRODUCTO(id_producto) ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT chk_stock_cantidad CHECK (cantidad_actual >= 0),
    CONSTRAINT chk_stock_minimo CHECK (nivel_minimo >= 1)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 3. MOVIMIENTO_ENTRADA
CREATE TABLE IF NOT EXISTS MOVIMIENTO_ENTRADA (
    id_movimiento INT(11) NOT NULL AUTO_INCREMENT,
    ref_producto INT(11) NOT NULL,
    ref_usuario INT(11) NOT NULL,
    cantidad INT(11) NOT NULL,
    costo_unitario DECIMAL(10,2) NOT NULL,
    proveedor_factura VARCHAR(80) NOT NULL,
    fecha_hora DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_movimiento PRIMARY KEY (id_movimiento),
    CONSTRAINT fk_mov_producto FOREIGN KEY (ref_producto)
        REFERENCES PRODUCTO(id_producto) ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_mov_usuario FOREIGN KEY (ref_usuario)
        REFERENCES USUARIO(id_usuario) ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT chk_mov_cantidad CHECK (cantidad > 0),
    CONSTRAINT chk_mov_costo CHECK (costo_unitario >= 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 4. ALERTA_BAJO_INVENTARIO
CREATE TABLE IF NOT EXISTS ALERTA_BAJO_INVENTARIO (
    id_alerta INT(11) NOT NULL AUTO_INCREMENT,
    ref_stock INT(11) NOT NULL,
    fecha_hora DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    cantidad_restante INT(11) NOT NULL,
    umbral_configurado INT(11) NOT NULL,
    estado VARCHAR(20) NOT NULL DEFAULT 'Activada',
    CONSTRAINT pk_alerta PRIMARY KEY (id_alerta),
    CONSTRAINT fk_alerta_stock FOREIGN KEY (ref_stock)
        REFERENCES STOCK(id_stock) ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 5. CIERRE_CAJA
CREATE TABLE IF NOT EXISTS CIERRE_CAJA (
    id_cierre INT(11) NOT NULL AUTO_INCREMENT,
    ref_usuario INT(11) NOT NULL,
    fecha_jornada DATE NOT NULL,
    total_ingresos DECIMAL(12,2) NOT NULL,
    efectivo_fisico DECIMAL(12,2) NOT NULL,
    diferencia DECIMAL(12,2) NOT NULL,
    observaciones TEXT NULL,
    estado VARCHAR(20) NOT NULL DEFAULT 'Confirmado',
    CONSTRAINT pk_cierre PRIMARY KEY (id_cierre),
    CONSTRAINT uk_cierre_fecha UNIQUE (fecha_jornada),
    CONSTRAINT fk_cierre_usuario FOREIGN KEY (ref_usuario)
        REFERENCES USUARIO(id_usuario) ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT chk_cierre_ingresos CHECK (total_ingresos >= 0),
    CONSTRAINT chk_cierre_efectivo CHECK (efectivo_fisico >= 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 6. REPORTE_CIERRE
CREATE TABLE IF NOT EXISTS REPORTE_CIERRE (
    id_reporte INT(11) NOT NULL AUTO_INCREMENT,
    ref_cierre INT(11) NOT NULL,
    total_unidades_vendidas INT(11) NOT NULL,
    total_ingresos DECIMAL(12,2) NOT NULL,
    diferencia DECIMAL(12,2) NOT NULL,
    fecha_generacion DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_reporte_cierre PRIMARY KEY (id_reporte),
    CONSTRAINT uk_reporte_cierre UNIQUE (ref_cierre),
    CONSTRAINT fk_reporte_cierre FOREIGN KEY (ref_cierre)
        REFERENCES CIERRE_CAJA(id_cierre) ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT chk_reporte_unidades CHECK (total_unidades_vendidas >= 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 7. VENTA
CREATE TABLE IF NOT EXISTS VENTA (
    id_venta INT(11) NOT NULL AUTO_INCREMENT,
    ref_cliente INT(11) NULL,
    ref_usuario INT(11) NOT NULL,
    ref_cierre INT(11) NULL,
    fecha_hora DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    total DECIMAL(10,2) NOT NULL,
    estado VARCHAR(20) NOT NULL DEFAULT 'Confirmada',
    CONSTRAINT pk_venta PRIMARY KEY (id_venta),
    CONSTRAINT fk_venta_cliente FOREIGN KEY (ref_cliente)
        REFERENCES CLIENTE(id_cliente) ON UPDATE CASCADE ON DELETE SET NULL,
    CONSTRAINT fk_venta_usuario FOREIGN KEY (ref_usuario)
        REFERENCES USUARIO(id_usuario) ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_venta_cierre FOREIGN KEY (ref_cierre)
        REFERENCES CIERRE_CAJA(id_cierre) ON UPDATE CASCADE ON DELETE SET NULL,
    CONSTRAINT chk_venta_total CHECK (total > 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 8. DETALLE_VENTA
CREATE TABLE IF NOT EXISTS DETALLE_VENTA (
    id_detalle INT(11) NOT NULL AUTO_INCREMENT,
    ref_venta INT(11) NOT NULL,
    ref_producto INT(11) NOT NULL,
    cantidad INT(11) NOT NULL,
    precio_unitario DECIMAL(10,2) NOT NULL,
    subtotal DECIMAL(10,2) NOT NULL,
    CONSTRAINT pk_detalle_venta PRIMARY KEY (id_detalle),
    CONSTRAINT fk_detalle_venta FOREIGN KEY (ref_venta)
        REFERENCES VENTA(id_venta) ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT fk_detalle_producto FOREIGN KEY (ref_producto)
        REFERENCES PRODUCTO(id_producto) ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT chk_detalle_cantidad CHECK (cantidad > 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 9. COMPROBANTE_VENTA
CREATE TABLE IF NOT EXISTS COMPROBANTE_VENTA (
    id_comprobante INT(11) NOT NULL AUTO_INCREMENT,
    ref_venta INT(11) NOT NULL,
    numero_comprobante VARCHAR(30) NOT NULL,
    fecha_emision DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    valor_total DECIMAL(10,2) NOT NULL,
    CONSTRAINT pk_comprobante PRIMARY KEY (id_comprobante),
    CONSTRAINT uk_comprobante_venta UNIQUE (ref_venta),
    CONSTRAINT uk_comprobante_numero UNIQUE (numero_comprobante),
    CONSTRAINT fk_comprobante_venta FOREIGN KEY (ref_venta)
        REFERENCES VENTA(id_venta) ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
