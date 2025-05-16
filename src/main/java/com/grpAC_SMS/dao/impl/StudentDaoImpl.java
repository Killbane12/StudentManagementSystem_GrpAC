package com.grpAC_SMS.dao.impl;

import com.grpAC_SMS.dao.StudentDao;
import com.grpAC_SMS.model.Student;
import com.grpAC_SMS.model.User;
import com.grpAC_SMS.util.DatabaseConnector;
import java.sql.*;

/**
 * Implementation of the StudentDao interface to handle
 * data access operations related to students and users.
 */
public class StudentDaoImpl implements StudentDao {


    @Override
    public User getUserByUsername(String username) {
        String sql = "SELECT * FROM Users WHERE username = ?";

        // Check database connection
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            // If a user is found, populate and return the User object
            if (rs.next()) {
                User user = new User();
                user.setEmail(rs.getString("email"));
                return user;
            }

        } catch (SQLException e) {
            e.printStackTrace(); // Log exception details
        }
        return null;  // Return null if no user is found or an error occurs
    }

    /**
     * Retrieves a Student object by looking up the associated user by username.
     * Performs a join between Students and Users table to get full student info.
     *
     * @param username the username to identify the student
     * @return a Student object if found, otherwise null
     */
    @Override
    public Student getStudentByUsername(String username) {
        // SQL query joins Students and Users table using user_id to retrieve student details
        String sql = "SELECT s.*, u.email FROM Students s " +
                "JOIN Users u ON s.user_id = u.user_id " +
                "WHERE u.username = ?";


        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Set the username parameter in the query
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            // If a student is found, populate and return the Student object
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
            e.printStackTrace();  // Log any SQL exceptions
        }
        return null;   // Return null if no student is found or an error occurs
    }
}