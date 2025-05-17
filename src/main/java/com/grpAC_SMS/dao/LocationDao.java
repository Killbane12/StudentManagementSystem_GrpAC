package com.grpAC_SMS.dao;

import com.grpAC_SMS.model.Location;
import java.util.List;
import java.util.Optional;

public interface LocationDao {
    Location add(Location location);
    Optional<Location> findById(int locationId);
    Optional<Location> findByName(String name);
    List<Location> findAll();
    void update(Location location);
    boolean delete(int locationId);
}
