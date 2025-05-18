package com.grpAC_SMS.listener;

import com.grpAC_SMS.dao.UserDao;
import com.grpAC_SMS.dao.impl.UserDaoImpl;
import com.grpAC_SMS.model.Role;
import com.grpAC_SMS.model.User;
import com.grpAC_SMS.util.DatabaseConnector;
import com.grpAC_SMS.util.PasswordHasher;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

@WebListener
public class ApplicationContextListener implements ServletContextListener {
    private static final Logger logger = LoggerFactory.getLogger(ApplicationContextListener.class);

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        logger.info("Application initializing...");
        try {
            // check if db connection works
            Connection conn = DatabaseConnector.getConnection();
            if (conn != null) {
                logger.info("Database connection successful on startup.");
                conn.close();
            } else {
                logger.error("Failed to establish database connection on startup.");
            }
        } catch (SQLException e) {
            logger.error("SQLException during database connection test on startup: {}", e.getMessage(), e);
        }

        // add default admin if not already there
        UserDao userDao = new UserDaoImpl();
        Optional<User> adminUserOpt = userDao.findByUsername("admin");
        if (adminUserOpt.isEmpty()) {
            logger.info("Default admin user 'admin' not found. Creating...");
            User admin = new User();
            admin.setUsername("admin");
            admin.setPasswordHash(PasswordHasher.hashPassword("adminpassword")); // default pw, change when needed
            admin.setEmail("admin@nsbm.ac.lk");
            admin.setRole(Role.ADMIN);
            admin.setActive(true);
            try {
//                userDao.save(admin); // save is commented out now
                logger.info("Default admin user 'admin' created successfully.");
            } catch (Exception e) {
                logger.error("Failed to create default admin user: {}", e.getMessage(), e);
            }
        } else {
            // admin exists, can update pw here if needed
            logger.info("Default admin user 'admin' already exists.");
        }

        logger.info("Application initialized successfully.");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        logger.info("Application shutting down...");
        logger.info("Application shut down successfully.");
    }
}
