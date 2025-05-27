package com.grpAC_SMS.dao.impl;

import com.grpAC_SMS.dao.LocationDao;
import com.grpAC_SMS.model.Location;
import com.grpAC_SMS.util.DatabaseConnector;
import com.grpAC_SMS.exception.DataAccessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class LocationDaoImpl implements LocationDao {
    private static final Logger logger = LoggerFactory.getLogger(LocationDaoImpl.class);

    @Override
    public Location add(Location location) {
        String sql = "INSERT INTO Locations (location_name, capacity) VALUES (?, ?)";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, location.getLocationName());
            if (location.getCapacity() != null) {
                pstmt.setInt(2, location.getCapacity());
            } else {
                pstmt.setNull(2, Types.INTEGER);
            }

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating location failed, no rows affected.");
            }
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    location.setLocationId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating location failed, no ID obtained.");
                }
            }
            logger.info("Location added successfully: {}", location.getLocationName());
            return location;
        } catch (SQLException e) {
            logger.error("Error adding location {}: {}", location.getLocationName(), e.getMessage());
            if (e.getMessage().contains("Duplicate entry")) {
                throw new DataAccessException("Location name '" + location.getLocationName() + "' already exists.", e);
            }
            throw new DataAccessException("Error adding location.", e);
        }
    }

    @Override
    public Optional<Location> findById(int locationId) {
        String sql = "SELECT * FROM Locations WHERE location_id = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, locationId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapRowToLocation(rs));
            }
        } catch (SQLException e) {
            logger.error("Error finding location by ID {}: {}", locationId, e.getMessage());
            throw new DataAccessException("Error finding location by ID.", e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Location> findByName(String name) {
        String sql = "SELECT * FROM Locations WHERE location_name = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapRowToLocation(rs));
            }
        } catch (SQLException e) {
            logger.error("Error finding location by name {}: {}", name, e.getMessage());
            throw new DataAccessException("Error finding location by name.", e);
        }
        return Optional.empty();
    }

    @Override
    public List<Location> findAll() {
        List<Location> locations = new ArrayList<>();
        String sql = "SELECT * FROM Locations ORDER BY location_name";
        try (Connection conn = DatabaseConnector.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                locations.add(mapRowToLocation(rs));
            }
        } catch (SQLException e) {
            logger.error("Error fetching all locations: {}", e.getMessage());
            throw new DataAccessException("Error fetching all locations.", e);
        }
        return locations;
    }

    @Override
    public void update(Location location) {
        String sql = "UPDATE Locations SET location_name = ?, capacity = ? WHERE location_id = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, location.getLocationName());
            if (location.getCapacity() != null) {
                pstmt.setInt(2, location.getCapacity());
            } else {
                pstmt.setNull(2, Types.INTEGER);
            }
            pstmt.setInt(3, location.getLocationId());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                logger.warn("Updating location with ID {} failed, no rows affected or location not found.", location.getLocationId());
            } else {
                logger.info("Location updated successfully: {}", location.getLocationName());
            }
        } catch (SQLException e) {
            logger.error("Error updating location {}: {}", location.getLocationName(), e.getMessage());
            if (e.getMessage().contains("Duplicate entry")) {
                throw new DataAccessException("Location name '" + location.getLocationName() + "' already exists for another location.", e);
            }
            throw new DataAccessException("Error updating location.", e);
        }
    }

    @Override
    public boolean delete(int locationId) {
        String sql = "DELETE FROM Locations WHERE location_id = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, locationId);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                logger.info("Location with ID {} deleted successfully.", locationId);
                return true;
            } else {
                logger.warn("Deleting location with ID {} failed, no rows affected or location not found.", locationId);
                return false;
            }
        } catch (SQLException e) {
            logger.error("Error deleting location with ID {}: {}", locationId, e.getMessage());
            if (e.getErrorCode() == 1451) { // FK constraint
                throw new DataAccessException("Cannot delete location. It is referenced by Lecture Sessions.", e);
            }
            throw new DataAccessException("Error deleting location.", e);
        }
    }

    private Location mapRowToLocation(ResultSet rs) throws SQLException {
        Location location = new Location();
        location.setLocationId(rs.getInt("location_id"));
        location.setLocationName(rs.getString("location_name"));
        location.setCapacity(rs.getObject("capacity", Integer.class));
        location.setCreatedAt(rs.getTimestamp("created_at"));
        location.setUpdatedAt(rs.getTimestamp("updated_at"));
        return location;
    }
}
