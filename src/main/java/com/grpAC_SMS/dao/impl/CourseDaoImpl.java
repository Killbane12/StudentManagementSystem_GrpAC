package com.grpAC_SMS.dao.impl;

import com.grpAC_SMS.dao.CourseDao;
import com.grpAC_SMS.model.Course;
import com.grpAC_SMS.model.Department;
import com.grpAC_SMS.model.Program;
import com.grpAC_SMS.util.DatabaseConnector;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of CourseDao for database operations
 */
public class CourseDaoImpl implements CourseDao {

    // Retrieves program details for a specific student
    @Override
    public Program getStudentProgram(int studentId) {
        String sql = "SELECT p.program_name, p.description, p.duration_years " +
                "FROM Programs p, Students s " +
                "WHERE s.student_id = ? AND s.program_id = p.program_id";

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, studentId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Program program = new Program();
                program.setProgramName(rs.getString("program_name"));
                program.setDescription(rs.getString("description"));
                program.setDurationYears(rs.getInt("duration_years"));
                return program;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Gets department by department ID
    @Override
    public Department getDepartmentById(int departmentId) {
        String sql = "SELECT department_name FROM Departments WHERE department_id = ?";

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, departmentId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Department department = new Department();
                department.setDepartmentId(departmentId);
                department.setDepartmentName(rs.getString("department_name"));
                return department;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Retrieves all courses for a given department
    @Override
    public List<Course> getCoursesByProgramId(int departmentId) {
        List<Course> courses = new ArrayList<>();
        String sql = "SELECT course_id, course_code, course_name, description, credits " +
                "FROM Courses WHERE department_id = ?";

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, departmentId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Course course = new Course();
                course.setCourseId(rs.getInt("course_id"));
                course.setCourseCode(rs.getString("course_code"));
                course.setCourseName(rs.getString("course_name"));
                course.setDescription(rs.getString("description"));
                course.setCredits(rs.getInt("credits"));
                courses.add(course);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return courses;
    }
}