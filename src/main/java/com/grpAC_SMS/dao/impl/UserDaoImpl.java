package com.grpAC_SMS.dao.impl;

import com.grpAC_SMS.dao.UserDao;
import com.grpAC_SMS.exception.DataAccessException;
import com.grpAC_SMS.model.Role;
import com.grpAC_SMS.model.User;
import com.grpAC_SMS.util.DatabaseConnector;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserDaoImpl implements UserDao {

    // --- mapRowToUser Helper Method (Crucial) ---
    private User mapRowToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setUserId(rs.getInt("user_id"));
        user.setUsername(rs.getString("username"));
        user.setPasswordHash(rs.getString("password_hash"));
        user.setEmail(rs.getString("email"));
        try {
            user.setRole(Role.valueOf(rs.getString("role").toUpperCase())); // Ensure ENUM matches case
        } catch (IllegalArgumentException e) {
            System.err.println("Warning: Invalid role string in database for user_id " + rs.getInt("user_id") + ": " + rs.getString("role"));
            // Handle appropriately, e.g., set a default role or throw an error
            // For now, let it potentially be null if not matched or throw an error during login if role is essential
        }
        user.setActive(rs.getBoolean("is_active"));
        user.setCreatedAt(rs.getTimestamp("created_at"));
        user.setUpdatedAt(rs.getTimestamp("updated_at"));
        return user;
    }

    @Override
    public Optional<User> findByUsername(String username) throws DataAccessException {
        String sql = "SELECT * FROM Users WHERE username = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = DatabaseConnector.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapRowToUser(rs));
            } else {
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error finding user by username: " + username, e);
        } finally {
            DatabaseConnector.closeQuietly(rs);
            DatabaseConnector.closeQuietly(stmt);
            DatabaseConnector.closeQuietly(conn);
        }
    }

    @Override
    public void create(User user) throws DataAccessException {
        String sql = "INSERT INTO Users (username, password_hash, email, role, is_active) VALUES (?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = DatabaseConnector.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPasswordHash());
            stmt.setString(3, user.getEmail());
            stmt.setString(4, user.getRole().name()); // Store ENUM name as string
            stmt.setBoolean(5, user.isActive());
            stmt.executeUpdate();
            System.out.println("User created: " + user.getUsername());
        } catch (SQLException e) {
            throw new DataAccessException("Error creating user: " + user.getUsername(), e);
        } finally {
            DatabaseConnector.closeQuietly(stmt);
            DatabaseConnector.closeQuietly(conn);
        }
    }

    @Override
    public Optional<User> findById(int userId) throws DataAccessException {
        String sql = "SELECT * FROM Users WHERE user_id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = DatabaseConnector.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapRowToUser(rs));
            } else {
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error finding user by ID: " + userId, e);
        } finally {
            DatabaseConnector.closeQuietly(rs);
            DatabaseConnector.closeQuietly(stmt);
            DatabaseConnector.closeQuietly(conn);
        }
    }

    @Override
    public List<User> findAll() throws DataAccessException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM Users";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = DatabaseConnector.getConnection();
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();
            while (rs.next()) {
                users.add(mapRowToUser(rs));
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error finding all users", e);
        } finally {
            DatabaseConnector.closeQuietly(rs);
            DatabaseConnector.closeQuietly(stmt);
            DatabaseConnector.closeQuietly(conn);
        }
        return users;
    }

    @Override
    public boolean update(User user) throws DataAccessException {
        String sql = "UPDATE Users SET username = ?, password_hash = ?, email = ?, role = ?, is_active = ? WHERE user_id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = DatabaseConnector.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPasswordHash());
            stmt.setString(3, user.getEmail());
            stmt.setString(4, user.getRole().name());
            stmt.setBoolean(5, user.isActive());
            stmt.setInt(6, user.getUserId());
            int rowsAffected = stmt.executeUpdate();
            System.out.println("User updated: " + user.getUsername() + ", Rows affected: " + rowsAffected);
            return rowsAffected > 0;
        } catch (SQLException e) {
            throw new DataAccessException("Error updating user: " + user.getUsername(), e);
        } finally {
            DatabaseConnector.closeQuietly(stmt);
            DatabaseConnector.closeQuietly(conn);
        }
    }

    @Override
    public boolean delete(int userId) throws DataAccessException {
        String sql = "DELETE FROM Users WHERE user_id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = DatabaseConnector.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            int rowsAffected = stmt.executeUpdate();
            System.out.println("User deleted with ID: " + userId + ", Rows affected: " + rowsAffected);
            return rowsAffected > 0;
        } catch (SQLException e) {
            throw new DataAccessException("Error deleting user with ID: " + userId, e);
        } finally {
            DatabaseConnector.closeQuietly(stmt);
            DatabaseConnector.closeQuietly(conn);
        }
    }
}