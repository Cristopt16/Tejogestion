package com.tejogestion.vista;

import com.tejogestion.config.DriverSQL;
import com.tejogestion.config.SesionUsuario;
import com.tejogestion.vista.modulo2.InterfazInventario;
import com.tejogestion.vista.modulo2.InterfazVentas;
import com.tejogestion.vista.modulo3.*;
import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Dashboard principal de TejoGestion que integra los módulos 02 y 03
 * con control de acceso basado en el rol del usuario autenticado.
 */
public class MenuPrincipal extends JFrame {
    private final JTabbedPane tabbedPane;

    public MenuPrincipal(int idUsuario, String nombreUsuario, String rolUsuario) {
        SesionUsuario.iniciarSesion(idUsuario, nombreUsuario, rolUsuario);

        setTitle("TejoGestion - Dashboard Principal");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1120, 720);
        setLocationRelativeTo(null);

        JPanel pnlHeader = new JPanel(new BorderLayout());
        pnlHeader.setBackground(new Color(22, 92, 52));
        pnlHeader.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));

        JLabel lblMarca = new JLabel("TejoGestion | Sistema de Gestión Integral");
        lblMarca.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblMarca.setForeground(Color.WHITE);
        pnlHeader.add(lblMarca, BorderLayout.WEST);

        JPanel pnlUser = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pnlUser.setOpaque(false);
        JLabel lblSesion = new JLabel("Sesión: " + nombreUsuario + " (" + rolUsuario + ")");
        lblSesion.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSesion.setForeground(Color.WHITE);
        JButton btnCerrarSesion = new JButton("Cambiar Usuario");
        btnCerrarSesion.addActionListener(e -> {
            dispose();
            mostrarLogin();
        });
        pnlUser.add(lblSesion);
        pnlUser.add(btnCerrarSesion);
        pnlHeader.add(pnlUser, BorderLayout.EAST);

        tabbedPane = new JTabbedPane(JTabbedPane.LEFT);
        tabbedPane.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        boolean esCliente = "Cliente".equalsIgnoreCase(rolUsuario);

        if (!esCliente) {
            tabbedPane.addTab("Inventario (Mód. 2)", new InterfazInventario(idUsuario));
            tabbedPane.addTab("Ventas y Caja (Mód. 2)", new InterfazVentas(idUsuario));
            tabbedPane.addTab("Torneos (Mód. 3)", new InterfazTorneo(idUsuario, rolUsuario));
            tabbedPane.addTab("Inscripciones (Mód. 3)", new InterfazInscripcion(rolUsuario));

            InterfazFixture pnlFixture = new InterfazFixture();
            InterfazMarcador pnlMarcador = new InterfazMarcador();
            tabbedPane.addTab("Fixture (Mód. 3)", pnlFixture);
            tabbedPane.addTab("Marcadores (Mód. 3)", pnlMarcador);

            tabbedPane.addTab("Posiciones (Mód. 3)", new InterfazPosiciones(
                    rolUsuario,
                    () -> tabbedPane.setSelectedComponent(pnlFixture),
                    () -> tabbedPane.setSelectedComponent(pnlMarcador)
            ));
        } else {
            // Para rol Cliente: solo consulta pública en modo lectura (CP-0310)
            tabbedPane.addTab("Torneos (Lectura)", new InterfazTorneo(idUsuario, rolUsuario));
            tabbedPane.addTab("Posiciones y Cronograma", new InterfazPosiciones(rolUsuario));
        }

        setLayout(new BorderLayout());
        add(pnlHeader, BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);
    }

    public static void mostrarLogin() {
        JTextField txtUser = new JTextField("admin");
        JPasswordField txtPass = new JPasswordField("admin123");
        Object[] form = {
            "Usuario:", txtUser,
            "Contraseña:", txtPass
        };

        int opt = JOptionPane.showConfirmDialog(null, form, "TejoGestion - Iniciar Sesión", JOptionPane.OK_CANCEL_OPTION);
        if (opt != JOptionPane.OK_OPTION) {
            System.exit(0);
        }

        String user = txtUser.getText().trim();
        String pass = new String(txtPass.getPassword()).trim();
        String sql = "SELECT u.id_usuario, u.nombre, r.nombre AS rol " +
                     "FROM USUARIO u INNER JOIN ROL r ON u.ref_rol = r.id_rol " +
                     "WHERE u.usuario = ? AND u.contrasena = ? AND u.estado = 'Activo'";

        try (Connection con = DriverSQL.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, user);
            ps.setString(2, pass);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt("id_usuario");
                    String nombre = rs.getString("nombre");
                    String rol = rs.getString("rol");
                    MenuPrincipal menu = new MenuPrincipal(id, nombre, rol);
                    menu.setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(null, "Usuario o contraseña inválidos.", "Acceso Denegado", JOptionPane.ERROR_MESSAGE);
                    mostrarLogin();
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Error al conectar con la base de datos:\n" + ex.getMessage(),
                    "Error de Conexión", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MenuPrincipal::mostrarLogin);
    }
}
