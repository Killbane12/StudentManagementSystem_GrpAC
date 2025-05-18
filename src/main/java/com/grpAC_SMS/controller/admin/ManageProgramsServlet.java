package com.grpAC_SMS.controller.admin;

import com.grpAC_SMS.dao.DepartmentDao;
import com.grpAC_SMS.dao.ProgramDao;
import com.grpAC_SMS.dao.impl.DepartmentDaoImpl;
import com.grpAC_SMS.dao.impl.ProgramDaoImpl;
import com.grpAC_SMS.exception.DataAccessException;
import com.grpAC_SMS.model.Department;
import com.grpAC_SMS.model.Program;
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

@WebServlet("/ManageProgramsServlet")
public class ManageProgramsServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(ManageProgramsServlet.class);
    private ProgramDao programDao;
    private DepartmentDao departmentDao;

    @Override
    public void init() {
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
                    deleteProgram(request, response);
                    break;
                case ApplicationConstants.ACTION_LIST:
                default:
                    listPrograms(request, response);
                    break;
            }
        } catch (DataAccessException e) {
            logger.error("DataAccessException in Programs GET: {}", e.getMessage());
            request.getSession().setAttribute(ApplicationConstants.SESSION_ERROR_MESSAGE, "Database operation failed: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/ManageProgramsServlet?action=" + ApplicationConstants.ACTION_LIST);
        } catch (NumberFormatException e) {
            logger.error("NumberFormatException in Programs GET: {}", e.getMessage());
            request.getSession().setAttribute(ApplicationConstants.SESSION_ERROR_MESSAGE, "Invalid ID format.");
            response.sendRedirect(request.getContextPath() + "/ManageProgramsServlet?action=" + ApplicationConstants.ACTION_LIST);
        } catch (Exception e) {
            logger.error("General Exception in Programs GET: {}", e.getMessage(), e);
            request.getSession().setAttribute(ApplicationConstants.SESSION_ERROR_MESSAGE, "An unexpected error occurred: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/ManageProgramsServlet?action=" + ApplicationConstants.ACTION_LIST);
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        if (action == null) { // Should not happen if form is set up correctly
            response.sendRedirect(request.getContextPath() + "/ManageProgramsServlet?action=" + ApplicationConstants.ACTION_LIST);
            return;
        }

        try {
            switch (action) {
                case ApplicationConstants.ACTION_CREATE:
                    createProgram(request, response);
                    break;
                case ApplicationConstants.ACTION_UPDATE:
                    updateProgram(request, response);
                    break;
                default:
                    listPrograms(request, response);
                    break;
            }
        } catch (DataAccessException e) {
            logger.error("DataAccessException in Programs POST for action {}: {}", action, e.getMessage());
            request.setAttribute(ApplicationConstants.REQ_ATTR_ERROR_MESSAGE, "Operation failed: " + e.getMessage()); // Use request attribute for form redisplay
            preserveFormDataOnError(request, action); // Preserve data
            if (ApplicationConstants.ACTION_CREATE.equals(action)) {
                showNewForm(request, response);
            } else if (ApplicationConstants.ACTION_UPDATE.equals(action)) {
                showEditForm(request, response); // This might need the ID to be correctly passed or re-fetched
            } else {
                response.sendRedirect(request.getContextPath() + "/ManageProgramsServlet?action=" + ApplicationConstants.ACTION_LIST);
            }
        } catch (NumberFormatException e) {
            logger.error("NumberFormatException in Programs POST for action {}: {}", action, e.getMessage());
            request.setAttribute(ApplicationConstants.REQ_ATTR_ERROR_MESSAGE, "Invalid numeric input.");
            preserveFormDataOnError(request, action);
            if (ApplicationConstants.ACTION_CREATE.equals(action)) {
                showNewForm(request, response);
            } else if (ApplicationConstants.ACTION_UPDATE.equals(action)) {
                showEditForm(request, response);
            } else {
                response.sendRedirect(request.getContextPath() + "/ManageProgramsServlet?action=" + ApplicationConstants.ACTION_LIST);
            }
        } catch (Exception e) {
            logger.error("General Exception in Programs POST for action {}: {}", action, e.getMessage(), e);
            request.getSession().setAttribute(ApplicationConstants.SESSION_ERROR_MESSAGE, "An unexpected error occurred: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/ManageProgramsServlet?action=" + ApplicationConstants.ACTION_LIST);
        }
    }

    private void preserveFormDataOnError(HttpServletRequest request, String action) {
        Program program = new Program();
        program.setProgramName(request.getParameter("programName"));
        program.setDescription(request.getParameter("description"));

        String deptIdStr = request.getParameter("departmentId");
        if (!InputValidator.isNullOrEmpty(deptIdStr) && InputValidator.isInteger(deptIdStr)) {
            program.setDepartmentId(Integer.parseInt(deptIdStr));
        }
        String durationStr = request.getParameter("durationYears");
        if (!InputValidator.isNullOrEmpty(durationStr) && InputValidator.isInteger(durationStr)) {
            program.setDurationYears(Integer.parseInt(durationStr));
        }
        if (ApplicationConstants.ACTION_UPDATE.equals(action) && request.getParameter("programId") != null) {
            try {
                program.setProgramId(Integer.parseInt(request.getParameter("programId")));
            } catch (NumberFormatException e) { /* ignore if new */ }
        }
        request.setAttribute("program", program);
        // Re-fetch departments for dropdown consistency
        List<Department> departmentList = departmentDao.findAll();
        request.setAttribute("departmentList", departmentList);
    }


    private void listPrograms(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<Program> programList = programDao.findAllWithDetails(); // Get department names
        request.setAttribute("programList", programList);
        RequestDispatcher dispatcher = request.getRequestDispatcher("/admin/programs_list.jsp");
        dispatcher.forward(request, response);
    }

    private void showNewForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (request.getAttribute("program") == null) { // Not from a failed POST
            request.setAttribute("program", new Program());
        }
        List<Department> departmentList = departmentDao.findAll();
        request.setAttribute("departmentList", departmentList);
        RequestDispatcher dispatcher = request.getRequestDispatcher("/admin/program_form.jsp");
        dispatcher.forward(request, response);
    }

    private void showEditForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (request.getAttribute("program") == null) { // Not from a failed POST
            int id = Integer.parseInt(request.getParameter("id")); // from URL parameter
            Program existingProgram = programDao.findById(id)
                    .orElseThrow(() -> new DataAccessException("Program not found with ID: " + id));
            request.setAttribute("program", existingProgram);
        }
        // If it is from a failed POST, 'program' attribute is already set by preserveFormDataOnError
        // but we still need departmentList if not already set
        if (request.getAttribute("departmentList") == null) {
            List<Department> departmentList = departmentDao.findAll();
            request.setAttribute("departmentList", departmentList);
        }
        RequestDispatcher dispatcher = request.getRequestDispatcher("/admin/program_form.jsp");
        dispatcher.forward(request, response);
    }

    private void createProgram(HttpServletRequest request, HttpServletResponse response) throws IOException, DataAccessException, ServletException {
        String name = request.getParameter("programName");
        String deptIdStr = request.getParameter("departmentId");
        String durationStr = request.getParameter("durationYears");
        String description = request.getParameter("description");

        if (InputValidator.isNullOrEmpty(name) || InputValidator.isNullOrEmpty(deptIdStr)) {
            request.setAttribute(ApplicationConstants.REQ_ATTR_ERROR_MESSAGE, "Program Name and Department are required.");
            preserveFormDataOnError(request, ApplicationConstants.ACTION_CREATE);
            showNewForm(request, response);
            return;
        }
        if (!InputValidator.isInteger(deptIdStr)) {
            request.setAttribute(ApplicationConstants.REQ_ATTR_ERROR_MESSAGE, "Invalid Department ID.");
            preserveFormDataOnError(request, ApplicationConstants.ACTION_CREATE);
            showNewForm(request, response);
            return;
        }
        int departmentId = Integer.parseInt(deptIdStr);
        Integer duration = null;
        if (!InputValidator.isNullOrEmpty(durationStr)) {
            if (!InputValidator.isInteger(durationStr) || Integer.parseInt(durationStr) <= 0) {
                request.setAttribute(ApplicationConstants.REQ_ATTR_ERROR_MESSAGE, "Duration must be a positive number.");
                preserveFormDataOnError(request, ApplicationConstants.ACTION_CREATE);
                showNewForm(request, response);
                return;
            }
            duration = Integer.parseInt(durationStr);
        }

        Program program = new Program();
        program.setProgramName(name);
        program.setDepartmentId(departmentId);
        program.setDurationYears(duration);
        program.setDescription(description);

        programDao.add(program);
        request.getSession().setAttribute(ApplicationConstants.SESSION_SUCCESS_MESSAGE, "Program created successfully!");
        response.sendRedirect(request.getContextPath() + "/ManageProgramsServlet?action=" + ApplicationConstants.ACTION_LIST);
    }

    private void updateProgram(HttpServletRequest request, HttpServletResponse response) throws IOException, DataAccessException, ServletException {
        int id = Integer.parseInt(request.getParameter("programId"));
        String name = request.getParameter("programName");
        String deptIdStr = request.getParameter("departmentId");
        String durationStr = request.getParameter("durationYears");
        String description = request.getParameter("description");

        if (InputValidator.isNullOrEmpty(name) || InputValidator.isNullOrEmpty(deptIdStr)) {
            request.setAttribute(ApplicationConstants.REQ_ATTR_ERROR_MESSAGE, "Program Name and Department are required.");
            preserveFormDataOnError(request, ApplicationConstants.ACTION_UPDATE);
            showEditForm(request, response);
            return;
        }
        if (!InputValidator.isInteger(deptIdStr)) {
            request.setAttribute(ApplicationConstants.REQ_ATTR_ERROR_MESSAGE, "Invalid Department ID.");
            preserveFormDataOnError(request, ApplicationConstants.ACTION_UPDATE);
            showEditForm(request, response);
            return;
        }
        int departmentId = Integer.parseInt(deptIdStr);
        Integer duration = null;
        if (!InputValidator.isNullOrEmpty(durationStr)) {
            if (!InputValidator.isInteger(durationStr) || Integer.parseInt(durationStr) <= 0) {
                request.setAttribute(ApplicationConstants.REQ_ATTR_ERROR_MESSAGE, "Duration must be a positive number.");
                preserveFormDataOnError(request, ApplicationConstants.ACTION_UPDATE);
                showEditForm(request, response);
                return;
            }
            duration = Integer.parseInt(durationStr);
        }

        Program program = programDao.findById(id).orElseThrow(() -> new DataAccessException("Program not found for update."));
        program.setProgramName(name);
        program.setDepartmentId(departmentId);
        program.setDurationYears(duration);
        program.setDescription(description);

        programDao.update(program);
        request.getSession().setAttribute(ApplicationConstants.SESSION_SUCCESS_MESSAGE, "Program updated successfully!");
        response.sendRedirect(request.getContextPath() + "/ManageProgramsServlet?action=" + ApplicationConstants.ACTION_LIST);
    }

    private void deleteProgram(HttpServletRequest request, HttpServletResponse response) throws IOException, DataAccessException {
        int id = Integer.parseInt(request.getParameter("id"));
        boolean success = programDao.delete(id);
        if (success) {
            request.getSession().setAttribute(ApplicationConstants.SESSION_SUCCESS_MESSAGE, "Program deleted successfully!");
        } else {
            // DAO delete throws exception if it fails due to FK, so this path might not be hit for that specific reason
            request.getSession().setAttribute(ApplicationConstants.SESSION_ERROR_MESSAGE, "Program could not be deleted (or not found). Check for dependencies.");
        }
        response.sendRedirect(request.getContextPath() + "/ManageProgramsServlet?action=" + ApplicationConstants.ACTION_LIST);
    }
}