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



    /**
     * Checks if a string is valid based on specified minimum and maximum lengths.
     * A string is considered valid if it is not null, not empty after trimming,
     * and its length is within the specified range.
     *
     * @param str       The string to validate.
     * @param minLength The minimum allowed length of the string.
     * @param maxLength The maximum allowed length of the string.
     * @return true if the string is valid, false otherwise.
     */
    public static boolean isValidString(String str, int minLength, int maxLength) {
        if (str == null) {
            return false;
        }
        String trimmedStr = str.trim();
        if (trimmedStr.isEmpty()) {
            return false;
        }
        int length = trimmedStr.length();
        return length >= minLength && length <= maxLength;
    }

    /**
     * Checks if a string is a valid email address.
     *
     * @param email The string to check.
     * @return true if the string is a valid email address, false otherwise.
     */
    public static boolean isValidEmail(String email) {
        if (email == null) {
            return false;
        }
        String trimmedEmail = email.trim();
        if (trimmedEmail.isEmpty()) {
            return false;
        }
        // Basic email validation regex (you can use a more robust one)
        return trimmedEmail.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$");
    }

    /**
     * Checks if a string is a valid phone number.
     *
     * @param phoneNumber The string to check.
     * @return true if the string is a valid phone number, false otherwise.
     */
    public static boolean isValidPhoneNumber(String phoneNumber) {
        if (phoneNumber == null) {
            return false;
        }
        String trimmedPhoneNumber = phoneNumber.trim();
        if (trimmedPhoneNumber.isEmpty()) {
            return false;
        }
        // Basic phone number validation regex
        return trimmedPhoneNumber.matches("^\\d{10}$");
    }



}
