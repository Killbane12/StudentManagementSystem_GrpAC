package com.grpAC_SMS.controller.admin;

import com.grpAC_SMS.dao.AttendanceDao;
import com.grpAC_SMS.dao.impl.AttendanceDaoImpl;
import com.grpAC_SMS.model.Attendance;
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

@WebServlet("/AdminManageAttendanceServlet")
public class AdminManageAttendanceServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(AdminManageAttendanceServlet.class);
    private AttendanceDao attendanceDao;

    @Override
    public void init() {
        attendanceDao = new AttendanceDaoImpl();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        if (action == null) action = ApplicationConstants.ACTION_LIST;

        try {
            switch (action) {
                // Admin might manually mark/edit attendance in rare cases.
                // case ApplicationConstants.ACTION_EDIT: showEditAttendanceForm(request,response)
                case ApplicationConstants.ACTION_LIST:
                default:
                    listAllAttendance(request, response);
                    break;
            }
        } catch (Exception e) {
            logger.error("Error in AdminManageAttendanceServlet GET: {}", e.getMessage(), e);
            request.getSession().setAttribute(ApplicationConstants.SESSION_ERROR_MESSAGE, "Error processing attendance: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/admin/dashboard.jsp");
        }
    }

    // doPost could handle attendance updates by Admin.

    private void listAllAttendance(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // TODO: Add filtering by student, course, session, date range
        List<Attendance> attendanceList = attendanceDao.findAllWithDetails();
        request.setAttribute("attendanceList", attendanceList);
        RequestDispatcher dispatcher = request.getRequestDispatcher("/admin/attendance_oversight_list.jsp");
        dispatcher.forward(request, response);
    }
}