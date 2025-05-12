package com.grpAC_SMS.model;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Model class representing a Location (Lecture Hall, Lab).
 */
public class Location implements Serializable {
    private static final long serialVersionUID = 1L;

    private int locationId;
    private String locationName;
    private Integer capacity; // Use Integer to allow null
    private Timestamp createdAt;
    private Timestamp updatedAt;

    public Location() {
    }

    // Getters and Setters
    public int getLocationId() {
        return locationId;
    }

    public void setLocationId(int locationId) {
        this.locationId = locationId;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
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
        return "Location{" + "locationId=" + locationId + ", locationName='" + locationName + '\'' + '}';
    }
}