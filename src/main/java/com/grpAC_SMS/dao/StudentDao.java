package com.grpAC_SMS.dao;

import com.grpAC_SMS.model.Student;
import com.grpAC_SMS.model.User;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public interface StudentDao {
    Student getStudentByUsername(String username);

    User getUserByUsername(String username);
    
//     List<Student> selectStudents();
}