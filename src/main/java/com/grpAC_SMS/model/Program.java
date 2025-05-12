package com.grpAC_SMS.model;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Model class representing an Academic Program (Degree/Diploma).
 */
public class Program implements Serializable {
    private static final long serialVersionUID = 1L;

    private int programId;
    private String programName;
    private int departmentId; // Foreign Key ID
    private String description;
    private Integer durationYears; // Use Integer to allow null
    private Timestamp createdAt;
    private Timestamp updatedAt;

    // Optional: Include Department object for relationship mapping
    // private Department department;

    public Program() {
    }

    // Getters and Setters
    public int getProgramId() {
        return programId;
    }

    public void setProgramId(int programId) {
        this.programId = programId;
    }

    public String getProgramName() {
        return programName;
    }

    public void setProgramName(String programName) {
        this.programName = programName;
    }

    public int getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(int departmentId) {
        this.departmentId = departmentId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getDurationYears() {
        return durationYears;
    }

    public void setDurationYears(Integer durationYears) {
        this.durationYears = durationYears;
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
        return "Program{" + "programId=" + programId + ", programName='" + programName + '\'' + '}';
    }
}
