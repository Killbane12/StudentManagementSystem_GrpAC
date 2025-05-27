package com.grpAC_SMS.dao;

import com.grpAC_SMS.model.Course;
import java.util.List;
import java.util.Optional;

public interface CourseDao {
    Course add(Course course);
    Optional<Course> findById(int courseId);
    Optional<Course> findByCode(String courseCode);
    List<Course> findAll();
    List<Course> findAllWithDetails(); // For display with program and department names
    List<Course> findByProgramId(int programId);
    List<Course> findByDepartmentId(int departmentId);
    List<Course> findCoursesByStudentIdAndTermId(int studentId, int termId); // For student dashboard
    List<Course> findCoursesByFacultyIdAndTermId(int facultyMemberId, int termId); // For faculty dashboard
    void update(Course course);
    boolean delete(int courseId);
    long countTotalCourses();
    void assignFacultyToCourseInSession(int courseId, int facultyMemberId, int lectureSessionId); // Not here, belongs to LectureSessionDao
}
