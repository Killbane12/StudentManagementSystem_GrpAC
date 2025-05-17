package com.grpAC_SMS.dao.impl;

import com.grpAC_SMS.dao.AttendanceDao;
import com.grpAC_SMS.model.Attendance;
import com.grpAC_SMS.util.DatabaseConnector;
import com.grpAC_SMS.exception.DataAccessException;
import com.grpAC_SMS.util.DateFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class AttendanceDaoImpl implements AttendanceDao {
    private static final Logger logger = LoggerFactory.getLogger(AttendanceDaoImpl.class);

    private static final String SELECT_ATTENDANCE_DETAILS_SQL =
            "SELECT a.*, CONCAT(s.first_name, ' ', s.last_name) as student_name, s.student_unique_id, " +
                    "c.course_name, ls.session_start_datetime " +
                    "FROM Attendance a " +
                    "JOIN Students s ON a.student_id = s.student_id " +
                    "JOIN LectureSessions ls ON a.lecture_session_id = ls.lecture_session_id " +
                    "JOIN Courses c ON ls.course_id = c.course_id ";

    @Override
    public Attendance add(Attendance attendance) {
        String sql = "INSERT INTO Attendance (student_id, lecture_session_id, punch_in_timestamp, is_present, " +
                "recorded_by_faculty_id, device_id) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, attendance.getStudentId());
            pstmt.setInt(2, attendance.getLectureSessionId());
            pstmt.setTimestamp(3, attendance.getPunchInTimestamp());
            pstmt.setBoolean(4, attendance.isPresent());
            if (attendance.getRecordedByFacultyId() != null) pstmt.setInt(5, attendance.getRecordedByFacultyId());
            else pstmt.setNull(5, Types.INTEGER);
            pstmt.setString(6, attendance.getDeviceId());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating attendance record failed, no rows affected.");
            }
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    attendance.setAttendanceId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating attendance record failed, no ID obtained.");
                }
            }
            logger.info("Attendance record added for student {}, session {}", attendance.getStudentId(), attendance.getLectureSessionId());
            return attendance;
        } catch (SQLException e) {
            logger.error("Error adding attendance for student {}, session {}: {}",
                    attendance.getStudentId(), attendance.getLectureSessionId(), e.getMessage());
            if (e.getMessage().contains("Duplicate entry") && e.getMessage().contains("uk_student_lecture_session")) {
                throw new DataAccessException("Attendance record already exists for this student and lecture session.", e);
            }
            throw new DataAccessException("Error adding attendance record.", e);
        }
    }

    @Override
    public Optional<Attendance> findById(int attendanceId) {
        String sql = SELECT_ATTENDANCE_DETAILS_SQL + "WHERE a.attendance_id = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, attendanceId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapRowToAttendanceWithDetails(rs));
            }
        } catch (SQLException e) {
            logger.error("Error finding attendance by ID {}: {}", attendanceId, e.getMessage());
            throw new DataAccessException("Error finding attendance by ID.", e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Attendance> findByStudentAndSession(int studentId, int lectureSessionId) {
        // Uses basic mapper as details might not be needed for simple checks
        String sql = "SELECT * FROM Attendance WHERE student_id = ? AND lecture_session_id = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, studentId);
            pstmt.setInt(2, lectureSessionId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapRowToAttendance(rs));
            }
        } catch (SQLException e) {
            logger.error("Error finding attendance for student {} and session {}: {}", studentId, lectureSessionId, e.getMessage());
            throw new DataAccessException("Error finding attendance by student and session.", e);
        }
        return Optional.empty();
    }

    @Override
    public List<Attendance> findAll() { // Basic find all
        List<Attendance> attendances = new ArrayList<>();
        String sql = "SELECT * FROM Attendance ORDER BY punch_in_timestamp DESC";
        try (Connection conn = DatabaseConnector.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                attendances.add(mapRowToAttendance(rs));
            }
        } catch (SQLException e) {
            logger.error("Error fetching all attendance records: {}", e.getMessage());
            throw new DataAccessException("Error fetching all attendance records.", e);
        }
        return attendances;
    }

    @Override
    public List<Attendance> findAllWithDetails() {
        List<Attendance> attendances = new ArrayList<>();
        String sql = SELECT_ATTENDANCE_DETAILS_SQL + "ORDER BY a.punch_in_timestamp DESC, s.last_name";
        try (Connection conn = DatabaseConnector.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                attendances.add(mapRowToAttendanceWithDetails(rs));
            }
        } catch (SQLException e) {
            logger.error("Error fetching all attendance records with details: {}", e.getMessage());
            throw new DataAccessException("Error fetching all attendance records with details.", e);
        }
        return attendances;
    }


    @Override
    public List<Attendance> findByStudentId(int studentId) {
        List<Attendance> attendances = new ArrayList<>();
        String sql = SELECT_ATTENDANCE_DETAILS_SQL + "WHERE a.student_id = ? ORDER BY ls.session_start_datetime DESC";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, studentId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                attendances.add(mapRowToAttendanceWithDetails(rs));
            }
        } catch (SQLException e) {
            logger.error("Error fetching attendance for student ID {}: {}", studentId, e.getMessage());
            throw new DataAccessException("Error fetching attendance by student.", e);
        }
        return attendances;
    }

    @Override
    public List<Attendance> findByLectureSessionId(int lectureSessionId) {
        List<Attendance> attendances = new ArrayList<>();
        String sql = SELECT_ATTENDANCE_DETAILS_SQL + "WHERE a.lecture_session_id = ? ORDER BY s.last_name";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, lectureSessionId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                attendances.add(mapRowToAttendanceWithDetails(rs));
            }
        } catch (SQLException e) {
            logger.error("Error fetching attendance for lecture session ID {}: {}", lectureSessionId, e.getMessage());
            throw new DataAccessException("Error fetching attendance by lecture session.", e);
        }
        return attendances;
    }

    @Override
    public List<Attendance> findByStudentAndCourse(int studentId, int courseId) {
        List<Attendance> attendances = new ArrayList<>();
        String sql = SELECT_ATTENDANCE_DETAILS_SQL +
                "WHERE a.student_id = ? AND ls.course_id = ? " +
                "ORDER BY ls.session_start_datetime DESC";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, studentId);
            pstmt.setInt(2, courseId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                attendances.add(mapRowToAttendanceWithDetails(rs));
            }
        } catch (SQLException e) {
            logger.error("Error fetching attendance for student {} and course {}: {}", studentId, courseId, e.getMessage());
            throw new DataAccessException("Error fetching attendance by student and course.", e);
        }
        return attendances;
    }


    @Override
    public void update(Attendance attendance) {
        String sql = "UPDATE Attendance SET student_id = ?, lecture_session_id = ?, punch_in_timestamp = ?, is_present = ?, " +
                "recorded_by_faculty_id = ?, device_id = ? WHERE attendance_id = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, attendance.getStudentId());
            pstmt.setInt(2, attendance.getLectureSessionId());
            pstmt.setTimestamp(3, attendance.getPunchInTimestamp());
            pstmt.setBoolean(4, attendance.isPresent());
            if (attendance.getRecordedByFacultyId() != null) pstmt.setInt(5, attendance.getRecordedByFacultyId());
            else pstmt.setNull(5, Types.INTEGER);
            pstmt.setString(6, attendance.getDeviceId());
            pstmt.setInt(7, attendance.getAttendanceId());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                logger.warn("Updating attendance with ID {} failed, no rows affected or record not found.", attendance.getAttendanceId());
            } else {
                logger.info("Attendance record updated successfully for ID: {}", attendance.getAttendanceId());
            }
        } catch (SQLException e) {
            logger.error("Error updating attendance record with ID {}: {}", attendance.getAttendanceId(), e.getMessage());
            if (e.getMessage().contains("Duplicate entry") && e.getMessage().contains("uk_student_lecture_session")) {
                throw new DataAccessException("Cannot update: Attendance record already exists for this student and lecture session under a different attendance ID.", e);
            }
            throw new DataAccessException("Error updating attendance record.", e);
        }
    }

    @Override
    public boolean delete(int attendanceId) {
        String sql = "DELETE FROM Attendance WHERE attendance_id = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, attendanceId);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                logger.info("Attendance record with ID {} deleted successfully.", attendanceId);
                return true;
            } else {
                logger.warn("Deleting attendance with ID {} failed, no rows affected or record not found.", attendanceId);
                return false;
            }
        } catch (SQLException e) {
            logger.error("Error deleting attendance record with ID {}: {}", attendanceId, e.getMessage());
            throw new DataAccessException("Error deleting attendance record.", e);
        }
    }

    @Override
    public double calculateAttendancePercentage(int studentId, int courseId, int termId) {
        String sqlTotalSessions = "SELECT COUNT(DISTINCT ls.lecture_session_id) " +
                "FROM LectureSessions ls " +
                "JOIN Enrollments e ON ls.course_id = e.course_id AND ls.academic_term_id = e.academic_term_id " +
                "WHERE e.student_id = ? AND ls.course_id = ? AND ls.academic_term_id = ? AND e.status = 'ENROLLED'";

        String sqlPresentSessions = "SELECT COUNT(DISTINCT a.lecture_session_id) " +
                "FROM Attendance a " +
                "JOIN LectureSessions ls ON a.lecture_session_id = ls.lecture_session_id " +
                "WHERE a.student_id = ? AND ls.course_id = ? AND ls.academic_term_id = ? AND a.is_present = TRUE";

        long totalSessions = 0;
        long presentSessions = 0;

        try (Connection conn = DatabaseConnector.getConnection()) {
            // Get total applicable sessions for the student in that course and term
            try (PreparedStatement pstmtTotal = conn.prepareStatement(sqlTotalSessions)) {
                pstmtTotal.setInt(1, studentId);
                pstmtTotal.setInt(2, courseId);
                pstmtTotal.setInt(3, termId);
                ResultSet rsTotal = pstmtTotal.executeQuery();
                if (rsTotal.next()) {
                    totalSessions = rsTotal.getLong(1);
                }
            }

            // Get sessions where student was present
            try (PreparedStatement pstmtPresent = conn.prepareStatement(sqlPresentSessions)) {
                pstmtPresent.setInt(1, studentId);
                pstmtPresent.setInt(2, courseId);
                pstmtPresent.setInt(3, termId);
                ResultSet rsPresent = pstmtPresent.executeQuery();
                if (rsPresent.next()) {
                    presentSessions = rsPresent.getLong(1);
                }
            }

            if (totalSessions == 0) {
                return 0.0; // Avoid division by zero, or could be 100% if no sessions implies full attendance.
            }
            return ((double) presentSessions / totalSessions) * 100.0;

        } catch (SQLException e) {
            logger.error("Error calculating attendance percentage for student {}, course {}, term {}: {}",
                    studentId, courseId, termId, e.getMessage());
            throw new DataAccessException("Error calculating attendance percentage.", e);
        }
    }

    @Override
    public Map<Integer, Long[]> getAttendanceCountsForCourse(int courseId, int termId) {
        Map<Integer, Long[]> studentAttendanceCounts = new HashMap<>();
        // Long[] will be [present_count, total_sessions_student_enrolled_for]

        // Get all students enrolled in the course for the term
        String enrolledStudentsSql = "SELECT student_id FROM Enrollments WHERE course_id = ? AND academic_term_id = ? AND status = 'ENROLLED'";
        // Get total sessions for the course in the term
        String totalCourseSessionsSql = "SELECT lecture_session_id FROM LectureSessions WHERE course_id = ? AND academic_term_id = ?";
        // Get present count for a student for these sessions
        String presentCountSql = "SELECT COUNT(DISTINCT lecture_session_id) FROM Attendance " +
                "WHERE student_id = ? AND lecture_session_id IN (SELECT lecture_session_id FROM LectureSessions WHERE course_id = ? AND academic_term_id = ?) " +
                "AND is_present = TRUE";

        try (Connection conn = DatabaseConnector.getConnection()) {
            List<Integer> lectureSessionIdsForCourse = new ArrayList<>();
            try (PreparedStatement pstmtTotalCourseSessions = conn.prepareStatement(totalCourseSessionsSql)) {
                pstmtTotalCourseSessions.setInt(1, courseId);
                pstmtTotalCourseSessions.setInt(2, termId);
                ResultSet rsSessions = pstmtTotalCourseSessions.executeQuery();
                while(rsSessions.next()){
                    lectureSessionIdsForCourse.add(rsSessions.getInt("lecture_session_id"));
                }
            }
            long totalSessionsInCourse = lectureSessionIdsForCourse.size();
            if (totalSessionsInCourse == 0) return studentAttendanceCounts; // No sessions, no attendance

            try (PreparedStatement pstmtEnrolled = conn.prepareStatement(enrolledStudentsSql)) {
                pstmtEnrolled.setInt(1, courseId);
                pstmtEnrolled.setInt(2, termId);
                ResultSet rsEnrolled = pstmtEnrolled.executeQuery();

                while (rsEnrolled.next()) {
                    int studentId = rsEnrolled.getInt("student_id");
                    long presentCount = 0;
                    try (PreparedStatement pstmtPresent = conn.prepareStatement(presentCountSql)) {
                        pstmtPresent.setInt(1, studentId);
                        pstmtPresent.setInt(2, courseId);
                        pstmtPresent.setInt(3, termId);
                        ResultSet rsPresent = pstmtPresent.executeQuery();
                        if (rsPresent.next()) {
                            presentCount = rsPresent.getLong(1);
                        }
                    }
                    // Assuming student is considered for all sessions of the course they are enrolled in.
                    studentAttendanceCounts.put(studentId, new Long[]{presentCount, totalSessionsInCourse});
                }
            }
        } catch (SQLException e) {
            logger.error("Error getting attendance counts for course {} term {}: {}", courseId, termId, e.getMessage());
            throw new DataAccessException("Error getting attendance counts.", e);
        }
        return studentAttendanceCounts;
    }

    private Attendance mapRowToAttendance(ResultSet rs) throws SQLException {
        Attendance attendance = new Attendance();
        attendance.setAttendanceId(rs.getInt("attendance_id"));
        attendance.setStudentId(rs.getInt("student_id"));
        attendance.setLectureSessionId(rs.getInt("lecture_session_id"));
        attendance.setPunchInTimestamp(rs.getTimestamp("punch_in_timestamp"));
        attendance.setPresent(rs.getBoolean("is_present"));
        attendance.setRecordedByFacultyId(rs.getObject("recorded_by_faculty_id", Integer.class));
        attendance.setDeviceId(rs.getString("device_id"));
        attendance.setCreatedAt(rs.getTimestamp("created_at"));
        attendance.setUpdatedAt(rs.getTimestamp("updated_at"));
        return attendance;
    }

    private Attendance mapRowToAttendanceWithDetails(ResultSet rs) throws SQLException {
        Attendance attendance = mapRowToAttendance(rs);
        attendance.setStudentName(rs.getString("student_name"));
        attendance.setStudentUniqueId(rs.getString("student_unique_id"));
        attendance.setCourseName(rs.getString("course_name"));
        attendance.setSessionStartDateTime(rs.getTimestamp("session_start_datetime"));
        return attendance;
    }
}
