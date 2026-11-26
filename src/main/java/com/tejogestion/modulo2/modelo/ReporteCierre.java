package com.tejogestion.modulo2.modelo;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class ReporteCierre {
    private Integer idReporte;
    private Integer refCierre;
    private Integer totalUnidadesVendidas;
    private BigDecimal totalIngresos;
    private BigDecimal diferencia;
    private Timestamp fechaGeneracion;

    public String getResumen() {
        return String.format("Reporte Cierre #%d | Unidades: %d | Ingresos Sistema: $%s | Diferencia: $%s",
                refCierre, totalUnidadesVendidas, totalIngresos.toPlainString(), diferencia.toPlainString());
    }

    public Integer getIdReporte() { return idReporte; }
    public void setIdReporte(Integer idReporte) { this.idReporte = idReporte; }
    public Integer getRefCierre() { return refCierre; }
    public void setRefCierre(Integer refCierre) { this.refCierre = refCierre; }
    public Integer getTotalUnidadesVendidas() { return totalUnidadesVendidas; }
    public void setTotalUnidadesVendidas(Integer totalUnidadesVendidas) { this.totalUnidadesVendidas = totalUnidadesVendidas; }
    public BigDecimal getTotalIngresos() { return totalIngresos; }
    public void setTotalIngresos(BigDecimal totalIngresos) { this.totalIngresos = totalIngresos; }
    public BigDecimal getDiferencia() { return diferencia; }
    public void setDiferencia(BigDecimal diferencia) { this.diferencia = diferencia; }
    public Timestamp getFechaGeneracion() { return fechaGeneracion; }
    public void setFechaGeneracion(Timestamp fechaGeneracion) { this.fechaGeneracion = fechaGeneracion; }
}
