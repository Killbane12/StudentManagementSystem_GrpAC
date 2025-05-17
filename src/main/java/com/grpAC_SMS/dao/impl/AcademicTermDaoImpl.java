package com.grpAC_SMS.dao.impl;

import com.grpAC_SMS.dao.AcademicTermDao;
import com.grpAC_SMS.model.AcademicTerm;
import com.grpAC_SMS.util.DatabaseConnector;
import com.grpAC_SMS.exception.DataAccessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AcademicTermDaoImpl implements AcademicTermDao {
    private static final Logger logger = LoggerFactory.getLogger(AcademicTermDaoImpl.class);

    @Override
    public AcademicTerm add(AcademicTerm academicTerm) {
        String sql = "INSERT INTO AcademicTerms (term_name, start_date, end_date) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, academicTerm.getTermName());
            pstmt.setDate(2, academicTerm.getStartDate());
            pstmt.setDate(3, academicTerm.getEndDate());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating academic term failed, no rows affected.");
            }
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    academicTerm.setAcademicTermId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating academic term failed, no ID obtained.");
                }
            }
            logger.info("Academic term added successfully: {}", academicTerm.getTermName());
            return academicTerm;
        } catch (SQLException e) {
            logger.error("Error adding academic term {}: {}", academicTerm.getTermName(), e.getMessage());
            if (e.getMessage().contains("Duplicate entry")) {
                throw new DataAccessException("Academic term name '" + academicTerm.getTermName() + "' already exists.", e);
            }
            throw new DataAccessException("Error adding academic term.", e);
        }
    }

    @Override
    public Optional<AcademicTerm> findById(int academicTermId) {
        String sql = "SELECT * FROM AcademicTerms WHERE academic_term_id = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, academicTermId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapRowToAcademicTerm(rs));
            }
        } catch (SQLException e) {
            logger.error("Error finding academic term by ID {}: {}", academicTermId, e.getMessage());
            throw new DataAccessException("Error finding academic term by ID.", e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<AcademicTerm> findByName(String termName) {
        String sql = "SELECT * FROM AcademicTerms WHERE term_name = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, termName);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapRowToAcademicTerm(rs));
            }
        } catch (SQLException e) {
            logger.error("Error finding academic term by name {}: {}", termName, e.getMessage());
            throw new DataAccessException("Error finding academic term by name.", e);
        }
        return Optional.empty();
    }

    @Override
    public List<AcademicTerm> findAll() {
        List<AcademicTerm> terms = new ArrayList<>();
        String sql = "SELECT * FROM AcademicTerms ORDER BY start_date DESC, term_name";
        try (Connection conn = DatabaseConnector.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                terms.add(mapRowToAcademicTerm(rs));
            }
        } catch (SQLException e) {
            logger.error("Error fetching all academic terms: {}", e.getMessage());
            throw new DataAccessException("Error fetching all academic terms.", e);
        }
        return terms;
    }

    @Override
    public Optional<AcademicTerm> findCurrentTerm(LocalDate date) {
        String sql = "SELECT * FROM AcademicTerms WHERE ? BETWEEN start_date AND end_date ORDER BY start_date DESC LIMIT 1";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDate(1, Date.valueOf(date));
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapRowToAcademicTerm(rs));
            } else { // If no term covers today, find the most recent past or nearest future one
                // This logic can be complex. For now, if not found, try latest.
                List<AcademicTerm> allTerms = findAll();
                if (!allTerms.isEmpty()) {
                    // A more sophisticated approach would be to find the one whose start_date is closest to 'date'
                    // Or the latest one that has started. For now, simple latest from findAll.
                    return Optional.of(allTerms.get(0)); // findAll orders by start_date DESC
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding current academic term for date {}: {}", date, e.getMessage());
            throw new DataAccessException("Error finding current academic term.", e);
        }
        return Optional.empty();
    }


    @Override
    public void update(AcademicTerm academicTerm) {
        String sql = "UPDATE AcademicTerms SET term_name = ?, start_date = ?, end_date = ? WHERE academic_term_id = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, academicTerm.getTermName());
            pstmt.setDate(2, academicTerm.getStartDate());
            pstmt.setDate(3, academicTerm.getEndDate());
            pstmt.setInt(4, academicTerm.getAcademicTermId());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                logger.warn("Updating academic term with ID {} failed, no rows affected or term not found.", academicTerm.getAcademicTermId());
            } else {
                logger.info("Academic term updated successfully: {}", academicTerm.getTermName());
            }
        } catch (SQLException e) {
            logger.error("Error updating academic term {}: {}", academicTerm.getTermName(), e.getMessage());
            if (e.getMessage().contains("Duplicate entry")) {
                throw new DataAccessException("Academic term name '" + academicTerm.getTermName() + "' already exists for another term.", e);
            }
            throw new DataAccessException("Error updating academic term.", e);
        }
    }

    @Override
    public boolean delete(int academicTermId) {
        String sql = "DELETE FROM AcademicTerms WHERE academic_term_id = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, academicTermId);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                logger.info("Academic term with ID {} deleted successfully.", academicTermId);
                return true;
            } else {
                logger.warn("Deleting academic term with ID {} failed, no rows affected or term not found.", academicTermId);
                return false;
            }
        } catch (SQLException e) {
            logger.error("Error deleting academic term with ID {}: {}", academicTermId, e.getMessage());
            if (e.getErrorCode() == 1451) { // FK constraint
                throw new DataAccessException("Cannot delete academic term. It is referenced by other records (e.g., Enrollments, Lecture Sessions).", e);
            }
            throw new DataAccessException("Error deleting academic term.", e);
        }
    }

    private AcademicTerm mapRowToAcademicTerm(ResultSet rs) throws SQLException {
        AcademicTerm term = new AcademicTerm();
        term.setAcademicTermId(rs.getInt("academic_term_id"));
        term.setTermName(rs.getString("term_name"));
        term.setStartDate(rs.getDate("start_date"));
        term.setEndDate(rs.getDate("end_date"));
        term.setCreatedAt(rs.getTimestamp("created_at"));
        term.setUpdatedAt(rs.getTimestamp("updated_at"));
        return term;
    }
}
