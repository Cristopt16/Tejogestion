package com.tejogestion.modulo3.validacion;

import java.sql.Date;
import java.sql.Time;

/**
 * Validaciones de negocio para generación y programación de fixtures.
 */
public class ValidacionFixture {

    public static void validarMinimoEquipos(int cantidadEquipos) {
        if (cantidadEquipos < 2) {
            throw new IllegalArgumentException("Se requieren al menos 2 equipos inscritos para generar el fixture.");
        }
    }

    public static void validarProgramacionPartida(Integer idCancha, Date fecha, Time hora, boolean existeCruce) {
        if (idCancha == null || idCancha <= 0) {
            throw new IllegalArgumentException("Debe seleccionar una cancha válida.");
        }
        if (fecha == null || hora == null) {
            throw new IllegalArgumentException("Debe indicar fecha y hora de inicio para la partida.");
        }
        if (existeCruce) {
            throw new IllegalArgumentException("Conflicto de horario: la cancha ya está reservada para esa fecha y hora.");
        }
    }
}
