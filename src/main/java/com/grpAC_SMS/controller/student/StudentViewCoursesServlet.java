package com.grpAC_SMS.controller.student;

import com.grpAC_SMS.dao.CourseDao;
import com.grpAC_SMS.dao.StudentDao;
import com.grpAC_SMS.dao.impl.CourseDaoImpl;
import com.grpAC_SMS.dao.impl.StudentDaoImpl;
import com.grpAC_SMS.model.*;
import com.grpAC_SMS.util.ApplicationConstants;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "StudentViewCoursesServlet", value = "/student/my_enrolled_courses")
public class StudentViewCoursesServlet extends HttpServlet {
    private StudentDao studentDao;
    private CourseDao courseDao;

    // Initialize DAO implementations
    @Override
    public void init() throws ServletException {
        super.init();
        studentDao = new StudentDaoImpl();
        courseDao = new CourseDaoImpl(); // CourseDao initialize කරනවා
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Check if user is logged in
        HttpSession session = request.getSession();
        if (session == null || session.getAttribute(ApplicationConstants.LOGGED_IN_USER_SESSION_ATTR) == null) {
            response.sendRedirect(request.getContextPath() + "/auth/login");
            return;
        }

        // Get logged in student details
        User sessionUser = (User) session.getAttribute(ApplicationConstants.LOGGED_IN_USER_SESSION_ATTR);
        String username = sessionUser.getUsername();


        Student student = studentDao.getStudentByUsername(username); // studentDao එකෙන් student id එක ගන්නවා


        if (student == null) {
            response.sendRedirect(request.getContextPath() + "/auth/login?error=student_not_found");
            return;
        }

        // Fetch student's academic information
        Program program = courseDao.getStudentProgram(student.getStudentId()); // studentDao එකෙන් ගත්ත studentId එක courseDao එකට දෙනවා
        Department department = courseDao.getDepartmentById(student.getStudentId());
        List<Course> courses = courseDao.getCoursesByProgramId(department.getDepartmentId());

        // Set attributes for JSP
        request.setAttribute("student", student);
        request.setAttribute("program", program);
        request.setAttribute("department", department);
        request.setAttribute("courses", courses);

        // Forward to view page
        RequestDispatcher dispatcher = request.getRequestDispatcher("/student/my_enrolled_courses.jsp");
        dispatcher.forward(request, response);
    }
}