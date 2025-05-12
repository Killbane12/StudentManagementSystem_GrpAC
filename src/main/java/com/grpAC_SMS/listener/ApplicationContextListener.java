package com.grpAC_SMS.listener;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

/**
 * Application Lifecycle Listener for startup and shutdown events.
 */
@WebListener
public class ApplicationContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("========================================================");
        System.out.println("SMS Application Initializing...");
        // Initialize resources here (e.g., trigger DB connector static block)
        try {
            Class.forName("com.grpAC_SMS.util.DatabaseConnector"); // Explicitly load to run static block
        } catch (ClassNotFoundException e) {
            System.err.println("FATAL: Could not initialize DatabaseConnector.");
            // Potentially throw runtime exception to halt startup
        }
        System.out.println("SMS Application Initialized.");
        System.out.println("========================================================");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("========================================================");
        System.out.println("SMS Application Shutting Down...");
        // Clean up resources here (e.g., close connection pools if used)
        System.out.println("SMS Application Destroyed.");
        System.out.println("========================================================");
    }
}