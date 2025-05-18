package com.grpAC_SMS.dao.impl;

import com.grpAC_SMS.dao.GradeDao;
import com.grpAC_SMS.model.Grade;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static javax.swing.text.html.HTML.Tag.SELECT;

public class GradeDaoImpl implements GradeDao {
    String j_url = "jdbc:mysql://localhost:3306/grpac_sms_db";
    String j_un = "root";
    String j_pass = "";

    @Override
    public List<Grade> getGradesByStudentId(int studentId) {
        List<Grade> gradeList = new ArrayList<>();

        String sql = "SELECT * FROM enrollments WHERE student_id = 1;\n" +
                "SELECT * FROM grades WHERE enrollment_id IN (SELECT enrollment_id FROM enrollments WHERE student_id = 1);\n" +
                "SELECT c.course_name, g.grade_value, g.assessment_type\n" +
                "FROM grades g\n" +
                "JOIN enrollments e ON g.enrollment_id = e.enrollment_id\n" +
                "JOIN courses c ON e.course_id = c.course_id\n" +
                "WHERE e.student_id = 1;";

        try (Connection conn = DriverManager.getConnection(j_url, j_un, j_pass);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, studentId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Grade grade = new Grade();
                grade.setCourseName(rs.getString("course_name"));
                grade.setGradeValue(rs.getString("grade_value"));
                grade.setAssessmentType(rs.getString("assessment_type"));

                gradeList.add(grade);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return gradeList;
    }
}
