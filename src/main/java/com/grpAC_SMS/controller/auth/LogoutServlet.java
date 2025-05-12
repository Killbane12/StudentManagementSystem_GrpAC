package com.grpAC_SMS.controller.auth;

import com.grpAC_SMS.model.User;
import com.grpAC_SMS.util.ApplicationConstants;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * Handles user logout requests.
 */
@WebServlet(name = "LogoutServlet", value = "/LogoutServlet")
public class LogoutServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response); // Often GET or POST can trigger logout
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false); // Get existing session, don't create new
        if (session != null) {
            User user = (User) session.getAttribute(ApplicationConstants.LOGGED_IN_USER_SESSION_ATTR);
            if (user != null) {
                System.out.println("Logging out user: " + user.getUsername());
            } else {
                System.out.println("Logging out user session without user object.");
            }
            session.invalidate(); // Invalidate the session
        } else {
            System.out.println("Logout called but no active session found.");
        }

        // Redirect to login page with a success message parameter
        response.sendRedirect(request.getContextPath() + "/auth/login.jsp?logout=success");
    }
}