package com.grpAC_SMS.filter;

import com.grpAC_SMS.model.User;
import com.grpAC_SMS.util.ApplicationConstants;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@WebFilter("/*") // Apply to all requests initially, then refine paths
public class AuthenticationFilter implements Filter {
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationFilter.class);
    private static final Set<String> ALLOWED_PATHS = new HashSet<>(Arrays.asList(
            "/auth/login.jsp",
            "/LoginServlet",
            "/assets/", // Allow access to CSS, JS, images
            "/index.jsp" // Or redirect from index.jsp to login
    ));
    private static final Set<String> PUBLIC_SERVLET_PATHS = new HashSet<>(Arrays.asList(
            "/LoginServlet", "/LogoutServlet" // LogoutServlet should still be accessible to logged-in users
    ));


    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpSession session = httpRequest.getSession(false); // Don't create session if it doesn't exist

        String path = httpRequest.getRequestURI().substring(httpRequest.getContextPath().length());
        String servletPath = httpRequest.getServletPath(); // More reliable for servlet mappings

        logger.debug("AuthFilter: Processing request for path: {}, servletPath: {}", path, servletPath);


        boolean isLoggedIn = (session != null && session.getAttribute(ApplicationConstants.SESSION_USER) != null);

        // Check if the path starts with any of the allowed prefixes (e.g. /assets/)
        boolean isAllowedPath = ALLOWED_PATHS.stream().anyMatch(path::startsWith) || PUBLIC_SERVLET_PATHS.contains(servletPath);


        if (isLoggedIn || isAllowedPath) {
            if (isLoggedIn && (servletPath.equals("/LoginServlet") || path.equals("/auth/login.jsp")) ) {
                User user = (User) session.getAttribute(ApplicationConstants.SESSION_USER);
                logger.debug("User already logged in, redirecting from login page to dashboard.");
                redirectToDashboard(user, httpRequest, httpResponse);
                return;
            }
            logger.debug("AuthFilter: Access granted for path: {}", path);
            chain.doFilter(request, response); // User is logged in or accessing a public page
        } else {
            logger.warn("AuthFilter: Access denied for path: {}. User not logged in. Redirecting to login.", path);
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/auth/login.jsp");
        }
    }

    private void redirectToDashboard(User user, HttpServletRequest request, HttpServletResponse response) throws IOException {
        String contextPath = request.getContextPath();
        switch (user.getRole()) {
            case ADMIN:
                response.sendRedirect(contextPath + "/admin/dashboard.jsp");
                break;
            case FACULTY:
                response.sendRedirect(contextPath + "/faculty/dashboard.jsp");
                break;
            case STUDENT:
                response.sendRedirect(contextPath + "/student/dashboard.jsp");
                break;
            default:
                response.sendRedirect(contextPath + "/auth/login.jsp"); // Fallback
                break;
        }
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        logger.info("AuthenticationFilter initialized.");
    }

    @Override
    public void destroy() {
        logger.info("AuthenticationFilter destroyed.");
    }
}
