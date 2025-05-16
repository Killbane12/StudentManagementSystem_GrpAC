package com.grpAC_SMS.controller.student;


import com.grpAC_SMS.dao.StudentDao;
import com.grpAC_SMS.dao.impl.StudentDaoImpl;
import com.grpAC_SMS.model.Student;
import com.grpAC_SMS.model.User;
import com.grpAC_SMS.util.ApplicationConstants;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * Servlet that handles the student dashboard view.
 * Retrieves student and user details and forwards them to the dashboard JSP page.
 */
@WebServlet(name = "StudentDashboardServlet", value = "/student/dashboard")
public class StudentDashboardServlet extends HttpServlet {

    private StudentDao studentDao;

    /**
     * Initializes the servlet and sets up the StudentDao implementation.
     */
    @Override
    public void init() {
        studentDao = new StudentDaoImpl();
    }

    /**
     * Handles GET requests to the student dashboard.
     * Ensures the user is logged in, fetches student/user info, and forwards to the dashboard page.
     *
     * @param request  the HTTP request
     * @param response the HTTP response
     * @throws SecurityException in case of unauthorized access
     * @throws IOException       in case of an I/O error
     * @throws ServletException  in case of servlet-related errors
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws SecurityException, IOException, ServletException {

        // Get the current session, or create one if it doesn't exist
        HttpSession session = request.getSession();

        // Redirect to login page if no user is logged in
        if (session == null || session.getAttribute(ApplicationConstants.LOGGED_IN_USER_SESSION_ATTR) == null) {
            response.sendRedirect(request.getContextPath() + "/auth/login");
            return;  // Ensure further processing stops after redirect
        }

        // Retrieve the currently logged-in user from the session
        User sessionUser = (User) session.getAttribute(ApplicationConstants.LOGGED_IN_USER_SESSION_ATTR);
        String username = sessionUser.getUsername();

        // Fetch student information associated with the username
        Student student = studentDao.getStudentByUsername(username);
        request.setAttribute("student", student);

        // Fetch user information associated with the username
        User user = studentDao.getUserByUsername(username);
        request.setAttribute("user", user);

        // Forward request to the JSP page for rendering the dashboard
        RequestDispatcher dispatcher = request.getRequestDispatcher("/student/dashboard.jsp");
        dispatcher.forward(request, response);
    }

}
