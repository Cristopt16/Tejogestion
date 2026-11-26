package com.tejogestion.modulo3.validacion;

import com.tejogestion.modulo3.modelo.Torneo;
import java.util.List;

/**
 * Validaciones de negocio para la creación y gestión de torneos.
 */
public class ValidacionTorneo {

    public static void validarTorneo(Torneo torneo) {
        if (torneo == null) {
            throw new IllegalArgumentException("Los datos del torneo son obligatorios.");
        }
        if (torneo.getNombre() == null || torneo.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del torneo es obligatorio.");
        }
        if (torneo.getFechaInicio() == null) {
            throw new IllegalArgumentException("La fecha de inicio del torneo es obligatoria.");
        }
        if (torneo.getFechaFin() != null && torneo.getFechaFin().before(torneo.getFechaInicio())) {
            throw new IllegalArgumentException("La fecha de fin no puede ser anterior a la fecha de inicio.");
        }
        if (torneo.getModalidad() == null || torneo.getModalidad().trim().isEmpty()) {
            throw new IllegalArgumentException("Debe seleccionar una modalidad válida para el torneo.");
        }
        if (torneo.getCupoMaximo() == null || torneo.getCupoMaximo() < 2) {
            throw new IllegalArgumentException("El cupo máximo del torneo debe ser mayor o igual a 2 equipos.");
        }
        validarCanchas(torneo.getCanchasIds());
    }

    public static void validarCanchas(List<Integer> canchasIds) {
        if (canchasIds == null || canchasIds.isEmpty()) {
            throw new IllegalArgumentException("Debe seleccionar al menos una cancha para el torneo.");
        }
    }
}
