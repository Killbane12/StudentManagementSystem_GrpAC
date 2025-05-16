package com.grpAC_SMS.dao.impl;

import com.grpAC_SMS.dao.LocationDao;
import com.grpAC_SMS.exception.DataAccessException;
import com.grpAC_SMS.model.Location;
import com.grpAC_SMS.util.DatabaseConnector;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class LocationDaoImpl implements LocationDao {

    // --- mapRowToLocation Helper Method ---
    private Location mapRowToLocation(ResultSet rs) throws SQLException {
        Location location = new Location();
        location.setLocationId(rs.getInt("location_id"));
        location.setLocationName(rs.getString("location_name"));
        location.setCapacity(rs.getObject("capacity") != null ? rs.getInt("capacity") : null);
        location.setCreatedAt(rs.getTimestamp("created_at"));
        location.setUpdatedAt(rs.getTimestamp("updated_at"));
        return location;
    }

    @Override
    public void create(Location location) throws DataAccessException {
        String sql = "INSERT INTO Locations (location_name, capacity, created_at, updated_at) VALUES (?, ?, NOW(), NOW())";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, location.getLocationName());
            stmt.setObject(2, location.getCapacity());
            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    location.setLocationId(generatedKeys.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error creating location: " + location.getLocationName(), e);
        }
    }

    @Override
    public Optional<Location> findById(int locationId) throws DataAccessException {
        String sql = "SELECT * FROM Locations WHERE location_id = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, locationId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToLocation(rs));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error finding location by ID: " + locationId, e);
        }
        return Optional.empty();
    }

    @Override
    public List<Location> findAll() throws DataAccessException {
        List<Location> locations = new ArrayList<>();
        String sql = "SELECT * FROM Locations";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                locations.add(mapRowToLocation(rs));
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error finding all locations", e);
        }
        return locations;
    }

    @Override
    public boolean update(Location location) throws DataAccessException {
        String sql = "UPDATE Locations SET location_name = ?, capacity = ?, updated_at = NOW() WHERE location_id = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, location.getLocationName());
            stmt.setObject(2, location.getCapacity());
            stmt.setInt(3, location.getLocationId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataAccessException("Error updating location: " + location.getLocationName(), e);
        }
    }

    @Override
    public boolean delete(int locationId) throws DataAccessException {
        String sql = "DELETE FROM Locations WHERE location_id = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, locationId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataAccessException("Error deleting location with ID: " + locationId, e);
        }
    }
}
