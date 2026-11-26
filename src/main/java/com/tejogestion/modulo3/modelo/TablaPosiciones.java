package com.tejogestion.modulo3.modelo;

/**
 * Representa la fila de un equipo en la tabla de posiciones de un torneo.
 * Victoria = 3 pts, Empate = 1 pt, Derrota = 0 pts.
 */
public class TablaPosiciones {
    public static final int PUNTOS_VICTORIA = 3;
    public static final int PUNTOS_EMPATE = 1;
    public static final int PUNTOS_DERROTA = 0;

    private Integer idPosicion;
    private Integer refTorneo;
    private Integer refEquipo;
    private String nombreEquipo;
    private int partidasJugadas;
    private int partidasGanadas;
    private int partidasEmpatadas;
    private int partidasPerdidas;
    private int puntosFavor;
    private int puntosContra;
    private int diferenciaPuntos;
    private int puntajeTotal;

    public TablaPosiciones() {}

    public TablaPosiciones(Integer refTorneo, Integer refEquipo) {
        this.refTorneo = refTorneo;
        this.refEquipo = refEquipo;
    }

    public void registrarResultado(int favor, int contra) {
        this.partidasJugadas++;
        this.puntosFavor += favor;
        this.puntosContra += contra;
        if (favor > contra) {
            this.partidasGanadas++;
            this.puntajeTotal += PUNTOS_VICTORIA;
        } else if (favor == contra) {
            this.partidasEmpatadas++;
            this.puntajeTotal += PUNTOS_EMPATE;
        } else {
            this.partidasPerdidas++;
            this.puntajeTotal += PUNTOS_DERROTA;
        }
        recalcularDiferencia();
    }

    public void recalcularDiferencia() {
        this.diferenciaPuntos = this.puntosFavor - this.puntosContra;
    }

    public Integer getIdPosicion() { return idPosicion; }
    public void setIdPosicion(Integer idPosicion) { this.idPosicion = idPosicion; }
    public Integer getRefTorneo() { return refTorneo; }
    public void setRefTorneo(Integer refTorneo) { this.refTorneo = refTorneo; }
    public Integer getRefEquipo() { return refEquipo; }
    public void setRefEquipo(Integer refEquipo) { this.refEquipo = refEquipo; }
    public String getNombreEquipo() { return nombreEquipo; }
    public void setNombreEquipo(String nombreEquipo) { this.nombreEquipo = nombreEquipo; }
    public int getPartidasJugadas() { return partidasJugadas; }
    public void setPartidasJugadas(int partidasJugadas) { this.partidasJugadas = partidasJugadas; }
    public int getPartidasGanadas() { return partidasGanadas; }
    public void setPartidasGanadas(int partidasGanadas) { this.partidasGanadas = partidasGanadas; }
    public int getPartidasEmpatadas() { return partidasEmpatadas; }
    public void setPartidasEmpatadas(int partidasEmpatadas) { this.partidasEmpatadas = partidasEmpatadas; }
    public int getPartidasPerdidas() { return partidasPerdidas; }
    public void setPartidasPerdidas(int partidasPerdidas) { this.partidasPerdidas = partidasPerdidas; }
    public int getPuntosFavor() { return puntosFavor; }
    public void setPuntosFavor(int puntosFavor) { this.puntosFavor = puntosFavor; }
    public int getPuntosContra() { return puntosContra; }
    public void setPuntosContra(int puntosContra) { this.puntosContra = puntosContra; }
    public int getDiferenciaPuntos() { return diferenciaPuntos; }
    public void setDiferenciaPuntos(int diferenciaPuntos) { this.diferenciaPuntos = diferenciaPuntos; }
    public int getPuntajeTotal() { return puntajeTotal; }
    public void setPuntajeTotal(int puntajeTotal) { this.puntajeTotal = puntajeTotal; }
}
