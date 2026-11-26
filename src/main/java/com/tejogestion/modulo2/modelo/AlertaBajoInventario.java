package com.tejogestion.modulo2.modelo;

import java.sql.Timestamp;

public class AlertaBajoInventario {
    private Integer idAlerta;
    private Integer refStock;
    private String nombreProducto;
    private Timestamp fechaHora;
    private Integer cantidadRestante;
    private Integer umbralConfigurado;
    private String estado; // Activada, Notificada, Atendida

    public AlertaBajoInventario() {
        this.estado = "Activada";
    }

    public AlertaBajoInventario(Integer refStock, String nombreProducto, Integer cantidadRestante, Integer umbralConfigurado) {
        this.refStock = refStock;
        this.nombreProducto = nombreProducto;
        this.cantidadRestante = cantidadRestante;
        this.umbralConfigurado = umbralConfigurado;
        this.estado = "Activada";
    }

    public String generarMensaje() {
        return String.format("ALERTA STOCK CRÍTICO [%s]: Quedan %d unidades (Umbral mínimo: %d).",
                nombreProducto != null ? nombreProducto : "ID " + refStock, cantidadRestante, umbralConfigurado);
    }

    public Integer getIdAlerta() { return idAlerta; }
    public void setIdAlerta(Integer idAlerta) { this.idAlerta = idAlerta; }
    public Integer getRefStock() { return refStock; }
    public void setRefStock(Integer refStock) { this.refStock = refStock; }
    public String getNombreProducto() { return nombreProducto; }
    public void setNombreProducto(String nombreProducto) { this.nombreProducto = nombreProducto; }
    public Timestamp getFechaHora() { return fechaHora; }
    public void setFechaHora(Timestamp fechaHora) { this.fechaHora = fechaHora; }
    public Integer getCantidadRestante() { return cantidadRestante; }
    public void setCantidadRestante(Integer cantidadRestante) { this.cantidadRestante = cantidadRestante; }
    public Integer getUmbralConfigurado() { return umbralConfigurado; }
    public void setUmbralConfigurado(Integer umbralConfigurado) { this.umbralConfigurado = umbralConfigurado; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}
