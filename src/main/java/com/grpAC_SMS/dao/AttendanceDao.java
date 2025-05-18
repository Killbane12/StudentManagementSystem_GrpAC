package com.grpAC_SMS.dao;

import com.grpAC_SMS.model.Attendance;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface AttendanceDao {
    Attendance add(Attendance attendance);

    Optional<Attendance> findById(int attendanceId);

    Optional<Attendance> findByStudentAndSession(int studentId, int lectureSessionId);

    List<Attendance> findAll();

    List<Attendance> findAllWithDetails(); // Student name, course name from session

    List<Attendance> findByStudentId(int studentId);

    List<Attendance> findByLectureSessionId(int lectureSessionId); // All attendance for a session

    List<Attendance> findByStudentAndCourse(int studentId, int courseId); // All attendance for a student in a specific course

    void update(Attendance attendance); // For faculty to mark present/absent or NFC update

    boolean delete(int attendanceId);

    double calculateAttendancePercentage(int studentId, int courseId, int termId);

    Map<Integer, Long[]> getAttendanceCountsForCourse(int courseId, int termId); // Returns map of studentId -> [present, total_sessions_for_student]

}
