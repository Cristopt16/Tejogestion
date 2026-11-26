package com.tejogestion.modulo3.validacion;

/**
 * Validaciones de negocio para el registro de marcadores finales.
 */
public class ValidacionMarcador {

    public static void validarMarcadores(Integer marcadorLocal, Integer marcadorVisitante) {
        if (marcadorLocal == null || marcadorLocal < 0) {
            throw new IllegalArgumentException("El marcador local debe ser un entero mayor o igual a 0.");
        }
        if (marcadorVisitante == null || marcadorVisitante < 0) {
            throw new IllegalArgumentException("El marcador visitante debe ser un entero mayor o igual a 0.");
        }
    }

    public static void validarPartidaEditable(String estadoPartida) {
        if ("Finalizada".equalsIgnoreCase(estadoPartida)) {
            throw new IllegalStateException("Operación bloqueada: la partida ya se encuentra en estado Finalizada.");
        }
    }
}
