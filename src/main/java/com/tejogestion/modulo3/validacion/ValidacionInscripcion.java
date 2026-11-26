package com.tejogestion.modulo3.validacion;

import com.tejogestion.modulo3.modelo.Equipo;
import com.tejogestion.modulo3.modelo.Participante;

/**
 * Validaciones de negocio para la inscripción de equipos y participantes.
 */
public class ValidacionInscripcion {

    public static void validarInscripcion(Equipo equipo, int cuposDisponibles) {
        if (equipo == null || equipo.getRefTorneo() == null) {
            throw new IllegalArgumentException("Debe seleccionar un torneo válido.");
        }
        if (equipo.getNombreEquipo() == null || equipo.getNombreEquipo().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del equipo es obligatorio.");
        }
        if (cuposDisponibles <= 0) {
            throw new IllegalStateException("No hay cupos disponibles en el torneo.");
        }
        if (equipo.getParticipantes() == null || equipo.getParticipantes().isEmpty()) {
            throw new IllegalArgumentException("Debe registrar al menos un participante en el equipo.");
        }
        for (Participante p : equipo.getParticipantes()) {
            if (p.getNombreCompleto() == null || p.getNombreCompleto().trim().isEmpty()) {
                throw new IllegalArgumentException("Todos los participantes deben tener nombre completo.");
            }
            if (p.getDocumentoTelefono() == null || p.getDocumentoTelefono().trim().isEmpty()) {
                throw new IllegalArgumentException("Todos los participantes deben tener documento o teléfono.");
            }
        }
    }
}
