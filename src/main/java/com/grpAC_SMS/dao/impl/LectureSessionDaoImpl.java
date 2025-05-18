package com.grpAC_SMS.dao.impl;

import com.grpAC_SMS.dao.LectureSessionDao;
import com.grpAC_SMS.exception.DataAccessException;
import com.grpAC_SMS.model.LectureSession;
import com.grpAC_SMS.util.DatabaseConnector;
import com.grpAC_SMS.util.DateFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class LectureSessionDaoImpl implements LectureSessionDao {
    private static final Logger logger = LoggerFactory.getLogger(LectureSessionDaoImpl.class);

    private static final String SELECT_SESSION_WITH_DETAILS_SQL =
            "SELECT ls.*, c.course_name, CONCAT(f.first_name, ' ', f.last_name) as faculty_name, " +
                    "t.term_name, l.location_name " +
                    "FROM LectureSessions ls " +
                    "JOIN Courses c ON ls.course_id = c.course_id " +
                    "JOIN AcademicTerms t ON ls.academic_term_id = t.academic_term_id " +
                    "LEFT JOIN Faculty f ON ls.faculty_member_id = f.faculty_member_id " +
                    "LEFT JOIN Locations l ON ls.location_id = l.location_id ";


    @Override
    public LectureSession add(LectureSession lectureSession) {
        String sql = "INSERT INTO LectureSessions (course_id, faculty_member_id, academic_term_id, location_id, " +
                "session_start_datetime, session_end_datetime) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, lectureSession.getCourseId());
            if (lectureSession.getFacultyMemberId() != null) pstmt.setInt(2, lectureSession.getFacultyMemberId());
            else pstmt.setNull(2, Types.INTEGER);
            pstmt.setInt(3, lectureSession.getAcademicTermId());
            if (lectureSession.getLocationId() != null) pstmt.setInt(4, lectureSession.getLocationId());
            else pstmt.setNull(4, Types.INTEGER);
            pstmt.setTimestamp(5, java.sql.Timestamp.valueOf(lectureSession.getSessionStartDatetime()));
            pstmt.setTimestamp(6, java.sql.Timestamp.valueOf(lectureSession.getSessionEndDatetime()));
            pstmt.setTimestamp(5, DateFormatter.localDateTimeToTimestamp(lectureSession.getSessionStartDatetime()));
//            pstmt.setTimestamp(6, DateFormatter.localDateTimeToTimestamp(lectureSession.getSessionEndDatetime()));

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating lecture session failed, no rows affected.");
            }
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    lectureSession.setLectureSessionId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating lecture session failed, no ID obtained.");
                }
            }
            logger.info("Lecture session added successfully with ID: {}", lectureSession.getLectureSessionId());
            return lectureSession;
        } catch (SQLException e) {
            logger.error("Error adding lecture session: {}", e.getMessage());
            throw new DataAccessException("Error adding lecture session.", e);
        }
    }

    @Override
    public Optional<LectureSession> findById(int lectureSessionId) {
        String sql = SELECT_SESSION_WITH_DETAILS_SQL + "WHERE ls.lecture_session_id = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, lectureSessionId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapRowToLectureSessionWithDetails(rs));
            }
        } catch (SQLException e) {
            logger.error("Error finding lecture session by ID {}: {}", lectureSessionId, e.getMessage());
            throw new DataAccessException("Error finding lecture session by ID.", e);
        }
        return Optional.empty();
    }

    @Override
    public List<LectureSession> findAll() { // Basic find all
        List<LectureSession> sessions = new ArrayList<>();
        String sql = "SELECT * FROM LectureSessions ORDER BY session_start_datetime DESC";
        try (Connection conn = DatabaseConnector.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                sessions.add(mapRowToLectureSession(rs));
            }
        } catch (SQLException e) {
            logger.error("Error fetching all lecture sessions: {}", e.getMessage());
            throw new DataAccessException("Error fetching all lecture sessions.", e);
        }
        return sessions;
    }

    @Override
    public List<LectureSession> findAllWithDetails() {
        List<LectureSession> sessions = new ArrayList<>();
        String sql = SELECT_SESSION_WITH_DETAILS_SQL + "ORDER BY ls.session_start_datetime DESC";
        try (Connection conn = DatabaseConnector.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                sessions.add(mapRowToLectureSessionWithDetails(rs));
            }
        } catch (SQLException e) {
            logger.error("Error fetching all lecture sessions with details: {}", e.getMessage());
            throw new DataAccessException("Error fetching all lecture sessions with details.", e);
        }
        return sessions;
    }

    @Override
    public List<LectureSession> findByCourseId(int courseId) {
        List<LectureSession> sessions = new ArrayList<>();
        String sql = SELECT_SESSION_WITH_DETAILS_SQL + "WHERE ls.course_id = ? ORDER BY ls.session_start_datetime DESC";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, courseId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                sessions.add(mapRowToLectureSessionWithDetails(rs));
            }
        } catch (SQLException e) {
            logger.error("Error fetching lecture sessions for course ID {}: {}", courseId, e.getMessage());
            throw new DataAccessException("Error fetching lecture sessions by course.", e);
        }
        return sessions;
    }

    @Override
    public List<LectureSession> findByFacultyId(int facultyMemberId) {
        List<LectureSession> sessions = new ArrayList<>();
        String sql = SELECT_SESSION_WITH_DETAILS_SQL + "WHERE ls.faculty_member_id = ? ORDER BY ls.session_start_datetime DESC";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, facultyMemberId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                sessions.add(mapRowToLectureSessionWithDetails(rs));
            }
        } catch (SQLException e) {
            logger.error("Error fetching lecture sessions for faculty ID {}: {}", facultyMemberId, e.getMessage());
            throw new DataAccessException("Error fetching lecture sessions by faculty.", e);
        }
        return sessions;
    }

    @Override
    public List<LectureSession> findByTermId(int academicTermId) {
        List<LectureSession> sessions = new ArrayList<>();
        String sql = SELECT_SESSION_WITH_DETAILS_SQL + "WHERE ls.academic_term_id = ? ORDER BY ls.session_start_datetime DESC";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, academicTermId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                sessions.add(mapRowToLectureSessionWithDetails(rs));
            }
        } catch (SQLException e) {
            logger.error("Error fetching lecture sessions for term ID {}: {}", academicTermId, e.getMessage());
            throw new DataAccessException("Error fetching lecture sessions by term.", e);
        }
        return sessions;
    }

    @Override
    public List<LectureSession> findByFacultyAndTerm(int facultyMemberId, int academicTermId) {
        List<LectureSession> sessions = new ArrayList<>();
        String sql = SELECT_SESSION_WITH_DETAILS_SQL +
                "WHERE ls.faculty_member_id = ? AND ls.academic_term_id = ? " +
                "ORDER BY ls.session_start_datetime DESC";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, facultyMemberId);
            pstmt.setInt(2, academicTermId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                sessions.add(mapRowToLectureSessionWithDetails(rs));
            }
        } catch (SQLException e) {
            logger.error("Error fetching sessions for faculty {} and term {}: {}", facultyMemberId, academicTermId, e.getMessage());
            throw new DataAccessException("Error fetching sessions by faculty and term.", e);
        }
        return sessions;
    }


    @Override
    public void update(LectureSession lectureSession) {
        String sql = "UPDATE LectureSessions SET course_id = ?, faculty_member_id = ?, academic_term_id = ?, location_id = ?, " +
                "session_start_datetime = ?, session_end_datetime = ? WHERE lecture_session_id = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, lectureSession.getCourseId());
            if (lectureSession.getFacultyMemberId() != null) pstmt.setInt(2, lectureSession.getFacultyMemberId());
            else pstmt.setNull(2, Types.INTEGER);
            pstmt.setInt(3, lectureSession.getAcademicTermId());
            if (lectureSession.getLocationId() != null) pstmt.setInt(4, lectureSession.getLocationId());
            else pstmt.setNull(4, Types.INTEGER);
            pstmt.setTimestamp(5, java.sql.Timestamp.valueOf(lectureSession.getSessionStartDatetime()));
            pstmt.setTimestamp(6, java.sql.Timestamp.valueOf(lectureSession.getSessionEndDatetime()));
//            pstmt.setTimestamp(5, DateFormatter.localDateTimeToTimestamp(lectureSession.getSessionStartDatetime()));
//            pstmt.setTimestamp(6, DateFormatter.localDateTimeToTimestamp(lectureSession.getSessionEndDatetime()));
            pstmt.setInt(7, lectureSession.getLectureSessionId());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                logger.warn("Updating lecture session with ID {} failed, no rows affected or session not found.", lectureSession.getLectureSessionId());
            } else {
                logger.info("Lecture session updated successfully with ID: {}", lectureSession.getLectureSessionId());
            }
        } catch (SQLException e) {
            logger.error("Error updating lecture session with ID {}: {}", lectureSession.getLectureSessionId(), e.getMessage());
            throw new DataAccessException("Error updating lecture session.", e);
        }
    }

    @Override
    public boolean delete(int lectureSessionId) {
        String sql = "DELETE FROM LectureSessions WHERE lecture_session_id = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, lectureSessionId);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                logger.info("Lecture session with ID {} deleted successfully.", lectureSessionId);
                return true;
            } else {
                logger.warn("Deleting lecture session with ID {} failed, no rows affected or session not found.", lectureSessionId);
                return false;
            }
        } catch (SQLException e) {
            logger.error("Error deleting lecture session with ID {}: {}", lectureSessionId, e.getMessage());
            if (e.getErrorCode() == 1451) { // FK constraint
                throw new DataAccessException("Cannot delete lecture session. It has related attendance records.", e);
            }
            throw new DataAccessException("Error deleting lecture session.", e);
        }
    }

    private LectureSession mapRowToLectureSession(ResultSet rs) throws SQLException {
        LectureSession session = new LectureSession();
        session.setLectureSessionId(rs.getInt("lecture_session_id"));
        session.setCourseId(rs.getInt("course_id"));
        session.setFacultyMemberId(rs.getObject("faculty_member_id", Integer.class));
        session.setAcademicTermId(rs.getInt("academic_term_id"));
        session.setLocationId(rs.getObject("location_id", Integer.class));
        session.setSessionStartDatetime(rs.getTimestamp("session_start_datetime").toLocalDateTime());
        session.setSessionEndDatetime(rs.getTimestamp("session_end_datetime").toLocalDateTime());
//        session.setSessionStartDatetime(DateFormatter.timestampToLocalDateTime(rs.getTimestamp("session_start_datetime")));
//        session.setSessionEndDatetime(DateFormatter.timestampToLocalDateTime(rs.getTimestamp("session_end_datetime")));
        session.setCreatedAt(rs.getTimestamp("created_at"));
        session.setUpdatedAt(rs.getTimestamp("updated_at"));
        return session;
    }

    private LectureSession mapRowToLectureSessionWithDetails(ResultSet rs) throws SQLException {
        LectureSession session = mapRowToLectureSession(rs);
        session.setCourseName(rs.getString("course_name"));
        session.setFacultyName(rs.getString("faculty_name"));
        session.setTermName(rs.getString("term_name"));
        session.setLocationName(rs.getString("location_name"));
        return session;
    }
}
