package com.grpAC_SMS.dao.impl;

import com.grpAC_SMS.dao.StudentDao;
import com.grpAC_SMS.exception.DataAccessException;
import com.grpAC_SMS.model.Student;
import com.grpAC_SMS.util.DatabaseConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class StudentDaoImpl implements StudentDao {
    private static final Logger logger = LoggerFactory.getLogger(StudentDaoImpl.class);

    @Override
    public Student add(Student student) {
        String sql = "INSERT INTO Students (user_id, student_unique_id, first_name, last_name, date_of_birth, gender, " +
                "address, phone_number, enrollment_date, program_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            if (student.getUserId() != null) pstmt.setInt(1, student.getUserId());
            else pstmt.setNull(1, Types.INTEGER);

            pstmt.setString(2, student.getStudentUniqueId());
            pstmt.setString(3, student.getFirstName());
            pstmt.setString(4, student.getLastName());
            pstmt.setDate(5, student.getDateOfBirth());
            pstmt.setString(6, student.getGender());
            pstmt.setString(7, student.getAddress());
            pstmt.setString(8, student.getPhoneNumber());
            pstmt.setDate(9, student.getEnrollmentDate());

            if (student.getProgramId() != null) pstmt.setInt(10, student.getProgramId());
            else pstmt.setNull(10, Types.INTEGER);

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating student failed, no rows affected.");
            }
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    student.setStudentId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating student failed, no ID obtained.");
                }
            }
            logger.info("Student added successfully: {}", student.getStudentUniqueId());
            return student;
        } catch (SQLException e) {
            logger.error("Error adding student {}: {}", student.getStudentUniqueId(), e.getMessage());
            if (e.getMessage().contains("Duplicate entry") && e.getMessage().contains("student_unique_id")) {
                throw new DataAccessException("Student Unique ID '" + student.getStudentUniqueId() + "' already exists.", e);
            }
            if (e.getMessage().contains("Duplicate entry") && e.getMessage().contains("user_id")) {
                throw new DataAccessException("This user account is already linked to another student profile.", e);
            }
            throw new DataAccessException("Error adding student.", e);
        }
    }

    @Override
    public Optional<Student> findById(int studentId) {
        String sql = "SELECT s.*, p.program_name, u.email as user_email " +
                "FROM Students s " +
                "LEFT JOIN Programs p ON s.program_id = p.program_id " +
                "LEFT JOIN Users u ON s.user_id = u.user_id " +
                "WHERE s.student_id = ?";
        return querySingleStudent(sql, studentId);
    }

    @Override
    public Optional<Student> findByUserId(int userId) {
        String sql = "SELECT s.*, p.program_name, u.email as user_email " +
                "FROM Students s " +
                "LEFT JOIN Programs p ON s.program_id = p.program_id " +
                "LEFT JOIN Users u ON s.user_id = u.user_id " +
                "WHERE s.user_id = ?";
        return querySingleStudent(sql, userId);
    }

    @Override
    public Optional<Student> findByStudentUniqueId(String studentUniqueId) {
        String sql = "SELECT s.*, p.program_name, u.email as user_email " +
                "FROM Students s " +
                "LEFT JOIN Programs p ON s.program_id = p.program_id " +
                "LEFT JOIN Users u ON s.user_id = u.user_id " +
                "WHERE s.student_unique_id = ?";
        return querySingleStudent(sql, studentUniqueId);
    }

    private Optional<Student> querySingleStudent(String sql, Object param) {
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            if (param instanceof Integer) {
                pstmt.setInt(1, (Integer) param);
            } else if (param instanceof String) {
                pstmt.setString(1, (String) param);
            }
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapRowToStudentWithDetails(rs));
            }
        } catch (SQLException e) {
            logger.error("Error finding student with param {}: {}", param, e.getMessage());
            throw new DataAccessException("Error finding student.", e);
        }
        return Optional.empty();
    }

    @Override
    public List<Student> findAll() {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT * FROM Students ORDER BY last_name, first_name";
        try (Connection conn = DatabaseConnector.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                students.add(mapRowToStudent(rs)); // Basic mapper
            }
        } catch (SQLException e) {
            logger.error("Error fetching all students: {}", e.getMessage());
            throw new DataAccessException("Error fetching all students.", e);
        }
        return students;
    }

    @Override
    public List<Student> findAllWithDetails() {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT s.*, p.program_name, u.email as user_email " +
                "FROM Students s " +
                "LEFT JOIN Programs p ON s.program_id = p.program_id " +
                "LEFT JOIN Users u ON s.user_id = u.user_id " +
                "ORDER BY s.last_name, s.first_name";
        try (Connection conn = DatabaseConnector.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                students.add(mapRowToStudentWithDetails(rs));
            }
        } catch (SQLException e) {
            logger.error("Error fetching all students with details: {}", e.getMessage());
            throw new DataAccessException("Error fetching all students with details.", e);
        }
        return students;
    }

    @Override
    public void update(Student student) {
        String sql = "UPDATE Students SET user_id = ?, student_unique_id = ?, first_name = ?, last_name = ?, " +
                "date_of_birth = ?, gender = ?, address = ?, phone_number = ?, enrollment_date = ?, program_id = ? " +
                "WHERE student_id = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            if (student.getUserId() != null) pstmt.setInt(1, student.getUserId());
            else pstmt.setNull(1, Types.INTEGER);

            pstmt.setString(2, student.getStudentUniqueId());
            pstmt.setString(3, student.getFirstName());
            pstmt.setString(4, student.getLastName());
            pstmt.setDate(5, student.getDateOfBirth());
            pstmt.setString(6, student.getGender());
            pstmt.setString(7, student.getAddress());
            pstmt.setString(8, student.getPhoneNumber());
            pstmt.setDate(9, student.getEnrollmentDate());

            if (student.getProgramId() != null) pstmt.setInt(10, student.getProgramId());
            else pstmt.setNull(10, Types.INTEGER);

            pstmt.setInt(11, student.getStudentId());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                logger.warn("Updating student with ID {} failed, no rows affected or student not found.", student.getStudentId());
            } else {
                logger.info("Student updated successfully: {}", student.getStudentUniqueId());
            }
        } catch (SQLException e) {
            logger.error("Error updating student {}: {}", student.getStudentUniqueId(), e.getMessage());
            if (e.getMessage().contains("Duplicate entry") && e.getMessage().contains("student_unique_id")) {
                throw new DataAccessException("Student Unique ID '" + student.getStudentUniqueId() + "' already exists for another student.", e);
            }
            if (e.getMessage().contains("Duplicate entry") && e.getMessage().contains("user_id")) {
                throw new DataAccessException("This user account is already linked to another student profile.", e);
            }
            throw new DataAccessException("Error updating student.", e);
        }
    }

    @Override
    public boolean delete(int studentId) {
        String sql = "DELETE FROM Students WHERE student_id = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, studentId);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                logger.info("Student with ID {} deleted successfully.", studentId);
                return true;
            } else {
                logger.warn("Deleting student with ID {} failed, no rows affected or student not found.", studentId);
                return false;
            }
        } catch (SQLException e) {
            logger.error("Error deleting student with ID {}: {}", studentId, e.getMessage());
            if (e.getErrorCode() == 1451) { // FK constraint
                throw new DataAccessException("Cannot delete student. Student has related records (e.g., enrollments, attendance).", e);
            }
            throw new DataAccessException("Error deleting student.", e);
        }
    }

    @Override
    public List<Student> findUnmarkedStudentsForSession(int lectureSessionId, int courseId) {
        List<Student> students = new ArrayList<>();
        // Students enrolled in the course who are NOT in the Attendance table for this session OR are marked as absent
        // Simpler: just students enrolled in the course who are NOT in Attendance table with is_present = TRUE
        String sql = "SELECT s.*, p.program_name, u.email as user_email " +
                "FROM Students s " +
                "JOIN Enrollments e ON s.student_id = e.student_id " +
                "LEFT JOIN Programs p ON s.program_id = p.program_id " +
                "LEFT JOIN Users u ON s.user_id = u.user_id " +
                "WHERE e.course_id = ? " +
                "AND s.student_id NOT IN (SELECT att.student_id FROM Attendance att WHERE att.lecture_session_id = ? AND att.is_present = TRUE) " +
                "ORDER BY s.student_unique_id";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, courseId);
            pstmt.setInt(2, lectureSessionId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                students.add(mapRowToStudentWithDetails(rs));
            }
        } catch (SQLException e) {
            logger.error("Error finding unmarked students for session {}: {}", lectureSessionId, e.getMessage());
            throw new DataAccessException("Error finding unmarked students.", e);
        }
        return students;
    }

    @Override
    public List<Student> findByCourseIdAndTermId(int courseId, int termId) {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT s.*, p.program_name, u.email as user_email " +
                "FROM Students s " +
                "JOIN Enrollments e ON s.student_id = e.student_id " +
                "LEFT JOIN Programs p ON s.program_id = p.program_id " +
                "LEFT JOIN Users u ON s.user_id = u.user_id " +
                "WHERE e.course_id = ? AND e.academic_term_id = ? AND e.status = 'ENROLLED' " +
                "ORDER BY s.last_name, s.first_name";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, courseId);
            pstmt.setInt(2, termId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                students.add(mapRowToStudentWithDetails(rs));
            }
        } catch (SQLException e) {
            logger.error("Error fetching students for course {} and term {}: {}", courseId, termId, e.getMessage());
            throw new DataAccessException("Error fetching students by course and term.", e);
        }
        return students;
    }

    @Override
    public long countTotalStudents() {
        String sql = "SELECT COUNT(*) FROM Students";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                return rs.getLong(1);
            }
        } catch (SQLException e) {
            logger.error("Error counting total students: {}", e.getMessage());
            throw new DataAccessException("Error counting students.", e);
        }
        return 0;
    }


    private Student mapRowToStudent(ResultSet rs) throws SQLException {
        Student student = new Student();
        student.setStudentId(rs.getInt("student_id"));
        student.setUserId(rs.getObject("user_id", Integer.class));
        student.setStudentUniqueId(rs.getString("student_unique_id"));
        student.setFirstName(rs.getString("first_name"));
        student.setLastName(rs.getString("last_name"));
        student.setDateOfBirth(rs.getDate("date_of_birth"));
        student.setGender(rs.getString("gender"));
        student.setAddress(rs.getString("address"));
        student.setPhoneNumber(rs.getString("phone_number"));
        student.setEnrollmentDate(rs.getDate("enrollment_date"));
        student.setProgramId(rs.getObject("program_id", Integer.class));
        student.setCreatedAt(rs.getTimestamp("created_at"));
        student.setUpdatedAt(rs.getTimestamp("updated_at"));
        return student;
    }

    private Student mapRowToStudentWithDetails(ResultSet rs) throws SQLException {
        Student student = mapRowToStudent(rs);
        student.setProgramName(rs.getString("program_name")); // May be null if not joined or student has no program
        student.setUserEmail(rs.getString("user_email")); // May be null
        return student;
    }
}
