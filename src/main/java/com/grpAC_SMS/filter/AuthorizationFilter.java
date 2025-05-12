package com.grpAC_SMS.filter;

import com.grpAC_SMS.model.Role;
import com.grpAC_SMS.model.User;
import com.grpAC_SMS.util.ApplicationConstants;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * Checks if an authenticated user has the necessary role for the requested resource.
 * Assumes AuthenticationFilter runs first.
 */
// Apply to same patterns as AuthN filter, or refine if needed
@WebFilter(filterName = "AuthorizationFilter", urlPatterns = {"/admin/*", "/student/*", "/faculty/*"})
public class AuthorizationFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        System.out.println("AuthorizationFilter Initialized");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpSession session = httpRequest.getSession(false);

        User loggedInUser = (session != null) ? (User) session.getAttribute(ApplicationConstants.LOGGED_IN_USER_SESSION_ATTR) : null;

        // Authentication filter should handle unauthenticated users, but good to check.
        if (loggedInUser == null) {
            // Allow filter chain to continue, AuthN filter might redirect
            chain.doFilter(request, response);
            return;
        }

        String requestURI = httpRequest.getRequestURI();
        String contextPath = httpRequest.getContextPath();
        Role userRole = loggedInUser.getRole();
        boolean authorized = false;

        // Define authorization rules
        if (requestURI.startsWith(contextPath + "/admin/")) {
            authorized = (userRole == Role.ADMIN);
        } else if (requestURI.startsWith(contextPath + "/faculty/")) {
            authorized = (userRole == Role.FACULTY || userRole == Role.ADMIN); // Let Admin access Faculty section
        } else if (requestURI.startsWith(contextPath + "/student/")) {
            authorized = (userRole == Role.STUDENT || userRole == Role.ADMIN); // Let Admin access Student section
        } else {
            // Should not happen for patterns defined in @WebFilter, but could if filter applied more broadly
            authorized = true; // Assume allowed if not caught by specific patterns
        }

        if (authorized) {
            chain.doFilter(request, response); // Authorized, continue
        } else {
            // Not authorized
            System.out.println("AuthorizationFilter: User '" + loggedInUser.getUsername() + "' (" + userRole + ") DENIED access to " + requestURI);
            // Forward to an unauthorized page
            request.getRequestDispatcher("/common/unauthorized.jsp").forward(request, response);
            // Alternatively: httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied");
        }
    }

    @Override
    public void destroy() {
        System.out.println("AuthorizationFilter Destroyed");
    }
}