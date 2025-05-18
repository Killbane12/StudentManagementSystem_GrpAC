package com.grpAC_SMS.util;

import java.util.regex.Pattern;

public class InputValidator {

    // Basic email pattern
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    public static boolean isNullOrEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    public static boolean isValidEmail(String email) {
        if (isNullOrEmpty(email)) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email).matches();
    }

    public static boolean isValidPassword(String password) {
        // Example: At least 8 characters, one uppercase, one lowercase, one digit
        // For this project, we might just check for non-emptiness and length.
        if (isNullOrEmpty(password)) {
            return false;
        }
        return password.length() >= 6; // Simple length check
    }

    public static boolean isInteger(String str) {
        if (isNullOrEmpty(str)) return false;
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isPositiveInteger(String str) {
        if (!isInteger(str)) return false;
        return Integer.parseInt(str) > 0;
    }

    public static boolean isWithinRange(String str, int min, int max) {
        if (!isInteger(str)) return false;
        int val = Integer.parseInt(str);
        return val >= min && val <= max;
    }

    public static boolean isValidDate(String date) {
        // Basic check for YYYY-MM-DD format
        if (isNullOrEmpty(date)) return false;
        return date.matches("\\d{4}-\\d{2}-\\d{2}");
    }

    public static boolean isValidDateTimeLocal(String dateTime) {
        // Basic check for YYYY-MM-DDTHH:MM format
        if (isNullOrEmpty(dateTime)) return false;
        return dateTime.matches("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}");
    }
}
