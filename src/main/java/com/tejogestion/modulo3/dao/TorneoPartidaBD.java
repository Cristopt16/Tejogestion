package com.tejogestion.modulo3.dao;

import com.tejogestion.config.DriverSQL;
import com.tejogestion.modulo3.modelo.Fixture;
import com.tejogestion.modulo3.modelo.Partida;
import com.tejogestion.modulo3.modelo.TablaPosiciones;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * DAO para fixture, programación de partidas, marcadores y tabla de posiciones.
 */
public class TorneoPartidaBD {

    public boolean existeFixtureTorneo(int idTorneo) throws SQLException {
        String sql = "SELECT COUNT(*) FROM FIXTURE WHERE ref_torneo = ?";
        try (Connection con = DriverSQL.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idTorneo);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    public Integer guardarFixtureYPartidas(Fixture fixture, List<Partida> partidas) throws SQLException {
        String sqlFixture = "INSERT INTO FIXTURE (ref_torneo, modalidad_aplicada, total_rondas, estado) VALUES (?, ?, ?, 'Generado')";
        String sqlPartida = "INSERT INTO PARTIDA (ref_fixture, ref_equipo_local, ref_equipo_visitante, ref_cancha, numero_ronda, fecha_partida, hora_inicio, estado) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?, 'Pendiente')";
        String sqlUpdTorneo = "UPDATE TORNEO SET estado = 'EnCurso' WHERE id_torneo = ?";

        Connection con = null;
        try {
            con = DriverSQL.getConnection();
            con.setAutoCommit(false);

            int idFixture;
            try (PreparedStatement psF = con.prepareStatement(sqlFixture, Statement.RETURN_GENERATED_KEYS)) {
                psF.setInt(1, fixture.getRefTorneo());
                psF.setString(2, fixture.getModalidadAplicada());
                psF.setInt(3, fixture.getTotalRondas());
                psF.executeUpdate();
                try (ResultSet rs = psF.getGeneratedKeys()) {
                    if (!rs.next()) throw new SQLException("Error al generar ID de fixture.");
                    idFixture = rs.getInt(1);
                }
            }
            fixture.setIdFixture(idFixture);

            for (Partida p : partidas) {
                p.setRefFixture(idFixture);
                try (PreparedStatement psP = con.prepareStatement(sqlPartida, Statement.RETURN_GENERATED_KEYS)) {
                    psP.setInt(1, idFixture);
                    psP.setInt(2, p.getRefEquipoLocal());
                    psP.setInt(3, p.getRefEquipoVisitante());
                    if (p.getRefCancha() != null) psP.setInt(4, p.getRefCancha());
                    else psP.setNull(4, Types.INTEGER);
                    psP.setInt(5, p.getNumeroRonda());
                    if (p.getFechaPartida() != null) psP.setDate(6, p.getFechaPartida());
                    else psP.setNull(6, Types.DATE);
                    if (p.getHoraInicio() != null) psP.setTime(7, p.getHoraInicio());
                    else psP.setNull(7, Types.TIME);
                    psP.executeUpdate();
                    try (ResultSet rsP = psP.getGeneratedKeys()) {
                        if (rsP.next()) p.setIdPartida(rsP.getInt(1));
                    }
                }
            }

            try (PreparedStatement psU = con.prepareStatement(sqlUpdTorneo)) {
                psU.setInt(1, fixture.getRefTorneo());
                psU.executeUpdate();
            }

            con.commit();
            return idFixture;
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

    public boolean existeCruceCanchaFechaHora(int idCancha, Date fecha, Time hora, Integer idPartidaExcluir) throws SQLException {
        String sql = "SELECT COUNT(*) FROM PARTIDA WHERE ref_cancha = ? AND fecha_partida = ? AND hora_inicio = ? " +
                     "AND estado <> 'Cancelada' AND id_partida <> ?";
        try (Connection con = DriverSQL.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idCancha);
            ps.setDate(2, fecha);
            ps.setTime(3, hora);
            ps.setInt(4, idPartidaExcluir != null ? idPartidaExcluir : -1);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    public boolean programarPartida(int idPartida, int idCancha, Date fecha, Time hora) throws SQLException {
        String sql = "UPDATE PARTIDA SET ref_cancha = ?, fecha_partida = ?, hora_inicio = ? WHERE id_partida = ?";
        try (Connection con = DriverSQL.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idCancha);
            ps.setDate(2, fecha);
            ps.setTime(3, hora);
            ps.setInt(4, idPartida);
            return ps.executeUpdate() > 0;
        }
    }

    public List<Partida> listarPartidasTorneo(int idTorneo) throws SQLException {
        String sql = "SELECT p.*, el.nombre_equipo AS nombre_local, ev.nombre_equipo AS nombre_visitante, c.nombre AS nombre_cancha " +
                     "FROM PARTIDA p " +
                     "INNER JOIN FIXTURE f ON p.ref_fixture = f.id_fixture " +
                     "INNER JOIN EQUIPO el ON p.ref_equipo_local = el.id_equipo " +
                     "INNER JOIN EQUIPO ev ON p.ref_equipo_visitante = ev.id_equipo " +
                     "LEFT JOIN CANCHA c ON p.ref_cancha = c.id_cancha " +
                     "WHERE f.ref_torneo = ? ORDER BY p.numero_ronda ASC, p.id_partida ASC";
        List<Partida> lista = new ArrayList<>();
        try (Connection con = DriverSQL.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idTorneo);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapPartida(rs));
                }
            }
        }
        return lista;
    }

    public Partida obtenerPartidaPorId(int idPartida) throws SQLException {
        String sql = "SELECT p.*, el.nombre_equipo AS nombre_local, ev.nombre_equipo AS nombre_visitante, c.nombre AS nombre_cancha " +
                     "FROM PARTIDA p " +
                     "INNER JOIN EQUIPO el ON p.ref_equipo_local = el.id_equipo " +
                     "INNER JOIN EQUIPO ev ON p.ref_equipo_visitante = ev.id_equipo " +
                     "LEFT JOIN CANCHA c ON p.ref_cancha = c.id_cancha " +
                     "WHERE p.id_partida = ?";
        try (Connection con = DriverSQL.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idPartida);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapPartida(rs) : null;
            }
        }
    }

    public boolean registrarMarcadorFinal(int idPartida, int marcadorLocal, int marcadorVisitante, String observaciones) throws SQLException {
        String sqlLock = "SELECT p.estado, f.ref_torneo FROM PARTIDA p " +
                         "INNER JOIN FIXTURE f ON p.ref_fixture = f.id_fixture WHERE p.id_partida = ? FOR UPDATE";
        String sqlUpd = "UPDATE PARTIDA SET marcador_local = ?, marcador_visitante = ?, observaciones = ?, estado = 'Finalizada' WHERE id_partida = ?";

        Connection con = null;
        try {
            con = DriverSQL.getConnection();
            con.setAutoCommit(false);

            int refTorneo;
            try (PreparedStatement psL = con.prepareStatement(sqlLock)) {
                psL.setInt(1, idPartida);
                try (ResultSet rs = psL.executeQuery()) {
                    if (!rs.next()) throw new IllegalArgumentException("Partida no encontrada.");
                    if ("Finalizada".equalsIgnoreCase(rs.getString("estado"))) {
                        throw new IllegalStateException("Operación bloqueada: la partida ya está Finalizada.");
                    }
                    refTorneo = rs.getInt("ref_torneo");
                }
            }

            try (PreparedStatement psU = con.prepareStatement(sqlUpd)) {
                psU.setInt(1, marcadorLocal);
                psU.setInt(2, marcadorVisitante);
                if (observaciones != null) psU.setString(3, observaciones);
                else psU.setNull(3, Types.VARCHAR);
                psU.setInt(4, idPartida);
                psU.executeUpdate();
            }

            recalcularTablaPosiciones(con, refTorneo);

            con.commit();
            return true;
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

    private void recalcularTablaPosiciones(Connection con, int refTorneo) throws SQLException {
        Map<Integer, TablaPosiciones> mapa = new HashMap<>();
        String sqlEquipos = "SELECT id_equipo FROM EQUIPO WHERE ref_torneo = ?";
        try (PreparedStatement psE = con.prepareStatement(sqlEquipos)) {
            psE.setInt(1, refTorneo);
            try (ResultSet rs = psE.executeQuery()) {
                while (rs.next()) {
                    int idEq = rs.getInt("id_equipo");
                    mapa.put(idEq, new TablaPosiciones(refTorneo, idEq));
                }
            }
        }

        String sqlPartidas = "SELECT p.ref_equipo_local, p.ref_equipo_visitante, p.marcador_local, p.marcador_visitante " +
                             "FROM PARTIDA p INNER JOIN FIXTURE f ON p.ref_fixture = f.id_fixture " +
                             "WHERE f.ref_torneo = ? AND p.estado = 'Finalizada'";
        try (PreparedStatement psP = con.prepareStatement(sqlPartidas)) {
            psP.setInt(1, refTorneo);
            try (ResultSet rs = psP.executeQuery()) {
                while (rs.next()) {
                    int localId = rs.getInt("ref_equipo_local");
                    int visitId = rs.getInt("ref_equipo_visitante");
                    int ml = rs.getInt("marcador_local");
                    int mv = rs.getInt("marcador_visitante");

                    TablaPosiciones tpLocal = mapa.get(localId);
                    TablaPosiciones tpVisit = mapa.get(visitId);
                    if (tpLocal != null) tpLocal.registrarResultado(ml, mv);
                    if (tpVisit != null) tpVisit.registrarResultado(mv, ml);
                }
            }
        }

        String sqlUpdPos = "UPDATE TABLA_POSICIONES SET partidas_jugadas = ?, partidas_ganadas = ?, partidas_empatadas = ?, " +
                           "partidas_perdidas = ?, puntos_favor = ?, puntos_contra = ?, diferencia_puntos = ?, puntaje_total = ? " +
                           "WHERE ref_torneo = ? AND ref_equipo = ?";
        try (PreparedStatement psU = con.prepareStatement(sqlUpdPos)) {
            for (TablaPosiciones tp : mapa.values()) {
                psU.setInt(1, tp.getPartidasJugadas());
                psU.setInt(2, tp.getPartidasGanadas());
                psU.setInt(3, tp.getPartidasEmpatadas());
                psU.setInt(4, tp.getPartidasPerdidas());
                psU.setInt(5, tp.getPuntosFavor());
                psU.setInt(6, tp.getPuntosContra());
                psU.setInt(7, tp.getDiferenciaPuntos());
                psU.setInt(8, tp.getPuntajeTotal());
                psU.setInt(9, refTorneo);
                psU.setInt(10, tp.getRefEquipo());
                psU.addBatch();
            }
            psU.executeBatch();
        }
    }

    public List<TablaPosiciones> obtenerPosiciones(int idTorneo) throws SQLException {
        String sql = "SELECT tp.*, e.nombre_equipo FROM TABLA_POSICIONES tp " +
                     "INNER JOIN EQUIPO e ON tp.ref_equipo = e.id_equipo " +
                     "WHERE tp.ref_torneo = ? " +
                     "ORDER BY tp.puntaje_total DESC, tp.diferencia_puntos DESC, tp.puntos_favor DESC, e.nombre_equipo ASC";
        List<TablaPosiciones> lista = new ArrayList<>();
        try (Connection con = DriverSQL.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idTorneo);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    TablaPosiciones tp = new TablaPosiciones();
                    tp.setIdPosicion(rs.getInt("id_posicion"));
                    tp.setRefTorneo(rs.getInt("ref_torneo"));
                    tp.setRefEquipo(rs.getInt("ref_equipo"));
                    tp.setNombreEquipo(rs.getString("nombre_equipo"));
                    tp.setPartidasJugadas(rs.getInt("partidas_jugadas"));
                    tp.setPartidasGanadas(rs.getInt("partidas_ganadas"));
                    tp.setPartidasEmpatadas(rs.getInt("partidas_empatadas"));
                    tp.setPartidasPerdidas(rs.getInt("partidas_perdidas"));
                    tp.setPuntosFavor(rs.getInt("puntos_favor"));
                    tp.setPuntosContra(rs.getInt("puntos_contra"));
                    tp.setDiferenciaPuntos(rs.getInt("diferencia_puntos"));
                    tp.setPuntajeTotal(rs.getInt("puntaje_total"));
                    lista.add(tp);
                }
            }
        }
        return lista;
    }

    private Partida mapPartida(ResultSet rs) throws SQLException {
        Partida p = new Partida();
        p.setIdPartida(rs.getInt("id_partida"));
        p.setRefFixture(rs.getInt("ref_fixture"));
        p.setRefEquipoLocal(rs.getInt("ref_equipo_local"));
        p.setRefEquipoVisitante(rs.getInt("ref_equipo_visitante"));
        int refCancha = rs.getInt("ref_cancha");
        p.setRefCancha(rs.wasNull() ? null : refCancha);
        p.setNumeroRonda(rs.getInt("numero_ronda"));
        p.setFechaPartida(rs.getDate("fecha_partida"));
        p.setHoraInicio(rs.getTime("hora_inicio"));
        int ml = rs.getInt("marcador_local");
        p.setMarcadorLocal(rs.wasNull() ? null : ml);
        int mv = rs.getInt("marcador_visitante");
        p.setMarcadorVisitante(rs.wasNull() ? null : mv);
        p.setObservaciones(rs.getString("observaciones"));
        p.setEstado(rs.getString("estado"));
        p.setNombreLocal(rs.getString("nombre_local"));
        p.setNombreVisitante(rs.getString("nombre_visitante"));
        p.setNombreCancha(rs.getString("nombre_cancha"));
        return p;
    }
}
