package com.tejogestion.modulo3.controlador;

import com.tejogestion.modulo3.dao.TorneoBD;
import com.tejogestion.modulo3.dao.TorneoEquipoBD;
import com.tejogestion.modulo3.modelo.Equipo;
import com.tejogestion.modulo3.modelo.Torneo;
import com.tejogestion.modulo3.validacion.ValidacionInscripcion;
import java.sql.SQLException;
import java.util.List;

public class ControladorInscripcion {
    private final TorneoEquipoBD equipoBD = new TorneoEquipoBD();
    private final TorneoBD torneoBD = new TorneoBD();

    public Integer inscribirEquipo(Equipo equipo) throws SQLException {
        if (equipo == null || equipo.getRefTorneo() == null) {
            throw new IllegalArgumentException("Debe seleccionar un torneo válido.");
        }
        Torneo torneo = torneoBD.obtenerTorneoPorId(equipo.getRefTorneo());
        if (torneo == null) {
            throw new IllegalArgumentException("Torneo no encontrado.");
        }
        ValidacionInscripcion.validarInscripcion(equipo, torneo.getCuposDisponibles());
        if (equipoBD.existeEquipoEnTorneo(equipo.getRefTorneo(), equipo.getNombreEquipo())) {
            throw new IllegalArgumentException("Ya existe un equipo inscrito con ese nombre en el torneo.");
        }
        return equipoBD.inscribirEquipoTransaccional(equipo);
    }

    public List<Equipo> listarEquipos(int idTorneo) throws SQLException {
        return equipoBD.listarEquiposTorneo(idTorneo);
    }
}
