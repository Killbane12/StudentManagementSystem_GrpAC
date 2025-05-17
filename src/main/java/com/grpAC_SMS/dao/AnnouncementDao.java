package com.grpAC_SMS.dao;

import com.grpAC_SMS.model.Announcement;
import com.grpAC_SMS.exception.DataAccessException;
import java.util.List;

public interface AnnouncementDao {
    /**
     * Retrieves all announcements from the database.
     *
     * @return A list of all announcements, or an empty list if no announcements exist.
     * @throws DataAccessException if an error occurs while accessing the database.
     */
    List<Announcement> getAllAnnouncements() throws DataAccessException;

    /**
     * Retrieves an announcement by its unique ID.
     *
     * @param announcementId The ID of the announcement to retrieve.
     * @return The Announcement object, or null if no announcement with the given ID exists.
     * @throws DataAccessException if an error occurs while accessing the database.
     */
    Announcement getAnnouncementById(int announcementId) throws DataAccessException;

    /**
     * Adds a new announcement to the database.
     *
     * @param announcement The Announcement object to add.
     * @throws DataAccessException if an error occurs while adding the announcement to the database.
     */
    void addAnnouncement(Announcement announcement) throws DataAccessException;

    /**
     * Updates an existing announcement in the database.
     *
     * @param announcement The Announcement object to update.
     * @throws DataAccessException if an error occurs while updating the announcement in the database.
     */
    void updateAnnouncement(Announcement announcement) throws DataAccessException;

    /**
     * Deletes an announcement from the database by its ID.
     *
     * @param announcementId The ID of the announcement to delete.
     * @throws DataAccessException if an error occurs while deleting the announcement from the database.
     */
    void deleteAnnouncement(int announcementId) throws DataAccessException;
}
