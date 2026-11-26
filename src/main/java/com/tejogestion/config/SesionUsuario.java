package com.tejogestion.config;

/**
 * Contexto de sesión del usuario autenticado en la aplicación Swing.
 */
public class SesionUsuario {
    private static int idUsuario = 1;
    private static String nombreUsuario = "Administrador";
    private static String rol = "Administrador";

    private SesionUsuario() {}

    public static void iniciarSesion(int id, String nombre, String nombreRol) {
        idUsuario = id;
        nombreUsuario = nombre != null ? nombre : "Usuario";
        rol = nombreRol != null ? nombreRol : "Cliente";
    }

    public static int getIdUsuario() { return idUsuario; }
    public static String getNombreUsuario() { return nombreUsuario; }
    public static String getRol() { return rol; }

    public static boolean esCliente() {
        return "Cliente".equalsIgnoreCase(rol);
    }

    public static boolean esAdministrador() {
        return "Administrador".equalsIgnoreCase(rol);
    }
}
