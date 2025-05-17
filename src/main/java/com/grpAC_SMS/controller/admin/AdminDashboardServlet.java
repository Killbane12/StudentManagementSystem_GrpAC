package com.grpAC_SMS.controller.admin;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.IOException;

/**
 * Handles requests for the Admin Dashboard.
 */
@WebServlet(name = "AdminDashboardServlet", value = "/admin/dashboard")
public class AdminDashboardServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // TODO: Fetch overview data (counts, etc.) using DAOs
        System.out.println("Admin Dashboard doGet called.");
        // Set data as request attributes
        // request.setAttribute("studentCount", studentDao.count());
        RequestDispatcher dispatcher = request.getRequestDispatcher("/admin/dashboard.jsp");
        dispatcher.forward(request, response);
    }
}