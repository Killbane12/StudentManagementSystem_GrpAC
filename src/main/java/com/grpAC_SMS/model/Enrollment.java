package com.grpAC_SMS.model;

import java.sql.Date;
import java.sql.Timestamp;

public class Enrollment {
    private int enrollmentId;
    private int studentId;
    private int courseId;
    private int academicTermId;
    private Date enrollmentDate;
    private String status; // ENUM('ENROLLED', 'COMPLETED', 'DROPPED')
    private Timestamp createdAt;
    private Timestamp updatedAt;

    // For display purposes
    private String studentName;
    private String studentUniqueId;
    private String courseName;
    private String courseCode;
    private String termName;

    public Enrollment() {
    }

    // Getters and Setters
    public int getEnrollmentId() {
        return enrollmentId;
    }

    public void setEnrollmentId(int enrollmentId) {
        this.enrollmentId = enrollmentId;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public int getAcademicTermId() {
        return academicTermId;
    }

    public void setAcademicTermId(int academicTermId) {
        this.academicTermId = academicTermId;
    }

    public Date getEnrollmentDate() {
        return enrollmentDate;
    }

    public void setEnrollmentDate(Date enrollmentDate) {
        this.enrollmentDate = enrollmentDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public String getStudentUniqueId() {
        return studentUniqueId;
    }

    public void setStudentUniqueId(String studentUniqueId) {
        this.studentUniqueId = studentUniqueId;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public String getTermName() {
        return termName;
    }

    public void setTermName(String termName) {
        this.termName = termName;
    }

    @Override
    public String toString() {
        return "Enrollment{" +
                "enrollmentId=" + enrollmentId +
                ", studentId=" + studentId +
                ", courseId=" + courseId +
                ", academicTermId=" + academicTermId +
                ", status='" + status + '\'' +
                '}';
    }
}
