package com.tejogestion.modulo3.modelo;

import java.sql.Date;
import java.sql.Time;

/**
 * Representa una partida programada dentro de un fixture de torneo.
 */
public class Partida {
    private Integer idPartida;
    private Integer refFixture;
    private Integer refEquipoLocal;
    private Integer refEquipoVisitante;
    private Integer refCancha;
    private Integer numeroRonda;
    private Date fechaPartida;
    private Time horaInicio;
    private Integer marcadorLocal;
    private Integer marcadorVisitante;
    private String observaciones;
    private String estado;
    private String nombreLocal;
    private String nombreVisitante;
    private String nombreCancha;

    public Partida() {
        this.estado = "Pendiente";
    }

    public Partida(Integer refFixture, Integer refEquipoLocal, Integer refEquipoVisitante, Integer numeroRonda) {
        this.refFixture = refFixture;
        this.refEquipoLocal = refEquipoLocal;
        this.refEquipoVisitante = refEquipoVisitante;
        this.numeroRonda = numeroRonda;
        this.estado = "Pendiente";
    }

    public boolean estaFinalizada() {
        return "Finalizada".equalsIgnoreCase(this.estado);
    }

    public void registrarMarcador(int local, int visitante, String observaciones) {
        if (estaFinalizada()) {
            throw new IllegalStateException("No se puede modificar el marcador de una partida ya finalizada.");
        }
        this.marcadorLocal = local;
        this.marcadorVisitante = visitante;
        this.observaciones = observaciones;
        this.estado = "Finalizada";
    }

    public Integer getIdPartida() { return idPartida; }
    public void setIdPartida(Integer idPartida) { this.idPartida = idPartida; }
    public Integer getRefFixture() { return refFixture; }
    public void setRefFixture(Integer refFixture) { this.refFixture = refFixture; }
    public Integer getRefEquipoLocal() { return refEquipoLocal; }
    public void setRefEquipoLocal(Integer refEquipoLocal) { this.refEquipoLocal = refEquipoLocal; }
    public Integer getRefEquipoVisitante() { return refEquipoVisitante; }
    public void setRefEquipoVisitante(Integer refEquipoVisitante) { this.refEquipoVisitante = refEquipoVisitante; }
    public Integer getRefCancha() { return refCancha; }
    public void setRefCancha(Integer refCancha) { this.refCancha = refCancha; }
    public Integer getNumeroRonda() { return numeroRonda; }
    public void setNumeroRonda(Integer numeroRonda) { this.numeroRonda = numeroRonda; }
    public Date getFechaPartida() { return fechaPartida; }
    public void setFechaPartida(Date fechaPartida) { this.fechaPartida = fechaPartida; }
    public Time getHoraInicio() { return horaInicio; }
    public void setHoraInicio(Time horaInicio) { this.horaInicio = horaInicio; }
    public Integer getMarcadorLocal() { return marcadorLocal; }
    public void setMarcadorLocal(Integer marcadorLocal) { this.marcadorLocal = marcadorLocal; }
    public Integer getMarcadorVisitante() { return marcadorVisitante; }
    public void setMarcadorVisitante(Integer marcadorVisitante) { this.marcadorVisitante = marcadorVisitante; }
    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public String getNombreLocal() { return nombreLocal; }
    public void setNombreLocal(String nombreLocal) { this.nombreLocal = nombreLocal; }
    public String getNombreVisitante() { return nombreVisitante; }
    public void setNombreVisitante(String nombreVisitante) { this.nombreVisitante = nombreVisitante; }
    public String getNombreCancha() { return nombreCancha; }
    public void setNombreCancha(String nombreCancha) { this.nombreCancha = nombreCancha; }
}
