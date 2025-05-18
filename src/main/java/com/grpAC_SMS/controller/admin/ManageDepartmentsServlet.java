package com.grpAC_SMS.controller.admin;

import com.grpAC_SMS.dao.DepartmentDao;
import com.grpAC_SMS.dao.impl.DepartmentDaoImpl;
import com.grpAC_SMS.exception.DataAccessException;
import com.grpAC_SMS.model.Department;
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

@WebServlet("/ManageDepartmentsServlet")
public class ManageDepartmentsServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(ManageDepartmentsServlet.class);
    private DepartmentDao departmentDao;

    @Override
    public void init() {
        departmentDao = new DepartmentDaoImpl();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        if (action == null) action = "list";

        try {
            switch (action) {
                case "add":
                    showNewForm(request, response);
                    break;
                case "edit":
                    showEditForm(request, response);
                    break;
                case "delete":
                    deleteDepartment(request, response);
                    break;
                case "list":
                default:
                    listDepartments(request, response);
                    break;
            }
        } catch (DataAccessException e) {
            logger.error("DataAccessException in Departments GET: {}", e.getMessage());
            request.getSession().setAttribute("errorMessage", "Database operation failed: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/ManageDepartmentsServlet?action=list");
        } catch (Exception e) {
            logger.error("General Exception in Departments GET: {}", e.getMessage(), e);
            request.getSession().setAttribute("errorMessage", "An unexpected error occurred: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/ManageDepartmentsServlet?action=list");
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        try {
            switch (action) {
                case "create":
                    createDepartment(request, response);
                    break;
                case "update":
                    updateDepartment(request, response);
                    break;
                default:
                    listDepartments(request, response);
                    break;
            }
        } catch (DataAccessException e) {
            logger.error("DataAccessException in Departments POST: {}", e.getMessage());
            request.getSession().setAttribute("errorMessage", "Operation failed: " + e.getMessage());
            // Preserve form data on error
            Department dept = new Department();
            dept.setDepartmentName(request.getParameter("departmentName"));
            if (request.getParameter("departmentId") != null && !request.getParameter("departmentId").isEmpty()) {
                dept.setDepartmentId(Integer.parseInt(request.getParameter("departmentId")));
            }
            request.setAttribute("department", dept);

            if ("create".equals(action)) showNewForm(request, response);
            else if ("update".equals(action))
                showEditForm(request, response); // Needs re-fetch or careful handling of ID
            else response.sendRedirect(request.getContextPath() + "/ManageDepartmentsServlet?action=list");

        } catch (Exception e) {
            logger.error("General Exception in Departments POST: {}", e.getMessage(), e);
            request.getSession().setAttribute("errorMessage", "An unexpected error occurred: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/ManageDepartmentsServlet?action=list");
        }
    }

    private void listDepartments(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<Department> departmentList = departmentDao.findAll();
        request.setAttribute("departmentList", departmentList);
        RequestDispatcher dispatcher = request.getRequestDispatcher("/admin/departments_list.jsp");
        dispatcher.forward(request, response);
    }

    private void showNewForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (request.getAttribute("department") == null) { // Check if form data preserved from POST error
            request.setAttribute("department", new Department());
        }
        RequestDispatcher dispatcher = request.getRequestDispatcher("/admin/department_form.jsp");
        dispatcher.forward(request, response);
    }

    private void showEditForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (request.getAttribute("department") == null) { // Check if form data preserved from POST error
            int id = Integer.parseInt(request.getParameter("id"));
            Department existingDepartment = departmentDao.findById(id)
                    .orElseThrow(() -> new DataAccessException("Department not found with ID: " + id));
            request.setAttribute("department", existingDepartment);
        }
        RequestDispatcher dispatcher = request.getRequestDispatcher("/admin/department_form.jsp");
        dispatcher.forward(request, response);
    }

    private void createDepartment(HttpServletRequest request, HttpServletResponse response) throws IOException, DataAccessException {
        String name = request.getParameter("departmentName");
        if (InputValidator.isNullOrEmpty(name)) {
            request.getSession().setAttribute("errorMessage", "Department name cannot be empty.");
            Department dept = new Department();
            dept.setDepartmentName(name);
            request.setAttribute("department", dept);
            try {
                showNewForm(request, response);
            } catch (ServletException e) {
                logger.error("Servlet Exc in create", e);
            }
            return;
        }
        Department department = new Department(name);
        departmentDao.add(department);
        request.getSession().setAttribute("successMessage", "Department created successfully!");
        response.sendRedirect(request.getContextPath() + "/ManageDepartmentsServlet?action=list");
    }

    private void updateDepartment(HttpServletRequest request, HttpServletResponse response) throws IOException, DataAccessException {
        int id = Integer.parseInt(request.getParameter("departmentId"));
        String name = request.getParameter("departmentName");
        if (InputValidator.isNullOrEmpty(name)) {
            request.getSession().setAttribute("errorMessage", "Department name cannot be empty.");
            Department dept = departmentDao.findById(id).orElse(new Department());
            dept.setDepartmentName(name);
            request.setAttribute("department", dept);
            try {
                showEditForm(request, response);
            } catch (ServletException e) {
                logger.error("Servlet Exc in update", e);
            }
            return;
        }
        Department department = new Department(name);
        department.setDepartmentId(id);
        departmentDao.update(department);
        request.getSession().setAttribute("successMessage", "Department updated successfully!");
        response.sendRedirect(request.getContextPath() + "/ManageDepartmentsServlet?action=list");
    }

    private void deleteDepartment(HttpServletRequest request, HttpServletResponse response) throws IOException, DataAccessException {
        int id = Integer.parseInt(request.getParameter("id"));
        boolean success = departmentDao.delete(id);
        if (success) {
            request.getSession().setAttribute("successMessage", "Department deleted successfully!");
        } else {
            request.getSession().setAttribute("errorMessage", "Department could not be deleted or not found.");
        }
        response.sendRedirect(request.getContextPath() + "/ManageDepartmentsServlet?action=list");
    }
}
