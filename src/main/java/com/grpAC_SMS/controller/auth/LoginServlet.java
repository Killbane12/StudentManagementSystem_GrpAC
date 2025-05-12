package com.grpAC_SMS.controller.auth;

import com.grpAC_SMS.model.Role;
import com.grpAC_SMS.model.User;
import com.grpAC_SMS.service.UserService;
import com.grpAC_SMS.service.impl.UserServiceImpl;
import com.grpAC_SMS.util.ApplicationConstants;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.Optional;

@WebServlet(name = "LoginServlet", value = "/LoginServlet")
public class LoginServlet extends HttpServlet {

    private UserService userService;

    @Override
    public void init() throws ServletException {
        this.userService = new UserServiceImpl(); // Simple instantiation
        System.out.println("LoginServlet Initialized with UserService.");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("/auth/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String errorMessage = null;
        HttpSession session = request.getSession();

        try {
            Optional<User> userOpt = userService.authenticateUser(username, password);

            if (userOpt.isPresent()) {
                User user = userOpt.get();
                session.setAttribute(ApplicationConstants.LOGGED_IN_USER_SESSION_ATTR, user);
                System.out.println("Login successful for user: " + username + " with role: " + user.getRole());
                redirectToDashboard(user.getRole(), response, request.getContextPath());
                return;
            } else {
                errorMessage = "Invalid username or password, or account inactive.";
                System.out.println("Login failed for user: " + username);
            }

        } catch (Exception e) {
            System.err.println("Login error in Servlet: " + e.getMessage());
            e.printStackTrace();
            errorMessage = "An error occurred during login. Please try again.";
        }

        request.setAttribute(ApplicationConstants.ERROR_MESSAGE_ATTR, errorMessage);
        request.getRequestDispatcher("/auth/login.jsp").forward(request, response);
    }

    private void redirectToDashboard(Role role, HttpServletResponse response, String contextPath) throws IOException {
        String dashboardUrl = contextPath;
        if (role == null) { // Should not happen if user is present
            System.err.println("Critical error: User authenticated but role is null. Redirecting to login.");
            dashboardUrl += "/auth/login.jsp";
        } else {
            switch (role) {
                case ADMIN:
                    dashboardUrl += "/admin/dashboard";
                    break;
                case FACULTY:
                    dashboardUrl += "/faculty/dashboard";
                    break;
                case STUDENT:
                    dashboardUrl += "/student/dashboard";
                    break;
                default:
                    dashboardUrl += "/auth/login.jsp";
            }
        }
        response.sendRedirect(dashboardUrl);
    }
}