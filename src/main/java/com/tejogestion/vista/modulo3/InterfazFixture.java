package com.tejogestion.vista.modulo3;

import com.tejogestion.modulo3.controlador.ControladorFixture;
import com.tejogestion.modulo3.controlador.ControladorTorneo;
import com.tejogestion.modulo3.modelo.Partida;
import com.tejogestion.modulo3.modelo.Torneo;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Vista Swing para generación automática de fixture y programación de cancha/fecha/hora.
 */
public class InterfazFixture extends JPanel {
    private final ControladorFixture ctrlFixture = new ControladorFixture();
    private final ControladorTorneo ctrlTorneo = new ControladorTorneo();

    private JComboBox<String> cmbTorneo;
    private List<Torneo> torneosCache = new ArrayList<>();
    private DefaultTableModel modelo;
    private JTable tabla;

    public InterfazFixture() {
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JLabel lblTitulo = new JLabel("Módulo 03 - Generación de Fixture y Programación", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        add(lblTitulo, BorderLayout.NORTH);

        JPanel pnlTop = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        pnlTop.add(new JLabel("Torneo:"));
        cmbTorneo = new JComboBox<>();
        cmbTorneo.setPreferredSize(new Dimension(300, 26));
        cmbTorneo.addActionListener(e -> cargarPartidas());
        pnlTop.add(cmbTorneo);

        modelo = new DefaultTableModel(
                new String[]{"ID Partida", "Ronda", "Local", "Visitante", "Cancha", "Fecha", "Hora", "Estado"}, 0) {
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
        JButton btnGenerar = new JButton("Generar Fixture Automático");
        JButton btnProgramar = new JButton("Asignar Cancha / Fecha / Hora");
        JButton btnRefrescar = new JButton("Refrescar");

        btnGenerar.addActionListener(e -> generarFixture());
        btnProgramar.addActionListener(e -> abrirDialogoProgramar());
        btnRefrescar.addActionListener(e -> cargarTorneos());

        pnlBotones.add(btnGenerar);
        pnlBotones.add(btnProgramar);
        pnlBotones.add(btnRefrescar);
        add(pnlBotones, BorderLayout.SOUTH);

        cargarTorneos();
    }

    public void cargarTorneos() {
        try {
            torneosCache = ctrlTorneo.listarTorneos();
            cmbTorneo.removeAllItems();
            for (Torneo t : torneosCache) {
                cmbTorneo.addItem(t.getIdTorneo() + " - " + t.getNombre() + " [" + t.getModalidad() + "]");
            }
            cargarPartidas();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error cargando torneos: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private Torneo getTorneoSeleccionado() {
        int idx = cmbTorneo.getSelectedIndex();
        return (idx >= 0 && idx < torneosCache.size()) ? torneosCache.get(idx) : null;
    }

    private void cargarPartidas() {
        Torneo sel = getTorneoSeleccionado();
        if (sel == null) {
            modelo.setRowCount(0);
            return;
        }
        try {
            List<Partida> partidas = ctrlFixture.listarPartidas(sel.getIdTorneo());
            modelo.setRowCount(0);
            for (Partida p : partidas) {
                modelo.addRow(new Object[]{
                    p.getIdPartida(), p.getNumeroRonda(), p.getNombreLocal(), p.getNombreVisitante(),
                    p.getNombreCancha() != null ? p.getNombreCancha() : "Por asignar",
                    p.getFechaPartida() != null ? p.getFechaPartida() : "Por asignar",
                    p.getHoraInicio() != null ? p.getHoraInicio() : "Por asignar",
                    p.getEstado()
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error cargando partidas: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void generarFixture() {
        Torneo sel = getTorneoSeleccionado();
        if (sel == null) {
            JOptionPane.showMessageDialog(this, "Seleccione un torneo.");
            return;
        }
        try {
            Integer idFixture = ctrlFixture.generarFixture(sel.getIdTorneo());
            JOptionPane.showMessageDialog(this, "Fixture generado correctamente (ID: " + idFixture + ").");
            cargarPartidas();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "No se pudo generar el fixture: " + ex.getMessage(),
                    "Validación / Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void abrirDialogoProgramar() {
        int fila = tabla.getSelectedRow();
        Torneo sel = getTorneoSeleccionado();
        if (fila < 0 || sel == null) {
            JOptionPane.showMessageDialog(this, "Seleccione una partida de la tabla.");
            return;
        }
        int idPartida = (int) modelo.getValueAt(fila, 0);

        JComboBox<String> cmbCancha = new JComboBox<>();
        List<String[]> canchas = new ArrayList<>();
        try {
            canchas = ctrlFixture.listarCanchasTorneo(sel.getIdTorneo());
            for (String[] c : canchas) {
                cmbCancha.addItem(c[0] + " - " + c[1]);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error cargando canchas del torneo: " + ex.getMessage());
            return;
        }
        if (canchas.isEmpty()) {
            JOptionPane.showMessageDialog(this, "El torneo no tiene canchas asociadas.");
            return;
        }

        JTextField txtFecha = new JTextField(LocalDate.now().plusDays(1).toString());
        JTextField txtHora = new JTextField("15:00:00");

        Object[] form = {
            "Partida ID: " + idPartida + " (" + modelo.getValueAt(fila, 2) + " vs " + modelo.getValueAt(fila, 3) + ")",
            "Cancha:", cmbCancha,
            "Fecha (YYYY-MM-DD):", txtFecha,
            "Hora Inicio (HH:MM:SS):", txtHora
        };

        int opt = JOptionPane.showConfirmDialog(this, form, "Programar Partida", JOptionPane.OK_CANCEL_OPTION);
        if (opt == JOptionPane.OK_OPTION) {
            try {
                int idxC = cmbCancha.getSelectedIndex();
                int idCancha = Integer.parseInt(canchas.get(idxC)[0]);
                Date fecha = Date.valueOf(txtFecha.getText().trim());
                Time hora = Time.valueOf(txtHora.getText().trim());

                ctrlFixture.programarPartida(idPartida, idCancha, fecha, hora);
                JOptionPane.showMessageDialog(this, "Partida programada correctamente.");
                cargarPartidas();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error al programar partida: " + ex.getMessage(),
                        "Conflicto / Validación", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
