package com.grpAC_SMS.model;

import java.sql.Timestamp;
import java.time.LocalDateTime;

public class LectureSession {
    private int lectureSessionId;
    private int courseId;
    private Integer facultyMemberId; // Can be null
    private int academicTermId;
    private Integer locationId; // Can be null
    private LocalDateTime sessionStartDatetime;
    private LocalDateTime sessionEndDatetime;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    private String courseName;
    private String facultyName;
    private String termName;
    private String locationName;


    public LectureSession() {
    }

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

    public LocalDateTime getSessionStartDatetime() {
        return sessionStartDatetime;
    }

    public void setSessionStartDatetime(LocalDateTime sessionStartDatetime) {
        this.sessionStartDatetime = sessionStartDatetime;
    }

    public LocalDateTime getSessionEndDatetime() {
        return sessionEndDatetime;
    }

    public void setSessionEndDatetime(LocalDateTime sessionEndDatetime) {
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

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getFacultyName() {
        return facultyName;
    }

    public void setFacultyName(String facultyName) {
        this.facultyName = facultyName;
    }

    public String getTermName() {
        return termName;
    }

    public void setTermName(String termName) {
        this.termName = termName;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }


    @Override
    public String toString() {
        return "LectureSession{" +
                "lectureSessionId=" + lectureSessionId +
                ", courseId=" + courseId +
                ", sessionStartDatetime=" + sessionStartDatetime +
                '}';
    }
}
