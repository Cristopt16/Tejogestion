package com.tejogestion.modulo2.modelo;

import java.math.BigDecimal;

public class DetalleVenta {
    private Integer idDetalle;
    private Integer refVenta;
    private Integer refProducto;
    private String nombreProducto;
    private Integer cantidad;
    private BigDecimal precioUnitario;
    private BigDecimal subtotal;

    public DetalleVenta() {}

    public DetalleVenta(Integer refProducto, String nombreProducto, Integer cantidad, BigDecimal precioUnitario) {
        this.refProducto = refProducto;
        this.nombreProducto = nombreProducto;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        calcularSubtotal();
    }

    public BigDecimal calcularSubtotal() {
        this.subtotal = precioUnitario.multiply(BigDecimal.valueOf(cantidad));
        return this.subtotal;
    }

    public Integer getIdDetalle() { return idDetalle; }
    public void setIdDetalle(Integer idDetalle) { this.idDetalle = idDetalle; }
    public Integer getRefVenta() { return refVenta; }
    public void setRefVenta(Integer refVenta) { this.refVenta = refVenta; }
    public Integer getRefProducto() { return refProducto; }
    public void setRefProducto(Integer refProducto) { this.refProducto = refProducto; }
    public String getNombreProducto() { return nombreProducto; }
    public void setNombreProducto(String nombreProducto) { this.nombreProducto = nombreProducto; }
    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; calcularSubtotal(); }
    public BigDecimal getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(BigDecimal precioUnitario) { this.precioUnitario = precioUnitario; calcularSubtotal(); }
    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }
}
