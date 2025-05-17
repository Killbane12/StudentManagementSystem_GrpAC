package com.grpAC_SMS.dao.impl;

import com.grpAC_SMS.dao.AnnouncementDao;
import com.grpAC_SMS.model.Announcement;
import com.grpAC_SMS.util.DatabaseConnector;
import com.grpAC_SMS.exception.DataAccessException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AnnouncementDaoImpl implements AnnouncementDao {

    private static final String SQL_GET_ALL_ANNOUNCEMENTS = "SELECT * FROM announcements";
    private static final String SQL_GET_ANNOUNCEMENT_BY_ID = "SELECT * FROM announcements WHERE announcement_id = ?";
    private static final String SQL_ADD_ANNOUNCEMENT = "INSERT INTO announcements (title, content, posted_by_user_id, target_role, image_file_path, expiry_date, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String SQL_UPDATE_ANNOUNCEMENT = "UPDATE announcements SET title = ?, content = ?, posted_by_user_id = ?, target_role = ?, image_file_path = ?, expiry_date = ?, updated_at = ? WHERE announcement_id = ?";
    private static final String SQL_DELETE_ANNOUNCEMENT = "DELETE FROM announcements WHERE announcement_id = ?";

    @Override
    public List<Announcement> getAllAnnouncements() throws DataAccessException {
        List<Announcement> announcements = new ArrayList<>();
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_GET_ALL_ANNOUNCEMENTS);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                announcements.add(mapResultSetToAnnouncement(rs));
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error retrieving all announcements: " + e.getMessage(), e);
        }
        return announcements;
    }

    @Override
    public Announcement getAnnouncementById(int announcementId) throws DataAccessException {
        Announcement announcement = null;
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_GET_ANNOUNCEMENT_BY_ID)) {
            ps.setInt(1, announcementId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    announcement = mapResultSetToAnnouncement(rs);
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error retrieving announcement by ID: " + e.getMessage(), e);
        }
        return announcement;
    }

    @Override
    public void addAnnouncement(Announcement announcement) throws DataAccessException {
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_ADD_ANNOUNCEMENT)) {
            ps.setString(1, announcement.getTitle());
            ps.setString(2, announcement.getContent());
            ps.setInt(3, announcement.getPostedByUserId());
            ps.setString(4, announcement.getTargetRole());
            ps.setString(5, announcement.getImageFilePath());
            if (announcement.getExpiryDate() != null) {
                ps.setDate(6, new java.sql.Date(announcement.getExpiryDate().getTime()));
            } else {
                ps.setDate(6, null);
            }
            ps.setTimestamp(7, announcement.getCreatedAt());
            ps.setTimestamp(8, announcement.getUpdatedAt());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error adding announcement: " + e.getMessage(), e);
        }
    }

    @Override
    public void updateAnnouncement(Announcement announcement) throws DataAccessException {
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_UPDATE_ANNOUNCEMENT)) {
            ps.setString(1, announcement.getTitle());
            ps.setString(2, announcement.getContent());
            ps.setInt(3, announcement.getPostedByUserId());
            ps.setString(4, announcement.getTargetRole());
            ps.setString(5, announcement.getImageFilePath());
            if (announcement.getExpiryDate() != null) {
                ps.setDate(6, new java.sql.Date(announcement.getExpiryDate().getTime()));
            } else {
                ps.setDate(6, null);
            }
            ps.setTimestamp(7, announcement.getUpdatedAt());
            ps.setInt(8, announcement.getAnnouncementId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error updating announcement: " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteAnnouncement(int announcementId) throws DataAccessException {
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_DELETE_ANNOUNCEMENT)) {
            ps.setInt(1, announcementId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error deleting announcement: " + e.getMessage(), e);
        }
    }

    private Announcement mapResultSetToAnnouncement(ResultSet rs) throws SQLException {
        Announcement announcement = new Announcement();
        announcement.setAnnouncementId(rs.getInt("announcement_id"));
        announcement.setTitle(rs.getString("title"));
        announcement.setContent(rs.getString("content"));
        announcement.setPostedByUserId(rs.getInt("posted_by_user_id"));
        announcement.setTargetRole(rs.getString("target_role"));
        announcement.setImageFilePath(rs.getString("image_file_path"));
        java.sql.Date expiryDate = rs.getDate("expiry_date");
        if (expiryDate != null) {
            announcement.setExpiryDate(new java.util.Date(expiryDate.getTime()));
        }
        announcement.setCreatedAt(rs.getTimestamp("created_at"));
        announcement.setUpdatedAt(rs.getTimestamp("updated_at"));
        return announcement;
    }
}
