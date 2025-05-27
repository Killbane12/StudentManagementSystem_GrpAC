package com.grpAC_SMS.controller.admin;

import com.grpAC_SMS.dao.CourseDao;
import com.grpAC_SMS.dao.DepartmentDao;
import com.grpAC_SMS.dao.ProgramDao;
import com.grpAC_SMS.dao.impl.CourseDaoImpl;
import com.grpAC_SMS.dao.impl.DepartmentDaoImpl;
import com.grpAC_SMS.dao.impl.ProgramDaoImpl;
import com.grpAC_SMS.model.Course;
import com.grpAC_SMS.model.Department;
import com.grpAC_SMS.model.Program;
import com.grpAC_SMS.exception.DataAccessException;
import com.grpAC_SMS.util.ApplicationConstants;
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

@WebServlet("/ManageCoursesServlet")
public class ManageCoursesServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(ManageCoursesServlet.class);
    private CourseDao courseDao;
    private ProgramDao programDao;
    private DepartmentDao departmentDao;

    @Override
    public void init() {
        courseDao = new CourseDaoImpl();
        programDao = new ProgramDaoImpl();
        departmentDao = new DepartmentDaoImpl();
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
                    deleteCourse(request, response);
                    break;
                case ApplicationConstants.ACTION_LIST:
                default:
                    listCourses(request, response);
                    break;
            }
        } catch (DataAccessException e) {
            logger.error("DataAccessException in Courses GET: {}", e.getMessage());
            request.getSession().setAttribute(ApplicationConstants.SESSION_ERROR_MESSAGE, "Database operation failed: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/ManageCoursesServlet?action=" + ApplicationConstants.ACTION_LIST);
        } catch (NumberFormatException e) {
            logger.error("NumberFormatException in Courses GET: {}", e.getMessage());
            request.getSession().setAttribute(ApplicationConstants.SESSION_ERROR_MESSAGE, "Invalid ID format.");
            response.sendRedirect(request.getContextPath() + "/ManageCoursesServlet?action=" + ApplicationConstants.ACTION_LIST);
        } catch (Exception e) {
            logger.error("General Exception in Courses GET: {}", e.getMessage(), e);
            request.getSession().setAttribute(ApplicationConstants.SESSION_ERROR_MESSAGE, "An unexpected error occurred: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/ManageCoursesServlet?action=" + ApplicationConstants.ACTION_LIST);
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        if (action == null) {
            response.sendRedirect(request.getContextPath() + "/ManageCoursesServlet?action=" + ApplicationConstants.ACTION_LIST);
            return;
        }
        try {
            switch (action) {
                case ApplicationConstants.ACTION_CREATE:
                    createCourse(request, response);
                    break;
                case ApplicationConstants.ACTION_UPDATE:
                    updateCourse(request, response);
                    break;
                default:
                    listCourses(request, response);
                    break;
            }
        } catch (DataAccessException e) {
            logger.error("DataAccessException in Courses POST for action {}: {}", action, e.getMessage());
            request.setAttribute(ApplicationConstants.REQ_ATTR_ERROR_MESSAGE, "Operation failed: " + e.getMessage());
            preserveFormDataOnError(request, action);
            if (ApplicationConstants.ACTION_CREATE.equals(action)) {
                showNewForm(request, response);
            } else if (ApplicationConstants.ACTION_UPDATE.equals(action)) {
                showEditForm(request, response);
            } else {
                response.sendRedirect(request.getContextPath() + "/ManageCoursesServlet?action=" + ApplicationConstants.ACTION_LIST);
            }
        } catch (NumberFormatException e) {
            logger.error("NumberFormatException in Courses POST for action {}: {}", action, e.getMessage());
            request.setAttribute(ApplicationConstants.REQ_ATTR_ERROR_MESSAGE, "Invalid numeric input.");
            preserveFormDataOnError(request, action);
            if (ApplicationConstants.ACTION_CREATE.equals(action)) {
                showNewForm(request, response);
            } else if (ApplicationConstants.ACTION_UPDATE.equals(action)) {
                showEditForm(request, response);
            } else {
                response.sendRedirect(request.getContextPath() + "/ManageCoursesServlet?action=" + ApplicationConstants.ACTION_LIST);
            }
        }
        catch (Exception e) {
            logger.error("General Exception in Courses POST for action {}: {}",action, e.getMessage(), e);
            request.getSession().setAttribute(ApplicationConstants.SESSION_ERROR_MESSAGE, "An unexpected error occurred: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/ManageCoursesServlet?action=" + ApplicationConstants.ACTION_LIST);
        }
    }

    private void preserveFormDataOnError(HttpServletRequest request, String action) {
        Course course = new Course();
        course.setCourseCode(request.getParameter("courseCode"));
        course.setCourseName(request.getParameter("courseName"));
        course.setDescription(request.getParameter("description"));

        String creditsStr = request.getParameter("credits");
        if (!InputValidator.isNullOrEmpty(creditsStr) && InputValidator.isInteger(creditsStr)) {
            course.setCredits(Integer.parseInt(creditsStr));
        }
        String programIdStr = request.getParameter("programId");
        if (!InputValidator.isNullOrEmpty(programIdStr) && InputValidator.isInteger(programIdStr)) {
            if(Integer.parseInt(programIdStr) > 0) course.setProgramId(Integer.parseInt(programIdStr));
        }
        String deptIdStr = request.getParameter("departmentId");
        if (!InputValidator.isNullOrEmpty(deptIdStr) && InputValidator.isInteger(deptIdStr)) {
            course.setDepartmentId(Integer.parseInt(deptIdStr));
        }

        if (ApplicationConstants.ACTION_UPDATE.equals(action) && request.getParameter("courseId") != null) {
            try {
                course.setCourseId(Integer.parseInt(request.getParameter("courseId")));
            } catch (NumberFormatException e) { /* ignore */ }
        }
        request.setAttribute("course", course);

        // Re-fetch dropdown data
        if(request.getAttribute("programList") == null) request.setAttribute("programList", programDao.findAll());
        if(request.getAttribute("departmentList") == null) request.setAttribute("departmentList", departmentDao.findAll());
    }

    private void listCourses(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<Course> courseList = courseDao.findAllWithDetails();
        request.setAttribute("courseList", courseList);
        RequestDispatcher dispatcher = request.getRequestDispatcher("/admin/courses_list.jsp");
        dispatcher.forward(request, response);
    }

    private void showNewForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if(request.getAttribute("course") == null) request.setAttribute("course", new Course());
        if(request.getAttribute("programList") == null) request.setAttribute("programList", programDao.findAll());
        if(request.getAttribute("departmentList") == null) request.setAttribute("departmentList", departmentDao.findAll());
        RequestDispatcher dispatcher = request.getRequestDispatcher("/admin/course_form.jsp");
        dispatcher.forward(request, response);
    }

    private void showEditForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if(request.getAttribute("course") == null) {
            int id = Integer.parseInt(request.getParameter("id"));
            Course existingCourse = courseDao.findById(id)
                    .orElseThrow(() -> new DataAccessException("Course not found with ID: " + id));
            request.setAttribute("course", existingCourse);
        }
        if(request.getAttribute("programList") == null) request.setAttribute("programList", programDao.findAll());
        if(request.getAttribute("departmentList") == null) request.setAttribute("departmentList", departmentDao.findAll());
        RequestDispatcher dispatcher = request.getRequestDispatcher("/admin/course_form.jsp");
        dispatcher.forward(request, response);
    }

    private void createCourse(HttpServletRequest request, HttpServletResponse response) throws IOException, DataAccessException, ServletException {
        String courseCode = request.getParameter("courseCode");
        String courseName = request.getParameter("courseName");
        String description = request.getParameter("description");
        String creditsStr = request.getParameter("credits");
        String programIdStr = request.getParameter("programId");
        String departmentIdStr = request.getParameter("departmentId");

        if (InputValidator.isNullOrEmpty(courseCode) || InputValidator.isNullOrEmpty(courseName) || InputValidator.isNullOrEmpty(departmentIdStr)) {
            request.setAttribute(ApplicationConstants.REQ_ATTR_ERROR_MESSAGE, "Course Code, Name, and Department are required.");
            preserveFormDataOnError(request, ApplicationConstants.ACTION_CREATE);
            showNewForm(request, response);
            return;
        }
        if (!InputValidator.isInteger(departmentIdStr)) {
            request.setAttribute(ApplicationConstants.REQ_ATTR_ERROR_MESSAGE, "Invalid Department ID.");
            preserveFormDataOnError(request, ApplicationConstants.ACTION_CREATE);
            showNewForm(request, response);
            return;
        }
        int departmentId = Integer.parseInt(departmentIdStr);

        Integer credits = null;
        if (!InputValidator.isNullOrEmpty(creditsStr)) {
            if (!InputValidator.isInteger(creditsStr) || Integer.parseInt(creditsStr) < 0) {
                request.setAttribute(ApplicationConstants.REQ_ATTR_ERROR_MESSAGE, "Credits must be a non-negative number.");
                preserveFormDataOnError(request, ApplicationConstants.ACTION_CREATE);
                showNewForm(request, response);
                return;
            }
            credits = Integer.parseInt(creditsStr);
        }

        Integer programId = null;
        if (!InputValidator.isNullOrEmpty(programIdStr) && !programIdStr.equals("0")) { // "0" or "" for no specific program
            if (!InputValidator.isInteger(programIdStr)) {
                request.setAttribute(ApplicationConstants.REQ_ATTR_ERROR_MESSAGE, "Invalid Program ID.");
                preserveFormDataOnError(request, ApplicationConstants.ACTION_CREATE);
                showNewForm(request, response);
                return;
            }
            programId = Integer.parseInt(programIdStr);
        }

        Course course = new Course();
        course.setCourseCode(courseCode);
        course.setCourseName(courseName);
        course.setDescription(description);
        course.setCredits(credits);
        course.setProgramId(programId);
        course.setDepartmentId(departmentId);

        courseDao.add(course);
        request.getSession().setAttribute(ApplicationConstants.SESSION_SUCCESS_MESSAGE, "Course created successfully!");
        response.sendRedirect(request.getContextPath() + "/ManageCoursesServlet?action=" + ApplicationConstants.ACTION_LIST);
    }

    private void updateCourse(HttpServletRequest request, HttpServletResponse response) throws IOException, DataAccessException, ServletException {
        int id = Integer.parseInt(request.getParameter("courseId"));
        String courseCode = request.getParameter("courseCode");
        String courseName = request.getParameter("courseName");
        String description = request.getParameter("description");
        String creditsStr = request.getParameter("credits");
        String programIdStr = request.getParameter("programId");
        String departmentIdStr = request.getParameter("departmentId");

        if (InputValidator.isNullOrEmpty(courseCode) || InputValidator.isNullOrEmpty(courseName) || InputValidator.isNullOrEmpty(departmentIdStr)) {
            request.setAttribute(ApplicationConstants.REQ_ATTR_ERROR_MESSAGE, "Course Code, Name, and Department are required.");
            preserveFormDataOnError(request, ApplicationConstants.ACTION_UPDATE);
            showEditForm(request, response);
            return;
        }
        if (!InputValidator.isInteger(departmentIdStr)) {
            request.setAttribute(ApplicationConstants.REQ_ATTR_ERROR_MESSAGE, "Invalid Department ID.");
            preserveFormDataOnError(request, ApplicationConstants.ACTION_UPDATE);
            showEditForm(request, response);
            return;
        }
        int departmentId = Integer.parseInt(departmentIdStr);

        Integer credits = null;
        if (!InputValidator.isNullOrEmpty(creditsStr)) {
            if (!InputValidator.isInteger(creditsStr) || Integer.parseInt(creditsStr) < 0) {
                request.setAttribute(ApplicationConstants.REQ_ATTR_ERROR_MESSAGE, "Credits must be a non-negative number.");
                preserveFormDataOnError(request, ApplicationConstants.ACTION_UPDATE);
                showEditForm(request, response);
                return;
            }
            credits = Integer.parseInt(creditsStr);
        }

        Integer programId = null;
        if (!InputValidator.isNullOrEmpty(programIdStr) && !programIdStr.equals("0")) {
            if (!InputValidator.isInteger(programIdStr)) {
                request.setAttribute(ApplicationConstants.REQ_ATTR_ERROR_MESSAGE, "Invalid Program ID.");
                preserveFormDataOnError(request, ApplicationConstants.ACTION_UPDATE);
                showEditForm(request, response);
                return;
            }
            programId = Integer.parseInt(programIdStr);
        }

        Course course = courseDao.findById(id).orElseThrow(() -> new DataAccessException("Course not found for update."));
        course.setCourseCode(courseCode);
        course.setCourseName(courseName);
        course.setDescription(description);
        course.setCredits(credits);
        course.setProgramId(programId);
        course.setDepartmentId(departmentId);

        courseDao.update(course);
        request.getSession().setAttribute(ApplicationConstants.SESSION_SUCCESS_MESSAGE, "Course updated successfully!");
        response.sendRedirect(request.getContextPath() + "/ManageCoursesServlet?action=" + ApplicationConstants.ACTION_LIST);
    }

    private void deleteCourse(HttpServletRequest request, HttpServletResponse response) throws IOException, DataAccessException {
        int id = Integer.parseInt(request.getParameter("id"));
        boolean success = courseDao.delete(id);
        if (success) {
            request.getSession().setAttribute(ApplicationConstants.SESSION_SUCCESS_MESSAGE, "Course deleted successfully!");
        } else {
            request.getSession().setAttribute(ApplicationConstants.SESSION_ERROR_MESSAGE, "Course could not be deleted or not found.");
        }
        response.sendRedirect(request.getContextPath() + "/ManageCoursesServlet?action=" + ApplicationConstants.ACTION_LIST);
    }
}