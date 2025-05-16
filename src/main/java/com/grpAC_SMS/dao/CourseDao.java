package com.grpAC_SMS.dao;

import com.grpAC_SMS.exception.DataAccessException;
import com.grpAC_SMS.model.Course;

import java.util.List;
import java.util.Optional;

public interface CourseDao {
    void create(Course course) throws DataAccessException;

    Optional<Course> findById(int courseId) throws DataAccessException;

    List<Course> findAll() throws DataAccessException;

    boolean update(Course course) throws DataAccessException;

    boolean delete(int courseId) throws DataAccessException;

    List<Course> findByDepartment(int departmentId) throws DataAccessException; // Added method
}
