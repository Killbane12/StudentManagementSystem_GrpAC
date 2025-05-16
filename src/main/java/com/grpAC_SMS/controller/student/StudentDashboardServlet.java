package com.grpAC_SMS.controller.student;


import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet(name = "StudentDashboardServlet", value = "/student/dashboard")
public class StudentDashboardServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws SecurityException, IOException, ServletException {

        RequestDispatcher dispatcher = request.getRequestDispatcher("/student/dashboard.jsp");
        dispatcher.forward(request, response);
    }

}
