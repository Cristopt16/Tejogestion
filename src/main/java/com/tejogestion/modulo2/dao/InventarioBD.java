package com.tejogestion.modulo2.dao;

import com.tejogestion.config.DriverSQL;
import com.tejogestion.modulo2.modelo.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InventarioBD {

    public boolean existeProducto(String codigo, String nombre) throws SQLException {
        String sql = "SELECT COUNT(*) FROM PRODUCTO WHERE codigo = ? OR nombre = ?";
        try (Connection con = DriverSQL.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, codigo);
            ps.setString(2, nombre);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    public Integer insertarProductoYStock(Producto p, int nivelMinimoInicial) throws SQLException {
        String sqlProd = "INSERT INTO PRODUCTO (codigo, nombre, categoria, precio_venta, activo) VALUES (?, ?, ?, ?, ?)";
        String sqlStock = "INSERT INTO STOCK (ref_producto, cantidad_actual, nivel_minimo, estado) VALUES (?, 0, ?, 'Agotado')";
        Connection con = null;
        try {
            con = DriverSQL.getConnection();
            con.setAutoCommit(false);

            int idGenerado;
            try (PreparedStatement psProd = con.prepareStatement(sqlProd, Statement.RETURN_GENERATED_KEYS)) {
                psProd.setString(1, p.getCodigo());
                psProd.setString(2, p.getNombre());
                psProd.setString(3, p.getCategoria());
                psProd.setBigDecimal(4, p.getPrecioVenta());
                psProd.setBoolean(5, true);
                psProd.executeUpdate();
                try (ResultSet rs = psProd.getGeneratedKeys()) {
                    if (!rs.next()) throw new SQLException("No se generó ID para el producto.");
                    idGenerado = rs.getInt(1);
                }
            }

            try (PreparedStatement psStock = con.prepareStatement(sqlStock)) {
                psStock.setInt(1, idGenerado);
                psStock.setInt(2, nivelMinimoInicial);
                psStock.executeUpdate();
            }

            con.commit();
            p.setIdProducto(idGenerado);
            return idGenerado;
        } catch (SQLException e) {
            if (con != null) con.rollback();
            throw e;
        } finally {
            if (con != null) { con.setAutoCommit(true); con.close(); }
        }
    }

    public boolean registrarIngresoTransaccional(MovimientoEntrada mov) throws SQLException {
        String sqlMov = "INSERT INTO MOVIMIENTO_ENTRADA (ref_producto, ref_usuario, cantidad, costo_unitario, proveedor_factura) VALUES (?, ?, ?, ?, ?)";
        String sqlSel = "SELECT id_stock, cantidad_actual, nivel_minimo FROM STOCK WHERE ref_producto = ? FOR UPDATE";
        String sqlUpd = "UPDATE STOCK SET cantidad_actual = ?, estado = ? WHERE id_stock = ?";
        Connection con = null;
        try {
            con = DriverSQL.getConnection();
            con.setAutoCommit(false);

            try (PreparedStatement psMov = con.prepareStatement(sqlMov)) {
                psMov.setInt(1, mov.getRefProducto());
                psMov.setInt(2, mov.getRefUsuario());
                psMov.setInt(3, mov.getCantidad());
                psMov.setBigDecimal(4, mov.getCostoUnitario());
                psMov.setString(5, mov.getProveedorFactura());
                psMov.executeUpdate();
            }

            int idStock, cantActual, nivelMin;
            try (PreparedStatement psSel = con.prepareStatement(sqlSel)) {
                psSel.setInt(1, mov.getRefProducto());
                try (ResultSet rs = psSel.executeQuery()) {
                    if (!rs.next()) throw new SQLException("Registro de stock no encontrado para el producto.");
                    idStock = rs.getInt("id_stock");
                    cantActual = rs.getInt("cantidad_actual");
                    nivelMin = rs.getInt("nivel_minimo");
                }
            }

            Stock stockTemp = new Stock(idStock, mov.getRefProducto(), cantActual, nivelMin);
            stockTemp.sumarUnidades(mov.getCantidad());

            try (PreparedStatement psUpd = con.prepareStatement(sqlUpd)) {
                psUpd.setInt(1, stockTemp.getCantidadActual());
                psUpd.setString(2, stockTemp.getEstado());
                psUpd.setInt(3, idStock);
                psUpd.executeUpdate();
            }

            con.commit();
            return true;
        } catch (SQLException e) {
            if (con != null) con.rollback();
            throw e;
        } finally {
            if (con != null) { con.setAutoCommit(true); con.close(); }
        }
    }

    public List<Producto> consultarStockFiltros(String texto, String categoria, String estado) throws SQLException {
        StringBuilder sb = new StringBuilder(
            "SELECT p.id_producto, p.codigo, p.nombre, p.categoria, p.precio_venta, p.activo, " +
            "s.id_stock, s.cantidad_actual, s.nivel_minimo, s.estado AS estado_stock " +
            "FROM PRODUCTO p INNER JOIN STOCK s ON p.id_producto = s.ref_producto WHERE p.activo = TRUE "
        );
        List<Object> params = new ArrayList<>();
        if (texto != null && !texto.trim().isEmpty()) {
            sb.append("AND (p.codigo LIKE ? OR p.nombre LIKE ?) ");
            String like = "%" + texto.trim() + "%";
            params.add(like);
            params.add(like);
        }
        if (categoria != null && !categoria.trim().isEmpty() && !"Todas".equalsIgnoreCase(categoria)) {
            sb.append("AND p.categoria = ? ");
            params.add(categoria.trim());
        }
        if (estado != null && !estado.trim().isEmpty() && !"Todos".equalsIgnoreCase(estado)) {
            sb.append("AND s.estado = ? ");
            params.add(estado.trim());
        }
        sb.append("ORDER BY p.nombre ASC");

        List<Producto> lista = new ArrayList<>();
        try (Connection con = DriverSQL.getConnection();
             PreparedStatement ps = con.prepareStatement(sb.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Producto p = new Producto(
                        rs.getInt("id_producto"),
                        rs.getString("codigo"),
                        rs.getString("nombre"),
                        rs.getString("categoria"),
                        rs.getBigDecimal("precio_venta"),
                        rs.getBoolean("activo")
                    );
                    Stock s = new Stock(
                        rs.getInt("id_stock"),
                        p.getIdProducto(),
                        rs.getInt("cantidad_actual"),
                        rs.getInt("nivel_minimo")
                    );
                    s.setEstado(rs.getString("estado_stock"));
                    p.setStock(s);
                    lista.add(p);
                }
            }
        }
        return lista;
    }

    public boolean actualizarUmbralMinimo(int idProducto, int nuevoUmbral) throws SQLException {
        String sqlSel = "SELECT id_stock, cantidad_actual FROM STOCK WHERE ref_producto = ?";
        String sqlUpd = "UPDATE STOCK SET nivel_minimo = ?, estado = ? WHERE id_stock = ?";
        try (Connection con = DriverSQL.getConnection();
             PreparedStatement psSel = con.prepareStatement(sqlSel)) {
            psSel.setInt(1, idProducto);
            try (ResultSet rs = psSel.executeQuery()) {
                if (!rs.next()) return false;
                Stock s = new Stock(rs.getInt("id_stock"), idProducto, rs.getInt("cantidad_actual"), nuevoUmbral);
                try (PreparedStatement psUpd = con.prepareStatement(sqlUpd)) {
                    psUpd.setInt(1, nuevoUmbral);
                    psUpd.setString(2, s.getEstado());
                    psUpd.setInt(3, s.getIdStock());
                    return psUpd.executeUpdate() > 0;
                }
            }
        }
    }

    public Producto obtenerProductoConStock(int idProducto) throws SQLException {
        String sql = "SELECT p.*, s.id_stock, s.cantidad_actual, s.nivel_minimo, s.estado AS estado_stock " +
                     "FROM PRODUCTO p INNER JOIN STOCK s ON p.id_producto = s.ref_producto WHERE p.id_producto = ?";
        try (Connection con = DriverSQL.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idProducto);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                Producto p = new Producto(rs.getInt("id_producto"), rs.getString("codigo"),
                        rs.getString("nombre"), rs.getString("categoria"),
                        rs.getBigDecimal("precio_venta"), rs.getBoolean("activo"));
                Stock s = new Stock(rs.getInt("id_stock"), p.getIdProducto(),
                        rs.getInt("cantidad_actual"), rs.getInt("nivel_minimo"));
                p.setStock(s);
                return p;
            }
        }
    }

    public void insertarAlerta(AlertaBajoInventario alerta) throws SQLException {
        String sql = "INSERT INTO ALERTA_BAJO_INVENTARIO (ref_stock, cantidad_restante, umbral_configurado, estado) VALUES (?, ?, ?, ?)";
        try (Connection con = DriverSQL.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, alerta.getRefStock());
            ps.setInt(2, alerta.getCantidadRestante());
            ps.setInt(3, alerta.getUmbralConfigurado());
            ps.setString(4, alerta.getEstado());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) alerta.setIdAlerta(rs.getInt(1));
            }
        }
    }
}
