package com.grpAC_SMS.controller.faculty;

import com.grpAC_SMS.dao.*;
import com.grpAC_SMS.dao.impl.*;
import com.grpAC_SMS.model.*;
import com.grpAC_SMS.exception.DataAccessException;
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
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Optional;
import java.util.stream.Collectors;


@WebServlet("/FacultyRecordGradesServlet")
public class FacultyRecordGradesServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(FacultyRecordGradesServlet.class);
    private FacultyDao facultyDao;
    private CourseDao courseDao;
    private EnrollmentDao enrollmentDao;
    private GradeDao gradeDao;
    private AcademicTermDao academicTermDao;
    private StudentDao studentDao;


    @Override
    public void init() {
        facultyDao = new FacultyDaoImpl();
        courseDao = new CourseDaoImpl();
        enrollmentDao = new EnrollmentDaoImpl();
        gradeDao = new GradeDaoImpl();
        academicTermDao = new AcademicTermDaoImpl();
        studentDao = new StudentDaoImpl();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User loggedInUser = (session != null) ? (User) session.getAttribute(ApplicationConstants.SESSION_USER) : null;

        if (loggedInUser == null || loggedInUser.getRole() != Role.FACULTY) {
            response.sendRedirect(request.getContextPath() + "/auth/login.jsp");
            return;
        }

        String action = request.getParameter("action");
        if (action == null) action = "viewCoursesForGrading";

        try {
            Faculty facultyProfile = facultyDao.findByUserId(loggedInUser.getUserId())
                    .orElseThrow(() -> new ServletException("Faculty profile not found."));

            AcademicTerm currentTerm = academicTermDao.findCurrentTerm(LocalDate.now())
                    .orElse(academicTermDao.findAll().stream().findFirst().orElse(null)); // Fallback
            request.setAttribute("currentTerm", currentTerm);


            switch (action) {
                case "viewCoursesForGrading":
                    listCoursesForGrading(request, response, facultyProfile, currentTerm);
                    break;
                case "enterGradesForm": // Show form to enter grades for a specific course/assessment
                    showEnterGradesForm(request, response, facultyProfile, currentTerm);
                    break;
                default:
                    listCoursesForGrading(request, response, facultyProfile, currentTerm);
                    break;
            }
        } catch (Exception e) {
            logger.error("Error in FacultyRecordGradesServlet GET: {}", e.getMessage(), e);
            request.setAttribute(ApplicationConstants.REQ_ATTR_ERROR_MESSAGE, "Error processing request: " + e.getMessage());
            RequestDispatcher dispatcher = request.getRequestDispatcher("/FacultyDashboardServlet"); // Redirect to dashboard on error
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
            response.sendRedirect(request.getContextPath() + "/FacultyRecordGradesServlet?action=viewCoursesForGrading");
            return;
        }

        try {
            Faculty facultyProfile = facultyDao.findByUserId(loggedInUser.getUserId())
                    .orElseThrow(() -> new ServletException("Faculty profile not found."));

            switch (action) {
                case "saveGrades":
                    saveGrades(request, response, facultyProfile);
                    break;
                default:
                    response.sendRedirect(request.getContextPath() + "/FacultyRecordGradesServlet?action=viewCoursesForGrading");
                    break;
            }
        } catch (Exception e) {
            logger.error("Error in FacultyRecordGradesServlet POST: {}", e.getMessage(), e);
            // More specific error handling and redirect back to form might be needed
            request.getSession().setAttribute(ApplicationConstants.SESSION_ERROR_MESSAGE, "Error saving grades: " + e.getMessage());
            String courseId = request.getParameter("courseId");
            String termId = request.getParameter("termId");
            String assessmentType = request.getParameter("assessmentType");
            response.sendRedirect(String.format("%s/FacultyRecordGradesServlet?action=enterGradesForm&courseId=%s&termId=%s&assessmentType=%s",
                    request.getContextPath(), courseId, termId, java.net.URLEncoder.encode(assessmentType, "UTF-8")));
        }
    }

    private void listCoursesForGrading(HttpServletRequest request, HttpServletResponse response, Faculty faculty, AcademicTerm term) throws ServletException, IOException {
        if (term != null) {
            List<Course> courses = courseDao.findCoursesByFacultyIdAndTermId(faculty.getFacultyMemberId(), term.getAcademicTermId());
            request.setAttribute("coursesForGrading", courses);
            request.setAttribute("currentTermId", term.getAcademicTermId()); // Pass term ID for links
        } else {
            request.setAttribute(ApplicationConstants.REQ_ATTR_ERROR_MESSAGE, "No active academic term found.");
        }
        RequestDispatcher dispatcher = request.getRequestDispatcher("/faculty/select_course_for_grading.jsp"); // New JSP
        dispatcher.forward(request, response);
    }

    private void showEnterGradesForm(HttpServletRequest request, HttpServletResponse response, Faculty faculty, AcademicTerm term) throws ServletException, IOException {
        String courseIdStr = request.getParameter("courseId");
        String termIdStr = request.getParameter("termId");
        String assessmentType = request.getParameter("assessmentType"); // e.g., Midterm, Final Exam

        if (InputValidator.isNullOrEmpty(courseIdStr) || !InputValidator.isInteger(courseIdStr) ||
                InputValidator.isNullOrEmpty(termIdStr) || !InputValidator.isInteger(termIdStr) ||
                InputValidator.isNullOrEmpty(assessmentType)) {
            request.setAttribute(ApplicationConstants.REQ_ATTR_ERROR_MESSAGE, "Course, Term, and Assessment Type are required.");
            listCoursesForGrading(request, response, faculty, term);
            return;
        }
        int courseId = Integer.parseInt(courseIdStr);
        int academicTermId = Integer.parseInt(termIdStr);

        Course course = courseDao.findById(courseId).orElseThrow(() -> new ServletException("Course not found."));
        AcademicTerm selectedTerm = academicTermDao.findById(academicTermId).orElseThrow(() -> new ServletException("Term not found."));

        // Get enrolled students for this course and term
        List<Enrollment> enrollments = enrollmentDao.findByCourseId(courseId).stream()
                .filter(e -> e.getAcademicTermId() == academicTermId && "ENROLLED".equals(e.getStatus()))
                .collect(Collectors.toList());

        // Fetch existing grades for these enrollments and this assessment type to pre-fill form
        Map<Integer, Grade> existingGradesMap = new HashMap<>(); // enrollmentId -> Grade
        for (Enrollment enr : enrollments) {
            gradeDao.findByEnrollmentAndAssessmentType(enr.getEnrollmentId(), assessmentType)
                    .ifPresent(grade -> existingGradesMap.put(enr.getEnrollmentId(), grade));
        }

        request.setAttribute("course", course);
        request.setAttribute("term", selectedTerm);
        request.setAttribute("assessmentType", assessmentType);
        request.setAttribute("enrollmentsForGrading", enrollments); // List of Enrollment objects with student details
        request.setAttribute("existingGradesMap", existingGradesMap);

        // List of valid grade values
        String[] validGradeValues = {"A+", "A", "A-", "B+", "B", "B-", "C+", "C", "C-", "D+", "D", "E", "I", "P"}; // I=Incomplete, P=Pass (for non-graded)
        request.setAttribute("validGradeValues", validGradeValues);

        RequestDispatcher dispatcher = request.getRequestDispatcher("/faculty/record_grades_form.jsp");
        dispatcher.forward(request, response);
    }

    private void saveGrades(HttpServletRequest request, HttpServletResponse response, Faculty faculty) throws IOException, ServletException {
        String courseIdStr = request.getParameter("courseId");
        String termIdStr = request.getParameter("termId");
        String assessmentType = request.getParameter("assessmentType");

        if (InputValidator.isNullOrEmpty(courseIdStr) || !InputValidator.isInteger(courseIdStr) ||
                InputValidator.isNullOrEmpty(termIdStr) || !InputValidator.isInteger(termIdStr) ||
                InputValidator.isNullOrEmpty(assessmentType)) {
            throw new ServletException("Missing course, term, or assessment type for saving grades.");
        }
        int courseId = Integer.parseInt(courseIdStr);
        int termId = Integer.parseInt(termIdStr);

        // Iterate over students/enrollments submitted
        String[] enrollmentIds = request.getParameterValues("enrollmentId"); // Assuming form has <input type="hidden" name="enrollmentId" value="..."> for each student

        if (enrollmentIds == null || enrollmentIds.length == 0) {
            request.getSession().setAttribute(ApplicationConstants.SESSION_ERROR_MESSAGE, "No student grades submitted.");
            response.sendRedirect(String.format("%s/FacultyRecordGradesServlet?action=enterGradesForm&courseId=%d&termId=%d&assessmentType=%s",
                    request.getContextPath(), courseId, termId, java.net.URLEncoder.encode(assessmentType, "UTF-8")));
            return;
        }

        List<String> errors = new ArrayList<>();
        List<String> successes = new ArrayList<>();

        for (String enrollmentIdStr : enrollmentIds) {
            try {
                int enrollmentId = Integer.parseInt(enrollmentIdStr);
                String gradeValue = request.getParameter("gradeValue_" + enrollmentIdStr); // e.g., gradeValue_123
                String remarks = request.getParameter("remarks_" + enrollmentIdStr);

                if (InputValidator.isNullOrEmpty(gradeValue)) { // Only process if a grade is entered
                    continue;
                }
                // Basic validation of gradeValue (can be more sophisticated)
                // For now, assume it's one of the predefined values.

                Optional<Grade> existingGradeOpt = gradeDao.findByEnrollmentAndAssessmentType(enrollmentId, assessmentType);
                Grade gradeToSave;

                if (existingGradeOpt.isPresent()) {
                    gradeToSave = existingGradeOpt.get();
                    gradeToSave.setGradeValue(gradeValue);
                    gradeToSave.setRemarks(remarks);
                    gradeToSave.setGradedByFacultyId(faculty.getFacultyMemberId());
                    // graded_date is updated by DAO
                    gradeDao.update(gradeToSave);
                    successes.add("Updated grade for enrollment ID " + enrollmentId);
                } else {
                    gradeToSave = new Grade();
                    gradeToSave.setEnrollmentId(enrollmentId);
                    gradeToSave.setGradeValue(gradeValue);
                    gradeToSave.setAssessmentType(assessmentType);
                    gradeToSave.setGradedByFacultyId(faculty.getFacultyMemberId());
                    gradeToSave.setRemarks(remarks);
                    gradeDao.add(gradeToSave);
                    successes.add("Added grade for enrollment ID " + enrollmentId);
                }
            } catch (NumberFormatException e) {
                errors.add("Invalid enrollment ID format: " + enrollmentIdStr);
            } catch (DataAccessException e) {
                errors.add("DB error for enrollment ID " + enrollmentIdStr + ": " + e.getMessage());
            }
        }

        if (!errors.isEmpty()) {
            request.getSession().setAttribute(ApplicationConstants.SESSION_ERROR_MESSAGE, "Some grades failed to save: " + String.join("; ", errors));
        }
        if(!successes.isEmpty()){
            request.getSession().setAttribute(ApplicationConstants.SESSION_SUCCESS_MESSAGE, "Grades processed: " + successes.size() + " records affected.");
        }

        response.sendRedirect(String.format("%s/FacultyRecordGradesServlet?action=enterGradesForm&courseId=%d&termId=%d&assessmentType=%s",
                request.getContextPath(), courseId, termId, java.net.URLEncoder.encode(assessmentType, "UTF-8")));

    }
}