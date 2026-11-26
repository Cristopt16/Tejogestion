package com.tejogestion.modulo2.modelo;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class MovimientoEntrada {
    private Integer idMovimiento;
    private Integer refProducto;
    private Integer refUsuario;
    private Integer cantidad;
    private BigDecimal costoUnitario;
    private String proveedorFactura;
    private Timestamp fechaHora;

    public MovimientoEntrada() {}

    public MovimientoEntrada(Integer refProducto, Integer refUsuario, Integer cantidad, BigDecimal costoUnitario, String proveedorFactura) {
        this.refProducto = refProducto;
        this.refUsuario = refUsuario;
        this.cantidad = cantidad;
        this.costoUnitario = costoUnitario;
        this.proveedorFactura = proveedorFactura;
    }

    public BigDecimal calcularCostoTotal() {
        return costoUnitario.multiply(BigDecimal.valueOf(cantidad));
    }

    public Integer getIdMovimiento() { return idMovimiento; }
    public void setIdMovimiento(Integer idMovimiento) { this.idMovimiento = idMovimiento; }
    public Integer getRefProducto() { return refProducto; }
    public void setRefProducto(Integer refProducto) { this.refProducto = refProducto; }
    public Integer getRefUsuario() { return refUsuario; }
    public void setRefUsuario(Integer refUsuario) { this.refUsuario = refUsuario; }
    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }
    public BigDecimal getCostoUnitario() { return costoUnitario; }
    public void setCostoUnitario(BigDecimal costoUnitario) { this.costoUnitario = costoUnitario; }
    public String getProveedorFactura() { return proveedorFactura; }
    public void setProveedorFactura(String proveedorFactura) { this.proveedorFactura = proveedorFactura; }
    public Timestamp getFechaHora() { return fechaHora; }
    public void setFechaHora(Timestamp fechaHora) { this.fechaHora = fechaHora; }
}
