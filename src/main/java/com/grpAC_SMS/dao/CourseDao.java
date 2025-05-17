package com.grpAC_SMS.dao;

import com.grpAC_SMS.model.Course;
import com.grpAC_SMS.model.Department;
import com.grpAC_SMS.model.Program;
import com.grpAC_SMS.model.Student;
import com.grpAC_SMS.exception.DataAccessException;

import java.util.List;
import java.util.Optional;
import java.util.ArrayList;

/**
 * DAO interface for course-related database operations
 */
public interface CourseDao {

    // Get program details for a student
    Program getStudentProgram(int studentId);

    // Get department by ID
    Department getDepartmentById(int departmentId);

    // Get all courses for a program
    List<Course> getCoursesByProgramId(int programId);

}