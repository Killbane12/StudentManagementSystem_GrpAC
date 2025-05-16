package com.grpAC_SMS.dao;

import com.grpAC_SMS.exception.DataAccessException;
import com.grpAC_SMS.model.Faculty;

import java.util.List;
import java.util.Optional;

public interface FacultyDao {
    void create(Faculty faculty) throws DataAccessException;

    Optional<Faculty> findById(int facultyMemberId) throws DataAccessException;

    List<Faculty> findAll() throws DataAccessException;

    boolean update(Faculty faculty) throws DataAccessException;

    boolean delete(int facultyMemberId) throws DataAccessException;

    Optional<Faculty> findByUserId(int userId) throws DataAccessException; // Added method
}