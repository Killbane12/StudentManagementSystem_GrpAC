package com.grpAC_SMS.util;

public class ApplicationConstants {
    // Session Keys
    public static final String SESSION_USER = "loggedInUser"; // For storing the User object in session
    public static final String SESSION_SUCCESS_MESSAGE = "successMessage";
    public static final String SESSION_ERROR_MESSAGE = "errorMessage";

    // Request Attribute Keys (often used for passing data to JSPs or temporary messages)
    public static final String REQ_ATTR_ERROR_MESSAGE = "errorMessage"; // For request-scoped errors
    public static final String REQ_ATTR_SUCCESS_MESSAGE = "successMessage"; // For request-scoped success

    // User Roles (already in Role.java enum, but useful as strings here if needed elsewhere, though enum is preferred)
    public static final String ROLE_ADMIN_STR = "ADMIN";
    public static final String ROLE_STUDENT_STR = "STUDENT";
    public static final String ROLE_FACULTY_STR = "FACULTY";

    // Default Values
    public static final String DEFAULT_NFC_DEVICE_ID = "SIM_NFC_DEVICE_01";
    public static final String DEFAULT_ENROLLMENT_STATUS = "ENROLLED";

    // Action Parameters (Commonly used in servlets)
    public static final String ACTION_LIST = "list";
    public static final String ACTION_ADD = "add"; // Typically leads to a form
    public static final String ACTION_CREATE = "create"; // Typically processes form submission for new
    public static final String ACTION_EDIT = "edit"; // Typically leads to a form with existing data
    public static final String ACTION_UPDATE = "update"; // Typically processes form submission for update
    public static final String ACTION_DELETE = "delete";
    public static final String ACTION_VIEW = "view";
    public static final String ACTION_SEARCH = "search"; // If search functionality is added
    public static final String ACTION_TOGGLE_STATUS = "toggleStatus";


    // Pagination (if implemented)
    public static final int DEFAULT_PAGE_SIZE = 10;

    // Grade Values (could be an enum or map if more complex logic needed)
    // Example: public static final String GRADE_A_PLUS = "A+";

    // Assessment Types (examples)
    public static final String ASSESSMENT_MIDTERM = "Midterm";
    public static final String ASSESSMENT_FINAL_EXAM = "Final Exam";
    public static final String ASSESSMENT_ASSIGNMENT = "Assignment";

    // Student Unique ID Range
    public static final int STUDENT_ID_MIN_RANGE = 16100;
    public static final int STUDENT_ID_MAX_RANGE = 48500;
    public static final String STUDENT_ID_RANGE_PATTERN = "^(1[6-9][1-9]\\d{2}|[2-3]\\d{4}|4[0-7]\\d{3}|48[0-4]\\d{2}|48500)$";
    public static final String STUDENT_ID_RANGE_TITLE = "Must be between 16100 and 48500";

}