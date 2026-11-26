package com.tejogestion.modulo2.modelo;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class ComprobanteVenta {
    private Integer idComprobante;
    private Integer refVenta;
    private String numeroComprobante;
    private Timestamp fechaEmision;
    private BigDecimal valorTotal;

    public ComprobanteVenta() {}

    public ComprobanteVenta(Integer refVenta, String numeroComprobante, BigDecimal valorTotal) {
        this.refVenta = refVenta;
        this.numeroComprobante = numeroComprobante;
        this.valorTotal = valorTotal;
    }

    public String getDetalles() {
        return String.format("Comprobante N° %s | Venta ID: %d | Total: $%s",
                numeroComprobante, refVenta, valorTotal.toPlainString());
    }

    public Integer getIdComprobante() { return idComprobante; }
    public void setIdComprobante(Integer idComprobante) { this.idComprobante = idComprobante; }
    public Integer getRefVenta() { return refVenta; }
    public void setRefVenta(Integer refVenta) { this.refVenta = refVenta; }
    public String getNumeroComprobante() { return numeroComprobante; }
    public void setNumeroComprobante(String numeroComprobante) { this.numeroComprobante = numeroComprobante; }
    public Timestamp getFechaEmision() { return fechaEmision; }
    public void setFechaEmision(Timestamp fechaEmision) { this.fechaEmision = fechaEmision; }
    public BigDecimal getValorTotal() { return valorTotal; }
    public void setValorTotal(BigDecimal valorTotal) { this.valorTotal = valorTotal; }
}
