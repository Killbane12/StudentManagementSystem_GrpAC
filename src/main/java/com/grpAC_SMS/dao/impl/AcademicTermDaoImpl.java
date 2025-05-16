package com.grpAC_SMS.dao.impl;

import com.grpAC_SMS.dao.AcademicTermDao;
import com.grpAC_SMS.exception.DataAccessException;
import com.grpAC_SMS.model.AcademicTerm;
import com.grpAC_SMS.util.DatabaseConnector;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AcademicTermDaoImpl implements AcademicTermDao {

    // --- Helper Method to Map Row to AcademicTerm ---
    private AcademicTerm mapRowToAcademicTerm(ResultSet rs) throws SQLException {
        AcademicTerm academicTerm = new AcademicTerm();
        academicTerm.setAcademicTermId(rs.getInt("academic_term_id"));
        academicTerm.setTermName(rs.getString("term_name"));
        academicTerm.setStartDate(rs.getDate("start_date"));
        academicTerm.setEndDate(rs.getDate("end_date"));
        academicTerm.setCreatedAt(rs.getTimestamp("created_at"));
        academicTerm.setUpdatedAt(rs.getTimestamp("updated_at"));
        return academicTerm;
    }

    @Override
    public void create(AcademicTerm academicTerm) throws DataAccessException {
        String sql = "INSERT INTO AcademicTerms (term_name, start_date, end_date, created_at, updated_at) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, academicTerm.getTermName());
            stmt.setDate(2, new java.sql.Date(academicTerm.getStartDate().getTime()));
            stmt.setDate(3, new java.sql.Date(academicTerm.getEndDate().getTime()));
            stmt.setTimestamp(4, academicTerm.getCreatedAt());
            stmt.setTimestamp(5, academicTerm.getUpdatedAt());

            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                academicTerm.setAcademicTermId(rs.getInt(1));
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error creating Academic Term: " + academicTerm.getTermName(), e);
        }
    }

    @Override
    public Optional<AcademicTerm> findById(int academicTermId) throws DataAccessException {
        String sql = "SELECT * FROM AcademicTerms WHERE academic_term_id = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, academicTermId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapRowToAcademicTerm(rs));
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error finding Academic Term with ID: " + academicTermId, e);
        }
        return Optional.empty();
    }

    @Override
    public List<AcademicTerm> findAll() throws DataAccessException {
        List<AcademicTerm> terms = new ArrayList<>();
        String sql = "SELECT * FROM AcademicTerms";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                terms.add(mapRowToAcademicTerm(rs));
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error finding all Academic Terms", e);
        }
        return terms;
    }

    @Override
    public boolean update(AcademicTerm academicTerm) throws DataAccessException {
        String sql = "UPDATE AcademicTerms SET term_name = ?, start_date = ?, end_date = ?, updated_at = ? WHERE academic_term_id = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, academicTerm.getTermName());
            stmt.setDate(2, new java.sql.Date(academicTerm.getStartDate().getTime()));
            stmt.setDate(3, new java.sql.Date(academicTerm.getEndDate().getTime()));
            stmt.setTimestamp(4, academicTerm.getUpdatedAt());
            stmt.setInt(5, academicTerm.getAcademicTermId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataAccessException("Error updating Academic Term with ID: " + academicTerm.getAcademicTermId(), e);
        }
    }

    @Override
    public boolean delete(int academicTermId) throws DataAccessException {
        String sql = "DELETE FROM AcademicTerms WHERE academic_term_id = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, academicTermId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataAccessException("Error deleting Academic Term with ID: " + academicTermId, e);
        }
    }
}
