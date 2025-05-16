package com.grpAC_SMS.dao;

import com.grpAC_SMS.exception.DataAccessException;
import com.grpAC_SMS.model.Attendance;

import java.util.List;
import java.util.Optional;

public interface AttendanceDao {
    void create(Attendance attendance) throws DataAccessException;

    Optional<Attendance> findById(int attendanceId) throws DataAccessException;

    List<Attendance> findAll() throws DataAccessException;

    boolean update(Attendance attendance) throws DataAccessException;

    boolean delete(int attendanceId) throws DataAccessException;

    List<Attendance> findByLectureSession(int lectureSessionId) throws DataAccessException; // Added method
}
