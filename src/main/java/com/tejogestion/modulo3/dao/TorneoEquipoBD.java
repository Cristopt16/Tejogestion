package com.tejogestion.modulo3.dao;

import com.tejogestion.config.DriverSQL;
import com.tejogestion.modulo3.modelo.Equipo;
import com.tejogestion.modulo3.modelo.Participante;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para la inscripción transaccional de equipos, participantes e inicialización en tabla de posiciones.
 */
public class TorneoEquipoBD {

    public boolean existeEquipoEnTorneo(int idTorneo, String nombreEquipo) throws SQLException {
        String sql = "SELECT COUNT(*) FROM EQUIPO WHERE ref_torneo = ? AND LOWER(nombre_equipo) = LOWER(?)";
        try (Connection con = DriverSQL.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idTorneo);
            ps.setString(2, nombreEquipo.trim());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    public Integer inscribirEquipoTransaccional(Equipo equipo) throws SQLException {
        String sqlLockTorneo = "SELECT cupos_disponibles, estado FROM TORNEO WHERE id_torneo = ? FOR UPDATE";
        String sqlCheckDup = "SELECT COUNT(*) FROM EQUIPO WHERE ref_torneo = ? AND LOWER(nombre_equipo) = LOWER(?)";
        String sqlDescontar = "UPDATE TORNEO SET cupos_disponibles = cupos_disponibles - 1 WHERE id_torneo = ?";
        String sqlEquipo = "INSERT INTO EQUIPO (ref_torneo, nombre_equipo, estado) VALUES (?, ?, 'Inscrito')";
        String sqlPart = "INSERT INTO PARTICIPANTE (ref_equipo, nombre_completo, documento_telefono) VALUES (?, ?, ?)";
        String sqlPos = "INSERT INTO TABLA_POSICIONES (ref_torneo, ref_equipo, partidas_jugadas, partidas_ganadas, " +
                        "partidas_empatadas, partidas_perdidas, puntos_favor, puntos_contra, diferencia_puntos, puntaje_total) " +
                        "VALUES (?, ?, 0, 0, 0, 0, 0, 0, 0, 0)";

        Connection con = null;
        try {
            con = DriverSQL.getConnection();
            con.setAutoCommit(false);

            try (PreparedStatement psL = con.prepareStatement(sqlLockTorneo)) {
                psL.setInt(1, equipo.getRefTorneo());
                try (ResultSet rs = psL.executeQuery()) {
                    if (!rs.next()) {
                        throw new IllegalArgumentException("El torneo especificado no existe.");
                    }
                    int cupos = rs.getInt("cupos_disponibles");
                    if (cupos <= 0) {
                        throw new IllegalStateException("No hay cupos disponibles en este torneo.");
                    }
                }
            }

            try (PreparedStatement psDup = con.prepareStatement(sqlCheckDup)) {
                psDup.setInt(1, equipo.getRefTorneo());
                psDup.setString(2, equipo.getNombreEquipo().trim());
                try (ResultSet rs = psDup.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        throw new IllegalArgumentException("Ya existe un equipo inscrito con el nombre '" + equipo.getNombreEquipo() + "' en este torneo.");
                    }
                }
            }

            int idEquipo;
            try (PreparedStatement psE = con.prepareStatement(sqlEquipo, Statement.RETURN_GENERATED_KEYS)) {
                psE.setInt(1, equipo.getRefTorneo());
                psE.setString(2, equipo.getNombreEquipo().trim());
                psE.executeUpdate();
                try (ResultSet rs = psE.getGeneratedKeys()) {
                    if (!rs.next()) throw new SQLException("Error al generar ID de equipo.");
                    idEquipo = rs.getInt(1);
                }
            }
            equipo.setIdEquipo(idEquipo);

            try (PreparedStatement psP = con.prepareStatement(sqlPart, Statement.RETURN_GENERATED_KEYS)) {
                for (Participante p : equipo.getParticipantes()) {
                    psP.setInt(1, idEquipo);
                    psP.setString(2, p.getNombreCompleto().trim());
                    psP.setString(3, p.getDocumentoTelefono().trim());
                    psP.executeUpdate();
                    try (ResultSet rsP = psP.getGeneratedKeys()) {
                        if (rsP.next()) p.setIdParticipante(rsP.getInt(1));
                    }
                    p.setRefEquipo(idEquipo);
                }
            }

            try (PreparedStatement psPos = con.prepareStatement(sqlPos)) {
                psPos.setInt(1, equipo.getRefTorneo());
                psPos.setInt(2, idEquipo);
                psPos.executeUpdate();
            }

            try (PreparedStatement psDesc = con.prepareStatement(sqlDescontar)) {
                psDesc.setInt(1, equipo.getRefTorneo());
                psDesc.executeUpdate();
            }

            con.commit();
            return idEquipo;
        } catch (Exception e) {
            if (con != null) con.rollback();
            if (e instanceof SQLException) throw (SQLException) e;
            if (e instanceof RuntimeException) throw (RuntimeException) e;
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            if (con != null) {
                con.setAutoCommit(true);
                con.close();
            }
        }
    }

    public List<Equipo> listarEquiposTorneo(int idTorneo) throws SQLException {
        String sql = "SELECT * FROM EQUIPO WHERE ref_torneo = ? ORDER BY id_equipo ASC";
        List<Equipo> lista = new ArrayList<>();
        try (Connection con = DriverSQL.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idTorneo);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Equipo e = new Equipo();
                    e.setIdEquipo(rs.getInt("id_equipo"));
                    e.setRefTorneo(rs.getInt("ref_torneo"));
                    e.setNombreEquipo(rs.getString("nombre_equipo"));
                    e.setFechaInscripcion(rs.getTimestamp("fecha_inscripcion"));
                    e.setEstado(rs.getString("estado"));
                    e.setParticipantes(listarParticipantesEquipo(con, e.getIdEquipo()));
                    lista.add(e);
                }
            }
        }
        return lista;
    }

    private List<Participante> listarParticipantesEquipo(Connection con, int idEquipo) throws SQLException {
        String sql = "SELECT * FROM PARTICIPANTE WHERE ref_equipo = ? ORDER BY id_participante ASC";
        List<Participante> lista = new ArrayList<>();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idEquipo);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Participante p = new Participante();
                    p.setIdParticipante(rs.getInt("id_participante"));
                    p.setRefEquipo(rs.getInt("ref_equipo"));
                    p.setNombreCompleto(rs.getString("nombre_completo"));
                    p.setDocumentoTelefono(rs.getString("documento_telefono"));
                    lista.add(p);
                }
            }
        }
        return lista;
    }
}
