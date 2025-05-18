package com.grpAC_SMS.controller.student;

import com.grpAC_SMS.dao.*;
import com.grpAC_SMS.dao.impl.*;
import com.grpAC_SMS.model.*;
import com.grpAC_SMS.service.AttendanceService;
import com.grpAC_SMS.service.impl.AttendanceServiceImpl;
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
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Optional;
import java.util.stream.Collectors;


@WebServlet("/StudentViewAttendanceServlet")
public class StudentViewAttendanceServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(StudentViewAttendanceServlet.class);
    private StudentDao studentDao;
    private AttendanceDao attendanceDao;
    private EnrollmentDao enrollmentDao;
    private CourseDao courseDao;
    private AcademicTermDao academicTermDao;
    private LectureSessionDao lectureSessionDao; // Added declaration
    private AttendanceService attendanceService;


    // Helper DTO class for JSP display (remains the same)
    public static class CourseAttendanceView {
        private double percentage;
        private List<Attendance> detailedRecords;
        private Course course; // Now includes term name for display

        public CourseAttendanceView(Course course, double percentage, List<Attendance> detailedRecords) {
            this.course = course;
            this.percentage = percentage;
            this.detailedRecords = detailedRecords;
        }
        public Course getCourse() { return course; }
        public double getPercentage() { return percentage; }
        public List<Attendance> getDetailedRecords() { return detailedRecords; }
    }


    @Override
    public void init() {
        studentDao = new StudentDaoImpl();
        attendanceDao = new AttendanceDaoImpl();
        enrollmentDao = new EnrollmentDaoImpl();
        courseDao = new CourseDaoImpl();
        academicTermDao = new AcademicTermDaoImpl();
        lectureSessionDao = new LectureSessionDaoImpl(); // Added initialization
        attendanceService = new AttendanceServiceImpl();
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
                RequestDispatcher dispatcher = request.getRequestDispatcher("/student/my_attendance.jsp");
                dispatcher.forward(request, response);
                return;
            }
            Student student = studentOpt.get();

            String courseIdStr = request.getParameter("courseId");
            List<CourseAttendanceView> attendanceViews = new ArrayList<>();

            List<Enrollment> allEnrollments = enrollmentDao.findByStudentId(student.getStudentId());

            Map<Integer, Course> studentCoursesMap = new HashMap<>();
            for (Enrollment enr : allEnrollments) {
                if (!studentCoursesMap.containsKey(enr.getCourseId())) {
                    // Fetch course with details (program name, department name)
                    courseDao.findById(enr.getCourseId()).ifPresent(c -> studentCoursesMap.put(c.getCourseId(), c));
                }
            }

            if (!InputValidator.isNullOrEmpty(courseIdStr) && InputValidator.isInteger(courseIdStr)) {
                int courseId = Integer.parseInt(courseIdStr);
                Course specificCourse = studentCoursesMap.get(courseId);
                if (specificCourse != null) {
                    List<AcademicTerm> termsForCourse = allEnrollments.stream()
                            .filter(e -> e.getCourseId() == courseId)
                            .map(e -> academicTermDao.findById(e.getAcademicTermId()).orElse(null))
                            .filter(t -> t != null)
                            .distinct()
                            .sorted((t1, t2) -> t2.getStartDate().compareTo(t1.getStartDate())) // Sort terms, latest first
                            .collect(Collectors.toList());

                    for (AcademicTerm term : termsForCourse) {
                        double percentage = attendanceService.getAttendancePercentageForStudentCourses(
                                student.getStudentId(), List.of(courseId), term.getAcademicTermId()
                        ).getOrDefault(courseId, 0.0);

                        List<Attendance> detailedRecords = attendanceDao.findByStudentAndCourse(student.getStudentId(), courseId)
                                .stream().filter(att -> {
                                    Optional<LectureSession> lsOpt = lectureSessionDao.findById(att.getLectureSessionId());
                                    return lsOpt.isPresent() && lsOpt.get().getAcademicTermId() == term.getAcademicTermId();
                                })
                                .sorted((a1, a2) -> { // Sort attendance records by session start time
                                    Timestamp ts1 = a1.getSessionStartDateTime(); // Assuming Attendance model has this after join
                                    Timestamp ts2 = a2.getSessionStartDateTime();
                                    if (ts1 == null && ts2 == null) return 0;
                                    if (ts1 == null) return 1; // nulls last
                                    if (ts2 == null) return -1;
                                    return ts1.compareTo(ts2);
                                })
                                .collect(Collectors.toList());

                        Course courseInTerm = new Course();
                        courseInTerm.setCourseId(specificCourse.getCourseId());
                        courseInTerm.setCourseCode(specificCourse.getCourseCode());
                        // Include the term name in the course name for display context
                        courseInTerm.setCourseName(specificCourse.getCourseName() + " (" + term.getTermName() + ")");

                        attendanceViews.add(new CourseAttendanceView(courseInTerm, percentage, detailedRecords));
                    }
                } else {
                    request.setAttribute("errorMessage", "Attendance for specified course not found or you are not enrolled.");
                }
            } else {
                for (Course course : studentCoursesMap.values()) {
                    List<AcademicTerm> termsForCourse = allEnrollments.stream()
                            .filter(e -> e.getCourseId() == course.getCourseId())
                            .map(e -> academicTermDao.findById(e.getAcademicTermId()).orElse(null))
                            .filter(t -> t != null)
                            .distinct()
                            .sorted((t1, t2) -> t2.getStartDate().compareTo(t1.getStartDate())) // Sort terms
                            .collect(Collectors.toList());

                    for (AcademicTerm term : termsForCourse) {
                        double percentage = attendanceService.getAttendancePercentageForStudentCourses(
                                student.getStudentId(), List.of(course.getCourseId()), term.getAcademicTermId()
                        ).getOrDefault(course.getCourseId(), 0.0);

                        List<Attendance> detailedRecords = attendanceDao.findByStudentAndCourse(student.getStudentId(), course.getCourseId())
                                .stream().filter(att -> {
                                    Optional<LectureSession> lsOpt = lectureSessionDao.findById(att.getLectureSessionId()); // lectureSessionDao is now initialized
                                    return lsOpt.isPresent() && lsOpt.get().getAcademicTermId() == term.getAcademicTermId();
                                })
                                .sorted((a1, a2) -> {
                                    Timestamp ts1 = a1.getSessionStartDateTime();
                                    Timestamp ts2 = a2.getSessionStartDateTime();
                                    if (ts1 == null && ts2 == null) return 0;
                                    if (ts1 == null) return 1;
                                    if (ts2 == null) return -1;
                                    return ts1.compareTo(ts2);
                                })
                                .collect(Collectors.toList());

                        Course courseInTerm = new Course();
                        courseInTerm.setCourseId(course.getCourseId());
                        courseInTerm.setCourseCode(course.getCourseCode());
                        courseInTerm.setCourseName(course.getCourseName() + " (" + term.getTermName() + ")");
                        attendanceViews.add(new CourseAttendanceView(courseInTerm, percentage, detailedRecords));
                    }
                }
            }

            request.setAttribute("attendanceByCourseViews", attendanceViews);

        } catch (Exception e) {
            logger.error("Error fetching attendance for student {}: {}", loggedInUser.getUsername(), e.getMessage(), e);
            request.setAttribute("errorMessage", "Could not load your attendance records: " + e.getMessage());
        }
        RequestDispatcher dispatcher = request.getRequestDispatcher("/student/my_attendance.jsp");
        dispatcher.forward(request, response);
    }
}