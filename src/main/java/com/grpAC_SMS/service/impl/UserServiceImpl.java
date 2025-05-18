package com.grpAC_SMS.service.impl;

import com.grpAC_SMS.dao.UserDao;
import com.grpAC_SMS.dao.impl.UserDaoImpl;
import com.grpAC_SMS.exception.BusinessLogicException;
import com.grpAC_SMS.model.Role;
import com.grpAC_SMS.model.User;
import com.grpAC_SMS.service.UserService;
import com.grpAC_SMS.util.InputValidator;
import com.grpAC_SMS.util.PasswordHasher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

public class UserServiceImpl implements UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    private UserDao userDao;

    public UserServiceImpl() {
        this.userDao = new UserDaoImpl(); // fallback way
    }

    public UserServiceImpl(UserDao userDao) {
        this.userDao = userDao; // DI way (better)
    }

    @Override
    public User login(String username, String password) throws BusinessLogicException {
        if (InputValidator.isNullOrEmpty(username) || InputValidator.isNullOrEmpty(password)) {
            throw new BusinessLogicException("Username and password cannot be empty.");
        }

        Optional<User> userOpt = userDao.findByUsername(username);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (!user.isActive()) {
                logger.warn("Login attempt for inactive user: {}", username);
                throw new BusinessLogicException("Account is inactive. Please contact administrator.");
            }
            if (PasswordHasher.checkPassword(password, user.getPasswordHash())) {
                logger.info("User {} logged in successfully.", username);
                return user;
            } else {
                logger.warn("Invalid password attempt for user: {}", username);
                throw new BusinessLogicException("Invalid username or password.");
            }
        } else {
            logger.warn("Login attempt for non-existent user: {}", username);
            throw new BusinessLogicException("Invalid username or password.");
        }
    }

    @Override
    public User registerUser(String username, String password, String email, Role role, boolean isActive) throws BusinessLogicException {
        if (InputValidator.isNullOrEmpty(username) || !InputValidator.isValidPassword(password) || !InputValidator.isValidEmail(email) || role == null) {
            throw new BusinessLogicException("Invalid input for user registration. Check username, password (min 6 chars), email, and role.");
        }

        if (userDao.findByUsername(username).isPresent()) {
            throw new BusinessLogicException("Username already exists.");
        }

        // hashing pw before save
        String hashedPassword = PasswordHasher.hashPassword(password);
        User newUser = new User(username, hashedPassword, email, role, isActive);

        try {
            return userDao.save(newUser);
        } catch (com.grpAC_SMS.exception.DataAccessException e) {
            logger.error("Data access error during user registration for {}: {}", username, e.getMessage());
            throw new BusinessLogicException("Could not register user: " + e.getMessage(), e);
        }
    }

    @Override
    public User getUserById(int userId) {
        return userDao.findById(userId).orElse(null); // return null if not found
    }

    @Override
    public List<User> getAllUsers() {
        return userDao.findAll(); // just fetch all
    }

    @Override
    public void updateUser(User user, String newPasswordIfChanged) throws BusinessLogicException {
        if (user == null || user.getUserId() == 0) {
            throw new BusinessLogicException("User data is invalid for update.");
        }
        if (InputValidator.isNullOrEmpty(user.getUsername()) || !InputValidator.isValidEmail(user.getEmail()) || user.getRole() == null) {
            throw new BusinessLogicException("Invalid input for user update. Check username, email, and role.");
        }

        User existingUser = userDao.findById(user.getUserId())
                .orElseThrow(() -> new BusinessLogicException("User not found for update."));

        // set updated values
        existingUser.setUsername(user.getUsername());
        existingUser.setEmail(user.getEmail());
        existingUser.setRole(user.getRole());
        existingUser.setActive(user.isActive());

        // update pw if provided
        if (!InputValidator.isNullOrEmpty(newPasswordIfChanged)) {
            if (!InputValidator.isValidPassword(newPasswordIfChanged)) {
                throw new BusinessLogicException("New password does not meet complexity requirements (min 6 chars).");
            }
            existingUser.setPasswordHash(PasswordHasher.hashPassword(newPasswordIfChanged));
        }

        try {
            userDao.update(existingUser);
            logger.info("User {} updated successfully.", existingUser.getUsername());
        } catch (com.grpAC_SMS.exception.DataAccessException e) {
            logger.error("Data access error during user update for {}: {}", existingUser.getUsername(), e.getMessage());
            throw new BusinessLogicException("Could not update user: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean changeUserStatus(int userId, boolean isActive) {
        return userDao.activateUser(userId, isActive); // toggle active status
    }

    @Override
    public void deleteUser(int userId) throws BusinessLogicException {
        if (userId == 0) throw new BusinessLogicException("Invalid user ID for deletion.");
        try {
            // maybe check for special roles later (like admin)
            userDao.delete(userId);
        } catch (com.grpAC_SMS.exception.DataAccessException e) {
            logger.error("Data access error during user deletion for ID {}: {}", userId, e.getMessage());
            throw new BusinessLogicException("Could not delete user: " + e.getMessage(), e);
        }
    }
}