package com.grpAC_SMS.dao.impl;

import com.grpAC_SMS.dao.CourseDao;
import com.grpAC_SMS.model.Course;
import com.grpAC_SMS.model.Student;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CourseDaoImpl implements CourseDao {
    String j_url = "jdbc:mysql://localhost:3306/grpac_sms_db";
    String j_un = "root";
    String j_pass = "";

//public String test() {
//    System.out.println("test123");

    public List<Course> selectCourse() {
        List<Course> course = new ArrayList<>();

        try {

            Connection con = DriverManager.getConnection(j_url, j_un, j_pass);
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM courses");

            while (rs.next()) {
                Course course1 = new Course();
                course1.setCourseId(rs.getInt("course_id"));
                course1.setCourseCode(rs.getString("course_code"));
                course1.setCourseName(rs.getString("course_name"));
                course1.setDescription(rs.getString("description"));
                course1.setCredits(rs.getInt("credits"));
                course1.setProgramId(rs.getInt("program_id"));
                course1.setDepartmentId(rs.getInt("department_id"));
                course1.setCreatedAt(rs.getTimestamp("created_at"));
                course1.setUpdatedAt(rs.getTimestamp("updated_at"));
                course.add(course1);
                System.out.println(course1);
            }

        } catch (SQLException se) {
            se.printStackTrace();
            se.getMessage();

        }
        return course;
//    return "";
    }

    @Override
    public List<Course> getCoursesByStudentId(int studentId) {
        List<Course> courseList = new ArrayList<>();

        String sql = "SELECT c.course_id, c.course_code, c.course_name " +
                "FROM courses c " +
                "JOIN enrollments e ON c.course_id = e.course_id " +
                "WHERE e.student_id = ?";

        try (Connection conn = DriverManager.getConnection(j_url, j_un, j_pass);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, studentId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Course course = new Course();
                course.setCourseId(rs.getInt("course_id"));
                course.setCourseCode(rs.getString("course_code"));
                course.setCourseName(rs.getString("course_name"));
                courseList.add(course);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return courseList;
    }

}