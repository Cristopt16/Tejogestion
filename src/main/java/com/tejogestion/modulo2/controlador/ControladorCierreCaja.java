package com.tejogestion.modulo2.controlador;

import com.tejogestion.modulo2.dao.VentaBD;
import com.tejogestion.modulo2.modelo.CierreCaja;
import com.tejogestion.modulo2.modelo.ReporteCierre;
import com.tejogestion.modulo2.validacion.ValidacionVenta;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.SQLException;

public class ControladorCierreCaja {
    private final VentaBD ventaBD = new VentaBD();

    /**
     * Previsualiza la consolidación de ventas del día para el cierre de caja.
     * Valida que no exista un cierre previo para la fecha indicada.
     */
    public CierreCaja previsualizarConsolidacion(Date fecha, BigDecimal efectivoFisico, int idUsuario) throws SQLException {
        ValidacionVenta.validarEfectivoCierre(fecha, efectivoFisico);
        if (ventaBD.existeCierreFecha(fecha)) {
            throw new IllegalStateException("Ya existe un cierre de caja confirmado para la fecha " + fecha);
        }
        BigDecimal totalIngresos = ventaBD.consultarTotalIngresosFecha(fecha);
        CierreCaja cierre = new CierreCaja();
        cierre.setRefUsuario(idUsuario);
        cierre.setFechaJornada(fecha);
        cierre.setTotalIngresos(totalIngresos);
        cierre.setEfectivoFisico(efectivoFisico);
        cierre.calcularDiferencia();
        return cierre;
    }

    /**
     * Confirma el cierre de caja: inserta CIERRE_CAJA, vincula ventas del día
     * y genera REPORTE_CIERRE. Bloquea doble cierre.
     */
    public ReporteCierre confirmarCierre(CierreCaja cierre, String observaciones) throws SQLException {
        if (ventaBD.existeCierreFecha(cierre.getFechaJornada())) {
            throw new IllegalStateException("Operación bloqueada: la jornada ya fue cerrada previamente.");
        }
        cierre.registrarObservacion(observaciones);
        return ventaBD.insertarCierreYReporte(cierre);
    }
}
