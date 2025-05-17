package com.grpAC_SMS.util;

import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PasswordHasher {
    private static final Logger logger = LoggerFactory.getLogger(PasswordHasher.class);
    private static final int LOG_ROUNDS = 10; // As specified

    public static String hashPassword(String plainTextPassword) {
        if (plainTextPassword == null || plainTextPassword.isEmpty()) {
            logger.warn("Attempted to hash an empty or null password.");
            throw new IllegalArgumentException("Password cannot be empty or null.");
        }
        String salt = BCrypt.gensalt(LOG_ROUNDS);
        String hashedPassword = BCrypt.hashpw(plainTextPassword, salt);
        logger.debug("Password hashed successfully.");
        return hashedPassword;
    }

    public static boolean checkPassword(String plainTextPassword, String hashedPassword) {
        if (plainTextPassword == null || plainTextPassword.isEmpty() ||
                hashedPassword == null || hashedPassword.isEmpty()) {
            logger.warn("Attempted to check password with empty or null inputs.");
            return false;
        }
        try {
            boolean matches = BCrypt.checkpw(plainTextPassword, hashedPassword);
            logger.debug("Password check result: {}", matches);
            return matches;
        } catch (IllegalArgumentException e) {
            logger.error("Error during password check, possibly malformed hash: {}", hashedPassword, e);
            return false;
        }
    }

    public static void main(String[] args) {
        String passwordToHash = "pass";
        String generatedHash = hashPassword(passwordToHash);
        System.out.println("BCrypt hash for '" + passwordToHash + "': " + generatedHash);
        // Example output: BCrypt hash for 'adminpassword': $2a$10$xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
        // Copy this generated hash into your schema.sql file for the default admin user.
    }
}