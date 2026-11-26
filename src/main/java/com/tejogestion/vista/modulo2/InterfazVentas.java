package com.tejogestion.vista.modulo2;

import com.tejogestion.config.SesionUsuario;
import com.tejogestion.modulo2.controlador.ControladorCierreCaja;
import com.tejogestion.modulo2.controlador.ControladorInventario;
import com.tejogestion.modulo2.controlador.ControladorVenta;
import com.tejogestion.modulo2.modelo.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Vista Swing para el registro de ventas con carrito, comprobantes, alertas de bajo stock
 * y modal de Cierre de Caja diario.
 */
public class InterfazVentas extends JPanel {
    private final ControladorVenta ctrlVenta = new ControladorVenta();
    private final ControladorCierreCaja ctrlCierre = new ControladorCierreCaja();
    private final ControladorInventario ctrlInv = new ControladorInventario();
    private final int idUsuario;

    private JComboBox<String> cmbProductos;
    private List<Producto> catalogoActual = new ArrayList<>();
    private JTextField txtCantidad;
    private DefaultTableModel modeloCarrito;
    private JTable tablaCarrito;
    private JLabel lblTotal;
    private Venta ventaActual;

    public InterfazVentas() {
        this(SesionUsuario.getIdUsuario());
    }

    public InterfazVentas(int idUsuario) {
        this.idUsuario = idUsuario;
        this.ventaActual = new Venta();
        this.ventaActual.setRefUsuario(idUsuario);

        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JLabel lblTitulo = new JLabel("Módulo 02 - Punto de Venta y Cierre de Caja", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        add(lblTitulo, BorderLayout.NORTH);

        JPanel pnlAgregar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 5));
        pnlAgregar.setBorder(BorderFactory.createTitledBorder("Agregar Producto al Carrito"));
        pnlAgregar.add(new JLabel("Producto:"));
        cmbProductos = new JComboBox<>();
        cmbProductos.setPreferredSize(new Dimension(280, 26));
        pnlAgregar.add(cmbProductos);

        pnlAgregar.add(new JLabel("Cantidad:"));
        txtCantidad = new JTextField("1", 5);
        pnlAgregar.add(txtCantidad);

        JButton btnAgregar = new JButton("Agregar al Carrito");
        btnAgregar.addActionListener(e -> agregarAlCarrito());
        pnlAgregar.add(btnAgregar);

        JButton btnRecargar = new JButton("Recargar Catálogo");
        btnRecargar.addActionListener(e -> cargarCatalogoProductos());
        pnlAgregar.add(btnRecargar);

        modeloCarrito = new DefaultTableModel(
                new String[]{"ID Prod", "Producto", "Cantidad", "Precio Unit.", "Subtotal"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablaCarrito = new JTable(modeloCarrito);
        tablaCarrito.setRowHeight(24);

        lblTotal = new JLabel("Total Venta: $0.00", SwingConstants.RIGHT);
        lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTotal.setBorder(BorderFactory.createEmptyBorder(6, 0, 6, 10));

        JPanel pnlCentro = new JPanel(new BorderLayout(5, 5));
        pnlCentro.add(pnlAgregar, BorderLayout.NORTH);
        pnlCentro.add(new JScrollPane(tablaCarrito), BorderLayout.CENTER);
        pnlCentro.add(lblTotal, BorderLayout.SOUTH);
        add(pnlCentro, BorderLayout.CENTER);

        JPanel pnlBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 6));
        JButton btnProcesar = new JButton("Confirmar Venta");
        JButton btnQuitar = new JButton("Quitar Ítem");
        JButton btnLimpiar = new JButton("Vaciar Carrito");
        JButton btnCierreCaja = new JButton("Cierre de Caja Diario");

        btnProcesar.addActionListener(e -> procesarVenta());
        btnQuitar.addActionListener(e -> quitarItemSeleccionado());
        btnLimpiar.addActionListener(e -> limpiarCarrito());
        btnCierreCaja.addActionListener(e -> abrirModalCierreCaja());

        pnlBotones.add(btnProcesar);
        pnlBotones.add(btnQuitar);
        pnlBotones.add(btnLimpiar);
        pnlBotones.add(btnCierreCaja);
        add(pnlBotones, BorderLayout.SOUTH);

        cargarCatalogoProductos();
    }

    public void cargarCatalogoProductos() {
        try {
            catalogoActual = ctrlInv.consultarStock("", "Todas", "Todos");
            cmbProductos.removeAllItems();
            for (Producto p : catalogoActual) {
                int stock = p.getStock() != null ? p.getStock().getCantidadActual() : 0;
                cmbProductos.addItem(String.format("%s - %s ($%s | Stock: %d)",
                        p.getCodigo(), p.getNombre(), p.getPrecioVenta().toPlainString(), stock));
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al cargar catálogo: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void agregarAlCarrito() {
        int idx = cmbProductos.getSelectedIndex();
        if (idx < 0 || idx >= catalogoActual.size()) {
            JOptionPane.showMessageDialog(this, "Seleccione un producto válido.");
            return;
        }
        try {
            Producto p = catalogoActual.get(idx);
            int cantidad = Integer.parseInt(txtCantidad.getText().trim());
            if (cantidad <= 0) {
                throw new IllegalArgumentException("La cantidad debe ser mayor a 0.");
            }
            int stockDisp = p.getStock() != null ? p.getStock().getCantidadActual() : 0;
            int yaEnCarrito = 0;
            for (DetalleVenta d : ventaActual.getDetalles()) {
                if (d.getRefProducto().equals(p.getIdProducto())) {
                    yaEnCarrito += d.getCantidad();
                }
            }
            if (cantidad + yaEnCarrito > stockDisp) {
                JOptionPane.showMessageDialog(this,
                        "Stock insuficiente para '" + p.getNombre() + "'. Disponible: " + stockDisp,
                        "Validación de Stock", JOptionPane.WARNING_MESSAGE);
                return;
            }

            DetalleVenta det = new DetalleVenta(p.getIdProducto(), p.getNombre(), cantidad, p.getPrecioVenta());
            ventaActual.agregarDetalle(det);
            actualizarTablaCarrito();
            txtCantidad.setText("1");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al agregar producto: " + ex.getMessage(),
                    "Validación", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void quitarItemSeleccionado() {
        int fila = tablaCarrito.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione una fila del carrito para quitar.");
            return;
        }
        ventaActual.getDetalles().remove(fila);
        ventaActual.calcularTotal();
        actualizarTablaCarrito();
    }

    private void actualizarTablaCarrito() {
        modeloCarrito.setRowCount(0);
        for (DetalleVenta d : ventaActual.getDetalles()) {
            modeloCarrito.addRow(new Object[]{
                d.getRefProducto(), d.getNombreProducto(), d.getCantidad(),
                "$" + d.getPrecioUnitario().toPlainString(),
                "$" + d.getSubtotal().toPlainString()
            });
        }
        lblTotal.setText("Total Venta: $" + ventaActual.getTotal().toPlainString());
    }

    private void procesarVenta() {
        if (ventaActual.getDetalles().isEmpty()) {
            JOptionPane.showMessageDialog(this, "El carrito está vacío.");
            return;
        }
        try {
            ComprobanteVenta comp = ctrlVenta.procesarVenta(ventaActual);
            JOptionPane.showMessageDialog(this,
                    "Venta registrada exitosamente.\n" + comp.getDetalles(),
                    "Comprobante de Venta", JOptionPane.INFORMATION_MESSAGE);

            List<AlertaBajoInventario> alertas = ctrlVenta.getUltimasAlertas();
            if (alertas != null && !alertas.isEmpty()) {
                StringBuilder sb = new StringBuilder("Se generaron alertas de stock bajo:\n\n");
                for (AlertaBajoInventario a : alertas) {
                    sb.append("• ").append(a.generarMensaje()).append("\n");
                }
                JOptionPane.showMessageDialog(this, sb.toString(),
                        "Alerta de Bajo Inventario", JOptionPane.WARNING_MESSAGE);
            }

            limpiarCarrito();
            cargarCatalogoProductos();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "No se pudo procesar la venta: " + ex.getMessage(),
                    "Error en Venta", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limpiarCarrito() {
        ventaActual = new Venta();
        ventaActual.setRefUsuario(idUsuario);
        actualizarTablaCarrito();
    }

    private void abrirModalCierreCaja() {
        JTextField txtFecha = new JTextField(LocalDate.now().toString());
        JTextField txtEfectivo = new JTextField();
        JTextArea txtObs = new JTextArea(3, 22);

        Object[] form = {
            "Fecha Jornada (YYYY-MM-DD):", txtFecha,
            "Efectivo Físico Contado ($):", txtEfectivo,
            "Observaciones:", new JScrollPane(txtObs)
        };

        int opt = JOptionPane.showConfirmDialog(this, form, "Cierre de Caja Diario", JOptionPane.OK_CANCEL_OPTION);
        if (opt == JOptionPane.OK_OPTION) {
            try {
                Date fecha = Date.valueOf(txtFecha.getText().trim());
                BigDecimal efectivo = new BigDecimal(txtEfectivo.getText().trim());
                CierreCaja cierre = ctrlCierre.previsualizarConsolidacion(fecha, efectivo, idUsuario);

                String resumen = String.format(
                        "Fecha: %s\nTotal Ingresos Sistema: $%s\nEfectivo Contado: $%s\nDiferencia: $%s\n\n¿Confirmar cierre definitivo?",
                        fecha, cierre.getTotalIngresos().toPlainString(),
                        cierre.getEfectivoFisico().toPlainString(),
                        cierre.getDiferencia().toPlainString());

                int conf = JOptionPane.showConfirmDialog(this, resumen, "Previsualización de Cierre", JOptionPane.YES_NO_OPTION);
                if (conf == JOptionPane.YES_OPTION) {
                    ReporteCierre reporte = ctrlCierre.confirmarCierre(cierre, txtObs.getText().trim());
                    JOptionPane.showMessageDialog(this, "Cierre confirmado.\n" + reporte.getResumen(),
                            "Reporte de Cierre", JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error en cierre de caja: " + ex.getMessage(),
                        "Error / Bloqueo", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
