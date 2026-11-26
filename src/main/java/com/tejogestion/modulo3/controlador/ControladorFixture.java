package com.tejogestion.modulo3.controlador;

import com.tejogestion.modulo3.dao.TorneoBD;
import com.tejogestion.modulo3.dao.TorneoEquipoBD;
import com.tejogestion.modulo3.dao.TorneoPartidaBD;
import com.tejogestion.modulo3.modelo.Equipo;
import com.tejogestion.modulo3.modelo.Fixture;
import com.tejogestion.modulo3.modelo.Partida;
import com.tejogestion.modulo3.modelo.Torneo;
import com.tejogestion.modulo3.validacion.ValidacionFixture;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ControladorFixture {
    private final TorneoEquipoBD equipoBD = new TorneoEquipoBD();
    private final TorneoPartidaBD partidaBD = new TorneoPartidaBD();
    private final TorneoBD torneoBD = new TorneoBD();

    public Integer generarFixture(int idTorneo) throws SQLException {
        if (partidaBD.existeFixtureTorneo(idTorneo)) {
            throw new IllegalStateException("Ya existe un fixture generado para este torneo.");
        }
        Torneo torneo = torneoBD.obtenerTorneoPorId(idTorneo);
        if (torneo == null) {
            throw new IllegalArgumentException("Torneo no encontrado.");
        }
        List<Equipo> equipos = equipoBD.listarEquiposTorneo(idTorneo);
        ValidacionFixture.validarMinimoEquipos(equipos.size());

        String modalidad = torneo.getModalidad();
        List<Partida> partidas;
        int totalRondas;

        if ("EliminacionDirecta".equalsIgnoreCase(modalidad)) {
            partidas = generarEliminacionDirecta(equipos);
            totalRondas = Math.max(1, (int) Math.ceil(Math.log(equipos.size()) / Math.log(2)));
        } else {
            partidas = generarTodosContraTodos(equipos);
            totalRondas = (equipos.size() % 2 == 0) ? (equipos.size() - 1) : equipos.size();
        }

        Fixture fixture = new Fixture(idTorneo, modalidad, totalRondas);
        return partidaBD.guardarFixtureYPartidas(fixture, partidas);
    }

    /**
     * Genera emparejamientos Round-Robin sin repetir cruces.
     */
    private List<Partida> generarTodosContraTodos(List<Equipo> equipos) {
        List<Partida> partidas = new ArrayList<>();
        List<Equipo> rotacion = new ArrayList<>(equipos);
        if (rotacion.size() % 2 != 0) {
            rotacion.add(null); // Descanso (BYE)
        }
        int n = rotacion.size();
        int rondas = n - 1;
        for (int ronda = 1; ronda <= rondas; ronda++) {
            for (int i = 0; i < n / 2; i++) {
                Equipo local = rotacion.get(i);
                Equipo visitante = rotacion.get(n - 1 - i);
                if (local != null && visitante != null) {
                    partidas.add(new Partida(null, local.getIdEquipo(), visitante.getIdEquipo(), ronda));
                }
            }
            Equipo ultimo = rotacion.remove(n - 1);
            rotacion.add(1, ultimo);
        }
        return partidas;
    }

    /**
     * Genera llaves iniciales de eliminación directa.
     */
    private List<Partida> generarEliminacionDirecta(List<Equipo> equipos) {
        List<Partida> partidas = new ArrayList<>();
        List<Equipo> copia = new ArrayList<>(equipos);
        Collections.shuffle(copia);
        for (int i = 0; i + 1 < copia.size(); i += 2) {
            partidas.add(new Partida(null, copia.get(i).getIdEquipo(), copia.get(i + 1).getIdEquipo(), 1));
        }
        return partidas;
    }

    public boolean programarPartida(int idPartida, int idCancha, Date fecha, Time hora) throws SQLException {
        boolean cruce = partidaBD.existeCruceCanchaFechaHora(idCancha, fecha, hora, idPartida);
        ValidacionFixture.validarProgramacionPartida(idCancha, fecha, hora, cruce);
        return partidaBD.programarPartida(idPartida, idCancha, fecha, hora);
    }

    public List<Partida> listarPartidas(int idTorneo) throws SQLException {
        return partidaBD.listarPartidasTorneo(idTorneo);
    }

    public List<String[]> listarCanchasTorneo(int idTorneo) throws SQLException {
        return torneoBD.listarCanchasTorneo(idTorneo);
    }
}
