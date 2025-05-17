package com.grpAC_SMS.dao;

import com.grpAC_SMS.exception.DataAccessException;
import com.grpAC_SMS.model.Department;

import java.util.List;
import java.util.Optional;

public interface DepartmentDao {
    void create(Department department) throws DataAccessException;

    Optional<Department> findById(int departmentId) throws DataAccessException;

    List<Department> findAll() throws DataAccessException;

    boolean update(Department department) throws DataAccessException;

    boolean delete(int departmentId) throws DataAccessException;
}
