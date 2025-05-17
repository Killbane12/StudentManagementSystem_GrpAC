package com.grpAC_SMS.controller.admin;

import com.grpAC_SMS.dao.*;
import com.grpAC_SMS.dao.impl.*;
import com.grpAC_SMS.model.*;
import com.grpAC_SMS.exception.DataAccessException;
import com.grpAC_SMS.util.ApplicationConstants;
import com.grpAC_SMS.util.DateFormatter;
import com.grpAC_SMS.util.InputValidator;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@WebServlet("/ManageLectureSessionsServlet")
public class ManageLectureSessionsServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(ManageLectureSessionsServlet.class);
    private LectureSessionDao lectureSessionDao;
    private CourseDao courseDao;
    private FacultyDao facultyDao;
    private AcademicTermDao academicTermDao;
    private LocationDao locationDao;

    @Override
    public void init() {
        lectureSessionDao = new LectureSessionDaoImpl();
        courseDao = new CourseDaoImpl();
        facultyDao = new FacultyDaoImpl();
        academicTermDao = new AcademicTermDaoImpl();
        locationDao = new LocationDaoImpl();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        if (action == null) action = ApplicationConstants.ACTION_LIST;

        try {
            switch (action) {
                case ApplicationConstants.ACTION_ADD:
                    showNewForm(request, response);
                    break;
                case ApplicationConstants.ACTION_EDIT:
                    showEditForm(request, response);
                    break;
                case ApplicationConstants.ACTION_DELETE:
                    deleteSession(request, response);
                    break;
                case ApplicationConstants.ACTION_LIST:
                default:
                    listSessions(request, response);
                    break;
            }
        } catch (DataAccessException e) {
            logger.error("DataAccessException in LectureSessions GET: {}", e.getMessage());
            request.getSession().setAttribute(ApplicationConstants.SESSION_ERROR_MESSAGE, "Database operation failed: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/ManageLectureSessionsServlet?action=" + ApplicationConstants.ACTION_LIST);
        } catch (NumberFormatException e) {
            logger.error("NumberFormatException in LectureSessions GET: {}", e.getMessage());
            request.getSession().setAttribute(ApplicationConstants.SESSION_ERROR_MESSAGE, "Invalid ID format.");
            response.sendRedirect(request.getContextPath() + "/ManageLectureSessionsServlet?action=" + ApplicationConstants.ACTION_LIST);
        }catch (Exception e) {
            logger.error("General Exception in LectureSessions GET: {}", e.getMessage(), e);
            request.getSession().setAttribute(ApplicationConstants.SESSION_ERROR_MESSAGE, "An unexpected error occurred: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/ManageLectureSessionsServlet?action=" + ApplicationConstants.ACTION_LIST);
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        if (action == null) {
            response.sendRedirect(request.getContextPath() + "/ManageLectureSessionsServlet?action=" + ApplicationConstants.ACTION_LIST);
            return;
        }

        try {
            switch (action) {
                case ApplicationConstants.ACTION_CREATE:
                    createSession(request, response);
                    break;
                case ApplicationConstants.ACTION_UPDATE:
                    updateSession(request, response);
                    break;
                default:
                    listSessions(request, response);
                    break;
            }
        } catch (DataAccessException e) {
            logger.error("DataAccessException in LectureSessions POST for action {}: {}", action, e.getMessage());
            request.setAttribute(ApplicationConstants.REQ_ATTR_ERROR_MESSAGE, "Operation failed: " + e.getMessage());
            preserveFormDataOnError(request, action);
            if (ApplicationConstants.ACTION_CREATE.equals(action)) {
                showNewForm(request, response);
            } else if (ApplicationConstants.ACTION_UPDATE.equals(action)) {
                showEditForm(request, response);
            } else {
                response.sendRedirect(request.getContextPath() + "/ManageLectureSessionsServlet?action=" + ApplicationConstants.ACTION_LIST);
            }
        } catch (Exception e) {
            logger.error("General Exception in LectureSessions POST for action {}: {}", action, e.getMessage(), e);
            request.getSession().setAttribute(ApplicationConstants.SESSION_ERROR_MESSAGE, "An unexpected error occurred: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/ManageLectureSessionsServlet?action=" + ApplicationConstants.ACTION_LIST);
        }
    }

    private void loadDropdownData(HttpServletRequest request) {
        if(request.getAttribute("courseList") == null) request.setAttribute("courseList", courseDao.findAll());
        if(request.getAttribute("facultyList") == null) request.setAttribute("facultyList", facultyDao.findAll());
        if(request.getAttribute("termList") == null) request.setAttribute("termList", academicTermDao.findAll());
        if(request.getAttribute("locationList") == null) request.setAttribute("locationList", locationDao.findAll());
    }

    private void preserveFormDataOnError(HttpServletRequest request, String action) {
        LectureSession session = new LectureSession();
        if (ApplicationConstants.ACTION_UPDATE.equals(action) && request.getParameter("lectureSessionId") != null) {
            try {
                session.setLectureSessionId(Integer.parseInt(request.getParameter("lectureSessionId")));
            } catch (NumberFormatException e) { /* ignore */ }
        }

        String courseIdStr = request.getParameter("courseId");
        if (!InputValidator.isNullOrEmpty(courseIdStr) && InputValidator.isInteger(courseIdStr)) session.setCourseId(Integer.parseInt(courseIdStr));

        String facultyIdStr = request.getParameter("facultyMemberId");
        if (!InputValidator.isNullOrEmpty(facultyIdStr) && InputValidator.isInteger(facultyIdStr) && Integer.parseInt(facultyIdStr) > 0) {
            session.setFacultyMemberId(Integer.parseInt(facultyIdStr));
        }
        String termIdStr = request.getParameter("academicTermId");
        if (!InputValidator.isNullOrEmpty(termIdStr) && InputValidator.isInteger(termIdStr)) session.setAcademicTermId(Integer.parseInt(termIdStr));

        String locationIdStr = request.getParameter("locationId");
        if (!InputValidator.isNullOrEmpty(locationIdStr) && InputValidator.isInteger(locationIdStr) && Integer.parseInt(locationIdStr) > 0) {
            session.setLocationId(Integer.parseInt(locationIdStr));
        }

        if (InputValidator.isValidDateTimeLocal(request.getParameter("sessionStartDatetime"))) {
            session.setSessionStartDatetime(DateFormatter.stringToLocalDateTime(request.getParameter("sessionStartDatetime")));
        }
        if (InputValidator.isValidDateTimeLocal(request.getParameter("sessionEndDatetime"))) {
            session.setSessionEndDatetime(DateFormatter.stringToLocalDateTime(request.getParameter("sessionEndDatetime")));
        }
        request.setAttribute("session", session); // Renamed from "lectureSession" to "session" for brevity
        loadDropdownData(request); // Ensure dropdowns are repopulated
    }


    private void listSessions(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<LectureSession> sessionList = lectureSessionDao.findAllWithDetails();
        request.setAttribute("sessionList", sessionList);
        RequestDispatcher dispatcher = request.getRequestDispatcher("/admin/lecture_sessions_list.jsp");
        dispatcher.forward(request, response);
    }

    private void showNewForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if(request.getAttribute("session") == null) request.setAttribute("session", new LectureSession());
        loadDropdownData(request);
        RequestDispatcher dispatcher = request.getRequestDispatcher("/admin/lecture_session_form.jsp");
        dispatcher.forward(request, response);
    }

    private void showEditForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if(request.getAttribute("session") == null) {
            int id = Integer.parseInt(request.getParameter("id"));
            LectureSession existingSession = lectureSessionDao.findById(id)
                    .orElseThrow(() -> new DataAccessException("Lecture Session not found with ID: " + id));
            request.setAttribute("session", existingSession);
        }
        loadDropdownData(request);
        RequestDispatcher dispatcher = request.getRequestDispatcher("/admin/lecture_session_form.jsp");
        dispatcher.forward(request, response);
    }

    private void createSession(HttpServletRequest request, HttpServletResponse response) throws IOException, DataAccessException, ServletException {
        String courseIdStr = request.getParameter("courseId");
        String facultyIdStr = request.getParameter("facultyMemberId"); // Optional
        String termIdStr = request.getParameter("academicTermId");
        String locationIdStr = request.getParameter("locationId"); // Optional
        String startDatetimeStr = request.getParameter("sessionStartDatetime");
        String endDatetimeStr = request.getParameter("sessionEndDatetime");

        if (InputValidator.isNullOrEmpty(courseIdStr) || !InputValidator.isInteger(courseIdStr) ||
                InputValidator.isNullOrEmpty(termIdStr) || !InputValidator.isInteger(termIdStr) ||
                !InputValidator.isValidDateTimeLocal(startDatetimeStr) || !InputValidator.isValidDateTimeLocal(endDatetimeStr)) {
            request.setAttribute(ApplicationConstants.REQ_ATTR_ERROR_MESSAGE, "Course, Term, and valid Start/End DateTimes are required.");
            preserveFormDataOnError(request,ApplicationConstants.ACTION_CREATE);
            showNewForm(request, response);
            return;
        }

        LocalDateTime startDatetime = DateFormatter.stringToLocalDateTime(startDatetimeStr);
        LocalDateTime endDatetime = DateFormatter.stringToLocalDateTime(endDatetimeStr);

        if (startDatetime == null || endDatetime == null || startDatetime.isAfter(endDatetime) || startDatetime.isEqual(endDatetime)) {
            request.setAttribute(ApplicationConstants.REQ_ATTR_ERROR_MESSAGE, "Start DateTime must be before End DateTime.");
            preserveFormDataOnError(request,ApplicationConstants.ACTION_CREATE);
            showNewForm(request, response);
            return;
        }

        LectureSession session = new LectureSession();
        session.setCourseId(Integer.parseInt(courseIdStr));
        session.setAcademicTermId(Integer.parseInt(termIdStr));
        session.setSessionStartDatetime(startDatetime);
        session.setSessionEndDatetime(endDatetime);

        if (!InputValidator.isNullOrEmpty(facultyIdStr) && InputValidator.isInteger(facultyIdStr) && Integer.parseInt(facultyIdStr) > 0) {
            session.setFacultyMemberId(Integer.parseInt(facultyIdStr));
        }
        if (!InputValidator.isNullOrEmpty(locationIdStr) && InputValidator.isInteger(locationIdStr) && Integer.parseInt(locationIdStr) > 0) {
            session.setLocationId(Integer.parseInt(locationIdStr));
        }

        lectureSessionDao.add(session);
        request.getSession().setAttribute(ApplicationConstants.SESSION_SUCCESS_MESSAGE, "Lecture Session created successfully!");
        response.sendRedirect(request.getContextPath() + "/ManageLectureSessionsServlet?action=" + ApplicationConstants.ACTION_LIST);
    }

    private void updateSession(HttpServletRequest request, HttpServletResponse response) throws IOException, DataAccessException, ServletException {
        int id = Integer.parseInt(request.getParameter("lectureSessionId"));
        String courseIdStr = request.getParameter("courseId");
        String facultyIdStr = request.getParameter("facultyMemberId");
        String termIdStr = request.getParameter("academicTermId");
        String locationIdStr = request.getParameter("locationId");
        String startDatetimeStr = request.getParameter("sessionStartDatetime");
        String endDatetimeStr = request.getParameter("sessionEndDatetime");

        if (InputValidator.isNullOrEmpty(courseIdStr) || !InputValidator.isInteger(courseIdStr) ||
                InputValidator.isNullOrEmpty(termIdStr) || !InputValidator.isInteger(termIdStr) ||
                !InputValidator.isValidDateTimeLocal(startDatetimeStr) || !InputValidator.isValidDateTimeLocal(endDatetimeStr)) {
            request.setAttribute(ApplicationConstants.REQ_ATTR_ERROR_MESSAGE, "Course, Term, and valid Start/End DateTimes are required.");
            preserveFormDataOnError(request,ApplicationConstants.ACTION_UPDATE);
            showEditForm(request, response);
            return;
        }

        LocalDateTime startDatetime = DateFormatter.stringToLocalDateTime(startDatetimeStr);
        LocalDateTime endDatetime = DateFormatter.stringToLocalDateTime(endDatetimeStr);

        if (startDatetime == null || endDatetime == null || startDatetime.isAfter(endDatetime) || startDatetime.isEqual(endDatetime)) {
            request.setAttribute(ApplicationConstants.REQ_ATTR_ERROR_MESSAGE, "Start DateTime must be before End DateTime.");
            preserveFormDataOnError(request,ApplicationConstants.ACTION_UPDATE);
            showEditForm(request, response);
            return;
        }

        LectureSession session = lectureSessionDao.findById(id).orElseThrow(() -> new DataAccessException("Lecture Session not found for update."));
        session.setCourseId(Integer.parseInt(courseIdStr));
        session.setAcademicTermId(Integer.parseInt(termIdStr));
        session.setSessionStartDatetime(startDatetime);
        session.setSessionEndDatetime(endDatetime);

        session.setFacultyMemberId(null); // Reset optional fields
        session.setLocationId(null);

        if (!InputValidator.isNullOrEmpty(facultyIdStr) && InputValidator.isInteger(facultyIdStr) && Integer.parseInt(facultyIdStr) > 0) {
            session.setFacultyMemberId(Integer.parseInt(facultyIdStr));
        }
        if (!InputValidator.isNullOrEmpty(locationIdStr) && InputValidator.isInteger(locationIdStr) && Integer.parseInt(locationIdStr) > 0) {
            session.setLocationId(Integer.parseInt(locationIdStr));
        }

        lectureSessionDao.update(session);
        request.getSession().setAttribute(ApplicationConstants.SESSION_SUCCESS_MESSAGE, "Lecture Session updated successfully!");
        response.sendRedirect(request.getContextPath() + "/ManageLectureSessionsServlet?action=" + ApplicationConstants.ACTION_LIST);
    }

    private void deleteSession(HttpServletRequest request, HttpServletResponse response) throws IOException, DataAccessException {
        int id = Integer.parseInt(request.getParameter("id"));
        boolean success = lectureSessionDao.delete(id);
        if (success) {
            request.getSession().setAttribute(ApplicationConstants.SESSION_SUCCESS_MESSAGE, "Lecture Session deleted successfully!");
        } else {
            request.getSession().setAttribute(ApplicationConstants.SESSION_ERROR_MESSAGE, "Lecture Session could not be deleted or not found.");
        }
        response.sendRedirect(request.getContextPath() + "/ManageLectureSessionsServlet?action=" + ApplicationConstants.ACTION_LIST);
    }
}