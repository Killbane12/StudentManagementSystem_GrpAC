package com.grpAC_SMS.model;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Model class representing Student Attendance for a Lecture Session.
 */
public class Attendance implements Serializable {
    private static final long serialVersionUID = 1L;

    private int attendanceId;
    private int studentId; // FK
    private int lectureSessionId; // FK
    private Timestamp punchInTimestamp;
    private boolean isPresent; // Renamed from 'status' for clarity with BOOLEAN type
    private Integer recordedByFacultyId; // FK (nullable)
    private String deviceId; // Nullable
    private Timestamp createdAt;
    private Timestamp updatedAt;

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
    } // Use isPresent() for boolean getter

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

    @Override
    public String toString() {
        return "Attendance{" + "attendanceId=" + attendanceId + ", studentId=" + studentId + ", lectureSessionId=" + lectureSessionId + ", isPresent=" + isPresent + '}';
    }
}