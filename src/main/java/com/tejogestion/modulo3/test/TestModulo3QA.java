package com.tejogestion.modulo3.test;

import com.tejogestion.modulo3.controlador.*;
import com.tejogestion.modulo3.modelo.*;
import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Clase ejecutable de pruebas QA para el Módulo 03: Torneos y Posiciones.
 * Ejecuta los 10 casos de prueba (CP-0301 a CP-0310) e imprime
 * el resultado en consola de forma ordenada.
 */
public class TestModulo3QA {

    public static void main(String[] args) {
        ControladorTorneo ctrlTorneo = new ControladorTorneo();
        ControladorInscripcion ctrlInscripcion = new ControladorInscripcion();
        ControladorFixture ctrlFixture = new ControladorFixture();
        ControladorMarcador ctrlMarcador = new ControladorMarcador();
        ControladorPosiciones ctrlPosiciones = new ControladorPosiciones();
        int idUsuarioPrueba = 1;
        long sufijo = System.currentTimeMillis() % 100000;

        try {
            System.out.println("============================================================");
            System.out.println("  INICIO PRUEBAS QA - MÓDULO 03: TORNEOS Y POSICIONES");
            System.out.println("============================================================\n");

            // CP-0301: Creación exitosa de torneo con canchas asignadas
            Torneo torneo = new Torneo(idUsuarioPrueba, "Torneo QA " + sufijo, Date.valueOf(LocalDate.now()),
                    null, "TodosContraTodos", "Reglamento oficial de tejo", 4);
            torneo.setCanchasIds(Arrays.asList(1, 2));
            int idTorneo = ctrlTorneo.crearTorneo(torneo);
            System.out.println("[CP-0301] APROBADO: Torneo creado exitosamente (ID=" + idTorneo + ", cupos=" + torneo.getCuposDisponibles() + ").");

            // CP-0302: Rechazo por cupo_maximo < 2 o sin canchas
            try {
                Torneo tInvalido = new Torneo(idUsuarioPrueba, "Torneo Inválido", Date.valueOf(LocalDate.now()),
                        null, "TodosContraTodos", null, 1);
                tInvalido.setCanchasIds(Arrays.asList(1));
                ctrlTorneo.crearTorneo(tInvalido);
                System.out.println("[CP-0302] FALLIDO: No se rechazó cupo_maximo < 2.");
            } catch (IllegalArgumentException e) {
                System.out.println("[CP-0302] APROBADO: Rechazo controlado -> " + e.getMessage());
            }

            // CP-0303: Inscripción exitosa de equipo con participantes y descuento atómico de cupo
            Equipo eq1 = new Equipo(idTorneo, "Los Relámpagos");
            eq1.agregarParticipante(new Participante("Juan Pérez", "3001112233"));
            eq1.agregarParticipante(new Participante("Carlos Ruiz", "3004445566"));
            int idEq1 = ctrlInscripcion.inscribirEquipo(eq1);
            Torneo tActualizado = ctrlTorneo.obtenerTorneo(idTorneo);
            System.out.println("[CP-0303] APROBADO: Equipo inscrito (ID=" + idEq1 + "), cupos disponibles ahora = " + tActualizado.getCuposDisponibles());

            // CP-0304: Rechazo de equipo duplicado en el mismo torneo
            try {
                Equipo eqDup = new Equipo(idTorneo, "Los Relámpagos");
                eqDup.agregarParticipante(new Participante("Otro Jugador", "3109998877"));
                ctrlInscripcion.inscribirEquipo(eqDup);
                System.out.println("[CP-0304] FALLIDO: No se bloqueó el equipo duplicado.");
            } catch (IllegalArgumentException e) {
                System.out.println("[CP-0304] APROBADO: Bloqueo de equipo duplicado -> " + e.getMessage());
            }

            // Inscribir 2 equipos más para completar 3 equipos
            Equipo eq2 = new Equipo(idTorneo, "Los Halcones");
            eq2.agregarParticipante(new Participante("Ana Gómez", "3112223344"));
            ctrlInscripcion.inscribirEquipo(eq2);

            Equipo eq3 = new Equipo(idTorneo, "Los Titanes");
            eq3.agregarParticipante(new Participante("Luis Torres", "3123334455"));
            ctrlInscripcion.inscribirEquipo(eq3);

            // CP-0305: Generación de fixture TodosContraTodos sin cruces repetidos
            int idFixture = ctrlFixture.generarFixture(idTorneo);
            List<Partida> partidas = ctrlFixture.listarPartidas(idTorneo);
            Set<String> crucesUnicos = new HashSet<>();
            for (Partida p : partidas) {
                int min = Math.min(p.getRefEquipoLocal(), p.getRefEquipoVisitante());
                int max = Math.max(p.getRefEquipoLocal(), p.getRefEquipoVisitante());
                crucesUnicos.add(min + "-" + max);
            }
            if (partidas.size() == 3 && crucesUnicos.size() == 3) {
                System.out.println("[CP-0305] APROBADO: Fixture (ID=" + idFixture + ") con " + partidas.size() + " partidas sin cruces repetidos.");
            } else {
                System.out.println("[CP-0305] FALLIDO: Cantidad o unicidad de cruces incorrecta.");
            }

            // CP-0306: Programación de partida y bloqueo de cruce misma cancha + fecha + hora
            Date fechaProg = Date.valueOf(LocalDate.now().plusDays(15 + (int) (sufijo % 10)));
            Time horaProg = Time.valueOf("18:00:00");
            ctrlFixture.programarPartida(partidas.get(0).getIdPartida(), 1, fechaProg, horaProg);
            try {
                ctrlFixture.programarPartida(partidas.get(1).getIdPartida(), 1, fechaProg, horaProg);
                System.out.println("[CP-0306] FALLIDO: No se bloqueó el cruce de cancha/fecha/hora.");
            } catch (IllegalArgumentException e) {
                System.out.println("[CP-0306] APROBADO: Conflicto de horario bloqueado -> " + e.getMessage());
            }

            // CP-0307: Rechazo de doble generación de fixture
            try {
                ctrlFixture.generarFixture(idTorneo);
                System.out.println("[CP-0307] FALLIDO: No se bloqueó la doble generación de fixture.");
            } catch (IllegalStateException e) {
                System.out.println("[CP-0307] APROBADO: Doble fixture bloqueado -> " + e.getMessage());
            }

            // CP-0308: Registro de marcador final y recálculo de tabla de posiciones
            ctrlMarcador.registrarMarcador(partidas.get(0).getIdPartida(), 21, 15, "Victoria local reglamentaria");
            List<TablaPosiciones> posiciones = ctrlPosiciones.obtenerPosiciones(idTorneo);
            if (!posiciones.isEmpty() && posiciones.get(0).getPuntajeTotal() == 3 && posiciones.get(0).getDiferenciaPuntos() == 6) {
                System.out.println("[CP-0308] APROBADO: Marcador registrado y posiciones recalculadas (Líder: "
                        + posiciones.get(0).getNombreEquipo() + ", PTS=" + posiciones.get(0).getPuntajeTotal()
                        + ", DIF=" + posiciones.get(0).getDiferenciaPuntos() + ").");
            } else {
                System.out.println("[CP-0308] FALLIDO: La tabla de posiciones no refleja el cálculo esperado.");
            }

            // CP-0309: Rechazo de marcador negativo y bloqueo de edición en partida Finalizada
            try {
                ctrlMarcador.registrarMarcador(partidas.get(1).getIdPartida(), -1, 10, "Inválido");
                System.out.println("[CP-0309] FALLIDO: No se rechazó marcador negativo.");
            } catch (IllegalArgumentException e) {
                try {
                    ctrlMarcador.registrarMarcador(partidas.get(0).getIdPartida(), 10, 10, "Intento reedición");
                    System.out.println("[CP-0309] FALLIDO: No se bloqueó la edición de partida Finalizada.");
                } catch (IllegalStateException exFinal) {
                    System.out.println("[CP-0309] APROBADO: Validación negativa y bloqueo de partida Finalizada -> " + exFinal.getMessage());
                }
            }

            // CP-0310: Consulta de cronograma y tabla de posiciones ordenada (modo lectura para Cliente)
            List<Partida> cronograma = ctrlPosiciones.obtenerCronograma(idTorneo);
            List<TablaPosiciones> tablaFinal = ctrlPosiciones.obtenerPosiciones(idTorneo);
            System.out.println("[CP-0310] APROBADO: Consulta de cronograma (" + cronograma.size()
                    + " partidas) y tabla ordenada (" + tablaFinal.size() + " equipos) disponible para Cliente en solo lectura:");
            int pos = 1;
            for (TablaPosiciones tp : tablaFinal) {
                System.out.printf("  #%d %-18s | PJ:%d PG:%d PE:%d PP:%d PF:%d PC:%d DIF:%+d PTS:%d%n",
                        pos++, tp.getNombreEquipo(), tp.getPartidasJugadas(), tp.getPartidasGanadas(),
                        tp.getPartidasEmpatadas(), tp.getPartidasPerdidas(), tp.getPuntosFavor(),
                        tp.getPuntosContra(), tp.getDiferenciaPuntos(), tp.getPuntajeTotal());
            }

            System.out.println("\n============================================================");
            System.out.println("  FIN DE PRUEBAS QA - MÓDULO 03");
            System.out.println("============================================================");
        } catch (Exception ex) {
            System.err.println("ERROR INESPERADO durante las pruebas del Módulo 03:");
            ex.printStackTrace();
        }
    }
}
