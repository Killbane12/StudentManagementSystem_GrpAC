package com.grpAC_SMS.dao.impl;

import com.grpAC_SMS.dao.CourseDao;
import com.grpAC_SMS.exception.DataAccessException;
import com.grpAC_SMS.model.Course;
import com.grpAC_SMS.util.DatabaseConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CourseDaoImpl implements CourseDao {
    private static final Logger logger = LoggerFactory.getLogger(CourseDaoImpl.class);

    @Override
    public Course add(Course course) {
        String sql = "INSERT INTO Courses (course_code, course_name, description, credits, program_id, department_id) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, course.getCourseCode());
            pstmt.setString(2, course.getCourseName());
            pstmt.setString(3, course.getDescription());
            if (course.getCredits() != null) pstmt.setInt(4, course.getCredits());
            else pstmt.setNull(4, Types.INTEGER);
            if (course.getProgramId() != null) pstmt.setInt(5, course.getProgramId());
            else pstmt.setNull(5, Types.INTEGER);
            pstmt.setInt(6, course.getDepartmentId());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating course failed, no rows affected.");
            }
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    course.setCourseId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating course failed, no ID obtained.");
                }
            }
            logger.info("Course added successfully: {}", course.getCourseCode());
            return course;
        } catch (SQLException e) {
            logger.error("Error adding course {}: {}", course.getCourseCode(), e.getMessage());
            if (e.getMessage().contains("Duplicate entry")) {
                throw new DataAccessException("Course code '" + course.getCourseCode() + "' already exists.", e);
            }
            throw new DataAccessException("Error adding course.", e);
        }
    }

    @Override
    public Optional<Course> findById(int courseId) {
        String sql = "SELECT c.*, p.program_name, d.department_name " +
                "FROM Courses c " +
                "LEFT JOIN Programs p ON c.program_id = p.program_id " +
                "JOIN Departments d ON c.department_id = d.department_id " +
                "WHERE c.course_id = ?";
        return querySingleCourse(sql, courseId);
    }

    @Override
    public Optional<Course> findByCode(String courseCode) {
        String sql = "SELECT c.*, p.program_name, d.department_name " +
                "FROM Courses c " +
                "LEFT JOIN Programs p ON c.program_id = p.program_id " +
                "JOIN Departments d ON c.department_id = d.department_id " +
                "WHERE c.course_code = ?";
        return querySingleCourse(sql, courseCode);
    }

    private Optional<Course> querySingleCourse(String sql, Object param) {
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            if (param instanceof Integer) {
                pstmt.setInt(1, (Integer) param);
            } else if (param instanceof String) {
                pstmt.setString(1, (String) param);
            }
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapRowToCourseWithDetails(rs));
            }
        } catch (SQLException e) {
            logger.error("Error finding course with param {}: {}", param, e.getMessage());
            throw new DataAccessException("Error finding course.", e);
        }
        return Optional.empty();
    }


    @Override
    public List<Course> findAll() {
        List<Course> courses = new ArrayList<>();
        String sql = "SELECT * FROM Courses ORDER BY course_code";
        try (Connection conn = DatabaseConnector.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                courses.add(mapRowToCourse(rs));
            }
        } catch (SQLException e) {
            logger.error("Error fetching all courses: {}", e.getMessage());
            throw new DataAccessException("Error fetching all courses.", e);
        }
        return courses;
    }

    @Override
    public List<Course> findAllWithDetails() {
        List<Course> courses = new ArrayList<>();
        String sql = "SELECT c.*, p.program_name, d.department_name " +
                "FROM Courses c " +
                "LEFT JOIN Programs p ON c.program_id = p.program_id " +
                "JOIN Departments d ON c.department_id = d.department_id " +
                "ORDER BY c.course_code";
        try (Connection conn = DatabaseConnector.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                courses.add(mapRowToCourseWithDetails(rs));
            }
        } catch (SQLException e) {
            logger.error("Error fetching all courses with details: {}", e.getMessage());
            throw new DataAccessException("Error fetching all courses with details.", e);
        }
        return courses;
    }

    @Override
    public List<Course> findByProgramId(int programId) {
        List<Course> courses = new ArrayList<>();
        String sql = "SELECT c.*, p.program_name, d.department_name " +
                "FROM Courses c " +
                "LEFT JOIN Programs p ON c.program_id = p.program_id " +
                "JOIN Departments d ON c.department_id = d.department_id " +
                "WHERE c.program_id = ? ORDER BY c.course_code";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, programId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                courses.add(mapRowToCourseWithDetails(rs));
            }
        } catch (SQLException e) {
            logger.error("Error fetching courses for program ID {}: {}", programId, e.getMessage());
            throw new DataAccessException("Error fetching courses by program.", e);
        }
        return courses;
    }

    @Override
    public List<Course> findByDepartmentId(int departmentId) {
        List<Course> courses = new ArrayList<>();
        String sql = "SELECT c.*, p.program_name, d.department_name " +
                "FROM Courses c " +
                "LEFT JOIN Programs p ON c.program_id = p.program_id " +
                "JOIN Departments d ON c.department_id = d.department_id " +
                "WHERE c.department_id = ? ORDER BY c.course_code";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, departmentId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                courses.add(mapRowToCourseWithDetails(rs));
            }
        } catch (SQLException e) {
            logger.error("Error fetching courses for department ID {}: {}", departmentId, e.getMessage());
            throw new DataAccessException("Error fetching courses by department.", e);
        }
        return courses;
    }

    @Override
    public List<Course> findCoursesByStudentIdAndTermId(int studentId, int termId) {
        List<Course> courses = new ArrayList<>();
        String sql = "SELECT c.*, p.program_name, d.department_name " +
                "FROM Courses c " +
                "JOIN Enrollments e ON c.course_id = e.course_id " +
                "LEFT JOIN Programs p ON c.program_id = p.program_id " +
                "JOIN Departments d ON c.department_id = d.department_id " +
                "WHERE e.student_id = ? AND e.academic_term_id = ? AND e.status = 'ENROLLED' " +
                "ORDER BY c.course_name";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, studentId);
            pstmt.setInt(2, termId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                courses.add(mapRowToCourseWithDetails(rs));
            }
        } catch (SQLException e) {
            logger.error("Error fetching courses for student {} in term {}: {}", studentId, termId, e.getMessage());
            throw new DataAccessException("Error fetching student's enrolled courses.", e);
        }
        return courses;
    }

    @Override
    public List<Course> findCoursesByFacultyIdAndTermId(int facultyMemberId, int termId) {
        List<Course> courses = new ArrayList<>();
        // This requires joining through LectureSessions to see what courses a faculty is teaching in a specific term
        String sql = "SELECT DISTINCT c.*, p.program_name, d.department_name " +
                "FROM Courses c " +
                "JOIN LectureSessions ls ON c.course_id = ls.course_id " +
                "LEFT JOIN Programs p ON c.program_id = p.program_id " +
                "JOIN Departments d ON c.department_id = d.department_id " +
                "WHERE ls.faculty_member_id = ? AND ls.academic_term_id = ? " +
                "ORDER BY c.course_name";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, facultyMemberId);
            pstmt.setInt(2, termId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                courses.add(mapRowToCourseWithDetails(rs));
            }
        } catch (SQLException e) {
            logger.error("Error fetching courses for faculty {} in term {}: {}", facultyMemberId, termId, e.getMessage());
            throw new DataAccessException("Error fetching faculty's assigned courses.", e);
        }
        return courses;
    }


    @Override
    public void update(Course course) {
        String sql = "UPDATE Courses SET course_code = ?, course_name = ?, description = ?, credits = ?, " +
                "program_id = ?, department_id = ? WHERE course_id = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, course.getCourseCode());
            pstmt.setString(2, course.getCourseName());
            pstmt.setString(3, course.getDescription());
            if (course.getCredits() != null) pstmt.setInt(4, course.getCredits());
            else pstmt.setNull(4, Types.INTEGER);
            if (course.getProgramId() != null) pstmt.setInt(5, course.getProgramId());
            else pstmt.setNull(5, Types.INTEGER);
            pstmt.setInt(6, course.getDepartmentId());
            pstmt.setInt(7, course.getCourseId());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                logger.warn("Updating course with ID {} failed, no rows affected or course not found.", course.getCourseId());
            } else {
                logger.info("Course updated successfully: {}", course.getCourseCode());
            }
        } catch (SQLException e) {
            logger.error("Error updating course {}: {}", course.getCourseCode(), e.getMessage());
            if (e.getMessage().contains("Duplicate entry")) {
                throw new DataAccessException("Course code '" + course.getCourseCode() + "' already exists for another course.", e);
            }
            throw new DataAccessException("Error updating course.", e);
        }
    }

    @Override
    public boolean delete(int courseId) {
        String sql = "DELETE FROM Courses WHERE course_id = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, courseId);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                logger.info("Course with ID {} deleted successfully.", courseId);
                return true;
            } else {
                logger.warn("Deleting course with ID {} failed, no rows affected or course not found.", courseId);
                return false;
            }
        } catch (SQLException e) {
            logger.error("Error deleting course with ID {}: {}", courseId, e.getMessage());
            if (e.getErrorCode() == 1451) { // FK constraint
                throw new DataAccessException("Cannot delete course. It is referenced by other records (e.g., Enrollments, Lecture Sessions).", e);
            }
            throw new DataAccessException("Error deleting course.", e);
        }
    }

    @Override
    public long countTotalCourses() {
        String sql = "SELECT COUNT(*) FROM Courses";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                return rs.getLong(1);
            }
        } catch (SQLException e) {
            logger.error("Error counting total courses: {}", e.getMessage());
            throw new DataAccessException("Error counting courses.", e);
        }
        return 0;
    }

    @Override
    public void assignFacultyToCourseInSession(int courseId, int facultyMemberId, int lectureSessionId) {

    }

    private Course mapRowToCourse(ResultSet rs) throws SQLException {
        Course course = new Course();
        course.setCourseId(rs.getInt("course_id"));
        course.setCourseCode(rs.getString("course_code"));
        course.setCourseName(rs.getString("course_name"));
        course.setDescription(rs.getString("description"));
        course.setCredits(rs.getObject("credits", Integer.class));
        course.setProgramId(rs.getObject("program_id", Integer.class));
        course.setDepartmentId(rs.getInt("department_id"));
        course.setCreatedAt(rs.getTimestamp("created_at"));
        course.setUpdatedAt(rs.getTimestamp("updated_at"));
        return course;
    }

    private Course mapRowToCourseWithDetails(ResultSet rs) throws SQLException {
        Course course = mapRowToCourse(rs);
        course.setProgramName(rs.getString("program_name")); // May be null
        course.setDepartmentName(rs.getString("department_name"));
        return course;
    }
}
