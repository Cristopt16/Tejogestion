package com.tejogestion.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Clase de conexión centralizada a la base de datos MySQL.
 * Todos los DAOs del proyecto obtienen su conexión desde aquí.
 */
public class DriverSQL {

    private static final String URL = "jdbc:mysql://localhost:3306/tejogestion?useSSL=false&serverTimezone=America/Bogota&allowPublicKeyRetrieval=true";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Driver MySQL no encontrado. Asegúrese de incluir mysql-connector-java en el classpath.", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
