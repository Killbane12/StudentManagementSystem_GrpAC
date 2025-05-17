package com.grpAC_SMS.dao;

import com.grpAC_SMS.exception.DataAccessException;
import com.grpAC_SMS.model.Location;

import java.util.List;
import java.util.Optional;

public interface LocationDao {
    void create(Location location) throws DataAccessException;

    Optional<Location> findById(int locationId) throws DataAccessException;

    List<Location> findAll() throws DataAccessException;

    boolean update(Location location) throws DataAccessException;

    boolean delete(int locationId) throws DataAccessException;
}
