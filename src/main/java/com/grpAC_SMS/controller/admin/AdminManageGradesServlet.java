package com.grpAC_SMS.controller.admin;

import com.grpAC_SMS.dao.GradeDao;
import com.grpAC_SMS.dao.impl.GradeDaoImpl;
import com.grpAC_SMS.model.Grade;
import com.grpAC_SMS.util.ApplicationConstants;

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

@WebServlet("/AdminManageGradesServlet")
public class AdminManageGradesServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(AdminManageGradesServlet.class);
    private GradeDao gradeDao;

    @Override
    public void init() {
        gradeDao = new GradeDaoImpl();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        if (action == null) action = ApplicationConstants.ACTION_LIST;

        try {
            switch (action) {
                // Admin might edit grades in exceptional cases.
                // case ApplicationConstants.ACTION_EDIT:
                //     showEditGradeForm(request, response); // Needs a specific admin_grade_form.jsp
                //     break;
                case ApplicationConstants.ACTION_LIST:
                default:
                    listAllGrades(request, response);
                    break;
            }
        } catch (Exception e) {
            logger.error("Error in AdminManageGradesServlet GET: {}", e.getMessage(), e);
            request.getSession().setAttribute(ApplicationConstants.SESSION_ERROR_MESSAGE, "Error processing grades: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/admin/dashboard.jsp"); // Or a dedicated error page
        }
    }

    // doPost could handle grade updates by Admin if that functionality is added.
    // protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {}

    private void listAllGrades(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // TODO: Add filtering by student, course, term
        List<Grade> gradeList = gradeDao.findAllWithDetails();
        request.setAttribute("gradeList", gradeList);
        RequestDispatcher dispatcher = request.getRequestDispatcher("/admin/grades_oversight_list.jsp");
        dispatcher.forward(request, response);
    }
}