package com.grpAC_SMS.controller.faculty;

import com.grpAC_SMS.dao.*;
import com.grpAC_SMS.dao.impl.*;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@WebServlet("/FacultyDashboardServlet")
public class FacultyDashboardServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(FacultyDashboardServlet.class);
    private FacultyDao facultyDao;
    private CourseDao courseDao;
    private LectureSessionDao lectureSessionDao;
    private AnnouncementDao announcementDao;
    private AcademicTermDao academicTermDao;
    private GradeDao gradeDao; // To count grading tasks

    @Override
    public void init() {
        facultyDao = new FacultyDaoImpl();
        courseDao = new CourseDaoImpl();
        lectureSessionDao = new LectureSessionDaoImpl();
        announcementDao = new AnnouncementDaoImpl();
        academicTermDao = new AcademicTermDaoImpl();
        gradeDao = new GradeDaoImpl();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User loggedInUser = (session != null) ? (User) session.getAttribute(ApplicationConstants.SESSION_USER) : null;

        if (loggedInUser == null || loggedInUser.getRole() != Role.FACULTY) {
            response.sendRedirect(request.getContextPath() + "/auth/login.jsp");
            return;
        }

        try {
            Optional<Faculty> facultyOpt = facultyDao.findByUserId(loggedInUser.getUserId());
            if (facultyOpt.isEmpty()) {
                logger.error("Faculty profile not found for user ID: {}", loggedInUser.getUserId());
                request.getSession().setAttribute(ApplicationConstants.SESSION_ERROR_MESSAGE, "Faculty profile not found.");
                response.sendRedirect(request.getContextPath() + "/auth/login.jsp"); // Or an error page
                return;
            }
            Faculty facultyProfile = facultyOpt.get();

            // Get current academic term
            Optional<AcademicTerm> currentTermOpt = academicTermDao.findCurrentTerm(LocalDate.now());
            AcademicTerm currentTerm = null;
            if(currentTermOpt.isPresent()){
                currentTerm = currentTermOpt.get();
            } else {
                List<AcademicTerm> allTerms = academicTermDao.findAll(); // Fallback to latest if no current
                if(!allTerms.isEmpty()) currentTerm = allTerms.get(0);
            }


            Map<String, Object> dashboardData = new HashMap<>();
            dashboardData.put("facultyName", facultyProfile.getFirstName());

            if (currentTerm != null) {
                dashboardData.put("currentTermName", currentTerm.getTermName());
                List<Course> assignedCourses = courseDao.findCoursesByFacultyIdAndTermId(facultyProfile.getFacultyMemberId(), currentTerm.getAcademicTermId());
                dashboardData.put("assignedCoursesCount", assignedCourses.size());

                // Find upcoming lectures (e.g., next 7 days)
                // This is a simplified count. A proper query would be needed.
                List<LectureSession> allFacultySessionsInTerm = lectureSessionDao.findByFacultyAndTerm(facultyProfile.getFacultyMemberId(), currentTerm.getAcademicTermId());
                long upcomingLecturesCount = allFacultySessionsInTerm.stream()
                        .filter(ls -> ls.getSessionStartDatetime().isAfter(LocalDateTime.now()) && ls.getSessionStartDatetime().isBefore(LocalDateTime.now().plusDays(7)))
                        .count();
                dashboardData.put("upcomingLecturesCount", upcomingLecturesCount);

                // Count courses needing grading (very simplified: courses taught this term without any final grades)
                // This needs a more complex logic/query
                long gradingTasksCount = 0;
                for(Course c : assignedCourses){
                    // Check if any student in this course/term has a "Final Exam" grade entered by this faculty.
                    // This is a placeholder for a real count.
                }
                dashboardData.put("gradingTasksCount", 0); // Placeholder

            } else {
                dashboardData.put("currentTermName", "N/A");
                dashboardData.put("assignedCoursesCount", 0);
                dashboardData.put("upcomingLecturesCount", 0);
                dashboardData.put("gradingTasksCount", 0);
            }

            List<Announcement> announcements = announcementDao.findTargetedAnnouncements(Role.FACULTY, 5);
            dashboardData.put("announcements", announcements);

            request.setAttribute("facultyDashboardData", dashboardData);
            logger.debug("Forwarding to faculty dashboard for user: {}", loggedInUser.getUsername());
            RequestDispatcher dispatcher = request.getRequestDispatcher("/faculty/dashboard.jsp");
            dispatcher.forward(request, response);

        } catch (Exception e) {
            logger.error("Error loading faculty dashboard for user {}: {}", loggedInUser.getUsername(), e.getMessage(), e);
            request.setAttribute("errorMessage", "Could not load dashboard data: " + e.getMessage());
            RequestDispatcher dispatcher = request.getRequestDispatcher("/faculty/dashboard.jsp");
            dispatcher.forward(request, response);
        }
    }
}