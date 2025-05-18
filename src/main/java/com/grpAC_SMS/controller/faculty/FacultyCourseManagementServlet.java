package com.grpAC_SMS.controller.faculty;

import com.grpAC_SMS.dao.CourseDao;
import com.grpAC_SMS.dao.FacultyDao;
import com.grpAC_SMS.dao.StudentDao;
import com.grpAC_SMS.dao.AcademicTermDao;
import com.grpAC_SMS.dao.impl.CourseDaoImpl;
import com.grpAC_SMS.dao.impl.FacultyDaoImpl;
import com.grpAC_SMS.dao.impl.StudentDaoImpl;
import com.grpAC_SMS.dao.impl.AcademicTermDaoImpl;
import com.grpAC_SMS.model.*;
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
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@WebServlet("/FacultyCourseManagementServlet")
public class FacultyCourseManagementServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(FacultyCourseManagementServlet.class);
    private FacultyDao facultyDao;
    private CourseDao courseDao;
    private StudentDao studentDao;
    private AcademicTermDao academicTermDao;

    @Override
    public void init() {
        facultyDao = new FacultyDaoImpl();
        courseDao = new CourseDaoImpl();
        studentDao = new StudentDaoImpl();
        academicTermDao = new AcademicTermDaoImpl();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User loggedInUser = (session != null) ? (User) session.getAttribute(ApplicationConstants.SESSION_USER) : null;

        if (loggedInUser == null || loggedInUser.getRole() != Role.FACULTY) {
            response.sendRedirect(request.getContextPath() + "/auth/login.jsp");
            return;
        }

        String action = request.getParameter("action");
        if (action == null) action = "myCourses"; // Default

        try {
            Faculty facultyProfile = facultyDao.findByUserId(loggedInUser.getUserId())
                    .orElseThrow(() -> new ServletException("Faculty profile not found for user."));

            // Get current/selected term (allow faculty to select term in future enhancement)
            Optional<AcademicTerm> currentTermOpt = academicTermDao.findCurrentTerm(LocalDate.now());
            AcademicTerm currentTerm = null;
            if(currentTermOpt.isPresent()){
                currentTerm = currentTermOpt.get();
            } else {
                List<AcademicTerm> allTerms = academicTermDao.findAll();
                if(!allTerms.isEmpty()) currentTerm = allTerms.get(0); // Fallback
            }
            request.setAttribute("currentTerm", currentTerm); // For display

            switch (action) {
                case "myCourses":
                    listMyCourses(request, response, facultyProfile, currentTerm);
                    break;
                case "viewStudents":
                    viewEnrolledStudents(request, response, facultyProfile, currentTerm);
                    break;
                default:
                    listMyCourses(request, response, facultyProfile, currentTerm);
                    break;
            }
        } catch (Exception e) {
            logger.error("Error in FacultyCourseManagementServlet: {}", e.getMessage(), e);
            request.setAttribute("errorMessage", "An error occurred: " + e.getMessage());
            // Redirect to a safe page, like faculty dashboard
            RequestDispatcher dispatcher = request.getRequestDispatcher("/FacultyDashboardServlet");
            dispatcher.forward(request, response);
        }
    }

    private void listMyCourses(HttpServletRequest request, HttpServletResponse response, Faculty faculty, AcademicTerm term) throws ServletException, IOException {
        if (term != null) {
            List<Course> assignedCourses = courseDao.findCoursesByFacultyIdAndTermId(faculty.getFacultyMemberId(), term.getAcademicTermId());
            // The Course objects from this DAO method might already have termName if joined properly.
            // If not, you might need to set it or pass the term object separately.
            // For simplicity, assuming findCoursesByFacultyIdAndTermId can populate a transient 'termName' or similar.
            // Or, iterate and set if Course model supports it.
            for(Course course : assignedCourses) { // Example of manually setting term name if not in DAO result
                course.setTermName(term.getTermName()); // Add setTermName to Course.java or a DTO
                course.setAcademicTermId(term.getAcademicTermId()); // Also useful for links
            }
            request.setAttribute("assignedCourses", assignedCourses);
        } else {
            request.setAttribute("errorMessage", "No active academic term found to display courses.");
        }
        RequestDispatcher dispatcher = request.getRequestDispatcher("/faculty/my_courses.jsp");
        dispatcher.forward(request, response);
    }

    private void viewEnrolledStudents(HttpServletRequest request, HttpServletResponse response, Faculty faculty, AcademicTerm term) throws ServletException, IOException {
        String courseIdStr = request.getParameter("courseId");
        String termIdStr = request.getParameter("termId"); // Term ID should ideally come with the course link

        if (InputValidator.isNullOrEmpty(courseIdStr) || !InputValidator.isInteger(courseIdStr) ||
                InputValidator.isNullOrEmpty(termIdStr) || !InputValidator.isInteger(termIdStr)) {
            request.setAttribute("errorMessage", "Invalid course or term selection.");
            listMyCourses(request, response, faculty, term); // Go back to list
            return;
        }
        int courseId = Integer.parseInt(courseIdStr);
        int academicTermId = Integer.parseInt(termIdStr);

        // Verify faculty is indeed teaching this course in this term (security/consistency check)
        // This might be implicitly handled if links are generated from `listMyCourses` correctly.

        Course course = courseDao.findById(courseId).orElseThrow(() -> new ServletException("Course not found."));
        AcademicTerm selectedTerm = academicTermDao.findById(academicTermId).orElseThrow(()-> new ServletException("Term not found."));

        List<Student> enrolledStudents = studentDao.findByCourseIdAndTermId(courseId, academicTermId);

        request.setAttribute("course", course);
        request.setAttribute("term", selectedTerm);
        request.setAttribute("enrolledStudents", enrolledStudents);
        RequestDispatcher dispatcher = request.getRequestDispatcher("/faculty/course_details_faculty.jsp");
        dispatcher.forward(request, response);
    }
}