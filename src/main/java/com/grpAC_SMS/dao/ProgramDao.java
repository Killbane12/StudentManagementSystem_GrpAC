package com.grpAC_SMS.dao;

import com.grpAC_SMS.exception.DataAccessException;
import com.grpAC_SMS.model.Program;

import java.util.List;
import java.util.Optional;

public interface ProgramDao {
    void create(Program program) throws DataAccessException;

    Optional<Program> findById(int programId) throws DataAccessException;

    List<Program> findAll() throws DataAccessException;

    boolean update(Program program) throws DataAccessException;

    boolean delete(int programId) throws DataAccessException;
}