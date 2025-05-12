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
 * Ensures user is authenticated before accessing protected resources.
 */
@WebFilter(filterName = "AuthenticationFilter", urlPatterns = {"/admin/*", "/student/*", "/faculty/*"})
public class AuthenticationFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        System.out.println("AuthenticationFilter Initialized");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpSession session = httpRequest.getSession(false);

        String loginURI = httpRequest.getContextPath() + "/auth/login.jsp";
        String loginServletPath = httpRequest.getContextPath() + "/LoginServlet"; // Match @WebServlet value

        boolean isLoggedIn = (session != null && session.getAttribute(ApplicationConstants.LOGGED_IN_USER_SESSION_ATTR) != null);
        boolean isLoginRequest = httpRequest.getRequestURI().equals(loginURI) || httpRequest.getRequestURI().equals(loginServletPath);
        boolean isStaticResource = httpRequest.getRequestURI().startsWith(httpRequest.getContextPath() + "/assets/");

        if (isLoggedIn || isLoginRequest || isStaticResource) {
            if (isLoggedIn && httpRequest.getRequestURI().equals(loginURI)) {
                // If logged in, redirect from login page to dashboard
                User user = (User) session.getAttribute(ApplicationConstants.LOGGED_IN_USER_SESSION_ATTR);
                redirectToDashboard(user.getRole(), httpResponse, httpRequest.getContextPath());
            } else {
                // Logged in, or accessing login page/servlet, or static resource: Allow access
                chain.doFilter(request, response);
            }
        } else {
            // Not logged in and trying to access protected resource
            System.out.println("AuthenticationFilter: Access denied (not logged in). Redirecting to login.");
            httpResponse.sendRedirect(loginURI);
        }
    }

    private void redirectToDashboard(Role role, HttpServletResponse response, String contextPath) throws IOException {
        String dashboardUrl = contextPath;
        switch (role) {
            case ADMIN:
                dashboardUrl += "/admin/dashboard";
                break; // Use servlet path
            case FACULTY:
                dashboardUrl += "/faculty/dashboard";
                break; // Use servlet path
            case STUDENT:
                dashboardUrl += "/student/dashboard";
                break; // Use servlet path
            default:
                dashboardUrl += "/auth/login.jsp"; // Fallback
        }
        response.sendRedirect(dashboardUrl);
    }


    @Override
    public void destroy() {
        System.out.println("AuthenticationFilter Destroyed");
    }
}