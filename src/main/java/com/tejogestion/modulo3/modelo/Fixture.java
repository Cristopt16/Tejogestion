package com.tejogestion.modulo3.modelo;

import java.sql.Timestamp;

/**
 * Representa la cabecera del fixture generado para un torneo.
 */
public class Fixture {
    private Integer idFixture;
    private Integer refTorneo;
    private String modalidadAplicada;
    private Integer totalRondas;
    private Timestamp fechaGeneracion;
    private String estado;

    public Fixture() {
        this.estado = "Generado";
    }

    public Fixture(Integer refTorneo, String modalidadAplicada, Integer totalRondas) {
        this.refTorneo = refTorneo;
        this.modalidadAplicada = modalidadAplicada;
        this.totalRondas = totalRondas;
        this.estado = "Generado";
    }

    public Integer getIdFixture() { return idFixture; }
    public void setIdFixture(Integer idFixture) { this.idFixture = idFixture; }
    public Integer getRefTorneo() { return refTorneo; }
    public void setRefTorneo(Integer refTorneo) { this.refTorneo = refTorneo; }
    public String getModalidadAplicada() { return modalidadAplicada; }
    public void setModalidadAplicada(String modalidadAplicada) { this.modalidadAplicada = modalidadAplicada; }
    public Integer getTotalRondas() { return totalRondas; }
    public void setTotalRondas(Integer totalRondas) { this.totalRondas = totalRondas; }
    public Timestamp getFechaGeneracion() { return fechaGeneracion; }
    public void setFechaGeneracion(Timestamp fechaGeneracion) { this.fechaGeneracion = fechaGeneracion; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}
