package com.tejogestion.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DriverSQL {

    private static final String URL = "jdbc:mysql://localhost:3307/tejogestion?useUnicode=true&characterEncoding=UTF-8";
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