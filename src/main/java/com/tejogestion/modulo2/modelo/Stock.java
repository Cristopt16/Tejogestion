package com.tejogestion.modulo2.modelo;

public class Stock {
    private Integer idStock;
    private Integer refProducto;
    private Integer cantidadActual;
    private Integer nivelMinimo;
    private String estado; // Optimo, StockBajo, Agotado

    public Stock() {
        this.cantidadActual = 0;
        this.nivelMinimo = 5;
        this.estado = "Agotado";
    }

    public Stock(Integer idStock, Integer refProducto, Integer cantidadActual, Integer nivelMinimo) {
        this.idStock = idStock;
        this.refProducto = refProducto;
        this.cantidadActual = cantidadActual;
        this.nivelMinimo = nivelMinimo;
        actualizarEstado();
    }

    public boolean hayExistencias(int cantidadSolicitada) {
        return this.cantidadActual != null && this.cantidadActual >= cantidadSolicitada && cantidadSolicitada > 0;
    }

    public void sumarUnidades(int cantidad) {
        if (cantidad > 0) {
            this.cantidadActual += cantidad;
            actualizarEstado();
        }
    }

    public void descontarUnidades(int cantidad) {
        if (hayExistencias(cantidad)) {
            this.cantidadActual -= cantidad;
            actualizarEstado();
        } else {
            throw new IllegalStateException("Stock insuficiente para descontar " + cantidad + " unidades.");
        }
    }

    public boolean llegoNivelMinimo() {
        return this.cantidadActual <= this.nivelMinimo;
    }

    public String actualizarEstado() {
        if (this.cantidadActual == null || this.cantidadActual == 0) {
            this.estado = "Agotado";
        } else if (this.cantidadActual <= this.nivelMinimo) {
            this.estado = "StockBajo";
        } else {
            this.estado = "Optimo";
        }
        return this.estado;
    }

    public Integer getIdStock() { return idStock; }
    public void setIdStock(Integer idStock) { this.idStock = idStock; }
    public Integer getRefProducto() { return refProducto; }
    public void setRefProducto(Integer refProducto) { this.refProducto = refProducto; }
    public Integer getCantidadActual() { return cantidadActual; }
    public void setCantidadActual(Integer cantidadActual) { this.cantidadActual = cantidadActual; actualizarEstado(); }
    public Integer getNivelMinimo() { return nivelMinimo; }
    public void setNivelMinimo(Integer nivelMinimo) { this.nivelMinimo = nivelMinimo; actualizarEstado(); }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}
