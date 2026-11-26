package com.tejogestion.modulo2.validacion;

import com.tejogestion.modulo2.modelo.MovimientoEntrada;
import com.tejogestion.modulo2.modelo.Producto;
import java.math.BigDecimal;

public class ValidacionInventario {

    public static void validarProductoNuevo(Producto p) {
        if (p == null || p.getCodigo() == null || p.getCodigo().trim().isEmpty()) {
            throw new IllegalArgumentException("El código del producto es obligatorio.");
        }
        if (p.getNombre() == null || p.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre comercial del producto es obligatorio.");
        }
        if (p.getCategoria() == null || p.getCategoria().trim().isEmpty()) {
            throw new IllegalArgumentException("Debe seleccionar una categoría válida.");
        }
        if (p.getPrecioVenta() == null || p.getPrecioVenta().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El precio de venta debe ser mayor a $0.");
        }
    }

    public static void validarDatosIngreso(MovimientoEntrada mov) {
        if (mov == null || mov.getRefProducto() == null) {
            throw new IllegalArgumentException("Debe seleccionar un producto válido.");
        }
        if (mov.getCantidad() == null || mov.getCantidad() <= 0) {
            throw new IllegalArgumentException("La cantidad ingresada debe ser un entero mayor a 0.");
        }
        if (mov.getCostoUnitario() == null || mov.getCostoUnitario().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("El costo unitario no puede ser negativo.");
        }
        if (mov.getProveedorFactura() == null || mov.getProveedorFactura().trim().isEmpty()) {
            throw new IllegalArgumentException("Debe indicar el proveedor o número de factura.");
        }
    }

    public static void validarUmbralEntero(Integer umbral) {
        if (umbral == null || umbral < 1) {
            throw new IllegalArgumentException("El umbral mínimo debe ser un número entero mayor o igual a 1.");
        }
    }
}
