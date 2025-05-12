package com.grpAC_SMS.model;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Model class representing a specific Lecture Session instance.
 */
public class LectureSession implements Serializable {
    private static final long serialVersionUID = 1L;

    private int lectureSessionId;
    private int courseId; // FK
    private Integer facultyMemberId; // FK (nullable)
    private int academicTermId; // FK
    private Integer locationId; // FK (nullable)
    private Timestamp sessionStartDatetime;
    private Timestamp sessionEndDatetime;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    // Optional related objects
    // private Course course;
    // private Faculty faculty;
    // private Location location;

    public LectureSession() {
    }

    // Getters and Setters
    public int getLectureSessionId() {
        return lectureSessionId;
    }

    public void setLectureSessionId(int lectureSessionId) {
        this.lectureSessionId = lectureSessionId;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public Integer getFacultyMemberId() {
        return facultyMemberId;
    }

    public void setFacultyMemberId(Integer facultyMemberId) {
        this.facultyMemberId = facultyMemberId;
    }

    public int getAcademicTermId() {
        return academicTermId;
    }

    public void setAcademicTermId(int academicTermId) {
        this.academicTermId = academicTermId;
    }

    public Integer getLocationId() {
        return locationId;
    }

    public void setLocationId(Integer locationId) {
        this.locationId = locationId;
    }

    public Timestamp getSessionStartDatetime() {
        return sessionStartDatetime;
    }

    public void setSessionStartDatetime(Timestamp sessionStartDatetime) {
        this.sessionStartDatetime = sessionStartDatetime;
    }

    public Timestamp getSessionEndDatetime() {
        return sessionEndDatetime;
    }

    public void setSessionEndDatetime(Timestamp sessionEndDatetime) {
        this.sessionEndDatetime = sessionEndDatetime;
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
        return "LectureSession{" + "lectureSessionId=" + lectureSessionId + ", courseId=" + courseId + '}';
    }
}