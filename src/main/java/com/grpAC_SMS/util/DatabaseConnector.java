package com.grpAC_SMS.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnector {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseConnector.class);
    private static final Properties properties = new Properties();
    private static final String DB_PROPERTIES_FILE = "db/database.properties";

    static {
        try (InputStream input = DatabaseConnector.class.getClassLoader().getResourceAsStream(DB_PROPERTIES_FILE)) {
            if (input == null) {
                logger.error("Sorry, unable to find " + DB_PROPERTIES_FILE);
                throw new RuntimeException("Database properties file not found: " + DB_PROPERTIES_FILE);
            }
            properties.load(input);
            Class.forName(properties.getProperty("db.driver"));
            logger.info("Database driver loaded successfully.");
        } catch (IOException ex) {
            logger.error("Error loading database properties.", ex);
            throw new RuntimeException("Error loading database properties.", ex);
        } catch (ClassNotFoundException ex) {
            logger.error("Database driver class not found.", ex);
            throw new RuntimeException("Database driver class not found.", ex);
        }
    }

    public static Connection getConnection() throws SQLException {
        Connection connection = DriverManager.getConnection(
                properties.getProperty("db.url"),
                properties.getProperty("db.username"),
                properties.getProperty("db.password")
        );
        logger.debug("Database connection established.");
        return connection;
    }

    public static void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
                logger.debug("Database connection closed.");
            } catch (SQLException e) {
                logger.error("Error closing database connection.", e);
            }
        }
    }
}
