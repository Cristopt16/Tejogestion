package com.tejogestion.modulo2.validacion;

import com.tejogestion.modulo2.modelo.DetalleVenta;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;

public class ValidacionVenta {

    public static void validarItemsVenta(List<DetalleVenta> detalles) {
        if (detalles == null || detalles.isEmpty()) {
            throw new IllegalArgumentException("La venta debe contener al menos un producto.");
        }
        for (DetalleVenta d : detalles) {
            if (d.getCantidad() == null || d.getCantidad() <= 0) {
                throw new IllegalArgumentException("Todas las cantidades de venta deben ser mayores a 0.");
            }
        }
    }

    public static void validarEfectivoCierre(Date fecha, BigDecimal efectivoFisico) {
        if (fecha == null) {
            throw new IllegalArgumentException("La fecha de jornada es obligatoria.");
        }
        if (efectivoFisico == null || efectivoFisico.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("El efectivo físico contado no puede ser negativo.");
        }
    }
}
