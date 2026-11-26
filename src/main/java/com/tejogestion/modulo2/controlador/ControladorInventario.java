package com.tejogestion.modulo2.controlador;

import com.tejogestion.modulo2.dao.InventarioBD;
import com.tejogestion.modulo2.modelo.MovimientoEntrada;
import com.tejogestion.modulo2.modelo.Producto;
import com.tejogestion.modulo2.validacion.ValidacionInventario;
import java.sql.SQLException;
import java.util.List;

public class ControladorInventario {
    private final InventarioBD inventarioBD = new InventarioBD();

    public Integer crearFichaProducto(Producto producto, int umbralInicial) throws SQLException {
        ValidacionInventario.validarProductoNuevo(producto);
        ValidacionInventario.validarUmbralEntero(umbralInicial);
        if (inventarioBD.existeProducto(producto.getCodigo(), producto.getNombre())) {
            throw new IllegalArgumentException("Ya existe un producto con ese código o nombre en el catálogo.");
        }
        return inventarioBD.insertarProductoYStock(producto, umbralInicial);
    }

    public boolean registrarIngresoMercancia(MovimientoEntrada mov) throws SQLException {
        ValidacionInventario.validarDatosIngreso(mov);
        return inventarioBD.registrarIngresoTransaccional(mov);
    }

    public List<Producto> consultarStock(String filtro, String categoria, String estado) throws SQLException {
        return inventarioBD.consultarStockFiltros(filtro, categoria, estado);
    }

    public boolean configurarUmbral(int idProducto, int umbralMinimo) throws SQLException {
        ValidacionInventario.validarUmbralEntero(umbralMinimo);
        return inventarioBD.actualizarUmbralMinimo(idProducto, umbralMinimo);
    }
}
