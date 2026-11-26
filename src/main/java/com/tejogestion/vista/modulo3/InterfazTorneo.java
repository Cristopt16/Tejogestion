package com.tejogestion.vista.modulo3;

import com.tejogestion.config.SesionUsuario;
import com.tejogestion.modulo3.controlador.ControladorTorneo;
import com.tejogestion.modulo3.modelo.Torneo;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Vista Swing para creación y listado de torneos con asignación de canchas.
 */
public class InterfazTorneo extends JPanel {
    private final ControladorTorneo ctrlTorneo = new ControladorTorneo();
    private final int idUsuario;
    private final String rolUsuario;
    private DefaultTableModel modelo;
    private JTable tabla;

    public InterfazTorneo() {
        this(SesionUsuario.getIdUsuario(), SesionUsuario.getRol());
    }

    public InterfazTorneo(int idUsuario, String rolUsuario) {
        this.idUsuario = idUsuario;
        this.rolUsuario = rolUsuario;
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JLabel lblTitulo = new JLabel("Módulo 03 - Administración de Torneos", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        add(lblTitulo, BorderLayout.NORTH);

        modelo = new DefaultTableModel(
                new String[]{"ID", "Nombre", "Modalidad", "Fecha Inicio", "Cupo Máximo", "Disponibles", "Canchas IDs", "Estado"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabla = new JTable(modelo);
        tabla.setRowHeight(24);
        add(new JScrollPane(tabla), BorderLayout.CENTER);

        JPanel pnlBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 6));
        JButton btnCrear = new JButton("Crear Torneo");
        btnCrear.addActionListener(e -> abrirDialogoCrearTorneo());
        JButton btnRefrescar = new JButton("Refrescar Lista");
        btnRefrescar.addActionListener(e -> refrescarTabla());

        if (!"Cliente".equalsIgnoreCase(this.rolUsuario)) {
            pnlBotones.add(btnCrear);
        }
        pnlBotones.add(btnRefrescar);
        add(pnlBotones, BorderLayout.SOUTH);

        refrescarTabla();
    }

    public void refrescarTabla() {
        try {
            List<Torneo> torneos = ctrlTorneo.listarTorneos();
            modelo.setRowCount(0);
            for (Torneo t : torneos) {
                modelo.addRow(new Object[]{
                    t.getIdTorneo(), t.getNombre(), t.getModalidad(), t.getFechaInicio(),
                    t.getCupoMaximo(), t.getCuposDisponibles(), t.getCanchasIds().toString(), t.getEstado()
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al listar torneos: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void abrirDialogoCrearTorneo() {
        JTextField txtNombre = new JTextField();
        JComboBox<String> cmbModalidad = new JComboBox<>(new String[]{"TodosContraTodos", "EliminacionDirecta"});
        JTextField txtInicio = new JTextField(LocalDate.now().toString());
        JTextField txtFin = new JTextField();
        JTextField txtCupo = new JTextField("4");
        JTextArea txtReglamento = new JTextArea(3, 22);

        JPanel pnlCanchas = new JPanel(new FlowLayout(FlowLayout.LEFT));
        List<JCheckBox> checks = new ArrayList<>();
        try {
            for (String[] c : ctrlTorneo.listarCanchasDisponibles()) {
                JCheckBox cb = new JCheckBox(c[1]);
                cb.setActionCommand(c[0]);
                checks.add(cb);
                pnlCanchas.add(cb);
            }
        } catch (Exception ex) {
            pnlCanchas.add(new JLabel("Error cargando canchas: " + ex.getMessage()));
        }

        Object[] form = {
            "Nombre del Torneo:", txtNombre,
            "Modalidad:", cmbModalidad,
            "Fecha Inicio (YYYY-MM-DD):", txtInicio,
            "Fecha Fin (opcional YYYY-MM-DD):", txtFin,
            "Cupo Máximo (>= 2):", txtCupo,
            "Reglamento:", new JScrollPane(txtReglamento),
            "Canchas Asignadas:", pnlCanchas
        };

        int opt = JOptionPane.showConfirmDialog(this, form, "Crear Nuevo Torneo", JOptionPane.OK_CANCEL_OPTION);
        if (opt == JOptionPane.OK_OPTION) {
            try {
                Date fInicio = Date.valueOf(txtInicio.getText().trim());
                Date fFin = txtFin.getText().trim().isEmpty() ? null : Date.valueOf(txtFin.getText().trim());
                int cupo = Integer.parseInt(txtCupo.getText().trim());

                Torneo t = new Torneo(idUsuario, txtNombre.getText().trim(), fInicio, fFin,
                        (String) cmbModalidad.getSelectedItem(), txtReglamento.getText().trim(), cupo);

                List<Integer> canchasIds = new ArrayList<>();
                for (JCheckBox cb : checks) {
                    if (cb.isSelected()) {
                        canchasIds.add(Integer.parseInt(cb.getActionCommand()));
                    }
                }
                t.setCanchasIds(canchasIds);

                Integer id = ctrlTorneo.crearTorneo(t);
                JOptionPane.showMessageDialog(this, "Torneo creado exitosamente (ID: " + id + ").");
                refrescarTabla();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error al crear torneo: " + ex.getMessage(),
                        "Validación / Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
