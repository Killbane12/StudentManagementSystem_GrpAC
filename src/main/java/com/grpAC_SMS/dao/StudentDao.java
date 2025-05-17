package com.grpAC_SMS.dao;

import com.grpAC_SMS.model.Student;

import java.util.List;
import java.util.Optional;

public interface StudentDao {
    Student add(Student student);

    Optional<Student> findById(int studentId);

    Optional<Student> findByUserId(int userId);

    Optional<Student> findByStudentUniqueId(String studentUniqueId);

    List<Student> findAll();

    List<Student> findAllWithDetails(); // Includes program name, user email

    void update(Student student);

    boolean delete(int studentId);

    List<Student> findUnmarkedStudentsForSession(int lectureSessionId, int courseId); // For NFC

    List<Student> findByCourseIdAndTermId(int courseId, int termId); // Students enrolled in a course

    long countTotalStudents();
}
