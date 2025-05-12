package com.grpAC_SMS.util;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Utility class for managing Database Connections using properties file.
 */
public class DatabaseConnector {

    private static final String PROPERTIES_FILE = "db/database.properties";
    private static final Properties props = new Properties();

    static {
        System.out.println("Attempting to load database properties...");
        try (InputStream input = DatabaseConnector.class.getClassLoader().getResourceAsStream(PROPERTIES_FILE)) {
            if (input == null) {
                System.err.println("FATAL ERROR: Unable to find " + PROPERTIES_FILE);
            } else {
                props.load(input);
                Class.forName(props.getProperty("db.driver")); // Ensure driver is loaded
                System.out.println("Database properties loaded successfully.");
            }
        } catch (Exception e) {
            System.err.println("FATAL ERROR: Failed to load database properties or driver.");
            e.printStackTrace(); // Print stack trace for debugging during development
        }
    }

    // Private constructor to prevent instantiation
    private DatabaseConnector() {
    }

    /**
     * Gets a database connection.
     *
     * @return A Connection object.
     * @throws SQLException if a database access error occurs or the URL is null, or properties not loaded.
     */
    public static Connection getConnection() throws SQLException {
        if (props.isEmpty()) {
            throw new SQLException("Database properties not loaded. Cannot create connection.");
        }
        return DriverManager.getConnection(
                props.getProperty("db.url"),
                props.getProperty("db.username"),
                props.getProperty("db.password")
        );
    }

    /**
     * Closes a JDBC resource quietly (Connection, Statement, ResultSet).
     *
     * @param resource The AutoCloseable resource to close.
     */
    public static void closeQuietly(AutoCloseable resource) {
        if (resource != null) {
            try {
                resource.close();
            } catch (Exception e) {
                System.err.println("Warning: Failed to close resource quietly. " + e.getMessage());
            }
        }
    }
}