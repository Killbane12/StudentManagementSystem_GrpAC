package com.grpAC_SMS.model;

import java.sql.Timestamp;

public class Attendance {
    private int attendanceId;
    private int studentId;
    private int lectureSessionId;
    private Timestamp punchInTimestamp;
    private boolean isPresent;
    private Integer recordedByFacultyId; // Can be null
    private String deviceId; // Can be null
    private Timestamp createdAt;
    private Timestamp updatedAt;

    // For display purposes
    private String studentName;
    private String studentUniqueId;
    private String courseName; // From LectureSession -> Course
    private Timestamp sessionStartDateTime; // From LectureSession

    public Attendance() {
    }

    // Getters and Setters
    public int getAttendanceId() {
        return attendanceId;
    }

    public void setAttendanceId(int attendanceId) {
        this.attendanceId = attendanceId;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public int getLectureSessionId() {
        return lectureSessionId;
    }

    public void setLectureSessionId(int lectureSessionId) {
        this.lectureSessionId = lectureSessionId;
    }

    public Timestamp getPunchInTimestamp() {
        return punchInTimestamp;
    }

    public void setPunchInTimestamp(Timestamp punchInTimestamp) {
        this.punchInTimestamp = punchInTimestamp;
    }

    public boolean isPresent() {
        return isPresent;
    }

    public void setPresent(boolean present) {
        isPresent = present;
    }

    public Integer getRecordedByFacultyId() {
        return recordedByFacultyId;
    }

    public void setRecordedByFacultyId(Integer recordedByFacultyId) {
        this.recordedByFacultyId = recordedByFacultyId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public Timestamp getSessionStartDateTime() {
        return sessionStartDateTime;
    }

    public void setSessionStartDateTime(Timestamp sessionStartDateTime) {
        this.sessionStartDateTime = sessionStartDateTime;
    }

    public String getStudentUniqueId() {
        return studentUniqueId;
    }

    public void setStudentUniqueId(String studentUniqueId) {
        this.studentUniqueId = studentUniqueId;
    }

    @Override
    public String toString() {
        return "Attendance{" +
                "attendanceId=" + attendanceId +
                ", studentId=" + studentId +
                ", lectureSessionId=" + lectureSessionId +
                ", isPresent=" + isPresent +
                '}';
    }
}
