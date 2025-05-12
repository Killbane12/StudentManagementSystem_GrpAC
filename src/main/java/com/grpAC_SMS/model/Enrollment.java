package com.grpAC_SMS.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

/**
 * Model class representing a Student's Enrollment in a Course for a Term.
 */
public class Enrollment implements Serializable {
    private static final long serialVersionUID = 1L;

    private int enrollmentId;
    private int studentId; // FK
    private int courseId; // FK
    private int academicTermId; // FK
    private Date enrollmentDate;
    private String status; // Could use Enum: EnrollmentStatus { ENROLLED, COMPLETED, DROPPED }
    private Timestamp createdAt;
    private Timestamp updatedAt;

    // Optional related objects
    // private Student student;
    // private Course course;
    // private AcademicTerm academicTerm;

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

    @Override
    public String toString() {
        return "Enrollment{" + "enrollmentId=" + enrollmentId + ", studentId=" + studentId + ", courseId=" + courseId + '}';
    }
}