package com.grpAC_SMS.util;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DateFormatter {
    private static final Logger logger = LoggerFactory.getLogger(DateFormatter.class);

    // For <input type="date"> which is yyyy-MM-dd
    private static final DateTimeFormatter HTML_DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;
    // For <input type="datetime-local"> which is yyyy-MM-ddTHH:mm
    private static final DateTimeFormatter HTML_DATETIME_LOCAL_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
    // For general ISO LocalDateTime parsing/formatting (includes seconds if present)
    private static final DateTimeFormatter ISO_DATETIME_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;


    /**
     * Parses a string in "yyyy-MM-dd" format to a java.sql.Date.
     * @param dateString The date string.
     * @return java.sql.Date or null if parsing fails or input is invalid.
     */
    public static Date stringToSqlDate(String dateString) {
        if (InputValidator.isNullOrEmpty(dateString)) {
            return null;
        }
        try {
            LocalDate localDate = LocalDate.parse(dateString, HTML_DATE_FORMATTER);
            return Date.valueOf(localDate);
        } catch (DateTimeParseException e) {
            logger.warn("Failed to parse date string '{}' to SQL Date: {}", dateString, e.getMessage());
            return null;
        }
    }

    /**
     * Formats a java.sql.Date to a "yyyy-MM-dd" string.
     * @param sqlDate The java.sql.Date.
     * @return Formatted date string or an empty string if input is null.
     */
    public static String sqlDateToString(Date sqlDate) {
        if (sqlDate == null) {
            return "";
        }
        return sqlDate.toLocalDate().format(HTML_DATE_FORMATTER);
    }

    /**
     * Parses a string from HTML datetime-local input ("yyyy-MM-ddTHH:mm") to a LocalDateTime.
     * @param dateTimeLocalString The datetime string from HTML input.
     * @return LocalDateTime or null if parsing fails or input is invalid.
     */
    public static LocalDateTime htmlDateTimeLocalStringToLocalDateTime(String dateTimeLocalString) {
        if (InputValidator.isNullOrEmpty(dateTimeLocalString)) {
            return null;
        }
        try {
            // HTML datetime-local format is yyyy-MM-ddTHH:mm
            return LocalDateTime.parse(dateTimeLocalString, HTML_DATETIME_LOCAL_FORMATTER);
        } catch (DateTimeParseException e) {
            logger.warn("Failed to parse datetime-local string '{}' to LocalDateTime: {}. Trying with seconds.", dateTimeLocalString, e.getMessage());
            // Attempt to parse with seconds if the primary format fails (e.g. if seconds were included by some browsers)
            try {
                return LocalDateTime.parse(dateTimeLocalString, ISO_DATETIME_FORMATTER);
            } catch (DateTimeParseException ex) {
                logger.warn("Failed to parse datetime-local string '{}' with seconds either: {}", dateTimeLocalString, ex.getMessage());
                return null;
            }
        }
    }

    /**
     * Formats a LocalDateTime to a string suitable for HTML datetime-local input ("yyyy-MM-ddTHH:mm").
     * @param localDateTime The LocalDateTime object.
     * @return Formatted string or an empty string if input is null.
     */
    public static String localDateTimeToHtmlDateTimeLocalString(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return "";
        }
        return localDateTime.format(HTML_DATETIME_LOCAL_FORMATTER);
    }

    /**
     * Converts a LocalDateTime object to a java.sql.Timestamp.
     * @param localDateTime The LocalDateTime object.
     * @return java.sql.Timestamp or null if input is null.
     */
    public static Timestamp localDateTimeToTimestamp(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        return Timestamp.valueOf(localDateTime);
    }

    /**
     * Converts a java.sql.Timestamp object to a LocalDateTime.
     * @param timestamp The java.sql.Timestamp object.
     * @return LocalDateTime or null if input is null.
     */
    public static LocalDateTime timestampToLocalDateTime(Timestamp timestamp) {
        if (timestamp == null) {
            return null;
        }
        return timestamp.toLocalDateTime();
    }

    // stringToLocalDateTime and localDateTimeToString from previous version (using ISO format)
    // These might be useful for other general purpose conversions if needed.

    /**
     * Parses a string in ISO_LOCAL_DATE_TIME format (e.g., "yyyy-MM-ddTHH:mm:ss") to LocalDateTime.
     * @param dateTimeString The datetime string.
     * @return LocalDateTime or null if parsing fails.
     */
    public static LocalDateTime stringToLocalDateTime(String dateTimeString) {
        if (InputValidator.isNullOrEmpty(dateTimeString)) {
            return null;
        }
        try {
            return LocalDateTime.parse(dateTimeString, ISO_DATETIME_FORMATTER);
        } catch (DateTimeParseException e) {
            // Attempt parsing without seconds if the above fails, common for HTML inputs
            try {
                return LocalDateTime.parse(dateTimeString, HTML_DATETIME_LOCAL_FORMATTER);
            } catch (DateTimeParseException ex) {
                logger.warn("Failed to parse general dateTime string '{}' to LocalDateTime: {}", dateTimeString, ex.getMessage());
                return null;
            }
        }
    }

    /**
     * Formats a LocalDateTime to an ISO_LOCAL_DATE_TIME string (e.g., "yyyy-MM-ddTHH:mm:ss").
     * @param localDateTime The LocalDateTime object.
     * @return Formatted string or an empty string if input is null.
     */
    public static String localDateTimeToString(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return "";
        }
        return localDateTime.format(ISO_DATETIME_FORMATTER);
    }
}