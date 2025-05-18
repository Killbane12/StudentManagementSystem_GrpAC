package com.grpAC_SMS.dao;

import com.grpAC_SMS.model.User;

import java.util.List;
import java.util.Optional;

public interface UserDao {
    Optional<User> findByUsername(String username);

    Optional<User> findById(int userId);

    List<User> findAll();

    User save(User user); // For both create and update

    void update(User user);

    void delete(int userId);

    boolean activateUser(int userId, boolean isActive);

    int getNextUserId(); // For linking student/faculty profiles before user record is fully saved by UserServlet
}
