package com.grpAC_SMS.model;

import java.sql.Timestamp;

public class Grade {
    private int gradeId;
    private int enrollmentId;
    private String gradeValue;
    private String assessmentType;
    private Integer gradedByFacultyId; // Can be null
    private Timestamp gradedDate;
    private String remarks;

    private String studentName;
    private String courseName;
    private String facultyName;

    public Grade() {
    }

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

    public String getFacultyName() {
        return facultyName;
    }

    public void setFacultyName(String facultyName) {
        this.facultyName = facultyName;
    }


    @Override
    public String toString() {
        return "Grade{" +
                "gradeId=" + gradeId +
                ", enrollmentId=" + enrollmentId +
                ", gradeValue='" + gradeValue + '\'' +
                '}';
    }
}
