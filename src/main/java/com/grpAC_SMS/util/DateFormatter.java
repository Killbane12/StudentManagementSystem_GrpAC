package com.grpAC_SMS.util;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Utility class for formatting and parsing dates.
 */
public class DateFormatter {

    // Define standard date formats
    private static final String USER_DATE_FORMAT = "yyyy-MM-dd"; // Common for HTML date inputs
    private static final String DISPLAY_DATE_FORMAT = "dd MMM yyyy"; // E.g., 15 Oct 2024

    // Private constructor
    private DateFormatter() {
    }

    /**
     * Formats a Date object into a user-friendly string (dd MMM yyyy).
     *
     * @param date The date to format.
     * @return Formatted date string or empty string if date is null.
     */
    public static String formatForDisplay(Date date) {
        if (date == null) return "";
        SimpleDateFormat sdf = new SimpleDateFormat(DISPLAY_DATE_FORMAT);
        return sdf.format(date);
    }

    /**
     * Formats a Timestamp object into a user-friendly string (dd MMM yyyy HH:mm).
     *
     * @param timestamp The timestamp to format.
     * @return Formatted timestamp string or empty string if timestamp is null.
     */
    public static String formatTimestampForDisplay(Timestamp timestamp) {
        if (timestamp == null) return "";
        SimpleDateFormat sdf = new SimpleDateFormat(DISPLAY_DATE_FORMAT + " HH:mm");
        return sdf.format(timestamp);
    }

    /**
     * Parses a date string (expected format yyyy-MM-dd) into a java.util.Date object.
     *
     * @param dateString The date string to parse.
     * @return The parsed Date object, or null if parsing fails or input is blank.
     */
    public static Date parseDate(String dateString) {
        if (InputValidator.isNullOrBlank(dateString)) return null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(USER_DATE_FORMAT);
            sdf.setLenient(false); // Don't allow invalid dates like 2024-02-30
            return sdf.parse(dateString);
        } catch (ParseException e) {
            System.err.println("Error parsing date string: " + dateString + " - " + e.getMessage());
            return null;
        }
    }

    /**
     * Converts java.util.Date to java.sql.Date.
     *
     * @param utilDate The java.util.Date.
     * @return The java.sql.Date, or null if input is null.
     */
    public static java.sql.Date convertUtilDateToSqlDate(Date utilDate) {
        if (utilDate == null) return null;
        return new java.sql.Date(utilDate.getTime());
    }
}