package com.grpAC_SMS.dao;

import com.grpAC_SMS.exception.DataAccessException;
import com.grpAC_SMS.model.User;

import java.util.List;
import java.util.Optional;

public interface UserDao {
    void create(User user) throws DataAccessException;

    Optional<User> findById(int userId) throws DataAccessException;

    Optional<User> findByUsername(String username) throws DataAccessException; // Crucial for login

    List<User> findAll() throws DataAccessException;

    boolean update(User user) throws DataAccessException;

    boolean delete(int userId) throws DataAccessException;
}