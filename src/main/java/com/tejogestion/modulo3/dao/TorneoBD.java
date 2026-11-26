package com.tejogestion.modulo3.dao;

import com.tejogestion.config.DriverSQL;
import com.tejogestion.modulo3.modelo.Torneo;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para la gestión transaccional de torneos y sus canchas asociadas.
 */
public class TorneoBD {

    public Integer insertarTorneoConCanchas(Torneo t) throws SQLException {
        String sqlTorneo = "INSERT INTO TORNEO (ref_usuario, nombre, fecha_inicio, fecha_fin, modalidad, reglamento, cupo_maximo, cupos_disponibles, estado) " +
                           "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        String sqlCancha = "INSERT INTO TORNEO_CANCHA (ref_torneo, ref_cancha) VALUES (?, ?)";

        Connection con = null;
        try {
            con = DriverSQL.getConnection();
            con.setAutoCommit(false);

            int idTorneo;
            int cuposInit = t.getCuposDisponibles() != null ? t.getCuposDisponibles() : t.getCupoMaximo();
            try (PreparedStatement ps = con.prepareStatement(sqlTorneo, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, t.getRefUsuario());
                ps.setString(2, t.getNombre().trim());
                ps.setDate(3, t.getFechaInicio());
                if (t.getFechaFin() != null) ps.setDate(4, t.getFechaFin());
                else ps.setNull(4, Types.DATE);
                ps.setString(5, t.getModalidad());
                if (t.getReglamento() != null) ps.setString(6, t.getReglamento());
                else ps.setNull(6, Types.VARCHAR);
                ps.setInt(7, t.getCupoMaximo());
                ps.setInt(8, cuposInit);
                ps.setString(9, t.getEstado() != null ? t.getEstado() : "InscripcionesAbiertas");
                ps.executeUpdate();
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (!rs.next()) throw new SQLException("No se generó ID para el torneo.");
                    idTorneo = rs.getInt(1);
                }
            }

            try (PreparedStatement psC = con.prepareStatement(sqlCancha)) {
                for (Integer idCancha : t.getCanchasIds()) {
                    psC.setInt(1, idTorneo);
                    psC.setInt(2, idCancha);
                    psC.addBatch();
                }
                psC.executeBatch();
            }

            con.commit();
            t.setIdTorneo(idTorneo);
            t.setCuposDisponibles(cuposInit);
            return idTorneo;
        } catch (SQLException e) {
            if (con != null) con.rollback();
            throw e;
        } finally {
            if (con != null) {
                con.setAutoCommit(true);
                con.close();
            }
        }
    }

    public List<Torneo> listarTorneos() throws SQLException {
        String sql = "SELECT t.*, GROUP_CONCAT(tc.ref_cancha ORDER BY tc.ref_cancha) AS canchas " +
                     "FROM TORNEO t LEFT JOIN TORNEO_CANCHA tc ON t.id_torneo = tc.ref_torneo " +
                     "GROUP BY t.id_torneo ORDER BY t.id_torneo DESC";
        List<Torneo> lista = new ArrayList<>();
        try (Connection con = DriverSQL.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Torneo t = mapTorneo(rs);
                String canchasStr = rs.getString("canchas");
                if (canchasStr != null && !canchasStr.trim().isEmpty()) {
                    List<Integer> ids = new ArrayList<>();
                    for (String part : canchasStr.split(",")) {
                        ids.add(Integer.parseInt(part.trim()));
                    }
                    t.setCanchasIds(ids);
                }
                lista.add(t);
            }
        }
        return lista;
    }

    public Torneo obtenerTorneoPorId(int idTorneo) throws SQLException {
        String sql = "SELECT * FROM TORNEO WHERE id_torneo = ?";
        String sqlCanchas = "SELECT ref_cancha FROM TORNEO_CANCHA WHERE ref_torneo = ?";
        try (Connection con = DriverSQL.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idTorneo);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                Torneo t = mapTorneo(rs);
                try (PreparedStatement psC = con.prepareStatement(sqlCanchas)) {
                    psC.setInt(1, idTorneo);
                    try (ResultSet rsC = psC.executeQuery()) {
                        List<Integer> ids = new ArrayList<>();
                        while (rsC.next()) ids.add(rsC.getInt("ref_cancha"));
                        t.setCanchasIds(ids);
                    }
                }
                return t;
            }
        }
    }

    public List<String[]> listarCanchasDisponibles() throws SQLException {
        String sql = "SELECT id_cancha, nombre FROM CANCHA WHERE estado = 'Disponible' ORDER BY id_cancha";
        List<String[]> lista = new ArrayList<>();
        try (Connection con = DriverSQL.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new String[]{String.valueOf(rs.getInt("id_cancha")), rs.getString("nombre")});
            }
        }
        return lista;
    }

    public List<String[]> listarCanchasTorneo(int idTorneo) throws SQLException {
        String sql = "SELECT c.id_cancha, c.nombre FROM CANCHA c " +
                     "INNER JOIN TORNEO_CANCHA tc ON c.id_cancha = tc.ref_cancha " +
                     "WHERE tc.ref_torneo = ? ORDER BY c.id_cancha";
        List<String[]> lista = new ArrayList<>();
        try (Connection con = DriverSQL.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idTorneo);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(new String[]{String.valueOf(rs.getInt("id_cancha")), rs.getString("nombre")});
                }
            }
        }
        return lista;
    }

    private Torneo mapTorneo(ResultSet rs) throws SQLException {
        Torneo t = new Torneo();
        t.setIdTorneo(rs.getInt("id_torneo"));
        t.setRefUsuario(rs.getInt("ref_usuario"));
        t.setNombre(rs.getString("nombre"));
        t.setFechaInicio(rs.getDate("fecha_inicio"));
        t.setFechaFin(rs.getDate("fecha_fin"));
        t.setModalidad(rs.getString("modalidad"));
        t.setReglamento(rs.getString("reglamento"));
        t.setCupoMaximo(rs.getInt("cupo_maximo"));
        t.setCuposDisponibles(rs.getInt("cupos_disponibles"));
        t.setEstado(rs.getString("estado"));
        return t;
    }
}
