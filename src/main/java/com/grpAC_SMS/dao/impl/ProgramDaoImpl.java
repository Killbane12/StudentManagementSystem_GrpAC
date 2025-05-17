package com.grpAC_SMS.dao.impl;

import com.grpAC_SMS.dao.ProgramDao;
import com.grpAC_SMS.exception.DataAccessException;
import com.grpAC_SMS.model.Program;
import com.grpAC_SMS.util.DatabaseConnector;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProgramDaoImpl implements ProgramDao {

    private Program mapRowToProgram(ResultSet rs) throws SQLException {
        Program program = new Program();
        program.setProgramId(rs.getInt("program_id"));
        program.setProgramName(rs.getString("program_name"));
        program.setDepartmentId(rs.getInt("department_id"));
        program.setDescription(rs.getString("description"));
        program.setDurationYears(rs.getInt("duration_years"));
        program.setCreatedAt(rs.getTimestamp("created_at"));
        program.setUpdatedAt(rs.getTimestamp("updated_at"));
        return program;
    }

    @Override
    public void create(Program program) throws DataAccessException {
        String sql = "INSERT INTO Programs (program_name, department_id, description, duration_years) VALUES (?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = DatabaseConnector.getConnection();
            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, program.getProgramName());
            stmt.setInt(2, program.getDepartmentId());
            stmt.setString(3, program.getDescription());
            stmt.setInt(4, program.getDurationYears());
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    program.setProgramId(generatedKeys.getInt(1));
                }
            }
            System.out.println("Program created: " + program.getProgramName());
        } catch (SQLException e) {
            throw new DataAccessException("Error creating program: " + program.getProgramName(), e);
        } finally {
            DatabaseConnector.closeQuietly(stmt);
            DatabaseConnector.closeQuietly(conn);
        }
    }

    @Override
    public Optional<Program> findById(int programId) throws DataAccessException {
        String sql = "SELECT * FROM Programs WHERE program_id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = DatabaseConnector.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, programId);
            rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapRowToProgram(rs));
            } else {
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error finding program by ID: " + programId, e);
        } finally {
            DatabaseConnector.closeQuietly(rs);
            DatabaseConnector.closeQuietly(stmt);
            DatabaseConnector.closeQuietly(conn);
        }
    }

    @Override
    public List<Program> findAll() throws DataAccessException {
        List<Program> programs = new ArrayList<>();
        String sql = "SELECT * FROM Programs";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = DatabaseConnector.getConnection();
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();
            while (rs.next()) {
                programs.add(mapRowToProgram(rs));
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error finding all programs", e);
        } finally {
            DatabaseConnector.closeQuietly(rs);
            DatabaseConnector.closeQuietly(stmt);
            DatabaseConnector.closeQuietly(conn);
        }
        return programs;
    }

    @Override
    public boolean update(Program program) throws DataAccessException {
        String sql = "UPDATE Programs SET program_name = ?, department_id = ?, description = ?, duration_years = ? WHERE program_id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = DatabaseConnector.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, program.getProgramName());
            stmt.setInt(2, program.getDepartmentId());
            stmt.setString(3, program.getDescription());
            stmt.setInt(4, program.getDurationYears());
            stmt.setInt(5, program.getProgramId());
            int rowsAffected = stmt.executeUpdate();
            System.out.println("Program updated: " + program.getProgramName() + ", Rows affected: " + rowsAffected);
            return rowsAffected > 0;
        } catch (SQLException e) {
            throw new DataAccessException("Error updating program: " + program.getProgramName(), e);
        } finally {
            DatabaseConnector.closeQuietly(stmt);
            DatabaseConnector.closeQuietly(conn);
        }
    }

    @Override
    public boolean delete(int programId) throws DataAccessException {
        String sql = "DELETE FROM Programs WHERE program_id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = DatabaseConnector.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, programId);
            int rowsAffected = stmt.executeUpdate();
            System.out.println("Program deleted with ID: " + programId + ", Rows affected: " + rowsAffected);
            return rowsAffected > 0;
        } catch (SQLException e) {
            throw new DataAccessException("Error deleting program with ID: " + programId, e);
        } finally {
            DatabaseConnector.closeQuietly(stmt);
            DatabaseConnector.closeQuietly(conn);
        }
    }
}