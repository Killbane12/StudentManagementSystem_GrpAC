package com.grpAC_SMS.filter;

import com.grpAC_SMS.model.Role;
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@WebFilter("/*") // Apply to all, but logic will only trigger for protected areas
public class AuthorizationFilter implements Filter {
    private static final Logger logger = LoggerFactory.getLogger(AuthorizationFilter.class);

    // Define access control rules: Path prefix -> Set of allowed roles
    private static final Map<String, Set<Role>> protectedResources = new HashMap<>();

    static {
        // Admin resources
        Set<Role> adminOnly = new HashSet<>();
        adminOnly.add(Role.ADMIN);
        protectedResources.put("/admin/", adminOnly);
        protectedResources.put("/ManageUsersServlet", adminOnly);
        protectedResources.put("/ManageDepartmentsServlet", adminOnly);
        // ... Add all admin servlets and /admin/ paths

        // Faculty resources
        Set<Role> facultyOnly = new HashSet<>();
        facultyOnly.add(Role.FACULTY);
        protectedResources.put("/faculty/", facultyOnly);
        protectedResources.put("/FacultyDashboardServlet", facultyOnly);
        // ... Add all faculty servlets and /faculty/ paths

        // Student resources
        Set<Role> studentOnly = new HashSet<>();
        studentOnly.add(Role.STUDENT);
        protectedResources.put("/student/", studentOnly);
        protectedResources.put("/StudentDashboardServlet", studentOnly);
        // ... Add all student servlets and /student/ paths
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpSession session = httpRequest.getSession(false);

        String path = httpRequest.getRequestURI().substring(httpRequest.getContextPath().length());
        String servletPath = httpRequest.getServletPath(); // Using servletPath for servlets

        logger.debug("AuthzFilter: Processing request for path: {}, servletPath: {}", path, servletPath);


        User currentUser = null;
        if (session != null) {
            currentUser = (User) session.getAttribute(ApplicationConstants.SESSION_USER);
        }

        boolean authorized = true; // Assume authorized unless a rule matches and fails
        boolean needsAuthCheck = false;

        // Check JSP paths
        for (Map.Entry<String, Set<Role>> entry : protectedResources.entrySet()) {
            if (path.startsWith(entry.getKey())) {
                needsAuthCheck = true;
                if (currentUser == null || !entry.getValue().contains(currentUser.getRole())) {
                    authorized = false;
                } else {
                    authorized = true; // Explicitly set true if rule matched and role is allowed
                }
                break; // First matching rule determines authorization
            }
        }

        // Check Servlet paths if not already determined by JSP path rule
        if (!needsAuthCheck) {
            for (Map.Entry<String, Set<Role>> entry : protectedResources.entrySet()) {
                if (servletPath.equals(entry.getKey())) { // Exact match for servlets
                    needsAuthCheck = true;
                    if (currentUser == null || !entry.getValue().contains(currentUser.getRole())) {
                        authorized = false;
                    } else {
                        authorized = true;
                    }
                    break;
                }
            }
        }


        if (needsAuthCheck && !authorized) {
            logger.warn("AuthzFilter: Unauthorized access attempt to {} by user {} (Role: {})",
                    path, (currentUser != null ? currentUser.getUsername() : "Guest"), (currentUser != null ? currentUser.getRole() : "N/A"));
            if (currentUser == null) { // Not logged in but trying to access protected resource
                httpResponse.sendRedirect(httpRequest.getContextPath() + "/auth/login.jsp");
            } else { // Logged in but wrong role
                httpResponse.sendRedirect(httpRequest.getContextPath() + "/common/unauthorized.jsp");
            }
        } else {
            logger.debug("AuthzFilter: Access {} for path: {}", (needsAuthCheck ? (authorized ? "granted" : "denied") : "not protected/public"), path);
            chain.doFilter(request, response);
        }
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        logger.info("AuthorizationFilter initialized.");
        // Example: Add all admin servlets to protectedResources
        Set<Role> adminOnly = protectedResources.computeIfAbsent("/admin/", k -> new HashSet<>());
        adminOnly.add(Role.ADMIN);
        protectedResources.put("/AdminDashboardServlet", adminOnly);
        protectedResources.put("/ManageUsersServlet", adminOnly);
        protectedResources.put("/ManageDepartmentsServlet", adminOnly);
        protectedResources.put("/ManageProgramsServlet", adminOnly);
        protectedResources.put("/ManageCoursesServlet", adminOnly);
        protectedResources.put("/ManageFacultyServlet", adminOnly);
        protectedResources.put("/ManageStudentsServlet", adminOnly);
        protectedResources.put("/ManageAcademicTermsServlet", adminOnly);
        protectedResources.put("/ManageLocationsServlet", adminOnly);
        protectedResources.put("/ManageLectureSessionsServlet", adminOnly);
        protectedResources.put("/ManageEnrollmentsServlet", adminOnly);
        protectedResources.put("/AdminManageGradesServlet", adminOnly);
        protectedResources.put("/AdminManageAttendanceServlet", adminOnly);
        protectedResources.put("/ManageAnnouncementsServlet", adminOnly);
        protectedResources.put("/SimulatedAttendancePunchServlet", adminOnly); // And any related fetch servlets for NFC

        Set<Role> facultyOnly = protectedResources.computeIfAbsent("/faculty/", k -> new HashSet<>());
        facultyOnly.add(Role.FACULTY);
        protectedResources.put("/FacultyDashboardServlet", facultyOnly);
        protectedResources.put("/FacultyCourseManagementServlet", facultyOnly);
        protectedResources.put("/FacultyRecordGradesServlet", facultyOnly);
        protectedResources.put("/FacultyRecordAttendanceServlet", facultyOnly);

        Set<Role> studentOnly = protectedResources.computeIfAbsent("/student/", k -> new HashSet<>());
        studentOnly.add(Role.STUDENT);
        protectedResources.put("/StudentDashboardServlet", studentOnly);
        protectedResources.put("/StudentViewCoursesServlet", studentOnly);
        protectedResources.put("/StudentViewGradesServlet", studentOnly);
        protectedResources.put("/StudentViewAttendanceServlet", studentOnly);
    }

    @Override
    public void destroy() {
        logger.info("AuthorizationFilter destroyed.");
    }
}
