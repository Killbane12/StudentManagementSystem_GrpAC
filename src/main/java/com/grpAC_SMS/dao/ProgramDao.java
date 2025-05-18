package com.grpAC_SMS.dao;

import com.grpAC_SMS.model.Program;

import java.util.List;
import java.util.Optional;

public interface ProgramDao {
    Program add(Program program);

    Optional<Program> findById(int programId);

    Optional<Program> findByName(String name);

    List<Program> findAll();

    List<Program> findAllWithDetails(); // For display with department name

    List<Program> findByDepartmentId(int departmentId);

    void update(Program program);

    boolean delete(int programId);
}
