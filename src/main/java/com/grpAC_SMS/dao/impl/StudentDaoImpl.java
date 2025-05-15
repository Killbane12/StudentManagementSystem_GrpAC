package com.grpAC_SMS.dao.impl;

import com.grpAC_SMS.dao.StudentDao;
import com.grpAC_SMS.model.Student;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StudentDaoImpl implements StudentDao {
    String j_url = "jdbc:mysql://localhost:3306/grpac_sms_db";
    String j_un = "root";
    String j_pass = "";

//public String test() {
//    System.out.println("test123");

    public List<Student> selectStudents() {
        List<Student> students = new ArrayList<>();

        try {

            Connection con = DriverManager.getConnection(j_url, j_un, j_pass);
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM students");

            while (rs.next()) {
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
                student.setEnrollmentDate(rs.getDate("enrollment_date"));
                student.setProgramId(rs.getInt("program_id"));
                student.setCreatedAt(rs.getTimestamp("created_at"));
                student.setUpdatedAt(rs.getTimestamp("updated_at"));

                students.add(student);
                System.out.println(student);
            }

        } catch (SQLException se) {
            se.printStackTrace();
            se.getMessage();

        }
        return students;
//    return "";
    }
}