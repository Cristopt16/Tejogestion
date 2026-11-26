package com.tejogestion.vista.modulo3;

import com.tejogestion.modulo3.controlador.ControladorFixture;
import com.tejogestion.modulo3.controlador.ControladorMarcador;
import com.tejogestion.modulo3.controlador.ControladorTorneo;
import com.tejogestion.modulo3.modelo.Partida;
import com.tejogestion.modulo3.modelo.Torneo;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Vista Swing para el registro de marcadores finales y observaciones de partidas.
 */
public class InterfazMarcador extends JPanel {
    private final ControladorMarcador ctrlMarcador = new ControladorMarcador();
    private final ControladorFixture ctrlFixture = new ControladorFixture();
    private final ControladorTorneo ctrlTorneo = new ControladorTorneo();

    private JComboBox<String> cmbTorneo;
    private List<Torneo> torneosCache = new ArrayList<>();
    private DefaultTableModel modelo;
    private JTable tabla;

    public InterfazMarcador() {
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JLabel lblTitulo = new JLabel("Módulo 03 - Registro de Marcadores Finales", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        add(lblTitulo, BorderLayout.NORTH);

        JPanel pnlTop = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        pnlTop.add(new JLabel("Torneo:"));
        cmbTorneo = new JComboBox<>();
        cmbTorneo.setPreferredSize(new Dimension(300, 26));
        cmbTorneo.addActionListener(e -> cargarPartidas());
        pnlTop.add(cmbTorneo);

        modelo = new DefaultTableModel(
                new String[]{"ID", "Ronda", "Local", "Visitante", "M. Local", "M. Visitante", "Observaciones", "Estado"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabla = new JTable(modelo);
        tabla.setRowHeight(24);

        JPanel pnlCentro = new JPanel(new BorderLayout(5, 5));
        pnlCentro.add(pnlTop, BorderLayout.NORTH);
        pnlCentro.add(new JScrollPane(tabla), BorderLayout.CENTER);
        add(pnlCentro, BorderLayout.CENTER);

        JPanel pnlBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 6));
        JButton btnRegistrar = new JButton("Registrar Marcador Final");
        JButton btnRefrescar = new JButton("Refrescar");

        btnRegistrar.addActionListener(e -> abrirDialogoMarcador());
        btnRefrescar.addActionListener(e -> cargarTorneos());

        pnlBotones.add(btnRegistrar);
        pnlBotones.add(btnRefrescar);
        add(pnlBotones, BorderLayout.SOUTH);

        cargarTorneos();
    }

    public void cargarTorneos() {
        try {
            torneosCache = ctrlTorneo.listarTorneos();
            cmbTorneo.removeAllItems();
            for (Torneo t : torneosCache) {
                cmbTorneo.addItem(t.getIdTorneo() + " - " + t.getNombre());
            }
            cargarPartidas();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error cargando torneos: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cargarPartidas() {
        int idx = cmbTorneo.getSelectedIndex();
        if (idx < 0 || idx >= torneosCache.size()) {
            modelo.setRowCount(0);
            return;
        }
        try {
            List<Partida> partidas = ctrlFixture.listarPartidas(torneosCache.get(idx).getIdTorneo());
            modelo.setRowCount(0);
            for (Partida p : partidas) {
                modelo.addRow(new Object[]{
                    p.getIdPartida(), p.getNumeroRonda(), p.getNombreLocal(), p.getNombreVisitante(),
                    p.getMarcadorLocal() != null ? p.getMarcadorLocal() : "-",
                    p.getMarcadorVisitante() != null ? p.getMarcadorVisitante() : "-",
                    p.getObservaciones() != null ? p.getObservaciones() : "",
                    p.getEstado()
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error cargando partidas: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void abrirDialogoMarcador() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione una partida de la tabla.");
            return;
        }
        int idPartida = (int) modelo.getValueAt(fila, 0);
        String local = (String) modelo.getValueAt(fila, 2);
        String visit = (String) modelo.getValueAt(fila, 3);

        JTextField txtLocal = new JTextField();
        JTextField txtVisit = new JTextField();
        JTextArea txtObs = new JTextArea(3, 22);

        Object[] form = {
            "Partida: " + local + " vs " + visit,
            "Marcador " + local + ":", txtLocal,
            "Marcador " + visit + ":", txtVisit,
            "Observaciones:", new JScrollPane(txtObs)
        };

        int opt = JOptionPane.showConfirmDialog(this, form, "Registrar Marcador Final", JOptionPane.OK_CANCEL_OPTION);
        if (opt == JOptionPane.OK_OPTION) {
            try {
                int ml = Integer.parseInt(txtLocal.getText().trim());
                int mv = Integer.parseInt(txtVisit.getText().trim());
                ctrlMarcador.registrarMarcador(idPartida, ml, mv, txtObs.getText().trim());
                JOptionPane.showMessageDialog(this, "Marcador guardado y tabla de posiciones actualizada.");
                cargarPartidas();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error al registrar marcador: " + ex.getMessage(),
                        "Validación / Bloqueo", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
