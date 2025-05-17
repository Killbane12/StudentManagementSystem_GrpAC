package com.grpAC_SMS.controller.auth;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

@WebServlet("/LogoutServlet")
public class LogoutServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(LogoutServlet.class);

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false); // Get current session, don't create new one
        if (session != null) {
            String username = "Guest";
            if (session.getAttribute("loggedInUser") != null) {
                com.grpAC_SMS.model.User user = (com.grpAC_SMS.model.User) session.getAttribute("loggedInUser");
                username = user.getUsername();
            }
            session.invalidate(); // Invalidate session
            logger.info("User {} logged out successfully.", username);
        }
        // Redirect to login page
        response.sendRedirect(request.getContextPath() + "/auth/login.jsp?logout=true");
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response); // Handle POST requests same as GET for logout
    }
}
