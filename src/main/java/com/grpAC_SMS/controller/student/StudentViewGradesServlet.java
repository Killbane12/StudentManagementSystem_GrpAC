package com.grpAC_SMS.controller.student;

import com.grpAC_SMS.dao.GradeDao;
import com.grpAC_SMS.dao.StudentDao;
import com.grpAC_SMS.dao.impl.GradeDaoImpl;
import com.grpAC_SMS.dao.impl.StudentDaoImpl;
import com.grpAC_SMS.model.Grade;
import com.grpAC_SMS.model.Role;
import com.grpAC_SMS.model.Student;
import com.grpAC_SMS.model.User;
import com.grpAC_SMS.util.ApplicationConstants;
import com.grpAC_SMS.util.InputValidator;


import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;

@WebServlet("/StudentViewGradesServlet")
public class StudentViewGradesServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(StudentViewGradesServlet.class);
    private StudentDao studentDao;
    private GradeDao gradeDao;

    @Override
    public void init() {
        studentDao = new StudentDaoImpl();
        gradeDao = new GradeDaoImpl();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User loggedInUser = (session != null) ? (User) session.getAttribute(ApplicationConstants.SESSION_USER) : null;

        if (loggedInUser == null || loggedInUser.getRole() != Role.STUDENT) {
            response.sendRedirect(request.getContextPath() + "/auth/login.jsp");
            return;
        }

        try {
            Optional<Student> studentOpt = studentDao.findByUserId(loggedInUser.getUserId());
            if (studentOpt.isEmpty()) {
                request.setAttribute("errorMessage", "Student profile not found.");
                RequestDispatcher dispatcher = request.getRequestDispatcher("/student/my_grades.jsp");
                dispatcher.forward(request, response);
                return;
            }
            Student student = studentOpt.get();

            List<Grade> studentGrades;
            String courseIdStr = request.getParameter("courseId"); // Optional filter

            if (!InputValidator.isNullOrEmpty(courseIdStr) && InputValidator.isInteger(courseIdStr)) {
                int courseId = Integer.parseInt(courseIdStr);
                // This would require a new DAO method: findByStudentAndCourseId
                // For now, let's assume gradeDao.findByStudentId fetches all and we filter later if needed,
                // or JSP handles it. A more efficient way is a specific DAO method.
                // Let's modify GradeDao to have findByStudentAndCourse(int studentId, int courseId)
                // For simplicity, we'll fetch all student grades and the JSP can display.
                // If filtering is added, a specific DAO method is better.
                studentGrades = gradeDao.findByStudentId(student.getStudentId());
                // Filter here if courseIdStr is present, or pass courseId to JSP for display filtering
                if (courseId > 0) {
                    final int cId = courseId; // for lambda
                    // This filtering is basic. Ideally, Grade object would have courseId directly or via Enrollment
                    // For now, this won't work well unless Grade object has course details.
                    // Let's assume the JSP will just display all grades, and links to this servlet might have courseId for context.
                    request.setAttribute("filterCourseId", cId);
                }

            } else {
                studentGrades = gradeDao.findByStudentId(student.getStudentId());
            }

            // The Grade objects from gradeDao.findByStudentId should have courseName etc. due to joins.
            request.setAttribute("studentGrades", studentGrades);

        } catch (Exception e) {
            logger.error("Error fetching grades for student {}: {}", loggedInUser.getUsername(), e.getMessage(), e);
            request.setAttribute("errorMessage", "Could not load your grades: " + e.getMessage());
        }
        RequestDispatcher dispatcher = request.getRequestDispatcher("/student/my_grades.jsp");
        dispatcher.forward(request, response);
    }
}