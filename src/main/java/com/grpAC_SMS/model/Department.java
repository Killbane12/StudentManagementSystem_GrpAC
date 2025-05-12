package com.grpAC_SMS.model;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Model class representing a Department (Faculty/School).
 */
public class Department implements Serializable {
    private static final long serialVersionUID = 1L;

    private int departmentId;
    private String departmentName;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    public Department() {
    }

    // Getters and Setters
    public int getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(int departmentId) {
        this.departmentId = departmentId;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
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
        return "Department{" + "departmentId=" + departmentId + ", departmentName='" + departmentName + '\'' + '}';
    }
}