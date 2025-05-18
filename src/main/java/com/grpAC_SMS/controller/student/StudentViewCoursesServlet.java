package com.grpAC_SMS.controller.student;

import com.grpAC_SMS.dao.CourseDao;
import com.grpAC_SMS.dao.impl.CourseDaoImpl;
import com.grpAC_SMS.model.Course;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.List;

@WebServlet("/StudentCourses")
public class StudentViewCoursesServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);

        if (session != null && session.getAttribute("studentId") != null) {
            int studentId = (Integer) session.getAttribute("studentId");

            CourseDao courseDao = new CourseDaoImpl();
            List<Course> courses = courseDao.getCoursesByStudentId(studentId);

            req.setAttribute("courses", courses);
            RequestDispatcher dispatcher = req.getRequestDispatcher("student/my_enrolled_course.jsp");
            dispatcher.forward(req, resp);
        } else {
            resp.sendRedirect("student_login.jsp");
        }
    }
}
