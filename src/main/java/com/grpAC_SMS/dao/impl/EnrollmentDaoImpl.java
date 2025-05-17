package com.grpAC_SMS.dao.impl;

import com.grpAC_SMS.dao.EnrollmentDao;
import com.grpAC_SMS.model.Enrollment;
import com.grpAC_SMS.util.DatabaseConnector;
import com.grpAC_SMS.exception.DataAccessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EnrollmentDaoImpl implements EnrollmentDao {
    private static final Logger logger = LoggerFactory.getLogger(EnrollmentDaoImpl.class);

    private static final String SELECT_ENROLLMENT_DETAILS_SQL =
            "SELECT e.*, CONCAT(s.first_name, ' ', s.last_name) as student_name, s.student_unique_id, " +
                    "c.course_name, c.course_code, t.term_name " +
                    "FROM Enrollments e " +
                    "JOIN Students s ON e.student_id = s.student_id " +
                    "JOIN Courses c ON e.course_id = c.course_id " +
                    "JOIN AcademicTerms t ON e.academic_term_id = t.academic_term_id ";


    @Override
    public Enrollment add(Enrollment enrollment) {
        String sql = "INSERT INTO Enrollments (student_id, course_id, academic_term_id, enrollment_date, status) " +
                "VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, enrollment.getStudentId());
            pstmt.setInt(2, enrollment.getCourseId());
            pstmt.setInt(3, enrollment.getAcademicTermId());
            pstmt.setDate(4, enrollment.getEnrollmentDate());
            pstmt.setString(5, enrollment.getStatus() != null ? enrollment.getStatus() : "ENROLLED");

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating enrollment failed, no rows affected.");
            }
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    enrollment.setEnrollmentId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating enrollment failed, no ID obtained.");
                }
            }
            logger.info("Enrollment added successfully for student {}, course {}, term {}",
                    enrollment.getStudentId(), enrollment.getCourseId(), enrollment.getAcademicTermId());
            return enrollment;
        } catch (SQLException e) {
            logger.error("Error adding enrollment: {}", e.getMessage());
            if (e.getMessage().contains("Duplicate entry") && e.getMessage().contains("uk_student_course_term")) {
                throw new DataAccessException("Student is already enrolled in this course for this term.", e);
            }
            throw new DataAccessException("Error adding enrollment.", e);
        }
    }

    @Override
    public Optional<Enrollment> findById(int enrollmentId) {
        String sql = SELECT_ENROLLMENT_DETAILS_SQL + "WHERE e.enrollment_id = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, enrollmentId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapRowToEnrollmentWithDetails(rs));
            }
        } catch (SQLException e) {
            logger.error("Error finding enrollment by ID {}: {}", enrollmentId, e.getMessage());
            throw new DataAccessException("Error finding enrollment by ID.", e);
        }
        return Optional.empty();
    }

    @Override
    public List<Enrollment> findAll() { // Basic find all
        List<Enrollment> enrollments = new ArrayList<>();
        String sql = "SELECT * FROM Enrollments ORDER BY enrollment_date DESC";
        try (Connection conn = DatabaseConnector.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                enrollments.add(mapRowToEnrollment(rs));
            }
        } catch (SQLException e) {
            logger.error("Error fetching all enrollments: {}", e.getMessage());
            throw new DataAccessException("Error fetching all enrollments.", e);
        }
        return enrollments;
    }

    @Override
    public List<Enrollment> findAllWithDetails() {
        List<Enrollment> enrollments = new ArrayList<>();
        String sql = SELECT_ENROLLMENT_DETAILS_SQL + "ORDER BY e.enrollment_date DESC, s.last_name, c.course_name";
        try (Connection conn = DatabaseConnector.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                enrollments.add(mapRowToEnrollmentWithDetails(rs));
            }
        } catch (SQLException e) {
            logger.error("Error fetching all enrollments with details: {}", e.getMessage());
            throw new DataAccessException("Error fetching all enrollments with details.", e);
        }
        return enrollments;
    }


    @Override
    public List<Enrollment> findByStudentId(int studentId) {
        List<Enrollment> enrollments = new ArrayList<>();
        String sql = SELECT_ENROLLMENT_DETAILS_SQL + "WHERE e.student_id = ? ORDER BY t.start_date DESC, c.course_name";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, studentId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                enrollments.add(mapRowToEnrollmentWithDetails(rs));
            }
        } catch (SQLException e) {
            logger.error("Error fetching enrollments for student ID {}: {}", studentId, e.getMessage());
            throw new DataAccessException("Error fetching enrollments by student.", e);
        }
        return enrollments;
    }

    @Override
    public List<Enrollment> findByCourseId(int courseId) {
        List<Enrollment> enrollments = new ArrayList<>();
        String sql = SELECT_ENROLLMENT_DETAILS_SQL + "WHERE e.course_id = ? ORDER BY t.start_date DESC, s.last_name";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, courseId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                enrollments.add(mapRowToEnrollmentWithDetails(rs));
            }
        } catch (SQLException e) {
            logger.error("Error fetching enrollments for course ID {}: {}", courseId, e.getMessage());
            throw new DataAccessException("Error fetching enrollments by course.", e);
        }
        return enrollments;
    }

    @Override
    public List<Enrollment> findByTermId(int academicTermId) {
        List<Enrollment> enrollments = new ArrayList<>();
        String sql = SELECT_ENROLLMENT_DETAILS_SQL + "WHERE e.academic_term_id = ? ORDER BY c.course_name, s.last_name";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, academicTermId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                enrollments.add(mapRowToEnrollmentWithDetails(rs));
            }
        } catch (SQLException e) {
            logger.error("Error fetching enrollments for term ID {}: {}", academicTermId, e.getMessage());
            throw new DataAccessException("Error fetching enrollments by term.", e);
        }
        return enrollments;
    }

    @Override
    public Optional<Enrollment> findByStudentCourseTerm(int studentId, int courseId, int termId) {
        String sql = SELECT_ENROLLMENT_DETAILS_SQL + "WHERE e.student_id = ? AND e.course_id = ? AND e.academic_term_id = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, studentId);
            pstmt.setInt(2, courseId);
            pstmt.setInt(3, termId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapRowToEnrollmentWithDetails(rs));
            }
        } catch (SQLException e) {
            logger.error("Error finding enrollment for student {}, course {}, term {}: {}", studentId, courseId, termId, e.getMessage());
            throw new DataAccessException("Error finding specific enrollment.", e);
        }
        return Optional.empty();
    }

    @Override
    public boolean isStudentEnrolledInCourseForTerm(int studentId, int courseId, int termId) {
        String sql = "SELECT 1 FROM Enrollments WHERE student_id = ? AND course_id = ? AND academic_term_id = ? AND status = 'ENROLLED' LIMIT 1";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, studentId);
            pstmt.setInt(2, courseId);
            pstmt.setInt(3, termId);
            ResultSet rs = pstmt.executeQuery();
            return rs.next(); // True if a record exists
        } catch (SQLException e) {
            logger.error("Error checking enrollment status for student {}, course {}, term {}: {}", studentId, courseId, termId, e.getMessage());
            throw new DataAccessException("Error checking enrollment status.", e);
        }
    }


    @Override
    public void update(Enrollment enrollment) { // Primarily for status
        String sql = "UPDATE Enrollments SET student_id = ?, course_id = ?, academic_term_id = ?, enrollment_date = ?, status = ? " +
                "WHERE enrollment_id = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, enrollment.getStudentId());
            pstmt.setInt(2, enrollment.getCourseId());
            pstmt.setInt(3, enrollment.getAcademicTermId());
            pstmt.setDate(4, enrollment.getEnrollmentDate());
            pstmt.setString(5, enrollment.getStatus());
            pstmt.setInt(6, enrollment.getEnrollmentId());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                logger.warn("Updating enrollment with ID {} failed, no rows affected or enrollment not found.", enrollment.getEnrollmentId());
            } else {
                logger.info("Enrollment updated successfully for ID: {}", enrollment.getEnrollmentId());
            }
        } catch (SQLException e) {
            logger.error("Error updating enrollment with ID {}: {}", enrollment.getEnrollmentId(), e.getMessage());
            if (e.getMessage().contains("Duplicate entry") && e.getMessage().contains("uk_student_course_term")) {
                throw new DataAccessException("Cannot update: This student is already enrolled in this course for this term under a different enrollment ID.", e);
            }
            throw new DataAccessException("Error updating enrollment.", e);
        }
    }

    @Override
    public boolean delete(int enrollmentId) {
        String sql = "DELETE FROM Enrollments WHERE enrollment_id = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, enrollmentId);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                logger.info("Enrollment with ID {} deleted successfully.", enrollmentId);
                return true;
            } else {
                logger.warn("Deleting enrollment with ID {} failed, no rows affected or enrollment not found.", enrollmentId);
                return false;
            }
        } catch (SQLException e) {
            logger.error("Error deleting enrollment with ID {}: {}", enrollmentId, e.getMessage());
            if (e.getErrorCode() == 1451) { // FK constraint
                throw new DataAccessException("Cannot delete enrollment. It has related records (e.g., Grades, Attendance).", e);
            }
            throw new DataAccessException("Error deleting enrollment.", e);
        }
    }

    private Enrollment mapRowToEnrollment(ResultSet rs) throws SQLException {
        Enrollment enrollment = new Enrollment();
        enrollment.setEnrollmentId(rs.getInt("enrollment_id"));
        enrollment.setStudentId(rs.getInt("student_id"));
        enrollment.setCourseId(rs.getInt("course_id"));
        enrollment.setAcademicTermId(rs.getInt("academic_term_id"));
        enrollment.setEnrollmentDate(rs.getDate("enrollment_date"));
        enrollment.setStatus(rs.getString("status"));
        enrollment.setCreatedAt(rs.getTimestamp("created_at"));
        enrollment.setUpdatedAt(rs.getTimestamp("updated_at"));
        return enrollment;
    }

    private Enrollment mapRowToEnrollmentWithDetails(ResultSet rs) throws SQLException {
        Enrollment enrollment = mapRowToEnrollment(rs);
        enrollment.setStudentName(rs.getString("student_name"));
        enrollment.setStudentUniqueId(rs.getString("student_unique_id"));
        enrollment.setCourseName(rs.getString("course_name"));
        enrollment.setCourseCode(rs.getString("course_code"));
        enrollment.setTermName(rs.getString("term_name"));
        return enrollment;
    }
}
