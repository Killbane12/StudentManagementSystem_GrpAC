package com.grpAC_SMS.util;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Utility class for hashing and verifying passwords using BCrypt.
 */
public class PasswordHasher {

    private static final int BCRYPT_WORKLOAD = 12; // Adjust workload as needed (10-12 typical)

    // Private constructor
    private PasswordHasher() {
    }

    /**
     * Hashes a plain text password using BCrypt.
     *
     * @param plainTextPassword The password to hash.
     * @return The BCrypt hashed password string.
     */
    public static String hashPassword(String plainTextPassword) {
        if (plainTextPassword == null || plainTextPassword.isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty.");
        }
        return BCrypt.hashpw(plainTextPassword, BCrypt.gensalt(BCRYPT_WORKLOAD));
    }

    /**
     * Verifies a plain text password against a stored BCrypt hash.
     *
     * @param plainTextPassword The password entered by the user.
     * @param hashedPassword    The hashed password retrieved from the database.
     * @return true if the password matches the hash, false otherwise.
     */
    public static boolean verifyPassword(String plainTextPassword, String hashedPassword) {
        if (plainTextPassword == null || plainTextPassword.isEmpty() || hashedPassword == null || hashedPassword.isEmpty()) {
            return false;
        }
        try {
            // BCrypt.checkpw handles nulls internally but good practice to check inputs
            return BCrypt.checkpw(plainTextPassword, hashedPassword);
        } catch (IllegalArgumentException e) {
            // This can happen if the stored hash is not a valid BCrypt format
            System.err.println("Error verifying password - invalid hash format: " + e.getMessage());
            return false;
        }
    }
}