package com.grpAC_SMS.controller.admin;

import com.grpAC_SMS.dao.AcademicTermDao;
import com.grpAC_SMS.dao.CourseDao;
import com.grpAC_SMS.dao.EnrollmentDao;
import com.grpAC_SMS.dao.StudentDao;
import com.grpAC_SMS.dao.impl.AcademicTermDaoImpl;
import com.grpAC_SMS.dao.impl.CourseDaoImpl;
import com.grpAC_SMS.dao.impl.EnrollmentDaoImpl;
import com.grpAC_SMS.dao.impl.StudentDaoImpl;
import com.grpAC_SMS.exception.DataAccessException;
import com.grpAC_SMS.model.Enrollment;
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
import java.util.List;

@WebServlet("/ManageEnrollmentsServlet")
public class ManageEnrollmentsServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(ManageEnrollmentsServlet.class);
    private EnrollmentDao enrollmentDao;
    private StudentDao studentDao;
    private CourseDao courseDao;
    private AcademicTermDao academicTermDao;

    @Override
    public void init() {
        enrollmentDao = new EnrollmentDaoImpl();
        studentDao = new StudentDaoImpl();
        courseDao = new CourseDaoImpl();
        academicTermDao = new AcademicTermDaoImpl();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        if (action == null) action = ApplicationConstants.ACTION_LIST;

        try {
            switch (action) {
                case ApplicationConstants.ACTION_ADD:
                    showNewForm(request, response);
                    break;
                case ApplicationConstants.ACTION_EDIT: // Editing enrollment might mean changing status or date
                    showEditForm(request, response);
                    break;
                case ApplicationConstants.ACTION_DELETE:
                    deleteEnrollment(request, response);
                    break;
                case ApplicationConstants.ACTION_LIST:
                default:
                    listEnrollments(request, response);
                    break;
            }
        } catch (DataAccessException e) {
            logger.error("DataAccessException in Enrollments GET: {}", e.getMessage());
            request.getSession().setAttribute(ApplicationConstants.SESSION_ERROR_MESSAGE, "Database operation failed: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/ManageEnrollmentsServlet?action=" + ApplicationConstants.ACTION_LIST);
        } catch (NumberFormatException e) {
            logger.error("NumberFormatException in Enrollments GET: {}", e.getMessage());
            request.getSession().setAttribute(ApplicationConstants.SESSION_ERROR_MESSAGE, "Invalid ID format.");
            response.sendRedirect(request.getContextPath() + "/ManageEnrollmentsServlet?action=" + ApplicationConstants.ACTION_LIST);
        } catch (Exception e) {
            logger.error("General Exception in Enrollments GET: {}", e.getMessage(), e);
            request.getSession().setAttribute(ApplicationConstants.SESSION_ERROR_MESSAGE, "An unexpected error occurred: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/ManageEnrollmentsServlet?action=" + ApplicationConstants.ACTION_LIST);
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        if (action == null) {
            response.sendRedirect(request.getContextPath() + "/ManageEnrollmentsServlet?action=" + ApplicationConstants.ACTION_LIST);
            return;
        }

        try {
            switch (action) {
                case ApplicationConstants.ACTION_CREATE:
                    createEnrollment(request, response);
                    break;
                case ApplicationConstants.ACTION_UPDATE:
                    updateEnrollment(request, response);
                    break;
                default:
                    listEnrollments(request, response);
                    break;
            }
        } catch (DataAccessException e) {
            logger.error("DataAccessException in Enrollments POST for action {}: {}", action, e.getMessage());
            request.setAttribute(ApplicationConstants.REQ_ATTR_ERROR_MESSAGE, "Operation failed: " + e.getMessage());
            preserveFormDataOnError(request, action);
            if (ApplicationConstants.ACTION_CREATE.equals(action)) {
                showNewForm(request, response);
            } else if (ApplicationConstants.ACTION_UPDATE.equals(action)) {
                showEditForm(request, response);
            } else {
                response.sendRedirect(request.getContextPath() + "/ManageEnrollmentsServlet?action=" + ApplicationConstants.ACTION_LIST);
            }
        } catch (Exception e) {
            logger.error("General Exception in Enrollments POST for action {}: {}", action, e.getMessage(), e);
            request.getSession().setAttribute(ApplicationConstants.SESSION_ERROR_MESSAGE, "An unexpected error occurred: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/ManageEnrollmentsServlet?action=" + ApplicationConstants.ACTION_LIST);
        }
    }

    private void loadDropdownData(HttpServletRequest request) {
        if (request.getAttribute("studentList") == null) request.setAttribute("studentList", studentDao.findAll());
        if (request.getAttribute("courseList") == null) request.setAttribute("courseList", courseDao.findAll());
        if (request.getAttribute("termList") == null) request.setAttribute("termList", academicTermDao.findAll());
        String[] statuses = {"ENROLLED", "COMPLETED", "DROPPED"};
        if (request.getAttribute("statusList") == null) request.setAttribute("statusList", statuses);
    }

    private void preserveFormDataOnError(HttpServletRequest request, String action) {
        Enrollment enrollment = new Enrollment();
        if (ApplicationConstants.ACTION_UPDATE.equals(action) && request.getParameter("enrollmentId") != null) {
            try {
                enrollment.setEnrollmentId(Integer.parseInt(request.getParameter("enrollmentId")));
            } catch (NumberFormatException e) { /* ignore */ }
        }
        String studentIdStr = request.getParameter("studentId");
        if (!InputValidator.isNullOrEmpty(studentIdStr) && InputValidator.isInteger(studentIdStr))
            enrollment.setStudentId(Integer.parseInt(studentIdStr));

        String courseIdStr = request.getParameter("courseId");
        if (!InputValidator.isNullOrEmpty(courseIdStr) && InputValidator.isInteger(courseIdStr))
            enrollment.setCourseId(Integer.parseInt(courseIdStr));

        String termIdStr = request.getParameter("academicTermId");
        if (!InputValidator.isNullOrEmpty(termIdStr) && InputValidator.isInteger(termIdStr))
            enrollment.setAcademicTermId(Integer.parseInt(termIdStr));

        if (InputValidator.isValidDate(request.getParameter("enrollmentDate")))
            enrollment.setEnrollmentDate(DateFormatter.stringToSqlDate(request.getParameter("enrollmentDate")));
        enrollment.setStatus(request.getParameter("status"));

        request.setAttribute("enrollment", enrollment);
        loadDropdownData(request);
    }

    private void listEnrollments(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<Enrollment> enrollmentList = enrollmentDao.findAllWithDetails();
        request.setAttribute("enrollmentList", enrollmentList);
        RequestDispatcher dispatcher = request.getRequestDispatcher("/admin/enrollments_list.jsp");
        dispatcher.forward(request, response);
    }

    private void showNewForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (request.getAttribute("enrollment") == null) request.setAttribute("enrollment", new Enrollment());
        loadDropdownData(request);
        RequestDispatcher dispatcher = request.getRequestDispatcher("/admin/enrollment_form.jsp");
        dispatcher.forward(request, response);
    }

    private void showEditForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (request.getAttribute("enrollment") == null) {
            int id = Integer.parseInt(request.getParameter("id"));
            Enrollment existingEnrollment = enrollmentDao.findById(id)
                    .orElseThrow(() -> new DataAccessException("Enrollment not found with ID: " + id));
            request.setAttribute("enrollment", existingEnrollment);
        }
        loadDropdownData(request);
        RequestDispatcher dispatcher = request.getRequestDispatcher("/admin/enrollment_form.jsp");
        dispatcher.forward(request, response);
    }

    private void createEnrollment(HttpServletRequest request, HttpServletResponse response) throws IOException, DataAccessException, ServletException {
        String studentIdStr = request.getParameter("studentId");
        String courseIdStr = request.getParameter("courseId");
        String termIdStr = request.getParameter("academicTermId");
        String enrollmentDateStr = request.getParameter("enrollmentDate");
        String status = request.getParameter("status");

        if (InputValidator.isNullOrEmpty(studentIdStr) || !InputValidator.isInteger(studentIdStr) ||
                InputValidator.isNullOrEmpty(courseIdStr) || !InputValidator.isInteger(courseIdStr) ||
                InputValidator.isNullOrEmpty(termIdStr) || !InputValidator.isInteger(termIdStr) ||
                !InputValidator.isValidDate(enrollmentDateStr) || InputValidator.isNullOrEmpty(status)) {
            request.setAttribute(ApplicationConstants.REQ_ATTR_ERROR_MESSAGE, "Student, Course, Term, valid Enrollment Date, and Status are required.");
            preserveFormDataOnError(request, ApplicationConstants.ACTION_CREATE);
            showNewForm(request, response);
            return;
        }

        Enrollment enrollment = new Enrollment();
        enrollment.setStudentId(Integer.parseInt(studentIdStr));
        enrollment.setCourseId(Integer.parseInt(courseIdStr));
        enrollment.setAcademicTermId(Integer.parseInt(termIdStr));
        enrollment.setEnrollmentDate(DateFormatter.stringToSqlDate(enrollmentDateStr));
        enrollment.setStatus(status);

        enrollmentDao.add(enrollment);
        request.getSession().setAttribute(ApplicationConstants.SESSION_SUCCESS_MESSAGE, "Enrollment created successfully!");
        response.sendRedirect(request.getContextPath() + "/ManageEnrollmentsServlet?action=" + ApplicationConstants.ACTION_LIST);
    }

    private void updateEnrollment(HttpServletRequest request, HttpServletResponse response) throws IOException, DataAccessException, ServletException {
        int id = Integer.parseInt(request.getParameter("enrollmentId"));
        String studentIdStr = request.getParameter("studentId"); // Usually not changed, but can be for correction
        String courseIdStr = request.getParameter("courseId");
        String termIdStr = request.getParameter("academicTermId");
        String enrollmentDateStr = request.getParameter("enrollmentDate");
        String status = request.getParameter("status");

        if (InputValidator.isNullOrEmpty(studentIdStr) || !InputValidator.isInteger(studentIdStr) ||
                InputValidator.isNullOrEmpty(courseIdStr) || !InputValidator.isInteger(courseIdStr) ||
                InputValidator.isNullOrEmpty(termIdStr) || !InputValidator.isInteger(termIdStr) ||
                !InputValidator.isValidDate(enrollmentDateStr) || InputValidator.isNullOrEmpty(status)) {
            request.setAttribute(ApplicationConstants.REQ_ATTR_ERROR_MESSAGE, "Student, Course, Term, valid Enrollment Date, and Status are required.");
            preserveFormDataOnError(request, ApplicationConstants.ACTION_UPDATE);
            showEditForm(request, response);
            return;
        }

        Enrollment enrollment = enrollmentDao.findById(id).orElseThrow(() -> new DataAccessException("Enrollment not found for update."));
        // Update fields (be careful about changing student/course/term for an existing enrollment ID, usually only status/date is changed)
        enrollment.setStudentId(Integer.parseInt(studentIdStr));
        enrollment.setCourseId(Integer.parseInt(courseIdStr));
        enrollment.setAcademicTermId(Integer.parseInt(termIdStr));
        enrollment.setEnrollmentDate(DateFormatter.stringToSqlDate(enrollmentDateStr));
        enrollment.setStatus(status);

        enrollmentDao.update(enrollment);
        request.getSession().setAttribute(ApplicationConstants.SESSION_SUCCESS_MESSAGE, "Enrollment updated successfully!");
        response.sendRedirect(request.getContextPath() + "/ManageEnrollmentsServlet?action=" + ApplicationConstants.ACTION_LIST);
    }

    private void deleteEnrollment(HttpServletRequest request, HttpServletResponse response) throws IOException, DataAccessException {
        int id = Integer.parseInt(request.getParameter("id"));
        boolean success = enrollmentDao.delete(id);
        if (success) {
            request.getSession().setAttribute(ApplicationConstants.SESSION_SUCCESS_MESSAGE, "Enrollment deleted successfully!");
        } else {
            request.getSession().setAttribute(ApplicationConstants.SESSION_ERROR_MESSAGE, "Enrollment could not be deleted or not found.");
        }
        response.sendRedirect(request.getContextPath() + "/ManageEnrollmentsServlet?action=" + ApplicationConstants.ACTION_LIST);
    }
}