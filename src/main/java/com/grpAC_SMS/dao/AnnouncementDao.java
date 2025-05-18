package com.grpAC_SMS.dao;

import com.grpAC_SMS.model.Announcement;
import com.grpAC_SMS.model.Role;
import java.util.List;
import java.util.Optional;

public interface AnnouncementDao {
    Announcement add(Announcement announcement);
    Optional<Announcement> findById(int announcementId);
    List<Announcement> findAll();
    List<Announcement> findAllWithDetails(); // Includes poster's username
    List<Announcement> findTargetedAnnouncements(Role targetRole, int limit); // For dashboards
    List<Announcement> findByTargetRole(Role targetRole);
    List<Announcement> findByUserId(int userId); // Announcements posted by a specific admin
    void update(Announcement announcement);
    boolean delete(int announcementId);
    long countTotalActiveAnnouncements();
}
