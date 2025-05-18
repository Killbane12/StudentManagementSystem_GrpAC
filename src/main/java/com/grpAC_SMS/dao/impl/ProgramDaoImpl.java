package com.grpAC_SMS.dao.impl;

import com.grpAC_SMS.dao.ProgramDao;
import com.grpAC_SMS.exception.DataAccessException;
import com.grpAC_SMS.model.Program;
import com.grpAC_SMS.util.DatabaseConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProgramDaoImpl implements ProgramDao {
    private static final Logger logger = LoggerFactory.getLogger(ProgramDaoImpl.class);

    @Override
    public Program add(Program program) {
        String sql = "INSERT INTO Programs (program_name, department_id, description, duration_years) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, program.getProgramName());
            pstmt.setInt(2, program.getDepartmentId());
            pstmt.setString(3, program.getDescription());
            if (program.getDurationYears() != null) {
                pstmt.setInt(4, program.getDurationYears());
            } else {
                pstmt.setNull(4, Types.INTEGER);
            }

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating program failed, no rows affected.");
            }
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    program.setProgramId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating program failed, no ID obtained.");
                }
            }
            logger.info("Program added successfully: {}", program.getProgramName());
            return program;
        } catch (SQLException e) {
            logger.error("Error adding program {}: {}", program.getProgramName(), e.getMessage());
            if (e.getMessage().contains("Duplicate entry")) {
                throw new DataAccessException("Program name '" + program.getProgramName() + "' already exists.", e);
            }
            throw new DataAccessException("Error adding program.", e);
        }
    }

    @Override
    public Optional<Program> findById(int programId) {
        String sql = "SELECT p.*, d.department_name FROM Programs p " +
                "JOIN Departments d ON p.department_id = d.department_id " +
                "WHERE p.program_id = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, programId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapRowToProgramWithDetails(rs));
            }
        } catch (SQLException e) {
            logger.error("Error finding program by ID {}: {}", programId, e.getMessage());
            throw new DataAccessException("Error finding program by ID.", e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Program> findByName(String name) {
        String sql = "SELECT p.*, d.department_name FROM Programs p " +
                "JOIN Departments d ON p.department_id = d.department_id " +
                "WHERE p.program_name = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapRowToProgramWithDetails(rs));
            }
        } catch (SQLException e) {
            logger.error("Error finding program by name {}: {}", name, e.getMessage());
            throw new DataAccessException("Error finding program by name.", e);
        }
        return Optional.empty();
    }

    @Override
    public List<Program> findAll() { // Basic find all without details
        List<Program> programs = new ArrayList<>();
        String sql = "SELECT * FROM Programs ORDER BY program_name";
        try (Connection conn = DatabaseConnector.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                programs.add(mapRowToProgram(rs)); // Use basic mapper
            }
        } catch (SQLException e) {
            logger.error("Error fetching all programs: {}", e.getMessage());
            throw new DataAccessException("Error fetching all programs.", e);
        }
        return programs;
    }

    @Override
    public List<Program> findAllWithDetails() {
        List<Program> programs = new ArrayList<>();
        String sql = "SELECT p.*, d.department_name FROM Programs p " +
                "JOIN Departments d ON p.department_id = d.department_id " +
                "ORDER BY p.program_name";
        try (Connection conn = DatabaseConnector.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                programs.add(mapRowToProgramWithDetails(rs));
            }
        } catch (SQLException e) {
            logger.error("Error fetching all programs with details: {}", e.getMessage());
            throw new DataAccessException("Error fetching all programs with details.", e);
        }
        return programs;
    }

    @Override
    public List<Program> findByDepartmentId(int departmentId) {
        List<Program> programs = new ArrayList<>();
        String sql = "SELECT p.*, d.department_name FROM Programs p " +
                "JOIN Departments d ON p.department_id = d.department_id " +
                "WHERE p.department_id = ? ORDER BY p.program_name";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, departmentId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                programs.add(mapRowToProgramWithDetails(rs));
            }
        } catch (SQLException e) {
            logger.error("Error fetching programs for department ID {}: {}", departmentId, e.getMessage());
            throw new DataAccessException("Error fetching programs by department.", e);
        }
        return programs;
    }


    @Override
    public void update(Program program) {
        String sql = "UPDATE Programs SET program_name = ?, department_id = ?, description = ?, duration_years = ? " +
                "WHERE program_id = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, program.getProgramName());
            pstmt.setInt(2, program.getDepartmentId());
            pstmt.setString(3, program.getDescription());
            if (program.getDurationYears() != null) {
                pstmt.setInt(4, program.getDurationYears());
            } else {
                pstmt.setNull(4, Types.INTEGER);
            }
            pstmt.setInt(5, program.getProgramId());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                logger.warn("Updating program with ID {} failed, no rows affected or program not found.", program.getProgramId());
            } else {
                logger.info("Program updated successfully: {}", program.getProgramName());
            }
        } catch (SQLException e) {
            logger.error("Error updating program {}: {}", program.getProgramName(), e.getMessage());
            if (e.getMessage().contains("Duplicate entry")) {
                throw new DataAccessException("Program name '" + program.getProgramName() + "' already exists for another record.", e);
            }
            throw new DataAccessException("Error updating program.", e);
        }
    }

    @Override
    public boolean delete(int programId) {
        String sql = "DELETE FROM Programs WHERE program_id = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, programId);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                logger.info("Program with ID {} deleted successfully.", programId);
                return true;
            } else {
                logger.warn("Deleting program with ID {} failed, no rows affected or program not found.", programId);
                return false;
            }
        } catch (SQLException e) {
            logger.error("Error deleting program with ID {}: {}", programId, e.getMessage());
            if (e.getErrorCode() == 1451) { // FK constraint
                throw new DataAccessException("Cannot delete program. It is referenced by other records (e.g., Students, Courses).", e);
            }
            throw new DataAccessException("Error deleting program.", e);
        }
    }

    private Program mapRowToProgram(ResultSet rs) throws SQLException {
        Program program = new Program();
        program.setProgramId(rs.getInt("program_id"));
        program.setProgramName(rs.getString("program_name"));
        program.setDepartmentId(rs.getInt("department_id"));
        program.setDescription(rs.getString("description"));
        program.setDurationYears(rs.getObject("duration_years", Integer.class));
        program.setCreatedAt(rs.getTimestamp("created_at"));
        program.setUpdatedAt(rs.getTimestamp("updated_at"));
        return program;
    }

    private Program mapRowToProgramWithDetails(ResultSet rs) throws SQLException {
        Program program = mapRowToProgram(rs);
        program.setDepartmentName(rs.getString("department_name"));
        return program;
    }
}
