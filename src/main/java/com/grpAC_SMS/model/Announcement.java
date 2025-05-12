package com.grpAC_SMS.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

/**
 * Model class representing a System Announcement.
 */
public class Announcement implements Serializable {
    private static final long serialVersionUID = 1L;

    private int announcementId;
    private String title;
    private String content;
    private int postedByUserId; // FK
    private String targetRole; // Could use Role Enum: private Role targetRole;
    private String imageFilePath; // Nullable
    private Date expiryDate; // Nullable
    private Timestamp createdAt;
    private Timestamp updatedAt;

    public Announcement() {
    }

    // Getters and Setters
    public int getAnnouncementId() {
        return announcementId;
    }

    public void setAnnouncementId(int announcementId) {
        this.announcementId = announcementId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getPostedByUserId() {
        return postedByUserId;
    }

    public void setPostedByUserId(int postedByUserId) {
        this.postedByUserId = postedByUserId;
    }

    public String getTargetRole() {
        return targetRole;
    }

    public void setTargetRole(String targetRole) {
        this.targetRole = targetRole;
    }

    public String getImageFilePath() {
        return imageFilePath;
    }

    public void setImageFilePath(String imageFilePath) {
        this.imageFilePath = imageFilePath;
    }

    public Date getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
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
        return "Announcement{" + "announcementId=" + announcementId + ", title='" + title + '\'' + '}';
    }
}