package com.tejogestion.modulo2.dao;

import com.tejogestion.config.DriverSQL;
import com.tejogestion.modulo2.modelo.*;
import java.math.BigDecimal;
import java.sql.*;

public class VentaBD {

    public ComprobanteVenta insertarVentaTransaccional(Venta venta) throws SQLException {
        String sqlVenta = "INSERT INTO VENTA (ref_cliente, ref_usuario, total, estado) VALUES (?, ?, ?, 'Confirmada')";
        String sqlDet = "INSERT INTO DETALLE_VENTA (ref_venta, ref_producto, cantidad, precio_unitario, subtotal) VALUES (?, ?, ?, ?, ?)";
        String sqlLockStock = "SELECT id_stock, cantidad_actual, nivel_minimo FROM STOCK WHERE ref_producto = ? FOR UPDATE";
        String sqlUpdStock = "UPDATE STOCK SET cantidad_actual = ?, estado = ? WHERE id_stock = ?";
        String sqlComp = "INSERT INTO COMPROBANTE_VENTA (ref_venta, numero_comprobante, valor_total) VALUES (?, ?, ?)";

        Connection con = null;
        try {
            con = DriverSQL.getConnection();
            con.setAutoCommit(false);

            venta.calcularTotal();
            int idVenta;
            try (PreparedStatement psV = con.prepareStatement(sqlVenta, Statement.RETURN_GENERATED_KEYS)) {
                if (venta.getRefCliente() != null) psV.setInt(1, venta.getRefCliente());
                else psV.setNull(1, Types.INTEGER);
                psV.setInt(2, venta.getRefUsuario());
                psV.setBigDecimal(3, venta.getTotal());
                psV.executeUpdate();
                try (ResultSet rs = psV.getGeneratedKeys()) {
                    if (!rs.next()) throw new SQLException("Error al generar ID de venta.");
                    idVenta = rs.getInt(1);
                }
            }
            venta.setIdVenta(idVenta);

            for (DetalleVenta det : venta.getDetalles()) {
                try (PreparedStatement psS = con.prepareStatement(sqlLockStock)) {
                    psS.setInt(1, det.getRefProducto());
                    try (ResultSet rs = psS.executeQuery()) {
                        if (!rs.next()) throw new SQLException("Producto sin stock asociado: ID " + det.getRefProducto());
                        Stock st = new Stock(rs.getInt("id_stock"), det.getRefProducto(),
                                rs.getInt("cantidad_actual"), rs.getInt("nivel_minimo"));
                        if (!st.hayExistencias(det.getCantidad())) {
                            throw new IllegalStateException("Stock insuficiente para el producto ID " + det.getRefProducto());
                        }
                        st.descontarUnidades(det.getCantidad());
                        try (PreparedStatement psUpd = con.prepareStatement(sqlUpdStock)) {
                            psUpd.setInt(1, st.getCantidadActual());
                            psUpd.setString(2, st.getEstado());
                            psUpd.setInt(3, st.getIdStock());
                            psUpd.executeUpdate();
                        }
                    }
                }

                try (PreparedStatement psD = con.prepareStatement(sqlDet)) {
                    psD.setInt(1, idVenta);
                    psD.setInt(2, det.getRefProducto());
                    psD.setInt(3, det.getCantidad());
                    psD.setBigDecimal(4, det.getPrecioUnitario());
                    psD.setBigDecimal(5, det.calcularSubtotal());
                    psD.executeUpdate();
                }
            }

            String numComp = String.format("CMP-2026-%05d", idVenta);
            ComprobanteVenta comp = new ComprobanteVenta(idVenta, numComp, venta.getTotal());
            try (PreparedStatement psC = con.prepareStatement(sqlComp, Statement.RETURN_GENERATED_KEYS)) {
                psC.setInt(1, idVenta);
                psC.setString(2, numComp);
                psC.setBigDecimal(3, venta.getTotal());
                psC.executeUpdate();
                try (ResultSet rs = psC.getGeneratedKeys()) {
                    if (rs.next()) comp.setIdComprobante(rs.getInt(1));
                }
            }

            con.commit();
            return comp;
        } catch (Exception e) {
            if (con != null) con.rollback();
            if (e instanceof SQLException) throw (SQLException) e;
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            if (con != null) { con.setAutoCommit(true); con.close(); }
        }
    }

    public boolean existeCierreFecha(Date fecha) throws SQLException {
        String sql = "SELECT COUNT(*) FROM CIERRE_CAJA WHERE fecha_jornada = ?";
        try (Connection con = DriverSQL.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setDate(1, fecha);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    public BigDecimal consultarTotalIngresosFecha(Date fecha) throws SQLException {
        String sql = "SELECT COALESCE(SUM(total), 0.00) FROM VENTA WHERE DATE(fecha_hora) = ? AND estado <> 'Cancelada'";
        try (Connection con = DriverSQL.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setDate(1, fecha);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getBigDecimal(1) : BigDecimal.ZERO;
            }
        }
    }

    public int consultarUnidadesVendidasFecha(Date fecha) throws SQLException {
        String sql = "SELECT COALESCE(SUM(dv.cantidad), 0) FROM DETALLE_VENTA dv " +
                     "INNER JOIN VENTA v ON dv.ref_venta = v.id_venta " +
                     "WHERE DATE(v.fecha_hora) = ? AND v.estado <> 'Cancelada'";
        try (Connection con = DriverSQL.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setDate(1, fecha);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }

    public ReporteCierre insertarCierreYReporte(CierreCaja cierre) throws SQLException {
        String sqlCierre = "INSERT INTO CIERRE_CAJA (ref_usuario, fecha_jornada, total_ingresos, efectivo_fisico, diferencia, observaciones, estado) " +
                           "VALUES (?, ?, ?, ?, ?, ?, 'Confirmado')";
        String sqlRep = "INSERT INTO REPORTE_CIERRE (ref_cierre, total_unidades_vendidas, total_ingresos, diferencia) VALUES (?, ?, ?, ?)";
        String sqlVincular = "UPDATE VENTA SET ref_cierre = ? WHERE DATE(fecha_hora) = ? AND estado <> 'Cancelada'";

        Connection con = null;
        try {
            con = DriverSQL.getConnection();
            con.setAutoCommit(false);

            int unidades = consultarUnidadesVendidasFecha(cierre.getFechaJornada());
            int idCierre;
            try (PreparedStatement psC = con.prepareStatement(sqlCierre, Statement.RETURN_GENERATED_KEYS)) {
                psC.setInt(1, cierre.getRefUsuario());
                psC.setDate(2, cierre.getFechaJornada());
                psC.setBigDecimal(3, cierre.getTotalIngresos());
                psC.setBigDecimal(4, cierre.getEfectivoFisico());
                psC.setBigDecimal(5, cierre.calcularDiferencia());
                psC.setString(6, cierre.getObservaciones());
                psC.executeUpdate();
                try (ResultSet rs = psC.getGeneratedKeys()) {
                    if (!rs.next()) throw new SQLException("Error al guardar el cierre de caja.");
                    idCierre = rs.getInt(1);
                }
            }
            cierre.setIdCierre(idCierre);
            cierre.setEstado("Confirmado");

            try (PreparedStatement psV = con.prepareStatement(sqlVincular)) {
                psV.setInt(1, idCierre);
                psV.setDate(2, cierre.getFechaJornada());
                psV.executeUpdate();
            }

            ReporteCierre rep = new ReporteCierre();
            rep.setRefCierre(idCierre);
            rep.setTotalUnidadesVendidas(unidades);
            rep.setTotalIngresos(cierre.getTotalIngresos());
            rep.setDiferencia(cierre.getDiferencia());

            try (PreparedStatement psR = con.prepareStatement(sqlRep, Statement.RETURN_GENERATED_KEYS)) {
                psR.setInt(1, idCierre);
                psR.setInt(2, unidades);
                psR.setBigDecimal(3, cierre.getTotalIngresos());
                psR.setBigDecimal(4, cierre.getDiferencia());
                psR.executeUpdate();
                try (ResultSet rs = psR.getGeneratedKeys()) {
                    if (rs.next()) rep.setIdReporte(rs.getInt(1));
                }
            }

            con.commit();
            return rep;
        } catch (SQLException e) {
            if (con != null) con.rollback();
            throw e;
        } finally {
            if (con != null) { con.setAutoCommit(true); con.close(); }
        }
    }
}
