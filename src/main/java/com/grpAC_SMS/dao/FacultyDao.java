package com.grpAC_SMS.dao;

import com.grpAC_SMS.model.Faculty;
import java.util.List;
import java.util.Optional;

public interface FacultyDao {
    Faculty add(Faculty faculty);
    Optional<Faculty> findById(int facultyMemberId);
    Optional<Faculty> findByUserId(int userId);
    Optional<Faculty> findByFacultyUniqueId(String facultyUniqueId);
    List<Faculty> findAll();
    List<Faculty> findAllWithDetails(); // Includes department name, user email
    void update(Faculty faculty);
    boolean delete(int facultyMemberId);
    long countTotalFaculty();
}
