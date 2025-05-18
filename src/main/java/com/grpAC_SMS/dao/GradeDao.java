package com.grpAC_SMS.dao;

import com.grpAC_SMS.model.Grade;

import java.util.List;

public interface GradeDao {
    List<Grade> getGradesByStudentId(int studentId);
}