package com.tejogestion.modulo2.controlador;

import com.tejogestion.modulo2.dao.InventarioBD;
import com.tejogestion.modulo2.modelo.AlertaBajoInventario;
import com.tejogestion.modulo2.modelo.Producto;
import com.tejogestion.modulo2.modelo.Stock;
import java.sql.SQLException;

public class ServicioInventario {
    private final InventarioBD inventarioBD = new InventarioBD();

    /**
     * Evalúa el stock de un producto después de una venta.
     * Si la cantidad actual es menor o igual al nivel mínimo,
     * genera e inserta una alerta de bajo inventario.
     */
    public AlertaBajoInventario evaluarStockPostVenta(int idProducto) throws SQLException {
        Producto p = inventarioBD.obtenerProductoConStock(idProducto);
        if (p != null && p.getStock() != null) {
            Stock s = p.getStock();
            if (s.llegoNivelMinimo()) {
                AlertaBajoInventario alerta = new AlertaBajoInventario(
                        s.getIdStock(), p.getNombre(), s.getCantidadActual(), s.getNivelMinimo());
                inventarioBD.insertarAlerta(alerta);
                return alerta;
            }
        }
        return null;
    }
}
