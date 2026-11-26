package com.tejogestion.modulo2.controlador;

import com.tejogestion.modulo2.dao.VentaBD;
import com.tejogestion.modulo2.modelo.*;
import com.tejogestion.modulo2.validacion.ValidacionVenta;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ControladorVenta {
    private final VentaBD ventaBD = new VentaBD();
    private final ServicioInventario servicioInv = new ServicioInventario();
    private List<AlertaBajoInventario> ultimasAlertas = new ArrayList<>();

    /**
     * Procesa una venta completa de forma transaccional:
     * 1. Valida los ítems.
     * 2. Ejecuta la venta transaccional (descuento de stock, inserción de registros).
     * 3. Evalúa alertas de bajo inventario por cada producto vendido.
     */
    public ComprobanteVenta procesarVenta(Venta venta) throws SQLException {
        ValidacionVenta.validarItemsVenta(venta.getDetalles());

        ComprobanteVenta comprobante = ventaBD.insertarVentaTransaccional(venta);
        ultimasAlertas.clear();

        for (DetalleVenta det : venta.getDetalles()) {
            AlertaBajoInventario alerta = servicioInv.evaluarStockPostVenta(det.getRefProducto());
            if (alerta != null) {
                ultimasAlertas.add(alerta);
            }
        }
        return comprobante;
    }

    public List<AlertaBajoInventario> getUltimasAlertas() {
        return ultimasAlertas;
    }
}
