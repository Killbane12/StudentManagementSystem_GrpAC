package com.grpAC_SMS.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

/**
 * Model class representing a Student.
 */
public class Student implements Serializable {
    private static final long serialVersionUID = 1L;

    private int studentId;
    private Integer userId; // Foreign Key ID (nullable)
    private String studentUniqueId;
    private String firstName;
    private String lastName;
    private Date dateOfBirth; // java.util.Date or java.sql.Date
    private String gender; // Could use an Enum: Gender { MALE, FEMALE, OTHER }
    private String address;
    private String phoneNumber;
    private Date enrollmentDate;
    private Integer programId; // Foreign Key ID (nullable)
    private Timestamp createdAt;
    private Timestamp updatedAt;

    // Optional: Include User/Program objects
    // private User user;
    // private Program program;

    public Student() {
    }

    // Getters and Setters
    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getStudentUniqueId() {
        return studentUniqueId;
    }

    public void setStudentUniqueId(String studentUniqueId) {
        this.studentUniqueId = studentUniqueId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Date getEnrollmentDate() {
        return enrollmentDate;
    }

    public void setEnrollmentDate(Date enrollmentDate) {
        this.enrollmentDate = enrollmentDate;
    }

    public Integer getProgramId() {
        return programId;
    }

    public void setProgramId(Integer programId) {
        this.programId = programId;
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
        return "Student{" + "studentId=" + studentId + ", name='" + firstName + " " + lastName + '\'' + '}';
    }
}
