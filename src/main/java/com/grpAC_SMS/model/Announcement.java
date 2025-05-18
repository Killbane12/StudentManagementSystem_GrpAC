package com.grpAC_SMS.model;

import java.sql.Date;
import java.sql.Timestamp;

public class Announcement {
    private int announcementId;
    private String title;
    private String content;
    private int postedByUserId;
    private String targetRole; // ENUM('ALL', 'STUDENT', 'FACULTY', 'ADMIN')
    private String imageFilePath; // For future use
    private Date expiryDate;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    private transient String contentHtml; // For display with <br>

    // For display purposes
    private String postedByUsername;

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
        // Automatically prepare HTML version when raw content is set
        if (content != null) {
            this.contentHtml = content.replace("\n", "<br />");
        } else {
            this.contentHtml = null;
        }
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

    public String getPostedByUsername() {
        return postedByUsername;
    }

    public void setPostedByUsername(String postedByUsername) {
        this.postedByUsername = postedByUsername;
    }

    public String getContentHtml() {
        // If contentHtml wasn't set (e.g. object created by DAO which only sets 'content')
        // ensure it's generated on first access.
        if (this.contentHtml == null && this.content != null) {
            this.contentHtml = this.content.replace("\n", "<br />");
        }
        return contentHtml;
    }

    public void setContentHtml(String contentHtml) {
        this.contentHtml = contentHtml;
    }

    @Override
    public String toString() {
        return "Announcement{" +
                "announcementId=" + announcementId +
                ", title='" + title + '\'' +
                ", targetRole='" + targetRole + '\'' +
                '}';
    }
}
