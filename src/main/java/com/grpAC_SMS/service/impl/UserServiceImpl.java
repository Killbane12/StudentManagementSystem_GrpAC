package com.grpAC_SMS.service.impl;

import com.grpAC_SMS.dao.UserDao;
import com.grpAC_SMS.dao.impl.UserDaoImpl;
import com.grpAC_SMS.exception.BusinessLogicException;
import com.grpAC_SMS.exception.DataAccessException;
import com.grpAC_SMS.model.User;
import com.grpAC_SMS.service.UserService;
import com.grpAC_SMS.util.InputValidator;
import com.grpAC_SMS.util.PasswordHasher;

import java.util.Optional;

public class UserServiceImpl implements UserService {

    private final UserDao userDao;

    public UserServiceImpl() {
        // Simple instantiation for now.
        this.userDao = new UserDaoImpl();
    }

    // Constructor for potential future dependency injection
    // public UserServiceImpl(UserDao userDao) {
    //     this.userDao = userDao;
    // }

    @Override
    public Optional<User> authenticateUser(String username, String password) throws BusinessLogicException, DataAccessException {
        if (InputValidator.isNullOrBlank(username) || InputValidator.isNullOrBlank(password)) {
            throw new BusinessLogicException("Username and password cannot be empty.");
        }

        try {
            Optional<User> userOpt = userDao.findByUsername(username);

            if (userOpt.isPresent()) {
                User user = userOpt.get();
                if (!user.isActive()) {
                    System.out.println("Authentication failed: User " + username + " is not active.");
                    return Optional.empty(); // User account is not active
                }
                // Verify the provided password against the stored hash
                if (PasswordHasher.verifyPassword(password, user.getPasswordHash())) {
                    System.out.println("Authentication successful for: " + username);
                    return Optional.of(user); // Authentication successful
                } else {
                    System.out.println("Authentication failed: Incorrect password for " + username);
                }
            } else {
                System.out.println("Authentication failed: User " + username + " not found.");
            }
            return Optional.empty(); // User not found or password mismatch
        } catch (DataAccessException e) {
            // Log error (e.g., using SLF4J logger if integrated)
            System.err.println("DataAccessException during authentication for user " + username + ": " + e.getMessage());
            throw e; // Re-throw to be handled by the servlet
        } catch (Exception e) { // Catch other unexpected exceptions during the process
            System.err.println("Unexpected error during authentication for user " + username + ": " + e.getMessage());
            e.printStackTrace(); // Print stack trace for unexpected errors during development
            throw new BusinessLogicException("An unexpected error occurred during authentication.", e);
        }
    }
}