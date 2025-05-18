package com.grpAC_SMS.model;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Model class representing a Grade given for an Enrollment.
 */
public class Grade implements Serializable {
    private static final long serialVersionUID = 1L;

    private int gradeId;
    private int enrollmentId; // FK
    private String gradeValue;
    private String assessmentType;
    private Integer gradedByFacultyId; // FK (nullable)
    private Timestamp gradedDate;
    private String remarks;
    private String courseName;

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    // Optional related object
    // private Enrollment enrollment;

    public Grade() {
    }

    // Getters and Setters
    public int getGradeId() {
        return gradeId;
    }

    public void setGradeId(int gradeId) {
        this.gradeId = gradeId;
    }

    public int getEnrollmentId() {
        return enrollmentId;
    }

    public void setEnrollmentId(int enrollmentId) {
        this.enrollmentId = enrollmentId;
    }

    public String getGradeValue() {
        return gradeValue;
    }

    public void setGradeValue(String gradeValue) {
        this.gradeValue = gradeValue;
    }

    public String getAssessmentType() {
        return assessmentType;
    }

    public void setAssessmentType(String assessmentType) {
        this.assessmentType = assessmentType;
    }

    public Integer getGradedByFacultyId() {
        return gradedByFacultyId;
    }

    public void setGradedByFacultyId(Integer gradedByFacultyId) {
        this.gradedByFacultyId = gradedByFacultyId;
    }

    public Timestamp getGradedDate() {
        return gradedDate;
    }

    public void setGradedDate(Timestamp gradedDate) {
        this.gradedDate = gradedDate;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    @Override
    public String toString() {
        return "Grade{" + "gradeId=" + gradeId + ", enrollmentId=" + enrollmentId + ", gradeValue='" + gradeValue + '\'' + '}';
    }
}