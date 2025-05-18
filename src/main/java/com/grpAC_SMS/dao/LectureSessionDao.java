package com.grpAC_SMS.dao;

import com.grpAC_SMS.model.LectureSession;

import java.util.List;
import java.util.Optional;

public interface LectureSessionDao {
    LectureSession add(LectureSession lectureSession);

    Optional<LectureSession> findById(int lectureSessionId);

    List<LectureSession> findAll();

    List<LectureSession> findAllWithDetails(); // Includes course, faculty, term, location names

    List<LectureSession> findByCourseId(int courseId);

    List<LectureSession> findByFacultyId(int facultyMemberId);

    List<LectureSession> findByTermId(int academicTermId);

    List<LectureSession> findByFacultyAndTerm(int facultyMemberId, int academicTermId); // For faculty view

    void update(LectureSession lectureSession);

    boolean delete(int lectureSessionId);
}
