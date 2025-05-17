package com.grpAC_SMS.dao;

import com.grpAC_SMS.exception.DataAccessException;
import com.grpAC_SMS.model.AcademicTerm;

import java.util.List;
import java.util.Optional;

public interface AcademicTermDao {
    void create(AcademicTerm academicTerm) throws DataAccessException;

    Optional<AcademicTerm> findById(int academicTermId) throws DataAccessException;

    List<AcademicTerm> findAll() throws DataAccessException;

    boolean update(AcademicTerm academicTerm) throws DataAccessException;

    boolean delete(int academicTermId) throws DataAccessException;
}

