package com.grpAC_SMS.controller.faculty;

import com.grpAC_SMS.dao.*;
import com.grpAC_SMS.dao.impl.*;
import com.grpAC_SMS.model.*;
import com.grpAC_SMS.service.AttendanceService;
import com.grpAC_SMS.service.impl.AttendanceServiceImpl;
import com.grpAC_SMS.exception.BusinessLogicException;
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
import java.util.*;
import java.util.stream.Collectors;

@WebServlet("/FacultyRecordAttendanceServlet")
public class FacultyRecordAttendanceServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(FacultyRecordAttendanceServlet.class);
    private FacultyDao facultyDao;
    private CourseDao courseDao;
    private LectureSessionDao lectureSessionDao;
    private StudentDao studentDao;
    private AttendanceService attendanceService; // Using service for attendance logic
    private AcademicTermDao academicTermDao;


    @Override
    public void init() {
        facultyDao = new FacultyDaoImpl();
        courseDao = new CourseDaoImpl();
        lectureSessionDao = new LectureSessionDaoImpl();
        studentDao = new StudentDaoImpl();
        attendanceService = new AttendanceServiceImpl(); // Instantiates its own DAOs
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
        if (action == null) action = "viewSessionsForAttendance";

        try {
            Faculty facultyProfile = facultyDao.findByUserId(loggedInUser.getUserId())
                    .orElseThrow(() -> new ServletException("Faculty profile not found."));

            AcademicTerm currentTerm = academicTermDao.findCurrentTerm(LocalDate.now())
                    .orElse(academicTermDao.findAll().stream().findFirst().orElse(null));
            request.setAttribute("currentTerm", currentTerm);

            switch (action) {
                case "viewSessionsForAttendance": // Select a course, then a session
                    listCoursesForAttendanceSelection(request, response, facultyProfile, currentTerm);
                    break;
                case "selectSession": // After course is chosen, list sessions for that course
                    listSessionsForCourse(request, response, facultyProfile, currentTerm);
                    break;
                case "recordAttendanceForm": // Show form to mark attendance for selected session
                    showRecordAttendanceForm(request, response, facultyProfile);
                    break;
                default:
                    listCoursesForAttendanceSelection(request, response, facultyProfile, currentTerm);
                    break;
            }
        } catch (Exception e) {
            logger.error("Error in FacultyRecordAttendanceServlet GET: {}", e.getMessage(), e);
            request.setAttribute(ApplicationConstants.REQ_ATTR_ERROR_MESSAGE, "Error processing request: " + e.getMessage());
            RequestDispatcher dispatcher = request.getRequestDispatcher("/FacultyDashboardServlet");
            dispatcher.forward(request, response);
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User loggedInUser = (session != null) ? (User) session.getAttribute(ApplicationConstants.SESSION_USER) : null;

        if (loggedInUser == null || loggedInUser.getRole() != Role.FACULTY) {
            response.sendRedirect(request.getContextPath() + "/auth/login.jsp");
            return;
        }

        String action = request.getParameter("action");
        if (action == null) {
            response.sendRedirect(request.getContextPath() + "/FacultyRecordAttendanceServlet?action=viewSessionsForAttendance");
            return;
        }

        try {
            Faculty facultyProfile = facultyDao.findByUserId(loggedInUser.getUserId())
                    .orElseThrow(() -> new ServletException("Faculty profile not found."));

            switch (action) {
                case "saveSessionAttendance":
                    saveSessionAttendance(request, response, facultyProfile);
                    break;
                default:
                    response.sendRedirect(request.getContextPath() + "/FacultyRecordAttendanceServlet?action=viewSessionsForAttendance");
                    break;
            }
        } catch (Exception e) {
            logger.error("Error in FacultyRecordAttendanceServlet POST: {}", e.getMessage(), e);
            request.getSession().setAttribute(ApplicationConstants.SESSION_ERROR_MESSAGE, "Error saving attendance: " + e.getMessage());
            // Redirect back to a relevant page, possibly the form with an error message
            String lectureSessionId = request.getParameter("lectureSessionId");
            if (lectureSessionId != null) {
                response.sendRedirect(request.getContextPath() + "/FacultyRecordAttendanceServlet?action=recordAttendanceForm&lectureSessionId=" + lectureSessionId);
            } else {
                response.sendRedirect(request.getContextPath() + "/FacultyRecordAttendanceServlet?action=viewSessionsForAttendance");
            }
        }
    }

    private void listCoursesForAttendanceSelection(HttpServletRequest request, HttpServletResponse response, Faculty faculty, AcademicTerm term) throws ServletException, IOException {
        if (term != null) {
            List<Course> courses = courseDao.findCoursesByFacultyIdAndTermId(faculty.getFacultyMemberId(), term.getAcademicTermId());
            request.setAttribute("coursesForAttendance", courses);
            request.setAttribute("currentTermId", term.getAcademicTermId());
        } else {
            request.setAttribute(ApplicationConstants.REQ_ATTR_ERROR_MESSAGE, "No active academic term found.");
        }
        RequestDispatcher dispatcher = request.getRequestDispatcher("/faculty/select_course_for_attendance.jsp"); // New JSP
        dispatcher.forward(request, response);
    }

    private void listSessionsForCourse(HttpServletRequest request, HttpServletResponse response, Faculty faculty, AcademicTerm term) throws ServletException, IOException {
        String courseIdStr = request.getParameter("courseId");
        String termIdStr = request.getParameter("termId"); // Should match currentTerm.getAcademicTermId()

        if (InputValidator.isNullOrEmpty(courseIdStr) || !InputValidator.isInteger(courseIdStr) ||
                InputValidator.isNullOrEmpty(termIdStr) || !InputValidator.isInteger(termIdStr) || term == null) {
            request.setAttribute(ApplicationConstants.REQ_ATTR_ERROR_MESSAGE, "Invalid course or term selection.");
            listCoursesForAttendanceSelection(request, response, faculty, term);
            return;
        }
        int courseId = Integer.parseInt(courseIdStr);
        int academicTermId = Integer.parseInt(termIdStr);

        Course course = courseDao.findById(courseId).orElseThrow(() -> new ServletException("Course not found."));

        // Get sessions for this course, term, and taught by this faculty
        List<LectureSession> sessions = lectureSessionDao.findByFacultyAndTerm(faculty.getFacultyMemberId(), academicTermId)
                .stream().filter(s -> s.getCourseId() == courseId).collect(Collectors.toList());

        request.setAttribute("course", course);
        request.setAttribute("term", term); // or fetch by termIdStr
        request.setAttribute("lectureSessions", sessions);
        RequestDispatcher dispatcher = request.getRequestDispatcher("/faculty/select_lecture_session_for_attendance.jsp");
        dispatcher.forward(request, response);
    }


    private void showRecordAttendanceForm(HttpServletRequest request, HttpServletResponse response, Faculty faculty) throws ServletException, IOException {
        String lectureSessionIdStr = request.getParameter("lectureSessionId");
        if (InputValidator.isNullOrEmpty(lectureSessionIdStr) || !InputValidator.isInteger(lectureSessionIdStr)) {
            request.setAttribute(ApplicationConstants.REQ_ATTR_ERROR_MESSAGE, "Lecture Session ID is required.");
            // Ideally redirect to selectSession page
            response.sendRedirect(request.getContextPath() + "/FacultyRecordAttendanceServlet?action=viewSessionsForAttendance");
            return;
        }
        int lectureSessionId = Integer.parseInt(lectureSessionIdStr);
        LectureSession lectureSession = lectureSessionDao.findById(lectureSessionId)
                .orElseThrow(() -> new ServletException("Lecture Session not found."));

        // Security check: ensure this faculty is assigned to this session or course.
        if(lectureSession.getFacultyMemberId() == null || lectureSession.getFacultyMemberId() != faculty.getFacultyMemberId()){
            // Also check if faculty teaches the course generally if session.facultyMemberId is null but admin expects any course faculty to mark
            boolean teachesCourse = !courseDao.findCoursesByFacultyIdAndTermId(faculty.getFacultyMemberId(), lectureSession.getAcademicTermId())
                    .stream().filter(c -> c.getCourseId() == lectureSession.getCourseId()).collect(Collectors.toList()).isEmpty();
            if(!teachesCourse){
                request.setAttribute(ApplicationConstants.REQ_ATTR_ERROR_MESSAGE, "You are not authorized to mark attendance for this session.");
                response.sendRedirect(request.getContextPath() + "/FacultyRecordAttendanceServlet?action=viewSessionsForAttendance");
                return;
            }
        }

        // Get students enrolled in the course of this lecture session for the correct term
        List<Student> enrolledStudents = studentDao.findByCourseIdAndTermId(lectureSession.getCourseId(), lectureSession.getAcademicTermId());

        // Fetch existing attendance records for these students in this session to pre-fill form
        Map<Integer, Attendance> existingAttendanceMap = new HashMap<>(); // studentId -> Attendance
        List<Attendance> sessionAttendanceRecords = attendanceService.getAttendanceForSession(lectureSessionId);
        for(Attendance att : sessionAttendanceRecords) {
            existingAttendanceMap.put(att.getStudentId(), att);
        }

        request.setAttribute("lectureSession", lectureSession);
        request.setAttribute("enrolledStudents", enrolledStudents);
        request.setAttribute("existingAttendanceMap", existingAttendanceMap);

        RequestDispatcher dispatcher = request.getRequestDispatcher("/faculty/record_session_attendance_form.jsp");
        dispatcher.forward(request, response);
    }

    private void saveSessionAttendance(HttpServletRequest request, HttpServletResponse response, Faculty faculty) throws IOException, BusinessLogicException {
        String lectureSessionIdStr = request.getParameter("lectureSessionId");
        if (InputValidator.isNullOrEmpty(lectureSessionIdStr) || !InputValidator.isInteger(lectureSessionIdStr)) {
            throw new BusinessLogicException("Lecture Session ID is missing.");
        }
        int lectureSessionId = Integer.parseInt(lectureSessionIdStr);

        String[] studentIds = request.getParameterValues("studentId"); // Hidden input for each student
        if (studentIds == null || studentIds.length == 0) {
            request.getSession().setAttribute(ApplicationConstants.SESSION_ERROR_MESSAGE, "No student attendance data submitted.");
            response.sendRedirect(request.getContextPath() + "/FacultyRecordAttendanceServlet?action=recordAttendanceForm&lectureSessionId=" + lectureSessionId);
            return;
        }

        List<Attendance> attendanceToSaveList = new ArrayList<>();
        List<String> errors = new ArrayList<>();

        for (String studentIdStr : studentIds) {
            try {
                int studentId = Integer.parseInt(studentIdStr);
                // For each student, checkbox name could be "isPresent_STUDENTID"
                String isPresentParam = request.getParameter("isPresent_" + studentId);
                boolean isPresent = "on".equalsIgnoreCase(isPresentParam) || "true".equalsIgnoreCase(isPresentParam); // Checkbox gives "on" if checked

                Attendance att = new Attendance();
                att.setStudentId(studentId);
                att.setLectureSessionId(lectureSessionId);
                att.setPresent(isPresent);
                // Service layer will handle if it's an add or update
                attendanceToSaveList.add(att);

            } catch (NumberFormatException e) {
                errors.add("Invalid student ID format: " + studentIdStr);
            }
        }

        if (!attendanceToSaveList.isEmpty()) {
            attendanceService.markMultipleStudentsAttendance(attendanceToSaveList, faculty.getFacultyMemberId());
            request.getSession().setAttribute(ApplicationConstants.SESSION_SUCCESS_MESSAGE, "Attendance records saved successfully.");
        }

        if (!errors.isEmpty()) {
            String errorString = String.join("; ", errors);
            request.getSession().setAttribute(ApplicationConstants.SESSION_ERROR_MESSAGE, (request.getSession().getAttribute(ApplicationConstants.SESSION_SUCCESS_MESSAGE) != null ? "Some errors occurred: " : "Errors occurred: ") + errorString);
        }

        response.sendRedirect(request.getContextPath() + "/FacultyRecordAttendanceServlet?action=recordAttendanceForm&lectureSessionId=" + lectureSessionId);
    }
}