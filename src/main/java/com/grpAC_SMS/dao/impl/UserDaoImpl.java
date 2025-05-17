package com.grpAC_SMS.dao.impl;

import com.grpAC_SMS.dao.UserDao;
import com.grpAC_SMS.exception.DataAccessException;
import com.grpAC_SMS.model.Role;
import com.grpAC_SMS.model.User;
import com.grpAC_SMS.util.DatabaseConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserDaoImpl implements UserDao {
    private static final Logger logger = LoggerFactory.getLogger(UserDaoImpl.class);

    @Override
    public Optional<User> findByUsername(String username) {
        String sql = "SELECT * FROM Users WHERE username = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapRowToUser(rs));
            }
        } catch (SQLException e) {
            logger.error("Error finding user by username {}: {}", username, e.getMessage());
            throw new DataAccessException("Error finding user by username.", e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<User> findById(int userId) {
        String sql = "SELECT * FROM Users WHERE user_id = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapRowToUser(rs));
            }
        } catch (SQLException e) {
            logger.error("Error finding user by ID {}: {}", userId, e.getMessage());
            throw new DataAccessException("Error finding user by ID.", e);
        }
        return Optional.empty();
    }

    @Override
    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM Users ORDER BY username";
        try (Connection conn = DatabaseConnector.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                users.add(mapRowToUser(rs));
            }
        } catch (SQLException e) {
            logger.error("Error fetching all users: {}", e.getMessage());
            throw new DataAccessException("Error fetching all users.", e);
        }
        return users;
    }

    @Override
    public User save(User user) { // Handles both insert and update logic (simplified for insert here)
        // A more robust save would check if user.getUserId() is 0 or not to decide insert/update
        // For this example, focusing on insert. Update should be a separate method or handled.
        String sql = "INSERT INTO Users (username, password_hash, email, role, is_active) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getPasswordHash());
            pstmt.setString(3, user.getEmail());
            pstmt.setString(4, user.getRole().name());
            pstmt.setBoolean(5, user.isActive());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating user failed, no rows affected.");
            }

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    user.setUserId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating user failed, no ID obtained.");
                }
            }
            logger.info("User saved successfully with ID: {}", user.getUserId());
            return user;
        } catch (SQLException e) {
            logger.error("Error saving user {}: {}", user.getUsername(), e.getMessage());
            if (e.getMessage().contains("Duplicate entry") && e.getMessage().contains("username")) {
                throw new DataAccessException("Username '" + user.getUsername() + "' already exists.", e);
            }
            if (e.getMessage().contains("Duplicate entry") && e.getMessage().contains("email")) {
                throw new DataAccessException("Email '" + user.getEmail() + "' already exists.", e);
            }
            throw new DataAccessException("Error saving user.", e);
        }
    }

    @Override
    public void update(User user) {
        String sql = "UPDATE Users SET username = ?, password_hash = ?, email = ?, role = ?, is_active = ? WHERE user_id = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getPasswordHash()); // Ensure password is only updated if provided
            pstmt.setString(3, user.getEmail());
            pstmt.setString(4, user.getRole().name());
            pstmt.setBoolean(5, user.isActive());
            pstmt.setInt(6, user.getUserId());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                logger.warn("Updating user with ID {} failed, no rows affected.", user.getUserId());
            } else {
                logger.info("User with ID {} updated successfully.", user.getUserId());
            }
        } catch (SQLException e) {
            logger.error("Error updating user {}: {}", user.getUsername(), e.getMessage());
            if (e.getMessage().contains("Duplicate entry") && e.getMessage().contains("username")) {
                throw new DataAccessException("Username '" + user.getUsername() + "' already exists for another user.", e);
            }
            if (e.getMessage().contains("Duplicate entry") && e.getMessage().contains("email")) {
                throw new DataAccessException("Email '" + user.getEmail() + "' already exists for another user.", e);
            }
            throw new DataAccessException("Error updating user.", e);
        }
    }

    @Override
    public boolean activateUser(int userId, boolean isActive) {
        String sql = "UPDATE Users SET is_active = ? WHERE user_id = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setBoolean(1, isActive);
            pstmt.setInt(2, userId);
            int affectedRows = pstmt.executeUpdate();
            logger.info("User ID {} active status set to {}. Rows affected: {}", userId, isActive, affectedRows);
            return affectedRows > 0;
        } catch (SQLException e) {
            logger.error("Error updating user active status for ID {}: {}", userId, e.getMessage());
            throw new DataAccessException("Error updating user active status.", e);
        }
    }


    @Override
    public void delete(int userId) {
        // Consider soft delete (setting is_active=false) or ensure cascading deletes are handled
        // For now, direct delete
        String sql = "DELETE FROM Users WHERE user_id = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                logger.warn("Deleting user with ID {} failed, no rows affected / user not found.", userId);
            } else {
                logger.info("User with ID {} deleted successfully.", userId);
            }
        } catch (SQLException e) {
            logger.error("Error deleting user with ID {}: {}", userId, e.getMessage());
            // Check for foreign key constraint violation if user is referenced elsewhere and not cascaded
            if (e.getMessage().contains("foreign key constraint fails")) {
                throw new DataAccessException("Cannot delete user. User is referenced by other records (e.g., student, faculty, announcements). Please deactivate instead or remove references.", e);
            }
            throw new DataAccessException("Error deleting user.", e);
        }
    }

    @Override
    public int getNextUserId() {
        // This is a problematic approach for distributed systems or high concurrency.
        // A better approach is to insert the User record, get its ID, then use it for Student/Faculty.
        // However, if strictly needed for a sequence, some databases support sequences directly.
        // For MySQL, AUTO_INCREMENT handles this. If you need it *before* insert:
        String sql = "SELECT AUTO_INCREMENT FROM information_schema.TABLES WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'Users'";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            logger.error("Error getting next user ID: {}", e.getMessage());
            throw new DataAccessException("Error getting next user ID.", e);
        }
        return 0; // Should indicate an error or throw
    }

    private User mapRowToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setUserId(rs.getInt("user_id"));
        user.setUsername(rs.getString("username"));
        user.setPasswordHash(rs.getString("password_hash"));
        user.setEmail(rs.getString("email"));
        user.setRole(Role.valueOf(rs.getString("role")));
        user.setActive(rs.getBoolean("is_active"));
        user.setCreatedAt(rs.getTimestamp("created_at"));
        user.setUpdatedAt(rs.getTimestamp("updated_at"));
        return user;
    }
}
