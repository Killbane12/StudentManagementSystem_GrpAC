package com.grpAC_SMS.dao.impl;

import com.grpAC_SMS.dao.FacultyDao;
import com.grpAC_SMS.model.Faculty;
import com.grpAC_SMS.util.DatabaseConnector;
import com.grpAC_SMS.exception.DataAccessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FacultyDaoImpl implements FacultyDao {
    private static final Logger logger = LoggerFactory.getLogger(FacultyDaoImpl.class);

    @Override
    public Faculty add(Faculty faculty) {
        String sql = "INSERT INTO Faculty (user_id, faculty_unique_id, first_name, last_name, department_id, " +
                "office_location, contact_email, phone_number) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            // Handle nullable user_id (faculty might not have system access yet)
            if (faculty.getUserId() != null) pstmt.setInt(1, faculty.getUserId());
            else pstmt.setNull(1, Types.INTEGER);

            pstmt.setString(2, faculty.getFacultyUniqueId());
            pstmt.setString(3, faculty.getFirstName());
            pstmt.setString(4, faculty.getLastName());
            pstmt.setInt(5, faculty.getDepartmentId());
            pstmt.setString(6, faculty.getOfficeLocation());
            pstmt.setString(7, faculty.getContactEmail());
            pstmt.setString(8, faculty.getPhoneNumber());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating faculty member failed, no rows affected.");
            }
            // Retrieve the auto-generated ID
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    faculty.setFacultyMemberId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating faculty member failed, no ID obtained.");
                }
            }
            logger.info("Faculty member added successfully: {}", faculty.getFacultyUniqueId());
            return faculty;
        } catch (SQLException e) {
            logger.error("Error adding faculty {}: {}", faculty.getFacultyUniqueId(), e.getMessage());
            // Special handling for unique constraint violations
            if (e.getMessage().contains("Duplicate entry") && e.getMessage().contains("faculty_unique_id")) {
                throw new DataAccessException("Faculty Unique ID '" + faculty.getFacultyUniqueId() + "' already exists.", e);
            }
            if (e.getMessage().contains("Duplicate entry") && e.getMessage().contains("user_id")) {
                throw new DataAccessException("This user account is already linked to another faculty profile.", e);
            }
            throw new DataAccessException("Error adding faculty member.", e);
        }
    }

    @Override
    public Optional<Faculty> findById(int facultyMemberId) {
        String sql = "SELECT f.*, d.department_name, u.email as user_email " +
                "FROM Faculty f " +
                "JOIN Departments d ON f.department_id = d.department_id " +
                "LEFT JOIN Users u ON f.user_id = u.user_id " +
                "WHERE f.faculty_member_id = ?";
        return querySingleFaculty(sql, facultyMemberId);
    }

    @Override
    public Optional<Faculty> findByUserId(int userId) {
        String sql = "SELECT f.*, d.department_name, u.email as user_email " +
                "FROM Faculty f " +
                "JOIN Departments d ON f.department_id = d.department_id " +
                "LEFT JOIN Users u ON f.user_id = u.user_id " +
                "WHERE f.user_id = ?";
        return querySingleFaculty(sql, userId);
    }

    @Override
    public Optional<Faculty> findByFacultyUniqueId(String facultyUniqueId) {
        String sql = "SELECT f.*, d.department_name, u.email as user_email " +
                "FROM Faculty f " +
                "JOIN Departments d ON f.department_id = d.department_id " +
                "LEFT JOIN Users u ON f.user_id = u.user_id " +
                "WHERE f.faculty_unique_id = ?";
        return querySingleFaculty(sql, facultyUniqueId);
    }

    // Helper method to reduce code duplication in similar finder methods
    private Optional<Faculty> querySingleFaculty(String sql, Object param) {
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            // Handle different parameter types (Integer or String)
            if (param instanceof Integer) {
                pstmt.setInt(1, (Integer) param);
            } else if (param instanceof String) {
                pstmt.setString(1, (String) param);
            }
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapRowToFacultyWithDetails(rs));
            }
        } catch (SQLException e) {
            logger.error("Error finding faculty with param {}: {}", param, e.getMessage());
            throw new DataAccessException("Error finding faculty.", e);
        }
        return Optional.empty();
    }

    @Override
    public List<Faculty> findAll() {
        List<Faculty> facultyList = new ArrayList<>();
        // Basic query without joining related tables
        String sql = "SELECT * FROM Faculty ORDER BY last_name, first_name";
        try (Connection conn = DatabaseConnector.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                facultyList.add(mapRowToFaculty(rs));
            }
        } catch (SQLException e) {
            logger.error("Error fetching all faculty: {}", e.getMessage());
            throw new DataAccessException("Error fetching all faculty.", e);
        }
        return facultyList;
    }

    @Override
    public List<Faculty> findAllWithDetails() {
        List<Faculty> facultyList = new ArrayList<>();
        // Enhanced query with department and user details
        String sql = "SELECT f.*, d.department_name, u.email as user_email " +
                "FROM Faculty f " +
                "JOIN Departments d ON f.department_id = d.department_id " +
                "LEFT JOIN Users u ON f.user_id = u.user_id " +
                "ORDER BY f.last_name, f.first_name";
        try (Connection conn = DatabaseConnector.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                facultyList.add(mapRowToFacultyWithDetails(rs));
            }
        } catch (SQLException e) {
            logger.error("Error fetching all faculty with details: {}", e.getMessage());
            throw new DataAccessException("Error fetching all faculty with details.", e);
        }
        return facultyList;
    }

    @Override
    public void update(Faculty faculty) {
        String sql = "UPDATE Faculty SET user_id = ?, faculty_unique_id = ?, first_name = ?, last_name = ?, department_id = ?, " +
                "office_location = ?, contact_email = ?, phone_number = ? " +
                "WHERE faculty_member_id = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Handle nullable user_id field
            if (faculty.getUserId() != null) pstmt.setInt(1, faculty.getUserId());
            else pstmt.setNull(1, Types.INTEGER);

            pstmt.setString(2, faculty.getFacultyUniqueId());
            pstmt.setString(3, faculty.getFirstName());
            pstmt.setString(4, faculty.getLastName());
            pstmt.setInt(5, faculty.getDepartmentId());
            pstmt.setString(6, faculty.getOfficeLocation());
            pstmt.setString(7, faculty.getContactEmail());
            pstmt.setString(8, faculty.getPhoneNumber());
            pstmt.setInt(9, faculty.getFacultyMemberId());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                logger.warn("Updating faculty with ID {} failed, no rows affected or faculty not found.", faculty.getFacultyMemberId());
            } else {
                logger.info("Faculty updated successfully: {}", faculty.getFacultyUniqueId());
            }
        } catch (SQLException e) {
            logger.error("Error updating faculty {}: {}", faculty.getFacultyUniqueId(), e.getMessage());
            // Handle specific constraint violations with user-friendly messages
            if (e.getMessage().contains("Duplicate entry") && e.getMessage().contains("faculty_unique_id")) {
                throw new DataAccessException("Faculty Unique ID '" + faculty.getFacultyUniqueId() + "' already exists for another faculty.", e);
            }
            if (e.getMessage().contains("Duplicate entry") && e.getMessage().contains("user_id")) {
                throw new DataAccessException("This user account is already linked to another faculty profile.", e);
            }
            throw new DataAccessException("Error updating faculty member.", e);
        }
    }

    @Override
    public boolean delete(int facultyMemberId) {
        String sql = "DELETE FROM Faculty WHERE faculty_member_id = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, facultyMemberId);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                logger.info("Faculty with ID {} deleted successfully.", facultyMemberId);
                return true;
            } else {
                logger.warn("Deleting faculty with ID {} failed, no rows affected or faculty not found.", facultyMemberId);
                return false;
            }
        } catch (SQLException e) {
            logger.error("Error deleting faculty with ID {}: {}", facultyMemberId, e.getMessage());
            // Check for foreign key constraint violations (faculty has related records)
            if (e.getErrorCode() == 1451) { // FK constraint
                throw new DataAccessException("Cannot delete faculty. Faculty member has related records (e.g., lecture sessions, grades).", e);
            }
            throw new DataAccessException("Error deleting faculty member.", e);
        }
    }

    @Override
    public long countTotalFaculty() {
        String sql = "SELECT COUNT(*) FROM Faculty";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                return rs.getLong(1);
            }
        } catch (SQLException e) {
            logger.error("Error counting total faculty: {}", e.getMessage());
            throw new DataAccessException("Error counting faculty.", e);
        }
        return 0;
    }

    // Basic mapper for faculty only
    private Faculty mapRowToFaculty(ResultSet rs) throws SQLException {
        Faculty faculty = new Faculty();
        faculty.setFacultyMemberId(rs.getInt("faculty_member_id"));
        faculty.setUserId(rs.getObject("user_id", Integer.class)); // Handle nullable user_id
        faculty.setFacultyUniqueId(rs.getString("faculty_unique_id"));
        faculty.setFirstName(rs.getString("first_name"));
        faculty.setLastName(rs.getString("last_name"));
        faculty.setDepartmentId(rs.getInt("department_id"));
        faculty.setOfficeLocation(rs.getString("office_location"));
        faculty.setContactEmail(rs.getString("contact_email"));
        faculty.setPhoneNumber(rs.getString("phone_number"));
        faculty.setCreatedAt(rs.getTimestamp("created_at"));
        faculty.setUpdatedAt(rs.getTimestamp("updated_at"));
        return faculty;
    }

    // Enhanced mapper that includes department and user information
    private Faculty mapRowToFacultyWithDetails(ResultSet rs) throws SQLException {
        Faculty faculty = mapRowToFaculty(rs);
        faculty.setDepartmentName(rs.getString("department_name"));
        faculty.setUserEmail(rs.getString("user_email"));
        return faculty;
    }
}