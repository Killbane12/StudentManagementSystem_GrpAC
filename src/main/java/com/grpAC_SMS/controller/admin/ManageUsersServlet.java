package com.grpAC_SMS.controller.admin;

import com.grpAC_SMS.model.User;
import com.grpAC_SMS.model.Role;
import com.grpAC_SMS.service.UserService;
import com.grpAC_SMS.service.impl.UserServiceImpl; // Assuming you have this service
import com.grpAC_SMS.exception.BusinessLogicException;
import com.grpAC_SMS.util.InputValidator;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

@WebServlet("/ManageUsersServlet")
public class ManageUsersServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(ManageUsersServlet.class);
    private UserService userService;

    @Override
    public void init() {
        userService = new UserServiceImpl(); // Or inject
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        if (action == null) {
            action = "list"; // Default action
        }

        try {
            switch (action) {
                case "add":
                    showNewUserForm(request, response);
                    break;
                case "edit":
                    showEditUserForm(request, response);
                    break;
                case "delete":
                    deleteUser(request, response);
                    break;
                case "toggleStatus":
                    toggleUserStatus(request, response);
                    break;
                case "list":
                default:
                    listUsers(request, response);
                    break;
            }
        } catch (BusinessLogicException e) {
            logger.error("BusinessLogicException in ManageUsersServlet doGet: {}", e.getMessage());
            request.setAttribute("errorMessage", "Operation failed: " + e.getMessage());
            listUsers(request, response); // Show list with error
        } catch (Exception e) {
            logger.error("Exception in ManageUsersServlet doGet: {}", e.getMessage(), e);
            request.setAttribute("errorMessage", "An unexpected error occurred.");
            listUsers(request, response);
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");

        // For POST, action might be in a hidden field if not in URL
        if (action == null) {
            String userId = request.getParameter("userId");
            action = (InputValidator.isNullOrEmpty(userId) || "0".equals(userId)) ? "create" : "update";
        }


        try {
            switch (action) {
                case "create":
                    createUser(request, response);
                    break;
                case "update":
                    updateUser(request, response);
                    break;
                default:
                    listUsers(request, response); // Fallback
                    break;
            }
        } catch (BusinessLogicException e) {
            logger.error("BusinessLogicException in ManageUsersServlet doPost: {}", e.getMessage());
            request.setAttribute("errorMessage", "Operation failed: " + e.getMessage());
            // If error on create/update, show form again with entered values and error
            if ("create".equals(action)) {
                preserveFormDataOnError(request);
                showNewUserForm(request, response);
            } else if ("update".equals(action)) {
                preserveFormDataOnError(request);
                try {
                    showEditUserForm(request, response); // Needs userId to be set correctly
                } catch (BusinessLogicException ex) {
                    logger.error("BusinessLogicException when showing Edit User Form in error handler: {}", ex.getMessage());
                    request.setAttribute("errorMessage", "Operation failed: " + ex.getMessage());
                    listUsers(request, response);
                }
            } else {
                listUsers(request, response);
            }
        } catch (Exception e) {
            logger.error("Exception in ManageUsersServlet doPost: {}", e.getMessage(), e);
            request.setAttribute("errorMessage", "An unexpected error occurred.");
            listUsers(request, response);
        }
    }

    private void preserveFormDataOnError(HttpServletRequest request) {
        User user = new User();
        if (!InputValidator.isNullOrEmpty(request.getParameter("userId"))) {
            try {
                user.setUserId(Integer.parseInt(request.getParameter("userId")));
            } catch (NumberFormatException e) { /* ignore, might be new user */ }
        }
        user.setUsername(request.getParameter("username"));
        user.setEmail(request.getParameter("email"));
        String roleStr = request.getParameter("role");
        if (!InputValidator.isNullOrEmpty(roleStr)) {
            try {
                user.setRole(Role.valueOf(roleStr.toUpperCase()));
            } catch (IllegalArgumentException e) { /* ignore */ }
        }
        user.setActive(request.getParameter("isActive") != null && "on".equalsIgnoreCase(request.getParameter("isActive")));
        request.setAttribute("user", user);
    }


    private void listUsers(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<User> userList = userService.getAllUsers();
        request.setAttribute("userList", userList);
        RequestDispatcher dispatcher = request.getRequestDispatcher("/admin/users_list.jsp");
        dispatcher.forward(request, response);
    }

    private void showNewUserForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setAttribute("roles", Role.values());
        // Preserve form data if it was a redirect after error
        if (request.getAttribute("user") == null) {
            request.setAttribute("user", new User()); // For a new user form
        }
        RequestDispatcher dispatcher = request.getRequestDispatcher("/admin/user_form.jsp");
        dispatcher.forward(request, response);
    }

    private void showEditUserForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, BusinessLogicException {
        int userId = Integer.parseInt(request.getParameter("userId"));
        User existingUser = userService.getUserById(userId);
        if (existingUser == null) {
            throw new BusinessLogicException("User not found with ID: " + userId);
        }
        request.setAttribute("user", existingUser);
        request.setAttribute("roles", Role.values());
        RequestDispatcher dispatcher = request.getRequestDispatcher("/admin/user_form.jsp");
        dispatcher.forward(request, response);
    }

    private void createUser(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, BusinessLogicException {
        String username = request.getParameter("username");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String roleStr = request.getParameter("role");
        boolean isActive = "on".equalsIgnoreCase(request.getParameter("isActive")); // Checkbox value

        if (InputValidator.isNullOrEmpty(username) || InputValidator.isNullOrEmpty(email) ||
                InputValidator.isNullOrEmpty(password) || InputValidator.isNullOrEmpty(roleStr)) {
            preserveFormDataOnError(request);
            throw new BusinessLogicException("All fields (username, email, password, role) are required.");
        }
        if (!InputValidator.isValidEmail(email)) {
            preserveFormDataOnError(request);
            throw new BusinessLogicException("Invalid email format.");
        }
        if (!InputValidator.isValidPassword(password)) { // Min 6 chars
            preserveFormDataOnError(request);
            throw new BusinessLogicException("Password must be at least 6 characters long.");
        }

        Role role;
        try {
            role = Role.valueOf(roleStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            preserveFormDataOnError(request);
            throw new BusinessLogicException("Invalid role specified.");
        }

        userService.registerUser(username, password, email, role, isActive);
        request.getSession().setAttribute("successMessage", "User created successfully!");
        response.sendRedirect(request.getContextPath() + "/ManageUsersServlet?action=list");
    }

    private void updateUser(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, BusinessLogicException {
        int userId = Integer.parseInt(request.getParameter("userId"));
        String username = request.getParameter("username");
        String email = request.getParameter("email");
        String password = request.getParameter("password"); // Optional: only if changing
        String roleStr = request.getParameter("role");
        boolean isActive = "on".equalsIgnoreCase(request.getParameter("isActive"));

        if (InputValidator.isNullOrEmpty(username) || InputValidator.isNullOrEmpty(email) || InputValidator.isNullOrEmpty(roleStr)) {
            preserveFormDataOnError(request);
            throw new BusinessLogicException("Username, email, and role cannot be empty.");
        }
        if (!InputValidator.isValidEmail(email)) {
            preserveFormDataOnError(request);
            throw new BusinessLogicException("Invalid email format.");
        }

        Role role;
        try {
            role = Role.valueOf(roleStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            preserveFormDataOnError(request);
            throw new BusinessLogicException("Invalid role specified.");
        }

        User user = new User(); //userService.getUserById(userId); // fetch existing is better
        user.setUserId(userId);
        user.setUsername(username);
        user.setEmail(email);
        user.setRole(role);
        user.setActive(isActive);
        // Password will be handled by service layer if provided

        userService.updateUser(user, password); // Pass plain password if changed, or null/empty
        request.getSession().setAttribute("successMessage", "User updated successfully!");
        response.sendRedirect(request.getContextPath() + "/ManageUsersServlet?action=list");
    }

    private void deleteUser(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, BusinessLogicException {
        int userId = Integer.parseInt(request.getParameter("userId"));

        // Prevent self-deletion for logged-in admin (example)
        User currentUser = (User) request.getSession().getAttribute("loggedInUser");
        if (currentUser != null && currentUser.getUserId() == userId && currentUser.getRole() == Role.ADMIN) {
            throw new BusinessLogicException("Admin users cannot delete their own account.");
        }

        userService.deleteUser(userId);
        request.getSession().setAttribute("successMessage", "User deleted successfully!");
        response.sendRedirect(request.getContextPath() + "/ManageUsersServlet?action=list");
    }

    private void toggleUserStatus(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, BusinessLogicException {
        int userId = Integer.parseInt(request.getParameter("userId"));
        User user = userService.getUserById(userId);
        if (user == null) {
            throw new BusinessLogicException("User not found with ID: " + userId);
        }

        // Prevent deactivating own admin account (example)
        User currentUser = (User) request.getSession().getAttribute("loggedInUser");
        if (currentUser != null && currentUser.getUserId() == userId && user.getRole() == Role.ADMIN && user.isActive()) {
            throw new BusinessLogicException("Admin users cannot deactivate their own account while logged in.");
        }

        boolean newStatus = !user.isActive();
        userService.changeUserStatus(userId, newStatus);
        request.getSession().setAttribute("successMessage", "User status updated successfully!");
        response.sendRedirect(request.getContextPath() + "/ManageUsersServlet?action=list");
    }
}
