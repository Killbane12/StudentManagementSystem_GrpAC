package com.grpAC_SMS.dao.impl;

import com.grpAC_SMS.dao.StudentDao;
import com.grpAC_SMS.model.Student;
import com.grpAC_SMS.model.User;
import com.grpAC_SMS.util.DatabaseConnector;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class StudentDaoImpl implements StudentDao {

    // Select user table in the student email
    @Override
    public User getUserByUsername(String username) {
        String sql = "SELECT * FROM student WHERE username = ?";

        try (Connection conn = DatabaseConnector.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                User user = new User();
                user.setEmail(rs.getString("email"));
                return user;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Select the student table in the student data
    @Override
    public Student getStudentByUsername(String username) {

        String sql = "SELECT s.*, u.email FROM Student s " +
                "JOIN Users u ON s.user_id = u.user_id" +
                "WHERE u.username = ?";

        try (Connection conn = DatabaseConnector.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Student student = new Student();
                student.setStudentId(rs.getInt("student_id"));
                student.setUserId(rs.getInt("user_id"));
                student.setStudentUniqueId(rs.getString("student_unique_id"));
                student.setFirstName(rs.getString("first_name"));
                student.setLastName(rs.getString("last_name"));
                student.setDateOfBirth(rs.getDate("date_of_birth"));
                student.setGender(rs.getString("gender"));
                student.setAddress(rs.getString("address"));
                student.setPhoneNumber(rs.getString("phone_number"));
                return student;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
}