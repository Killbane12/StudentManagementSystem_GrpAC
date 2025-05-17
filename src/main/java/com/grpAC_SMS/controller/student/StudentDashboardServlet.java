package com.grpAC_SMS.controller.student;

import com.grpAC_SMS.dao.*;
import com.grpAC_SMS.dao.impl.*;
import com.grpAC_SMS.model.*;
import com.grpAC_SMS.service.AttendanceService; // Using service for percentage
import com.grpAC_SMS.service.impl.AttendanceServiceImpl;
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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@WebServlet("/StudentDashboardServlet")
public class StudentDashboardServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(StudentDashboardServlet.class);
    private StudentDao studentDao;
    private CourseDao courseDao;
    private AnnouncementDao announcementDao;
    private AcademicTermDao academicTermDao;
    private AttendanceService attendanceService;


    @Override
    public void init() {
        studentDao = new StudentDaoImpl();
        courseDao = new CourseDaoImpl();
        announcementDao = new AnnouncementDaoImpl();
        academicTermDao = new AcademicTermDaoImpl();
        attendanceService = new AttendanceServiceImpl(); // Uses its own DAOs
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
                logger.error("Student profile not found for user ID: {}", loggedInUser.getUserId());
                request.getSession().setAttribute(ApplicationConstants.SESSION_ERROR_MESSAGE, "Your student profile could not be found.");
                response.sendRedirect(request.getContextPath() + "/auth/login.jsp");
                return;
            }
            Student studentProfile = studentOpt.get();
            request.setAttribute("studentProfile", studentProfile); // For welcome message

            // Get current academic term
            Optional<AcademicTerm> currentTermOpt = academicTermDao.findCurrentTerm(LocalDate.now());
            AcademicTerm currentTerm = null;
            if(currentTermOpt.isPresent()){
                currentTerm = currentTermOpt.get();
                request.setAttribute("currentTermName", currentTerm.getTermName());
            } else {
                List<AcademicTerm> allTerms = academicTermDao.findAll(); // Fallback to latest if no current
                if(!allTerms.isEmpty()) {
                    currentTerm = allTerms.get(0); // findAll is ordered by start_date DESC
                    request.setAttribute("currentTermName", currentTerm.getTermName() + " (Latest)");
                } else {
                    request.setAttribute("currentTermName", "N/A (No terms defined)");
                }
            }

            List<Course> enrolledCoursesInCurrentTerm = new ArrayList<>();
            Map<Integer, Double> attendancePercentages = new HashMap<>(); // CourseID -> Percentage
            List<Integer> courseIdsForAttendanceCalc = new ArrayList<>();

            if (currentTerm != null) {
                enrolledCoursesInCurrentTerm = courseDao.findCoursesByStudentIdAndTermId(studentProfile.getStudentId(), currentTerm.getAcademicTermId());
                for (Course course : enrolledCoursesInCurrentTerm) {
                    courseIdsForAttendanceCalc.add(course.getCourseId());
                }
                if(!courseIdsForAttendanceCalc.isEmpty()){
                    attendancePercentages = attendanceService.getAttendancePercentageForStudentCourses(studentProfile.getStudentId(), courseIdsForAttendanceCalc, currentTerm.getAcademicTermId());
                }
            }

            request.setAttribute("enrolledCourses", enrolledCoursesInCurrentTerm);
            request.setAttribute("attendancePercentages", attendancePercentages);

            List<Announcement> announcements = announcementDao.findTargetedAnnouncements(Role.STUDENT, 5); // Get latest 5
            request.setAttribute("announcements", announcements);

            logger.debug("Forwarding to student dashboard for user: {}", loggedInUser.getUsername());
            RequestDispatcher dispatcher = request.getRequestDispatcher("/student/dashboard.jsp");
            dispatcher.forward(request, response);

        } catch (Exception e) {
            logger.error("Error preparing student dashboard for user {}: {}", loggedInUser.getUsername(), e.getMessage(), e);
            request.setAttribute("errorMessage", "Could not load your dashboard data: " + e.getMessage());
            // Still attempt to forward to dashboard to show the error message there
            RequestDispatcher dispatcher = request.getRequestDispatcher("/student/dashboard.jsp");
            dispatcher.forward(request, response);
        }
    }
}