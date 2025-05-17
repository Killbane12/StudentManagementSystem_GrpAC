package com.grpAC_SMS.service;

import com.grpAC_SMS.model.User;
import com.grpAC_SMS.model.Role;
import com.grpAC_SMS.exception.BusinessLogicException;
import java.util.List;

public interface UserService {
    User login(String username, String password) throws BusinessLogicException;
    User registerUser(String username, String password, String email, Role role, boolean isActive) throws BusinessLogicException;
    // More specific user creation methods can be added, e.g., createStudentUser, createFacultyUser
    // which might also create corresponding Student/Faculty profiles.
    User getUserById(int userId);
    List<User> getAllUsers();
    void updateUser(User user, String newPasswordIfChanged) throws BusinessLogicException; // password can be null if not changing
    boolean changeUserStatus(int userId, boolean isActive);
    void deleteUser(int userId) throws BusinessLogicException;
}
