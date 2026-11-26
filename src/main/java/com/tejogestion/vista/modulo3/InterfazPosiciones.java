package com.tejogestion.vista.modulo3;

import com.tejogestion.config.SesionUsuario;
import com.tejogestion.modulo3.controlador.ControladorPosiciones;
import com.tejogestion.modulo3.controlador.ControladorTorneo;
import com.tejogestion.modulo3.modelo.Partida;
import com.tejogestion.modulo3.modelo.TablaPosiciones;
import com.tejogestion.modulo3.modelo.Torneo;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Vista Swing para visualizar cronograma y tabla de posiciones.
 * Si el usuario tiene rol Cliente, oculta los botones administrativos (modo solo lectura CP-0310).
 */
public class InterfazPosiciones extends JPanel {
    private final ControladorPosiciones ctrlPosiciones = new ControladorPosiciones();
    private final ControladorTorneo ctrlTorneo = new ControladorTorneo();
    private final String rolUsuario;

    private JComboBox<String> cmbTorneo;
    private List<Torneo> torneosCache = new ArrayList<>();
    private DefaultTableModel modeloPosiciones;
    private DefaultTableModel modeloCronograma;
    private JButton btnIrFixture;
    private JButton btnIrMarcadores;

    public InterfazPosiciones() {
        this(SesionUsuario.getRol(), null, null);
    }

    public InterfazPosiciones(String rolUsuario) {
        this(rolUsuario, null, null);
    }

    public InterfazPosiciones(String rolUsuario, Runnable accionAbrirFixture, Runnable accionAbrirMarcadores) {
        this.rolUsuario = rolUsuario != null ? rolUsuario : "Cliente";
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JLabel lblTitulo = new JLabel("Módulo 03 - Tabla de Posiciones y Cronograma", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        add(lblTitulo, BorderLayout.NORTH);

        JPanel pnlTop = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        pnlTop.add(new JLabel("Torneo:"));
        cmbTorneo = new JComboBox<>();
        cmbTorneo.setPreferredSize(new Dimension(300, 26));
        cmbTorneo.addActionListener(e -> refrescarDatos());
        pnlTop.add(cmbTorneo);

        modeloPosiciones = new DefaultTableModel(
                new String[]{"Pos", "Equipo", "PJ", "PG", "PE", "PP", "PF", "PC", "DIF", "PTS"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable tablaPos = new JTable(modeloPosiciones);
        tablaPos.setRowHeight(24);

        modeloCronograma = new DefaultTableModel(
                new String[]{"Ronda", "Local", "Visitante", "Marcador", "Cancha", "Fecha", "Hora", "Estado"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable tablaCrono = new JTable(modeloCronograma);
        tablaCrono.setRowHeight(24);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Tabla de Posiciones", new JScrollPane(tablaPos));
        tabs.addTab("Cronograma de Partidas", new JScrollPane(tablaCrono));

        JPanel pnlCentro = new JPanel(new BorderLayout(5, 5));
        pnlCentro.add(pnlTop, BorderLayout.NORTH);
        pnlCentro.add(tabs, BorderLayout.CENTER);
        add(pnlCentro, BorderLayout.CENTER);

        JPanel pnlBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 6));
        JButton btnRefrescar = new JButton("Actualizar Vista");
        btnRefrescar.addActionListener(e -> cargarTorneos());
        pnlBotones.add(btnRefrescar);

        btnIrFixture = new JButton("Administrar Fixture");
        btnIrMarcadores = new JButton("Registrar Marcadores");
        if (accionAbrirFixture != null) btnIrFixture.addActionListener(e -> accionAbrirFixture.run());
        if (accionAbrirMarcadores != null) btnIrMarcadores.addActionListener(e -> accionAbrirMarcadores.run());

        boolean esCliente = "Cliente".equalsIgnoreCase(this.rolUsuario);
        btnIrFixture.setVisible(!esCliente && accionAbrirFixture != null);
        btnIrMarcadores.setVisible(!esCliente && accionAbrirMarcadores != null);

        if (!esCliente) {
            if (accionAbrirFixture != null) pnlBotones.add(btnIrFixture);
            if (accionAbrirMarcadores != null) pnlBotones.add(btnIrMarcadores);
        } else {
            JLabel lblModo = new JLabel("Modo Solo Lectura (Cliente)");
            lblModo.setForeground(new Color(90, 90, 90));
            pnlBotones.add(lblModo);
        }
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
            refrescarDatos();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error cargando torneos: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void refrescarDatos() {
        int idx = cmbTorneo.getSelectedIndex();
        if (idx < 0 || idx >= torneosCache.size()) {
            modeloPosiciones.setRowCount(0);
            modeloCronograma.setRowCount(0);
            return;
        }
        int idTorneo = torneosCache.get(idx).getIdTorneo();
        try {
            List<TablaPosiciones> posiciones = ctrlPosiciones.obtenerPosiciones(idTorneo);
            modeloPosiciones.setRowCount(0);
            int puesto = 1;
            for (TablaPosiciones tp : posiciones) {
                modeloPosiciones.addRow(new Object[]{
                    puesto++, tp.getNombreEquipo(), tp.getPartidasJugadas(),
                    tp.getPartidasGanadas(), tp.getPartidasEmpatadas(), tp.getPartidasPerdidas(),
                    tp.getPuntosFavor(), tp.getPuntosContra(), tp.getDiferenciaPuntos(), tp.getPuntajeTotal()
                });
            }

            List<Partida> cronograma = ctrlPosiciones.obtenerCronograma(idTorneo);
            modeloCronograma.setRowCount(0);
            for (Partida p : cronograma) {
                String marcador = (p.getMarcadorLocal() != null && p.getMarcadorVisitante() != null)
                        ? p.getMarcadorLocal() + " - " + p.getMarcadorVisitante()
                        : "Pendiente";
                modeloCronograma.addRow(new Object[]{
                    p.getNumeroRonda(), p.getNombreLocal(), p.getNombreVisitante(), marcador,
                    p.getNombreCancha() != null ? p.getNombreCancha() : "Por asignar",
                    p.getFechaPartida() != null ? p.getFechaPartida() : "Por asignar",
                    p.getHoraInicio() != null ? p.getHoraInicio() : "Por asignar",
                    p.getEstado()
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error cargando posiciones/cronograma: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
