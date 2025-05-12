package com.grpAC_SMS.exception;

/**
 * Custom exception for errors occurring during business logic execution.
 */
public class BusinessLogicException extends Exception {

    public BusinessLogicException(String message) {
        super(message);
    }

    public BusinessLogicException(String message, Throwable cause) {
        super(message, cause);
    }
}