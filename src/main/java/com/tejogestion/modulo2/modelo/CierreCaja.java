package com.tejogestion.modulo2.modelo;

import java.math.BigDecimal;
import java.sql.Date;

public class CierreCaja {
    private Integer idCierre;
    private Integer refUsuario;
    private Date fechaJornada;
    private BigDecimal totalIngresos;
    private BigDecimal efectivoFisico;
    private BigDecimal diferencia;
    private String observaciones;
    private String estado;

    public CierreCaja() {
        this.estado = "Calculado";
    }

    public BigDecimal calcularDiferencia() {
        if (efectivoFisico != null && totalIngresos != null) {
            this.diferencia = efectivoFisico.subtract(totalIngresos);
        }
        return this.diferencia;
    }

    public void registrarObservacion(String obs) {
        this.observaciones = obs;
    }

    public Integer getIdCierre() { return idCierre; }
    public void setIdCierre(Integer idCierre) { this.idCierre = idCierre; }
    public Integer getRefUsuario() { return refUsuario; }
    public void setRefUsuario(Integer refUsuario) { this.refUsuario = refUsuario; }
    public Date getFechaJornada() { return fechaJornada; }
    public void setFechaJornada(Date fechaJornada) { this.fechaJornada = fechaJornada; }
    public BigDecimal getTotalIngresos() { return totalIngresos; }
    public void setTotalIngresos(BigDecimal totalIngresos) { this.totalIngresos = totalIngresos; }
    public BigDecimal getEfectivoFisico() { return efectivoFisico; }
    public void setEfectivoFisico(BigDecimal efectivoFisico) { this.efectivoFisico = efectivoFisico; }
    public BigDecimal getDiferencia() { return diferencia; }
    public void setDiferencia(BigDecimal diferencia) { this.diferencia = diferencia; }
    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}
