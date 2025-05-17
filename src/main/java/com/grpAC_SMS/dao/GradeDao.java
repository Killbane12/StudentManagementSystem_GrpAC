package com.grpAC_SMS.dao;

import com.grpAC_SMS.model.Grade;
import java.util.List;
import java.util.Optional;

public interface GradeDao {
    Grade add(Grade grade);
    Optional<Grade> findById(int gradeId);
    List<Grade> findAll();
    List<Grade> findAllWithDetails(); // Includes student, course, faculty names
    List<Grade> findByEnrollmentId(int enrollmentId);
    List<Grade> findByStudentId(int studentId); // All grades for a student
    List<Grade> findByCourseId(int courseId); // All grades for a course
    List<Grade> findGradesForFacultyCourse(int facultyMemberId, int courseId, int termId);
    Optional<Grade> findByEnrollmentAndAssessmentType(int enrollmentId, String assessmentType);
    void update(Grade grade);
    boolean delete(int gradeId);
}
