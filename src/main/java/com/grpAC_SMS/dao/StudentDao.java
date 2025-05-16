package com.grpAC_SMS.dao;

import com.grpAC_SMS.model.Student;
import com.grpAC_SMS.model.User;

public interface StudentDao {
    Student getStudentByUsername(String username);

    User getUserByUsername(String username);
}