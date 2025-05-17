package com.grpAC_SMS.dao.impl;

import com.grpAC_SMS.dao.DepartmentDao;
import com.grpAC_SMS.exception.DataAccessException;
import com.grpAC_SMS.model.Department;
import com.grpAC_SMS.util.DatabaseConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DepartmentDaoImpl implements DepartmentDao {
    private static final Logger logger = LoggerFactory.getLogger(DepartmentDaoImpl.class);

    @Override
    public Department add(Department department) {
        String sql = "INSERT INTO Departments (department_name) VALUES (?)";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, department.getDepartmentName());
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating department failed, no rows affected.");
            }
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    department.setDepartmentId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating department failed, no ID obtained.");
                }
            }
            logger.info("Department added successfully: {}", department.getDepartmentName());
            return department;
        } catch (SQLException e) {
            logger.error("Error adding department {}: {}", department.getDepartmentName(), e.getMessage());
            if (e.getMessage().contains("Duplicate entry")) {
                throw new DataAccessException("Department name '" + department.getDepartmentName() + "' already exists.", e);
            }
            throw new DataAccessException("Error adding department.", e);
        }
    }

    @Override
    public Optional<Department> findById(int departmentId) {
        String sql = "SELECT * FROM Departments WHERE department_id = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, departmentId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapRowToDepartment(rs));
            }
        } catch (SQLException e) {
            logger.error("Error finding department by ID {}: {}", departmentId, e.getMessage());
            throw new DataAccessException("Error finding department by ID.", e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Department> findByName(String name) {
        String sql = "SELECT * FROM Departments WHERE department_name = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapRowToDepartment(rs));
            }
        } catch (SQLException e) {
            logger.error("Error finding department by name {}: {}", name, e.getMessage());
            throw new DataAccessException("Error finding department by name.", e);
        }
        return Optional.empty();
    }

    @Override
    public List<Department> findAll() {
        List<Department> departments = new ArrayList<>();
        String sql = "SELECT * FROM Departments ORDER BY department_name";
        try (Connection conn = DatabaseConnector.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                departments.add(mapRowToDepartment(rs));
            }
        } catch (SQLException e) {
            logger.error("Error fetching all departments: {}", e.getMessage());
            throw new DataAccessException("Error fetching all departments.", e);
        }
        return departments;
    }

    @Override
    public void update(Department department) {
        String sql = "UPDATE Departments SET department_name = ? WHERE department_id = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, department.getDepartmentName());
            pstmt.setInt(2, department.getDepartmentId());
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                logger.warn("Updating department with ID {} failed, no rows affected or department not found.", department.getDepartmentId());
            } else {
                logger.info("Department updated successfully: {}", department.getDepartmentName());
            }
        } catch (SQLException e) {
            logger.error("Error updating department {}: {}", department.getDepartmentName(), e.getMessage());
            if (e.getMessage().contains("Duplicate entry")) {
                throw new DataAccessException("Department name '" + department.getDepartmentName() + "' already exists for another record.", e);
            }
            throw new DataAccessException("Error updating department.", e);
        }
    }

    @Override
    public boolean delete(int departmentId) {
        String sql = "DELETE FROM Departments WHERE department_id = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, departmentId);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                logger.info("Department with ID {} deleted successfully.", departmentId);
                return true;
            } else {
                logger.warn("Deleting department with ID {} failed, no rows affected or department not found.", departmentId);
                return false;
            }
        } catch (SQLException e) {
            logger.error("Error deleting department with ID {}: {}", departmentId, e.getMessage());
            if (e.getErrorCode() == 1451) { // MySQL specific error code for FK constraint violation
                throw new DataAccessException("Cannot delete department. It is referenced by other records (e.g., Programs, Courses, Faculty).", e);
            }
            throw new DataAccessException("Error deleting department.", e);
        }
    }

    private Department mapRowToDepartment(ResultSet rs) throws SQLException {
        Department department = new Department();
        department.setDepartmentId(rs.getInt("department_id"));
        department.setDepartmentName(rs.getString("department_name"));
        department.setCreatedAt(rs.getTimestamp("created_at"));
        department.setUpdatedAt(rs.getTimestamp("updated_at"));
        return department;
    }
}
