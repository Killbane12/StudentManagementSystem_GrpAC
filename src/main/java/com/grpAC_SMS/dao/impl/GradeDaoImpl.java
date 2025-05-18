package com.grpAC_SMS.dao.impl;

import com.grpAC_SMS.dao.GradeDao;
import com.grpAC_SMS.model.Grade;

import com.grpAC_SMS.util.DatabaseConnector;
import com.grpAC_SMS.exception.DataAccessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class GradeDaoImpl implements GradeDao {
    private static final Logger logger = LoggerFactory.getLogger(GradeDaoImpl.class);


    // Student name, course name and faculty name all in one query - very efficient!
    private static final String SELECT_GRADE_DETAILS_SQL =
            "SELECT g.*, CONCAT(s.first_name, ' ', s.last_name) as student_name, c.course_name, " +
                    "CONCAT(f.first_name, ' ', f.last_name) as graded_by_faculty_name " +
                    "FROM Grades g " +
                    "JOIN Enrollments e ON g.enrollment_id = e.enrollment_id " +
                    "JOIN Students s ON e.student_id = s.student_id " +
                    "JOIN Courses c ON e.course_id = c.course_id " +
                    "LEFT JOIN Faculty f ON g.graded_by_faculty_id = f.faculty_member_id ";

    @Override
    public Grade add(Grade grade) {
        String sql = "INSERT INTO Grades (enrollment_id, grade_value, assessment_type, graded_by_faculty_id, remarks) " +
                "VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, grade.getEnrollmentId());
            pstmt.setString(2, grade.getGradeValue());
            pstmt.setString(3, grade.getAssessmentType());
            // Special handling for null faculty ID
            if (grade.getGradedByFacultyId() != null) pstmt.setInt(4, grade.getGradedByFacultyId());
            else pstmt.setNull(4, Types.INTEGER);
            pstmt.setString(5, grade.getRemarks());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating grade failed, no rows affected.");
            }
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    grade.setGradeId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating grade failed, no ID obtained.");
                }
            }
            logger.info("Grade added successfully for enrollment ID: {}", grade.getEnrollmentId());
            return grade;
        } catch (SQLException e) {
            logger.error("Error adding grade for enrollment {}: {}", grade.getEnrollmentId(), e.getMessage());
            //  unique constraint for enrollment+assessment_type
            throw new DataAccessException("Error adding grade.", e);
        }
    }

    @Override
    public Optional<Grade> findById(int gradeId) {
        String sql = SELECT_GRADE_DETAILS_SQL + "WHERE g.grade_id = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, gradeId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapRowToGradeWithDetails(rs));
            }
        } catch (SQLException e) {
            logger.error("Error finding grade by ID {}: {}", gradeId, e.getMessage());
            throw new DataAccessException("Error finding grade by ID.", e);
        }
        return Optional.empty();
    }

    @Override
    public List<Grade> findAll() { // Basic find all
        List<Grade> grades = new ArrayList<>();
        String sql = "SELECT * FROM Grades ORDER BY graded_date DESC";
        try (Connection conn = DatabaseConnector.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                grades.add(mapRowToGrade(rs));
            }
        } catch (SQLException e) {
            logger.error("Error fetching all grades: {}", e.getMessage());
            throw new DataAccessException("Error fetching all grades.", e);
        }
        return grades;
    }

    @Override
    public List<Grade> findAllWithDetails() {

        List<Grade> grades = new ArrayList<>();
        String sql = SELECT_GRADE_DETAILS_SQL + "ORDER BY g.graded_date DESC, s.last_name, c.course_name";
        try (Connection conn = DatabaseConnector.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                grades.add(mapRowToGradeWithDetails(rs));
            }
        } catch (SQLException e) {
            logger.error("Error fetching all grades with details: {}", e.getMessage());
            throw new DataAccessException("Error fetching all grades with details.", e);
        }
        return grades;
    }

    @Override
    public List<Grade> findByEnrollmentId(int enrollmentId) {
        // get all grades for one student in one course
        List<Grade> grades = new ArrayList<>();
        String sql = SELECT_GRADE_DETAILS_SQL + "WHERE g.enrollment_id = ? ORDER BY g.assessment_type";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, enrollmentId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                grades.add(mapRowToGradeWithDetails(rs));
            }
        } catch (SQLException e) {
            logger.error("Error fetching grades for enrollment ID {}: {}", enrollmentId, e.getMessage());
            throw new DataAccessException("Error fetching grades by enrollment.", e);
        }
        return grades;
    }

    @Override
    public List<Grade> findByStudentId(int studentId) {

        // Order by course name make it easy to read for student!
        List<Grade> grades = new ArrayList<>();
        String sql = SELECT_GRADE_DETAILS_SQL + "WHERE e.student_id = ? ORDER BY c.course_name, g.assessment_type";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, studentId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                grades.add(mapRowToGradeWithDetails(rs));
            }
        } catch (SQLException e) {
            logger.error("Error fetching grades for student ID {}: {}", studentId, e.getMessage());
            throw new DataAccessException("Error fetching grades by student.", e);
        }
        return grades;
    }

    @Override
    public List<Grade> findByCourseId(int courseId) { // All grades for a course (across all students/terms)
        // Can see all grades ever given in one course
        List<Grade> grades = new ArrayList<>();
        String sql = SELECT_GRADE_DETAILS_SQL + "WHERE e.course_id = ? ORDER BY s.last_name, g.assessment_type";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, courseId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                grades.add(mapRowToGradeWithDetails(rs));
            }
        } catch (SQLException e) {
            logger.error("Error fetching grades for course ID {}: {}", courseId, e.getMessage());
            throw new DataAccessException("Error fetching grades by course.", e);
        }
        return grades;
    }

    @Override
    public List<Grade> findGradesForFacultyCourse(int facultyMemberId, int courseId, int termId) {

        List<Grade> grades = new ArrayList<>();
        String sql = SELECT_GRADE_DETAILS_SQL +
                "WHERE e.course_id = ? AND e.academic_term_id = ? " +

                "ORDER BY s.last_name, g.assessment_type";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, courseId);
            pstmt.setInt(2, termId);
            // pstmt.setInt(3, facultyMemberId); // if using the optional filter
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                grades.add(mapRowToGradeWithDetails(rs));
            }
        } catch (SQLException e) {
            logger.error("Error fetching grades for course {} in term {}: {}", courseId, termId, e.getMessage());
            throw new DataAccessException("Error fetching grades for faculty course view.", e);
        }
        return grades;
    }

    @Override
    public Optional<Grade> findByEnrollmentAndAssessmentType(int enrollmentId, String assessmentType) {
        // Find exact grade for specific test type

        String sql = SELECT_GRADE_DETAILS_SQL + "WHERE g.enrollment_id = ? AND g.assessment_type = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, enrollmentId);
            pstmt.setString(2, assessmentType);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapRowToGradeWithDetails(rs));
            }
        } catch (SQLException e) {
            logger.error("Error finding grade for enrollment {} and assessment {}: {}", enrollmentId, assessmentType, e.getMessage());
            throw new DataAccessException("Error finding grade by enrollment and assessment.", e);
        }
        return Optional.empty();
    }


    @Override
    public void update(Grade grade) {
        // Update function always set timestamp to current time!
        // This way we know when grade was last changed - good for audit trail
        String sql = "UPDATE Grades SET enrollment_id = ?, grade_value = ?, assessment_type = ?, " +
                "graded_by_faculty_id = ?, graded_date = CURRENT_TIMESTAMP, remarks = ? " +
                "WHERE grade_id = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, grade.getEnrollmentId());
            pstmt.setString(2, grade.getGradeValue());
            pstmt.setString(3, grade.getAssessmentType());
            if (grade.getGradedByFacultyId() != null) pstmt.setInt(4, grade.getGradedByFacultyId());
            else pstmt.setNull(4, Types.INTEGER);
            pstmt.setString(5, grade.getRemarks());
            pstmt.setInt(6, grade.getGradeId());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                logger.warn("Updating grade with ID {} failed, no rows affected or grade not found.", grade.getGradeId());
            } else {
                logger.info("Grade updated successfully for ID: {}", grade.getGradeId());
            }
        } catch (SQLException e) {
            logger.error("Error updating grade with ID {}: {}", grade.getGradeId(), e.getMessage());
            throw new DataAccessException("Error updating grade.", e);
        }
    }

    @Override
    public boolean delete(int gradeId) {

        String sql = "DELETE FROM Grades WHERE grade_id = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, gradeId);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                logger.info("Grade with ID {} deleted successfully.", gradeId);
                return true;
            } else {
                logger.warn("Deleting grade with ID {} failed, no rows affected or grade not found.", gradeId);
                return false;
            }
        } catch (SQLException e) {
            logger.error("Error deleting grade with ID {}: {}", gradeId, e.getMessage());
            throw new DataAccessException("Error deleting grade.", e);
        }
    }

    // Two helper methods for map database row to Grade object
    // First one is basic, second one include extra details from joins
    private Grade mapRowToGrade(ResultSet rs) throws SQLException {
        Grade grade = new Grade();
        grade.setGradeId(rs.getInt("grade_id"));
        grade.setEnrollmentId(rs.getInt("enrollment_id"));
        grade.setGradeValue(rs.getString("grade_value"));
        grade.setAssessmentType(rs.getString("assessment_type"));
        grade.setGradedByFacultyId(rs.getObject("graded_by_faculty_id", Integer.class));
        grade.setGradedDate(rs.getTimestamp("graded_date"));
        grade.setRemarks(rs.getString("remarks"));
        return grade;
    }

    private Grade mapRowToGradeWithDetails(ResultSet rs) throws SQLException {
        // This method use first method then add extra fields
        // Very smart design - no code duplication!
        Grade grade = mapRowToGrade(rs);
        grade.setStudentName(rs.getString("student_name"));
        grade.setCourseName(rs.getString("course_name"));
        grade.setFacultyName(rs.getString("graded_by_faculty_name"));
        return grade;
    }
}
