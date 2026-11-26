package com.tejogestion.vista.modulo2;

import com.tejogestion.config.SesionUsuario;
import com.tejogestion.modulo2.controlador.ControladorInventario;
import com.tejogestion.modulo2.modelo.MovimientoEntrada;
import com.tejogestion.modulo2.modelo.Producto;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.util.List;

/**
 * Vista Swing para la gestión de catálogo, stock, ingresos de mercancía y umbrales mínimos.
 */
public class InterfazInventario extends JPanel {
    private final ControladorInventario ctrlInv = new ControladorInventario();
    private final int idUsuario;
    private DefaultTableModel modelo;
    private JTable tabla;
    private JTextField txtFiltro;
    private JComboBox<String> cmbCategoria;
    private JComboBox<String> cmbEstado;

    public InterfazInventario() {
        this(SesionUsuario.getIdUsuario());
    }

    public InterfazInventario(int idUsuario) {
        this.idUsuario = idUsuario;
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JLabel lblTitulo = new JLabel("Módulo 02 - Gestión de Inventario y Stock", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        add(lblTitulo, BorderLayout.NORTH);

        JPanel pnlFiltros = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 5));
        pnlFiltros.setBorder(BorderFactory.createTitledBorder("Filtros Dinámicos"));
        pnlFiltros.add(new JLabel("Buscar (código/nombre):"));
        txtFiltro = new JTextField(16);
        pnlFiltros.add(txtFiltro);

        pnlFiltros.add(new JLabel("Categoría:"));
        cmbCategoria = new JComboBox<>(new String[]{"Todas", "Bebidas", "Snacks", "Deportivo", "Otros"});
        pnlFiltros.add(cmbCategoria);

        pnlFiltros.add(new JLabel("Estado:"));
        cmbEstado = new JComboBox<>(new String[]{"Todos", "Optimo", "StockBajo", "Agotado"});
        pnlFiltros.add(cmbEstado);

        JButton btnFiltrar = new JButton("Filtrar");
        btnFiltrar.addActionListener(e -> refrescarTabla());
        pnlFiltros.add(btnFiltrar);

        JButton btnLimpiar = new JButton("Limpiar");
        btnLimpiar.addActionListener(e -> {
            txtFiltro.setText("");
            cmbCategoria.setSelectedIndex(0);
            cmbEstado.setSelectedIndex(0);
            refrescarTabla();
        });
        pnlFiltros.add(btnLimpiar);

        modelo = new DefaultTableModel(
                new String[]{"ID", "Código", "Nombre", "Categoría", "Precio Venta", "Stock Actual", "Nivel Mínimo", "Estado"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabla = new JTable(modelo);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.setRowHeight(24);

        JPanel pnlCentro = new JPanel(new BorderLayout(5, 5));
        pnlCentro.add(pnlFiltros, BorderLayout.NORTH);
        pnlCentro.add(new JScrollPane(tabla), BorderLayout.CENTER);
        add(pnlCentro, BorderLayout.CENTER);

        JPanel pnlAcciones = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 6));
        JButton btnNuevo = new JButton("Alta de Producto");
        JButton btnIngreso = new JButton("Ingreso de Mercancía");
        JButton btnUmbral = new JButton("Ajustar Umbral Mínimo");
        JButton btnRefrescar = new JButton("Actualizar Lista");

        btnNuevo.addActionListener(e -> abrirDialogoNuevoProducto());
        btnIngreso.addActionListener(e -> abrirDialogoIngreso());
        btnUmbral.addActionListener(e -> abrirDialogoUmbral());
        btnRefrescar.addActionListener(e -> refrescarTabla());

        pnlAcciones.add(btnNuevo);
        pnlAcciones.add(btnIngreso);
        pnlAcciones.add(btnUmbral);
        pnlAcciones.add(btnRefrescar);
        add(pnlAcciones, BorderLayout.SOUTH);

        refrescarTabla();
    }

    public void refrescarTabla() {
        try {
            String filtro = txtFiltro.getText();
            String categoria = (String) cmbCategoria.getSelectedItem();
            String estado = (String) cmbEstado.getSelectedItem();
            List<Producto> lista = ctrlInv.consultarStock(filtro, categoria, estado);
            modelo.setRowCount(0);
            for (Producto p : lista) {
                int cant = p.getStock() != null ? p.getStock().getCantidadActual() : 0;
                int min = p.getStock() != null ? p.getStock().getNivelMinimo() : 0;
                String est = p.getStock() != null ? p.getStock().getEstado() : "N/A";
                modelo.addRow(new Object[]{
                    p.getIdProducto(), p.getCodigo(), p.getNombre(), p.getCategoria(),
                    "$" + p.getPrecioVenta().toPlainString(), cant, min, est
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "No se pudo consultar el inventario: " + ex.getMessage(),
                    "Error de Consulta", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void abrirDialogoNuevoProducto() {
        JTextField txtCodigo = new JTextField();
        JTextField txtNombre = new JTextField();
        JComboBox<String> cmbCat = new JComboBox<>(new String[]{"Bebidas", "Snacks", "Deportivo", "Otros"});
        JTextField txtPrecio = new JTextField();
        JTextField txtUmbral = new JTextField("5");

        Object[] form = {
            "Código:", txtCodigo,
            "Nombre Comercial:", txtNombre,
            "Categoría:", cmbCat,
            "Precio de Venta:", txtPrecio,
            "Umbral Mínimo Inicial:", txtUmbral
        };

        int opt = JOptionPane.showConfirmDialog(this, form, "Registrar Nuevo Producto", JOptionPane.OK_CANCEL_OPTION);
        if (opt == JOptionPane.OK_OPTION) {
            try {
                Producto p = new Producto(null, txtCodigo.getText().trim(), txtNombre.getText().trim(),
                        (String) cmbCat.getSelectedItem(), new BigDecimal(txtPrecio.getText().trim()), true);
                int umbral = Integer.parseInt(txtUmbral.getText().trim());
                Integer id = ctrlInv.crearFichaProducto(p, umbral);
                JOptionPane.showMessageDialog(this, "Producto creado con éxito (ID: " + id + ").");
                refrescarTabla();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error al crear producto: " + ex.getMessage(),
                        "Validación / Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void abrirDialogoIngreso() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione un producto de la tabla primero.");
            return;
        }
        int idProducto = (int) modelo.getValueAt(fila, 0);
        String nombreProd = (String) modelo.getValueAt(fila, 2);

        JTextField txtCantidad = new JTextField();
        JTextField txtCosto = new JTextField();
        JTextField txtFactura = new JTextField();

        Object[] form = {
            "Producto seleccionado: " + nombreProd,
            "Cantidad a ingresar:", txtCantidad,
            "Costo unitario:", txtCosto,
            "Proveedor / N° Factura:", txtFactura
        };

        int opt = JOptionPane.showConfirmDialog(this, form, "Ingreso de Mercancía", JOptionPane.OK_CANCEL_OPTION);
        if (opt == JOptionPane.OK_OPTION) {
            try {
                MovimientoEntrada mov = new MovimientoEntrada(
                        idProducto,
                        idUsuario,
                        Integer.parseInt(txtCantidad.getText().trim()),
                        new BigDecimal(txtCosto.getText().trim()),
                        txtFactura.getText().trim()
                );
                ctrlInv.registrarIngresoMercancia(mov);
                JOptionPane.showMessageDialog(this, "Ingreso de mercancía registrado y stock actualizado.");
                refrescarTabla();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error en ingreso: " + ex.getMessage(),
                        "Validación / Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void abrirDialogoUmbral() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione un producto de la tabla primero.");
            return;
        }
        int idProducto = (int) modelo.getValueAt(fila, 0);
        String nombreProd = (String) modelo.getValueAt(fila, 2);
        String actual = String.valueOf(modelo.getValueAt(fila, 6));

        String nuevo = JOptionPane.showInputDialog(this,
                "Nuevo umbral mínimo para '" + nombreProd + "':", actual);
        if (nuevo != null) {
            try {
                int umbral = Integer.parseInt(nuevo.trim());
                ctrlInv.configurarUmbral(idProducto, umbral);
                JOptionPane.showMessageDialog(this, "Umbral mínimo actualizado correctamente.");
                refrescarTabla();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error al actualizar umbral: " + ex.getMessage(),
                        "Validación / Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
