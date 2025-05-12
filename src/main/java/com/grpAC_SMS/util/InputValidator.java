package com.grpAC_SMS.util;

/**
 * Utility class for common input validation tasks.
 */
public class InputValidator {

    // Private constructor
    private InputValidator() {
    }

    /**
     * Checks if a string is null, empty, or contains only whitespace.
     *
     * @param value The string to check.
     * @return true if the string is null or empty, false otherwise.
     */
    public static boolean isNullOrBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    /**
     * Basic check for email format.
     *
     * @param email The string to check.
     * @return true if the string looks like a plausible email address, false otherwise.
     */
    public static boolean isValidEmailFormat(String email) {
        if (isNullOrBlank(email)) return false;
        // Simple regex - consider a more comprehensive one if needed
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        return email.matches(emailRegex);
    }


    // Add other common validation methods (e.g., date format, length check) as needed

    /**
     * Checks if a string represents a valid integer.
     *
     * @param value The string to check.
     * @return true if the string can be parsed as an integer, false otherwise.
     */
    public static boolean isValidInteger(String value) {
        if (isNullOrBlank(value)) return false;
        try {
            Integer.parseInt(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}