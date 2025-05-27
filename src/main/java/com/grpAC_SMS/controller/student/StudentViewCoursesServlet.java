package com.grpAC_SMS.controller.student;

import com.grpAC_SMS.dao.AcademicTermDao;
import com.grpAC_SMS.dao.EnrollmentDao;
import com.grpAC_SMS.dao.StudentDao;
import com.grpAC_SMS.dao.impl.AcademicTermDaoImpl;
import com.grpAC_SMS.dao.impl.EnrollmentDaoImpl;
import com.grpAC_SMS.dao.impl.StudentDaoImpl;
import com.grpAC_SMS.model.*;
import com.grpAC_SMS.util.ApplicationConstants;

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
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@WebServlet("/StudentViewCoursesServlet")
public class StudentViewCoursesServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(StudentViewCoursesServlet.class);
    private StudentDao studentDao;
    private EnrollmentDao enrollmentDao;
    private AcademicTermDao academicTermDao;

    @Override
    public void init() {
        studentDao = new StudentDaoImpl();
        enrollmentDao = new EnrollmentDaoImpl();
        academicTermDao = new AcademicTermDaoImpl();
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
                RequestDispatcher dispatcher = request.getRequestDispatcher("/student/my_enrolled_courses.jsp");
                dispatcher.forward(request, response);
                return;
            }
            Student student = studentOpt.get();

            List<Enrollment> allEnrollments = enrollmentDao.findByStudentId(student.getStudentId());
            // Group enrollments by academic term for display
            Map<AcademicTerm, List<Enrollment>> enrollmentsByTerm = allEnrollments.stream()
                    .collect(Collectors.groupingBy(enrollment ->
                            academicTermDao.findById(enrollment.getAcademicTermId())
                                    .orElseGet(() -> { // Create a placeholder term if somehow not found
                                        AcademicTerm placeholder = new AcademicTerm();
                                        placeholder.setTermName("Unknown Term (ID: " + enrollment.getAcademicTermId() + ")");
                                        return placeholder;
                                    })
                    ));

            // Sort the map by term start date (descending) for display order
            // This requires AcademicTerm to be comparable or sorting keys separately
            // For simplicity, JSP can iterate, or you can sort keys then retrieve.
            // For now, default map iteration order is fine for most cases.

            request.setAttribute("enrollmentsByTerm", enrollmentsByTerm);

        } catch (Exception e) {
            logger.error("Error fetching enrolled courses for student {}: {}", loggedInUser.getUsername(), e.getMessage(), e);
            request.setAttribute("errorMessage", "Could not load your enrolled courses: " + e.getMessage());
        }
        RequestDispatcher dispatcher = request.getRequestDispatcher("/student/my_enrolled_courses.jsp");
        dispatcher.forward(request, response);
    }
}