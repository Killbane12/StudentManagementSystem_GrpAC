package com.grpAC_SMS.controller.admin;

import com.grpAC_SMS.dao.*;
import com.grpAC_SMS.dao.impl.*;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/AdminDashboardServlet")
public class AdminDashboardServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(AdminDashboardServlet.class);
    private UserDao userDao;
    private StudentDao studentDao;
    private FacultyDao facultyDao;
    private CourseDao courseDao;
    private ProgramDao programDao;
    private AnnouncementDao announcementDao;


    @Override
    public void init() {
        userDao = new UserDaoImpl();
        studentDao = new StudentDaoImpl();
        facultyDao = new FacultyDaoImpl();
        courseDao = new CourseDaoImpl();
        programDao = new ProgramDaoImpl();
        announcementDao = new AnnouncementDaoImpl();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            Map<String, Object> dashboardData = new HashMap<>();
            dashboardData.put("totalUsers", userDao.findAll().size()); // Simplistic count
            dashboardData.put("totalStudents", studentDao.countTotalStudents());
            dashboardData.put("totalFaculty", facultyDao.countTotalFaculty());
            dashboardData.put("totalCourses", courseDao.countTotalCourses());
            dashboardData.put("totalPrograms", programDao.findAll().size()); // Simplistic
            dashboardData.put("totalActiveAnnouncements", announcementDao.countTotalActiveAnnouncements());

            request.setAttribute("adminDashboardData", dashboardData);
            RequestDispatcher dispatcher = request.getRequestDispatcher("/admin/dashboard.jsp");
            dispatcher.forward(request, response);
        } catch (Exception e) {
            logger.error("Error loading admin dashboard data: {}", e.getMessage(), e);
            request.setAttribute("errorMessage", "Failed to load dashboard statistics.");
            RequestDispatcher dispatcher = request.getRequestDispatcher("/admin/dashboard.jsp");
            dispatcher.forward(request, response);
        }
    }
}
