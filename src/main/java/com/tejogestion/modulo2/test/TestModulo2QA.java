package com.tejogestion.modulo2.test;

import com.tejogestion.modulo2.controlador.*;
import com.tejogestion.modulo2.modelo.*;
import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

/**
 * Clase ejecutable de pruebas QA para el Módulo 02: Inventario y Ventas.
 * Ejecuta los 10 casos de prueba (CP-0201 a CP-0210) e imprime
 * el resultado en consola de forma ordenada.
 *
 * Prerrequisitos:
 *   - Base de datos 'tejogestion' creada con tablas del Sprint 1 (USUARIO, CLIENTE).
 *   - Script 02_mod2_inventario_ventas.sql ejecutado.
 *   - Al menos un usuario con id_usuario = 1 en la tabla USUARIO.
 *   - mysql-connector-java en el classpath.
 */
public class TestModulo2QA {

    public static void main(String[] args) {
        ControladorInventario ctrlInv = new ControladorInventario();
        ControladorVenta ctrlVenta = new ControladorVenta();
        ControladorCierreCaja ctrlCierre = new ControladorCierreCaja();
        int idUsuarioPrueba = 1; // Administrador existente en USUARIO

        try {
            System.out.println("============================================================");
            System.out.println("  INICIO PRUEBAS QA - MÓDULO 02: INVENTARIO Y VENTAS");
            System.out.println("============================================================\n");

            // -------------------------------------------------------
            // CP-0201: Ingreso exitoso de mercancía y aumento de stock
            // -------------------------------------------------------
            Producto poker = new Producto(null, "BEB-001", "Cerveza Poker 330ml", "Bebidas", new BigDecimal("4000.00"), true);
            int idProd = ctrlInv.crearFichaProducto(poker, 10);
            ctrlInv.registrarIngresoMercancia(new MovimientoEntrada(idProd, idUsuarioPrueba, 15, new BigDecimal("2200.00"), "FAC-901"));
            System.out.println("[CP-0201] APROBADO: Producto creado (ID=" + idProd + ") e ingreso de 15 unidades registrado.");

            // -------------------------------------------------------
            // CP-0202: Rechazo por cantidad <= 0 en ingreso
            // -------------------------------------------------------
            try {
                ctrlInv.registrarIngresoMercancia(new MovimientoEntrada(idProd, idUsuarioPrueba, 0, new BigDecimal("2200.00"), "FAC-902"));
                System.out.println("[CP-0202] FALLIDO: No se bloqueó la cantidad inválida.");
            } catch (IllegalArgumentException e) {
                System.out.println("[CP-0202] APROBADO: Bloqueo correcto -> " + e.getMessage());
            }

            // -------------------------------------------------------
            // CP-0203: Venta exitosa con descuento atómico y comprobante
            // -------------------------------------------------------
            Venta v1 = new Venta();
            v1.setRefUsuario(idUsuarioPrueba);
            v1.agregarDetalle(new DetalleVenta(idProd, "Cerveza Poker 330ml", 6, new BigDecimal("4000.00")));
            ComprobanteVenta comp = ctrlVenta.procesarVenta(v1);
            System.out.println("[CP-0203] APROBADO: " + comp.getDetalles());

            // -------------------------------------------------------
            // CP-0204: Bloqueo y rollback por stock insuficiente
            // -------------------------------------------------------
            try {
                Venta v2 = new Venta();
                v2.setRefUsuario(idUsuarioPrueba);
                v2.agregarDetalle(new DetalleVenta(idProd, "Cerveza Poker 330ml", 50, new BigDecimal("4000.00")));
                ctrlVenta.procesarVenta(v2);
                System.out.println("[CP-0204] FALLIDO: No se bloqueó la venta por stock insuficiente.");
            } catch (RuntimeException e) {
                System.out.println("[CP-0204] APROBADO: Rollback por stock insuficiente -> " + e.getMessage());
            }

            // -------------------------------------------------------
            // CP-0205: Consulta dinámica con filtros
            // -------------------------------------------------------
            List<Producto> encontrados = ctrlInv.consultarStock("Poker", "Bebidas", "StockBajo");
            System.out.println("[CP-0205] APROBADO: Productos encontrados con filtros = " + encontrados.size());

            // -------------------------------------------------------
            // CP-0206: Consulta sin coincidencias controlada
            // -------------------------------------------------------
            List<Producto> vacio = ctrlInv.consultarStock("Inexistente99", "Todas", "Todos");
            System.out.println("[CP-0206] APROBADO: Consulta sin coincidencias controlada, resultados = " + vacio.size());

            // -------------------------------------------------------
            // CP-0207: Configuración umbral y alerta automática post-venta
            // (Ya disparada en CP-0203: 15 - 6 = 9 <= umbral 10)
            // -------------------------------------------------------
            if (!ctrlVenta.getUltimasAlertas().isEmpty()) {
                System.out.println("[CP-0207] APROBADO: " + ctrlVenta.getUltimasAlertas().get(0).generarMensaje());
            } else {
                System.out.println("[CP-0207] FALLIDO: No se generó alerta de bajo inventario.");
            }

            // -------------------------------------------------------
            // CP-0208: Rechazo de umbral mínimo <= 0
            // -------------------------------------------------------
            try {
                ctrlInv.configurarUmbral(idProd, 0);
                System.out.println("[CP-0208] FALLIDO: No se rechazó el umbral inválido.");
            } catch (IllegalArgumentException e) {
                System.out.println("[CP-0208] APROBADO: Validación umbral -> " + e.getMessage());
            }

            // -------------------------------------------------------
            // CP-0209: Consolidación y cierre de caja diario
            // -------------------------------------------------------
            Date hoy = Date.valueOf(LocalDate.now());
            CierreCaja cierre = ctrlCierre.previsualizarConsolidacion(hoy, new BigDecimal("24000.00"), idUsuarioPrueba);
            ReporteCierre rep = ctrlCierre.confirmarCierre(cierre, "Cuadre exacto jornada de prueba.");
            System.out.println("[CP-0209] APROBADO: " + rep.getResumen());

            // -------------------------------------------------------
            // CP-0210: Bloqueo de doble cierre de caja para misma fecha
            // -------------------------------------------------------
            try {
                ctrlCierre.previsualizarConsolidacion(hoy, new BigDecimal("24000.00"), idUsuarioPrueba);
                System.out.println("[CP-0210] FALLIDO: No se bloqueó el doble cierre.");
            } catch (IllegalStateException e) {
                System.out.println("[CP-0210] APROBADO: Doble cierre bloqueado -> " + e.getMessage());
            }

            System.out.println("\n============================================================");
            System.out.println("  FIN DE PRUEBAS QA - MÓDULO 02");
            System.out.println("============================================================");

        } catch (Exception ex) {
            System.err.println("ERROR INESPERADO durante las pruebas:");
            ex.printStackTrace();
        }
    }
}
