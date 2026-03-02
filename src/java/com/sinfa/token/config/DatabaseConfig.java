package com.sinfa.token.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Configuración de conexión a PostgreSQL
 * @author Miguel Araujo
 * @version 1.0
 */
public class DatabaseConfig {
    
    // Configuración de la base de datos
    private static final String DB_DRIVER = "org.postgresql.Driver";
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/token_db";
    private static final String DB_USER = "postgres";
    private static final String DB_PASSWORD = "12345"; // Cambiar por tu contraseña
    
    // Pool de conexiones básico
    private static Connection connection = null;
    
    /**
     * Obtiene una conexión a la base de datos
     * @return Connection
     * @throws SQLException
     */
    public static Connection getConnection() throws SQLException {
        try {
            // IMPORTANTE: No reutilizar conexión, crear nueva cada vez
            Class.forName(DB_DRIVER);
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            System.out.println("✓ Conexión exitosa a PostgreSQL");
            return conn;
        } catch (ClassNotFoundException e) {
            System.err.println("✗ Error: Driver PostgreSQL no encontrado");
            System.err.println("✗ Verifica que postgresql.jar esté en Libraries");
            e.printStackTrace();
            throw new SQLException("Driver no encontrado", e);
        } catch (SQLException e) {
            System.err.println("✗ Error de conexión a la base de datos");
            System.err.println("✗ URL: " + DB_URL);
            System.err.println("✗ Usuario: " + DB_USER);
            System.err.println("✗ Verifica que PostgreSQL esté corriendo");
            e.printStackTrace();
            throw e;
        }
    }
    
    /**
     * Cierra la conexión a la base de datos
     */
    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("✓ Conexión cerrada correctamente");
            }
        } catch (SQLException e) {
            System.err.println("✗ Error al cerrar conexión");
            e.printStackTrace();
        }
    }
    
    /**
     * Verifica si la conexión está activa
     * @return boolean
     */
    public static boolean testConnection() {
        try {
            Connection conn = getConnection();
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }
    
    /**
     * Obtiene información de la conexión
     * @return String
     */
    public static String getConnectionInfo() {
        try {
            Connection conn = getConnection();
            return String.format(
                "Base de Datos: %s\nUsuario: %s\nEstado: %s",
                conn.getCatalog(),
                DB_USER,
                conn.isClosed() ? "Cerrada" : "Activa"
            );
        } catch (SQLException e) {
            return "Error al obtener información de conexión";
        }
    }
}