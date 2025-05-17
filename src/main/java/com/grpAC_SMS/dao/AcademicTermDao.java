package com.grpAC_SMS.dao;

import com.grpAC_SMS.model.AcademicTerm;
import java.util.List;
import java.util.Optional;
import java.time.LocalDate;

public interface AcademicTermDao {
    AcademicTerm add(AcademicTerm academicTerm);
    Optional<AcademicTerm> findById(int academicTermId);
    Optional<AcademicTerm> findByName(String termName);
    List<AcademicTerm> findAll();
    Optional<AcademicTerm> findCurrentTerm(LocalDate date); // Find term covering a specific date
    void update(AcademicTerm academicTerm);
    boolean delete(int academicTermId);
}
