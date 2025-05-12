package com.grpAC_SMS.util;

/**
 * Holds application-wide constants.
 */
public final class ApplicationConstants { // final class
    // Session attribute names
    public static final String LOGGED_IN_USER_SESSION_ATTR = "loggedInUser";
    // Request attribute names
    public static final String ERROR_MESSAGE_ATTR = "errorMessage";
    public static final String SUCCESS_MESSAGE_ATTR = "successMessage";
    // Other constants
    public static final String DEFAULT_ENCODING = "UTF-8";

    // Private constructor to prevent instantiation
    private ApplicationConstants() {
    }
}