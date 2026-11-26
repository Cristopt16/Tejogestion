package com.tejogestion.modulo3.controlador;

import com.tejogestion.modulo3.dao.TorneoPartidaBD;
import com.tejogestion.modulo3.modelo.Partida;
import com.tejogestion.modulo3.validacion.ValidacionMarcador;
import java.sql.SQLException;

public class ControladorMarcador {
    private final TorneoPartidaBD partidaBD = new TorneoPartidaBD();

    public boolean registrarMarcador(int idPartida, int marcadorLocal, int marcadorVisitante, String observaciones) throws SQLException {
        ValidacionMarcador.validarMarcadores(marcadorLocal, marcadorVisitante);
        Partida partida = partidaBD.obtenerPartidaPorId(idPartida);
        if (partida == null) {
            throw new IllegalArgumentException("La partida especificada no existe.");
        }
        ValidacionMarcador.validarPartidaEditable(partida.getEstado());
        return partidaBD.registrarMarcadorFinal(idPartida, marcadorLocal, marcadorVisitante, observaciones);
    }
}
