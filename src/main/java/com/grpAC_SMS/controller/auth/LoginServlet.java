package com.grpAC_SMS.controller.auth;

import com.grpAC_SMS.exception.BusinessLogicException;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(LoginServlet.class);
    private UserService userService;

    @Override
    public void init() throws ServletException {
        this.userService = new UserServiceImpl(); // Consider dependency injection
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        HttpSession session = request.getSession(); // Create session if not exists

        try {
            User user = userService.login(username, password);
            session.setAttribute(ApplicationConstants.SESSION_USER, user);
            logger.info("User {} successfully logged in. Role: {}", user.getUsername(), user.getRole());

            // Redirect based on role
            String redirectURL = request.getContextPath();
            switch (user.getRole()) {
                case ADMIN:
                    redirectURL += "/AdminDashboardServlet"; // Or /admin/dashboard.jsp
                    break;
                case FACULTY:
                    redirectURL += "/FacultyDashboardServlet"; // Or faculty/dashboard.jsp
                    break;
                case STUDENT:
                    redirectURL += "/StudentDashboardServlet"; // Or /student/dashboard.jsp
                    break;
                default:
                    redirectURL += "/auth/login.jsp"; // Fallback
                    session.setAttribute("errorMessage", "Login successful, but role is undefined.");
                    break;
            }
            response.sendRedirect(redirectURL);

        } catch (BusinessLogicException e) {
            logger.warn("Login failed for username {}: {}", username, e.getMessage());
            session.setAttribute("errorMessage", e.getMessage());
            response.sendRedirect(request.getContextPath() + "/auth/login.jsp");
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // If user tries to access LoginServlet via GET, redirect to login page
        // Or, if already logged in, redirect to dashboard
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute(ApplicationConstants.SESSION_USER) != null) {
            User user = (User) session.getAttribute(ApplicationConstants.SESSION_USER);
            String redirectURL = request.getContextPath();
            switch (user.getRole()) {
                case ADMIN:
                    redirectURL += "/AdminDashboardServlet";
                    break;
                case FACULTY:
                    redirectURL += "/faculty/dashboard.jsp";
                    break;
                case STUDENT:
                    redirectURL += "/student/dashboard.jsp";
                    break;
                default:
                    redirectURL += "/auth/login.jsp";
                    break;
            }
            response.sendRedirect(redirectURL);
        } else {
            response.sendRedirect(request.getContextPath() + "/auth/login.jsp");
        }
    }
}
