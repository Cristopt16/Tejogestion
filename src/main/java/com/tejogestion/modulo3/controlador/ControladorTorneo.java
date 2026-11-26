package com.tejogestion.modulo3.controlador;

import com.tejogestion.modulo3.dao.TorneoBD;
import com.tejogestion.modulo3.modelo.Torneo;
import com.tejogestion.modulo3.validacion.ValidacionTorneo;
import java.sql.SQLException;
import java.util.List;

public class ControladorTorneo {
    private final TorneoBD torneoBD = new TorneoBD();

    public Integer crearTorneo(Torneo torneo) throws SQLException {
        ValidacionTorneo.validarTorneo(torneo);
        torneo.setCuposDisponibles(torneo.getCupoMaximo());
        return torneoBD.insertarTorneoConCanchas(torneo);
    }

    public List<Torneo> listarTorneos() throws SQLException {
        return torneoBD.listarTorneos();
    }

    public Torneo obtenerTorneo(int idTorneo) throws SQLException {
        return torneoBD.obtenerTorneoPorId(idTorneo);
    }

    public List<String[]> listarCanchasDisponibles() throws SQLException {
        return torneoBD.listarCanchasDisponibles();
    }

    public List<String[]> listarCanchasTorneo(int idTorneo) throws SQLException {
        return torneoBD.listarCanchasTorneo(idTorneo);
    }
}
