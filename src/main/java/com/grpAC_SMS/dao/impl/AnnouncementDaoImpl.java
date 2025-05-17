package com.grpAC_SMS.dao.impl;

import com.grpAC_SMS.dao.AnnouncementDao;
import com.grpAC_SMS.model.Announcement;
import com.grpAC_SMS.model.Role;
import com.grpAC_SMS.util.DatabaseConnector;
import com.grpAC_SMS.exception.DataAccessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AnnouncementDaoImpl implements AnnouncementDao {
    private static final Logger logger = LoggerFactory.getLogger(AnnouncementDaoImpl.class);

    private static final String SELECT_ANNOUNCEMENT_DETAILS_SQL =
            "SELECT a.*, u.username as posted_by_username " +
                    "FROM Announcements a " +
                    "JOIN Users u ON a.posted_by_user_id = u.user_id ";

    @Override
    public Announcement add(Announcement announcement) {
        String sql = "INSERT INTO Announcements (title, content, posted_by_user_id, target_role, image_file_path, expiry_date) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, announcement.getTitle());
            pstmt.setString(2, announcement.getContent());
            pstmt.setInt(3, announcement.getPostedByUserId());
            pstmt.setString(4, announcement.getTargetRole());
            pstmt.setString(5, announcement.getImageFilePath());
            pstmt.setDate(6, announcement.getExpiryDate());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating announcement failed, no rows affected.");
            }
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    announcement.setAnnouncementId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating announcement failed, no ID obtained.");
                }
            }
            logger.info("Announcement added successfully: {}", announcement.getTitle());
            return announcement;
        } catch (SQLException e) {
            logger.error("Error adding announcement {}: {}", announcement.getTitle(), e.getMessage());
            throw new DataAccessException("Error adding announcement.", e);
        }
    }

    @Override
    public Optional<Announcement> findById(int announcementId) {
        String sql = SELECT_ANNOUNCEMENT_DETAILS_SQL + "WHERE a.announcement_id = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, announcementId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapRowToAnnouncementWithDetails(rs));
            }
        } catch (SQLException e) {
            logger.error("Error finding announcement by ID {}: {}", announcementId, e.getMessage());
            throw new DataAccessException("Error finding announcement by ID.", e);
        }
        return Optional.empty();
    }

    @Override
    public List<Announcement> findAll() { // Basic find all
        List<Announcement> announcements = new ArrayList<>();
        String sql = "SELECT * FROM Announcements ORDER BY created_at DESC";
        try (Connection conn = DatabaseConnector.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                announcements.add(mapRowToAnnouncement(rs));
            }
        } catch (SQLException e) {
            logger.error("Error fetching all announcements: {}", e.getMessage());
            throw new DataAccessException("Error fetching all announcements.", e);
        }
        return announcements;
    }

    @Override
    public List<Announcement> findAllWithDetails() {
        List<Announcement> announcements = new ArrayList<>();
        String sql = SELECT_ANNOUNCEMENT_DETAILS_SQL + "ORDER BY a.created_at DESC";
        try (Connection conn = DatabaseConnector.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                announcements.add(mapRowToAnnouncementWithDetails(rs));
            }
        } catch (SQLException e) {
            logger.error("Error fetching all announcements with details: {}", e.getMessage());
            throw new DataAccessException("Error fetching all announcements with details.", e);
        }
        return announcements;
    }


    @Override
    public List<Announcement> findTargetedAnnouncements(Role targetRole, int limit) {
        List<Announcement> announcements = new ArrayList<>();
        // Shows announcements for the specific role OR for 'ALL', not expired
        String sql = SELECT_ANNOUNCEMENT_DETAILS_SQL +
                "WHERE (a.target_role = ? OR a.target_role = 'ALL') " +
                "AND (a.expiry_date IS NULL OR a.expiry_date >= CURDATE()) " +
                "ORDER BY a.created_at DESC LIMIT ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, targetRole.name());
            pstmt.setInt(2, limit);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                announcements.add(mapRowToAnnouncementWithDetails(rs));
            }
        } catch (SQLException e) {
            logger.error("Error fetching targeted announcements for role {}: {}", targetRole, e.getMessage());
            throw new DataAccessException("Error fetching targeted announcements.", e);
        }
        return announcements;
    }

    @Override
    public List<Announcement> findByTargetRole(Role targetRole) {
        List<Announcement> announcements = new ArrayList<>();
        String sql = SELECT_ANNOUNCEMENT_DETAILS_SQL +
                "WHERE (a.target_role = ? OR a.target_role = 'ALL') " +
                "AND (a.expiry_date IS NULL OR a.expiry_date >= CURDATE()) " +
                "ORDER BY a.created_at DESC";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, targetRole.name());
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                announcements.add(mapRowToAnnouncementWithDetails(rs));
            }
        } catch (SQLException e) {
            logger.error("Error fetching announcements by target role {}: {}", targetRole, e.getMessage());
            throw new DataAccessException("Error fetching announcements by target role.", e);
        }
        return announcements;
    }

    @Override
    public List<Announcement> findByUserId(int userId) { // Announcements posted by this user
        List<Announcement> announcements = new ArrayList<>();
        String sql = SELECT_ANNOUNCEMENT_DETAILS_SQL + "WHERE a.posted_by_user_id = ? ORDER BY a.created_at DESC";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                announcements.add(mapRowToAnnouncementWithDetails(rs));
            }
        } catch (SQLException e) {
            logger.error("Error fetching announcements posted by user ID {}: {}", userId, e.getMessage());
            throw new DataAccessException("Error fetching announcements by user.", e);
        }
        return announcements;
    }

    @Override
    public void update(Announcement announcement) {
        String sql = "UPDATE Announcements SET title = ?, content = ?, posted_by_user_id = ?, target_role = ?, " +
                "image_file_path = ?, expiry_date = ? WHERE announcement_id = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, announcement.getTitle());
            pstmt.setString(2, announcement.getContent());
            pstmt.setInt(3, announcement.getPostedByUserId());
            pstmt.setString(4, announcement.getTargetRole());
            pstmt.setString(5, announcement.getImageFilePath());
            pstmt.setDate(6, announcement.getExpiryDate());
            pstmt.setInt(7, announcement.getAnnouncementId());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                logger.warn("Updating announcement with ID {} failed, no rows affected or announcement not found.", announcement.getAnnouncementId());
            } else {
                logger.info("Announcement updated successfully: {}", announcement.getTitle());
            }
        } catch (SQLException e) {
            logger.error("Error updating announcement {}: {}", announcement.getTitle(), e.getMessage());
            throw new DataAccessException("Error updating announcement.", e);
        }
    }

    @Override
    public boolean delete(int announcementId) {
        String sql = "DELETE FROM Announcements WHERE announcement_id = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, announcementId);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                logger.info("Announcement with ID {} deleted successfully.", announcementId);
                return true;
            } else {
                logger.warn("Deleting announcement with ID {} failed, no rows affected or announcement not found.", announcementId);
                return false;
            }
        } catch (SQLException e) {
            logger.error("Error deleting announcement with ID {}: {}", announcementId, e.getMessage());
            throw new DataAccessException("Error deleting announcement.", e);
        }
    }

    @Override
    public long countTotalActiveAnnouncements() {
        String sql = "SELECT COUNT(*) FROM Announcements WHERE (expiry_date IS NULL OR expiry_date >= CURDATE())";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                return rs.getLong(1);
            }
        } catch (SQLException e) {
            logger.error("Error counting total active announcements: {}", e.getMessage());
            throw new DataAccessException("Error counting active announcements.", e);
        }
        return 0;
    }

    private Announcement mapRowToAnnouncement(ResultSet rs) throws SQLException {
        Announcement announcement = new Announcement();
        announcement.setAnnouncementId(rs.getInt("announcement_id"));
        announcement.setTitle(rs.getString("title"));
        announcement.setContent(rs.getString("content"));
        announcement.setPostedByUserId(rs.getInt("posted_by_user_id"));
        announcement.setTargetRole(rs.getString("target_role"));
        announcement.setImageFilePath(rs.getString("image_file_path"));
        announcement.setExpiryDate(rs.getDate("expiry_date"));
        announcement.setCreatedAt(rs.getTimestamp("created_at"));
        announcement.setUpdatedAt(rs.getTimestamp("updated_at"));
        return announcement;
    }

    private Announcement mapRowToAnnouncementWithDetails(ResultSet rs) throws SQLException {
        Announcement announcement = mapRowToAnnouncement(rs);
        announcement.setPostedByUsername(rs.getString("posted_by_username"));
        return announcement;
    }
}
