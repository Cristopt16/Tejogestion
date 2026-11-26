package com.tejogestion.modulo3.controlador;

import com.tejogestion.modulo3.dao.TorneoPartidaBD;
import com.tejogestion.modulo3.modelo.Partida;
import com.tejogestion.modulo3.modelo.TablaPosiciones;
import java.sql.SQLException;
import java.util.List;

public class ControladorPosiciones {
    private final TorneoPartidaBD partidaBD = new TorneoPartidaBD();

    public List<TablaPosiciones> obtenerPosiciones(int idTorneo) throws SQLException {
        return partidaBD.obtenerPosiciones(idTorneo);
    }

    public List<Partida> obtenerCronograma(int idTorneo) throws SQLException {
        return partidaBD.listarPartidasTorneo(idTorneo);
    }
}
