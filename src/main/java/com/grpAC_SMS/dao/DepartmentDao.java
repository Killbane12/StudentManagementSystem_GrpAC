package com.grpAC_SMS.dao;

import com.grpAC_SMS.model.Department;
import java.util.List;
import java.util.Optional;

public interface DepartmentDao {
    Department add(Department department);
    Optional<Department> findById(int departmentId);
    Optional<Department> findByName(String name);
    List<Department> findAll();
    void update(Department department);
    boolean delete(int departmentId); // Return boolean for success
}
