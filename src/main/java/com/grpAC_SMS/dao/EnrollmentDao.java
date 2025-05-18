package com.grpAC_SMS.dao;

import com.grpAC_SMS.model.Enrollment;

import java.util.List;
import java.util.Optional;

public interface EnrollmentDao {
    Enrollment add(Enrollment enrollment);

    Optional<Enrollment> findById(int enrollmentId);

    List<Enrollment> findAll();

    List<Enrollment> findAllWithDetails(); // Student name, course name, term name

    List<Enrollment> findByStudentId(int studentId);

    List<Enrollment> findByCourseId(int courseId);

    List<Enrollment> findByTermId(int academicTermId);

    Optional<Enrollment> findByStudentCourseTerm(int studentId, int courseId, int termId);

    boolean isStudentEnrolledInCourseForTerm(int studentId, int courseId, int termId);

    void update(Enrollment enrollment); // Mainly for status update

    boolean delete(int enrollmentId);
}
