package com.grpAC_SMS.model;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Model class representing a Faculty Member.
 */
public class Faculty implements Serializable {
    private static final long serialVersionUID = 1L;

    private int facultyMemberId;
    private Integer userId; // Foreign Key ID (nullable)
    private String facultyUniqueId;
    private String firstName;
    private String lastName;
    private int departmentId; // Foreign Key ID
    private String officeLocation;
    private String contactEmail;
    private String phoneNumber;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    public Faculty() {
    }

    // Getters and Setters
    public int getFacultyMemberId() {
        return facultyMemberId;
    }

    public void setFacultyMemberId(int facultyMemberId) {
        this.facultyMemberId = facultyMemberId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getFacultyUniqueId() {
        return facultyUniqueId;
    }

    public void setFacultyUniqueId(String facultyUniqueId) {
        this.facultyUniqueId = facultyUniqueId;
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

    public int getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(int departmentId) {
        this.departmentId = departmentId;
    }

    public String getOfficeLocation() {
        return officeLocation;
    }

    public void setOfficeLocation(String officeLocation) {
        this.officeLocation = officeLocation;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
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
        return "Faculty{" + "facultyMemberId=" + facultyMemberId + ", name='" + firstName + " " + lastName + '\'' + '}';
    }
}