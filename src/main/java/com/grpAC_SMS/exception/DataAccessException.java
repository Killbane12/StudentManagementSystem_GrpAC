package com.grpAC_SMS.exception;

/**
 * Custom exception for errors occurring during database access operations.
 */
public class DataAccessException extends Exception {

    public DataAccessException(String message) {
        super(message);
    }

    public DataAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}