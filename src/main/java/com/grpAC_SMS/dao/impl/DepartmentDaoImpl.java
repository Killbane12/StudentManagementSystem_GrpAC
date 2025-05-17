package com.grpAC_SMS.dao.impl;

import com.grpAC_SMS.dao.DepartmentDao;
import com.grpAC_SMS.exception.DataAccessException;
import com.grpAC_SMS.model.Department;
import com.grpAC_SMS.util.DatabaseConnector;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DepartmentDaoImpl implements DepartmentDao {

    private Department mapRowToDepartment(ResultSet rs) throws SQLException {
        Department department = new Department();
        department.setDepartmentId(rs.getInt("department_id"));
        department.setDepartmentName(rs.getString("department_name"));
        department.setCreatedAt(rs.getTimestamp("created_at"));
        department.setUpdatedAt(rs.getTimestamp("updated_at"));
        return department;
    }

    @Override
    public void create(Department department) throws DataAccessException {
        String sql = "INSERT INTO Departments (department_name) VALUES (?)";
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = DatabaseConnector.getConnection();
            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, department.getDepartmentName());
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    department.setDepartmentId(generatedKeys.getInt(1));
                }
            }
            System.out.println("Department created: " + department.getDepartmentName());
        } catch (SQLException e) {
            throw new DataAccessException("Error creating department: " + department.getDepartmentName(), e);
        } finally {
            DatabaseConnector.closeQuietly(stmt);
            DatabaseConnector.closeQuietly(conn);
        }
    }

    @Override
    public Optional<Department> findById(int departmentId) throws DataAccessException {
        String sql = "SELECT * FROM Departments WHERE department_id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = DatabaseConnector.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, departmentId);
            rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapRowToDepartment(rs));
            } else {
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error finding department by ID: " + departmentId, e);
        } finally {
            DatabaseConnector.closeQuietly(rs);
            DatabaseConnector.closeQuietly(stmt);
            DatabaseConnector.closeQuietly(conn);
        }
    }

    @Override
    public List<Department> findAll() throws DataAccessException {
        List<Department> departments = new ArrayList<>();
        String sql = "SELECT * FROM Departments";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = DatabaseConnector.getConnection();
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();
            while (rs.next()) {
                departments.add(mapRowToDepartment(rs));
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error finding all departments", e);
        } finally {
            DatabaseConnector.closeQuietly(rs);
            DatabaseConnector.closeQuietly(stmt);
            DatabaseConnector.closeQuietly(conn);
        }
        return departments;
    }

    @Override
    public boolean update(Department department) throws DataAccessException {
        String sql = "UPDATE Departments SET department_name = ? WHERE department_id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = DatabaseConnector.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, department.getDepartmentName());
            stmt.setInt(2, department.getDepartmentId());
            int rowsAffected = stmt.executeUpdate();
            System.out.println("Department updated: " + department.getDepartmentName() + ", Rows affected: " + rowsAffected);
            return rowsAffected > 0;
        } catch (SQLException e) {
            throw new DataAccessException("Error updating department: " + department.getDepartmentName(), e);
        } finally {
            DatabaseConnector.closeQuietly(stmt);
            DatabaseConnector.closeQuietly(conn);
        }
    }

    @Override
    public boolean delete(int departmentId) throws DataAccessException {
        String sql = "DELETE FROM Departments WHERE department_id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = DatabaseConnector.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, departmentId);
            int rowsAffected = stmt.executeUpdate();
            System.out.println("Department deleted with ID: " + departmentId + ", Rows affected: " + rowsAffected);
            return rowsAffected > 0;
        } catch (SQLException e) {
            throw new DataAccessException("Error deleting department with ID: " + departmentId, e);
        } finally {
            DatabaseConnector.closeQuietly(stmt);
            DatabaseConnector.closeQuietly(conn);
        }
    }
}