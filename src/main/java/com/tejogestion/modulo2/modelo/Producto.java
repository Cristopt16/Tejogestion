package com.tejogestion.modulo2.modelo;

import java.math.BigDecimal;

public class Producto {
    private Integer idProducto;
    private String codigo;
    private String nombre;
    private String categoria;
    private BigDecimal precioVenta;
    private Boolean activo;
    private Stock stock;

    public Producto() {
        this.activo = true;
    }

    public Producto(Integer idProducto, String codigo, String nombre, String categoria, BigDecimal precioVenta, Boolean activo) {
        this.idProducto = idProducto;
        this.codigo = codigo;
        this.nombre = nombre;
        this.categoria = categoria;
        this.precioVenta = precioVenta;
        this.activo = activo != null ? activo : true;
    }

    public BigDecimal obtenerPrecio() {
        return this.precioVenta;
    }

    public void actualizarDatos(String nombre, String categoria, BigDecimal precioVenta) {
        this.nombre = nombre;
        this.categoria = categoria;
        this.precioVenta = precioVenta;
    }

    public Integer getIdProducto() { return idProducto; }
    public void setIdProducto(Integer idProducto) { this.idProducto = idProducto; }
    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
    public BigDecimal getPrecioVenta() { return precioVenta; }
    public void setPrecioVenta(BigDecimal precioVenta) { this.precioVenta = precioVenta; }
    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }
    public Stock getStock() { return stock; }
    public void setStock(Stock stock) { this.stock = stock; }
}
