package com.grpAC_SMS.service;

import com.grpAC_SMS.exception.BusinessLogicException;
import com.grpAC_SMS.exception.DataAccessException;
import com.grpAC_SMS.model.User;

import java.util.Optional;

public interface UserService {
    Optional<User> authenticateUser(String username, String password) throws BusinessLogicException, DataAccessException;
    // You can add other user-related business logic methods here
    // e.g., void registerNewUser(User user, String plainPassword) throws BusinessLogicException, DataAccessException;
}