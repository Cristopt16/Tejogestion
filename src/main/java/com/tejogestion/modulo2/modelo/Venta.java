package com.tejogestion.modulo2.modelo;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class Venta {
    private Integer idVenta;
    private Integer refCliente; // NULL si es venta general de mostrador
    private Integer refUsuario;
    private Integer refCierre;
    private Timestamp fechaHora;
    private BigDecimal total;
    private String estado;
    private List<DetalleVenta> detalles = new ArrayList<>();

    public Venta() {
        this.estado = "Confirmada";
        this.total = BigDecimal.ZERO;
    }

    public BigDecimal calcularTotal() {
        BigDecimal suma = BigDecimal.ZERO;
        for (DetalleVenta d : detalles) {
            suma = suma.add(d.calcularSubtotal());
        }
        this.total = suma;
        return this.total;
    }

    public void cambiarEstado(String nuevoEstado) {
        this.estado = nuevoEstado;
    }

    public void agregarDetalle(DetalleVenta detalle) {
        this.detalles.add(detalle);
        calcularTotal();
    }

    public Integer getIdVenta() { return idVenta; }
    public void setIdVenta(Integer idVenta) { this.idVenta = idVenta; }
    public Integer getRefCliente() { return refCliente; }
    public void setRefCliente(Integer refCliente) { this.refCliente = refCliente; }
    public Integer getRefUsuario() { return refUsuario; }
    public void setRefUsuario(Integer refUsuario) { this.refUsuario = refUsuario; }
    public Integer getRefCierre() { return refCierre; }
    public void setRefCierre(Integer refCierre) { this.refCierre = refCierre; }
    public Timestamp getFechaHora() { return fechaHora; }
    public void setFechaHora(Timestamp fechaHora) { this.fechaHora = fechaHora; }
    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public List<DetalleVenta> getDetalles() { return detalles; }
    public void setDetalles(List<DetalleVenta> detalles) { this.detalles = detalles; calcularTotal(); }
}
