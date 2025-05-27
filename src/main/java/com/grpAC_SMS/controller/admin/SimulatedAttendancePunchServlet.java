package com.grpAC_SMS.controller.admin;

import com.grpAC_SMS.dao.AttendanceDao;
import com.grpAC_SMS.dao.EnrollmentDao;
import com.grpAC_SMS.dao.LectureSessionDao;
import com.grpAC_SMS.dao.StudentDao;
import com.grpAC_SMS.dao.impl.AttendanceDaoImpl;
import com.grpAC_SMS.dao.impl.EnrollmentDaoImpl;
import com.grpAC_SMS.dao.impl.LectureSessionDaoImpl;
import com.grpAC_SMS.dao.impl.StudentDaoImpl;
import com.grpAC_SMS.model.Attendance;
import com.grpAC_SMS.model.LectureSession;
import com.grpAC_SMS.model.Student;
import com.grpAC_SMS.util.ApplicationConstants;
import com.grpAC_SMS.util.InputValidator;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson; // You'll need to add Gson to pom.xml if not already for JSON responses

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

@WebServlet("/SimulatedAttendancePunchServlet")
public class SimulatedAttendancePunchServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(SimulatedAttendancePunchServlet.class);
    private AttendanceDao attendanceDao;
    private StudentDao studentDao;
    private LectureSessionDao lectureSessionDao;
    private EnrollmentDao enrollmentDao;
    private Gson gson = new Gson();


    @Override
    public void init() {
        attendanceDao = new AttendanceDaoImpl();
        studentDao = new StudentDaoImpl();
        lectureSessionDao = new LectureSessionDaoImpl();
        enrollmentDao = new EnrollmentDaoImpl();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        if ("getRandomStudent".equals(action)) {
            getRandomUnmarkedStudent(request, response);
        } else {
            // Potentially list active sessions for selection on the simulator page if not already handled by JSP.
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid action for GET request.");
        }
    }

    private void getRandomUnmarkedStudent(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        Map<String, Object> jsonResponse = new HashMap<>();

        String lectureSessionIdStr = request.getParameter("lectureSessionId");
        if (InputValidator.isNullOrEmpty(lectureSessionIdStr) || !InputValidator.isInteger(lectureSessionIdStr)) {
            jsonResponse.put("error", true);
            jsonResponse.put("message", "Invalid or missing Lecture Session ID.");
            out.print(gson.toJson(jsonResponse));
            out.flush();
            return;
        }
        int lectureSessionId = Integer.parseInt(lectureSessionIdStr);

        try {
            // Fetch courseId from lectureSessionId
            Optional<LectureSession> sessionOpt = lectureSessionDao.findById(lectureSessionId);
            if (sessionOpt.isEmpty()) {
                jsonResponse.put("error", true);
                jsonResponse.put("message", "Lecture session not found.");
                out.print(gson.toJson(jsonResponse));
                out.flush();
                return;
            }
            int courseId = sessionOpt.get().getCourseId();

            List<Student> unmarkedStudents = studentDao.findUnmarkedStudentsForSession(lectureSessionId, courseId);

            if (unmarkedStudents.isEmpty()) {
                jsonResponse.put("studentId", null);
                jsonResponse.put("message", "No unmarked students found for this session or all enrolled students are marked.");
            } else {
                Random random = new Random();
                Student randomStudent = unmarkedStudents.get(random.nextInt(unmarkedStudents.size()));
                jsonResponse.put("studentId", randomStudent.getStudentUniqueId()); // Send student_unique_id
                jsonResponse.put("message", "Random student fetched.");
            }
        } catch (Exception e) {
            logger.error("Error fetching random unmarked student for session {}: {}", lectureSessionId, e.getMessage(), e);
            jsonResponse.put("error", true);
            jsonResponse.put("message", "Server error: " + e.getMessage());
        }
        out.print(gson.toJson(jsonResponse));
        out.flush();
    }


    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String studentUniqueId = request.getParameter("studentId"); // This is student_unique_id from form
        String lectureSessionIdStr = request.getParameter("lectureSessionId");
        String deviceId = ApplicationConstants.DEFAULT_NFC_DEVICE_ID; // Hardcoded as per requirement

        request.getSession().removeAttribute("nfcStatusMessage"); // Clear old message
        request.getSession().removeAttribute("nfcMessageType");

        if (InputValidator.isNullOrEmpty(studentUniqueId) ||
                InputValidator.isNullOrEmpty(lectureSessionIdStr) || !InputValidator.isInteger(lectureSessionIdStr)) {

            request.getSession().setAttribute("nfcStatusMessage", "Error: Student ID and Lecture Session ID are required.");
            request.getSession().setAttribute("nfcMessageType", "error");
            response.sendRedirect(request.getContextPath() + "/admin/nfc_attendance_simulator.jsp?lsid=" + lectureSessionIdStr);
            return;
        }

        int lectureSessionId = Integer.parseInt(lectureSessionIdStr);

        try {
            Optional<Student> studentOpt = studentDao.findByStudentUniqueId(studentUniqueId);
            if (studentOpt.isEmpty()) {
                throw new Exception("Student with Unique ID '" + studentUniqueId + "' not found.");
            }
            Student student = studentOpt.get();

            Optional<LectureSession> sessionOpt = lectureSessionDao.findById(lectureSessionId);
            if (sessionOpt.isEmpty()) {
                throw new Exception("Lecture Session ID '" + lectureSessionId + "' not found.");
            }
            LectureSession lectureSession = sessionOpt.get();

            // Verify student is enrolled in the course of this lecture session
            if (!enrollmentDao.isStudentEnrolledInCourseForTerm(student.getStudentId(), lectureSession.getCourseId(), lectureSession.getAcademicTermId())) {
                throw new Exception("Student " + studentUniqueId + " is not enrolled in course " + lectureSession.getCourseName() + " for this term.");
            }


            // Check if attendance already marked for this student and session
            Optional<Attendance> existingAttendance = attendanceDao.findByStudentAndSession(student.getStudentId(), lectureSessionId);
            if (existingAttendance.isPresent() && existingAttendance.get().isPresent()) {
                request.getSession().setAttribute("nfcStatusMessage", "Attendance already marked as PRESENT for Student " + studentUniqueId + " in session " + lectureSessionId + ".");
                request.getSession().setAttribute("nfcMessageType", "info");
            } else {
                Attendance attendance = new Attendance();
                attendance.setStudentId(student.getStudentId());
                attendance.setLectureSessionId(lectureSessionId);
                attendance.setPunchInTimestamp(Timestamp.valueOf(LocalDateTime.now()));
                attendance.setPresent(true);
                attendance.setDeviceId(deviceId);
                // recorded_by_faculty_id will be null for NFC simulation

                if(existingAttendance.isPresent()){ // Update if exists but was absent or some other state
                    attendance.setAttendanceId(existingAttendance.get().getAttendanceId());
                    attendanceDao.update(attendance);
                } else {
                    attendanceDao.add(attendance);
                }
                logger.info("NFC Simulated Attendance: Student {} marked PRESENT for session {} by device {}", studentUniqueId, lectureSessionId, deviceId);
                request.getSession().setAttribute("nfcStatusMessage", "Success: Student " + studentUniqueId + " marked PRESENT for session " + lectureSessionId + ".");
                request.getSession().setAttribute("nfcMessageType", "success");
            }

        } catch (Exception e) {
            logger.error("Error marking NFC attendance: {}", e.getMessage(), e);
            request.getSession().setAttribute("nfcStatusMessage", "Error: " + e.getMessage());
            request.getSession().setAttribute("nfcMessageType", "error");
        }
        // Redirect back to the simulator page, preserving selected session if possible
        String redirectParams = "";
        if (!InputValidator.isNullOrEmpty(lectureSessionIdStr)) {
            redirectParams = "?selectedLectureSessionId=" + lectureSessionIdStr;
            if (!InputValidator.isNullOrEmpty(request.getParameter("courseName"))) {
                redirectParams += "&selectedCourseName=" + java.net.URLEncoder.encode(request.getParameter("courseName"), "UTF-8");
            }
        }
        response.sendRedirect(request.getContextPath() + "/admin/nfc_attendance_simulator.jsp" + redirectParams);
    }
}
