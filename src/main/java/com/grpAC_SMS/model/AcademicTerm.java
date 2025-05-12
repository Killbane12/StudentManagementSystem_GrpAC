package com.grpAC_SMS.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

/**
 * Model class representing an Academic Term (Semester).
 */
public class AcademicTerm implements Serializable {
    private static final long serialVersionUID = 1L;

    private int academicTermId;
    private String termName;
    private Date startDate;
    private Date endDate;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    public AcademicTerm() {
    }

    // Getters and Setters
    public int getAcademicTermId() {
        return academicTermId;
    }

    public void setAcademicTermId(int academicTermId) {
        this.academicTermId = academicTermId;
    }

    public String getTermName() {
        return termName;
    }

    public void setTermName(String termName) {
        this.termName = termName;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
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
        return "AcademicTerm{" + "academicTermId=" + academicTermId + ", termName='" + termName + '\'' + '}';
    }
}