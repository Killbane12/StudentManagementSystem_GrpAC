package com.grpAC_SMS.dao;

import com.grpAC_SMS.model.Student;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public interface StudentDao {
    List<Student> selectStudents();
}