package com.tejogestion.vista.modulo3;

import com.tejogestion.config.SesionUsuario;
import com.tejogestion.modulo3.controlador.ControladorInscripcion;
import com.tejogestion.modulo3.controlador.ControladorTorneo;
import com.tejogestion.modulo3.modelo.Equipo;
import com.tejogestion.modulo3.modelo.Participante;
import com.tejogestion.modulo3.modelo.Torneo;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Vista Swing para inscripción de equipos y participantes mostrando cupos restantes.
 */
public class InterfazInscripcion extends JPanel {
    private final ControladorInscripcion ctrlInscripcion = new ControladorInscripcion();
    private final ControladorTorneo ctrlTorneo = new ControladorTorneo();
    private final String rolUsuario;

    private JComboBox<String> cmbTorneo;
    private List<Torneo> torneosCache = new ArrayList<>();
    private JLabel lblCupos;
    private DefaultTableModel modelo;
    private JTable tabla;

    public InterfazInscripcion() {
        this(SesionUsuario.getRol());
    }

    public InterfazInscripcion(String rolUsuario) {
        this.rolUsuario = rolUsuario;
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JLabel lblTitulo = new JLabel("Módulo 03 - Inscripción de Equipos y Participantes", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        add(lblTitulo, BorderLayout.NORTH);

        JPanel pnlTop = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        pnlTop.add(new JLabel("Torneo:"));
        cmbTorneo = new JComboBox<>();
        cmbTorneo.setPreferredSize(new Dimension(280, 26));
        cmbTorneo.addActionListener(e -> cargarEquiposDelTorneo());
        pnlTop.add(cmbTorneo);

        lblCupos = new JLabel("Cupos Restantes: -");
        lblCupos.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblCupos.setForeground(new Color(0, 102, 51));
        pnlTop.add(lblCupos);

        modelo = new DefaultTableModel(
                new String[]{"ID Equipo", "Nombre Equipo", "Participantes", "Fecha Inscripción", "Estado"}, 0) {
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
        JButton btnInscribir = new JButton("Inscribir Equipo");
        btnInscribir.addActionListener(e -> abrirDialogoInscripcion());
        JButton btnRefrescar = new JButton("Refrescar");
        btnRefrescar.addActionListener(e -> cargarTorneos());

        if (!"Cliente".equalsIgnoreCase(this.rolUsuario)) {
            pnlBotones.add(btnInscribir);
        }
        pnlBotones.add(btnRefrescar);
        add(pnlBotones, BorderLayout.SOUTH);

        cargarTorneos();
    }

    public void cargarTorneos() {
        try {
            torneosCache = ctrlTorneo.listarTorneos();
            cmbTorneo.removeAllItems();
            for (Torneo t : torneosCache) {
                cmbTorneo.addItem(t.getIdTorneo() + " - " + t.getNombre() + " (" + t.getEstado() + ")");
            }
            cargarEquiposDelTorneo();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error cargando torneos: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private Torneo getTorneoSeleccionado() {
        int idx = cmbTorneo.getSelectedIndex();
        return (idx >= 0 && idx < torneosCache.size()) ? torneosCache.get(idx) : null;
    }

    private void cargarEquiposDelTorneo() {
        Torneo sel = getTorneoSeleccionado();
        if (sel == null) {
            lblCupos.setText("Cupos Restantes: -");
            modelo.setRowCount(0);
            return;
        }
        try {
            Torneo actualizado = ctrlTorneo.obtenerTorneo(sel.getIdTorneo());
            int disp = actualizado != null ? actualizado.getCuposDisponibles() : sel.getCuposDisponibles();
            int max = actualizado != null ? actualizado.getCupoMaximo() : sel.getCupoMaximo();
            lblCupos.setText("Cupos Restantes: " + disp + " / " + max);

            List<Equipo> equipos = ctrlInscripcion.listarEquipos(sel.getIdTorneo());
            modelo.setRowCount(0);
            for (Equipo eq : equipos) {
                StringBuilder parts = new StringBuilder();
                for (Participante p : eq.getParticipantes()) {
                    if (parts.length() > 0) parts.append(", ");
                    parts.append(p.getNombreCompleto()).append(" (").append(p.getDocumentoTelefono()).append(")");
                }
                modelo.addRow(new Object[]{
                    eq.getIdEquipo(), eq.getNombreEquipo(), parts.toString(), eq.getFechaInscripcion(), eq.getEstado()
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error cargando equipos: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void abrirDialogoInscripcion() {
        Torneo sel = getTorneoSeleccionado();
        if (sel == null) {
            JOptionPane.showMessageDialog(this, "Seleccione un torneo primero.");
            return;
        }

        JTextField txtEquipo = new JTextField();
        JTextArea txtParticipantes = new JTextArea(5, 28);
        JLabel lblAyuda = new JLabel("Un participante por línea con formato: Nombre Completo, Documento/Teléfono");

        Object[] form = {
            "Torneo: " + sel.getNombre(),
            "Nombre del Equipo:", txtEquipo,
            lblAyuda,
            new JScrollPane(txtParticipantes)
        };

        int opt = JOptionPane.showConfirmDialog(this, form, "Inscribir Equipo", JOptionPane.OK_CANCEL_OPTION);
        if (opt == JOptionPane.OK_OPTION) {
            try {
                Equipo eq = new Equipo(sel.getIdTorneo(), txtEquipo.getText().trim());
                for (String linea : txtParticipantes.getText().split("\\r?\\n")) {
                    String ln = linea.trim();
                    if (ln.isEmpty()) continue;
                    String[] partes = ln.split(",", 2);
                    String nombre = partes[0].trim();
                    String docTel = partes.length > 1 ? partes[1].trim() : "";
                    eq.agregarParticipante(new Participante(nombre, docTel));
                }

                Integer idEq = ctrlInscripcion.inscribirEquipo(eq);
                JOptionPane.showMessageDialog(this, "Equipo inscrito exitosamente (ID: " + idEq + ").");
                cargarEquiposDelTorneo();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error al inscribir equipo: " + ex.getMessage(),
                        "Validación / Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
