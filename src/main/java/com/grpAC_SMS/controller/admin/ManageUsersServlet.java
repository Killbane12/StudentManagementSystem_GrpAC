package com.grpAC_SMS.controller.admin;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.IOException;

/**
 * Handles CRUD operations for User accounts by Admin.
 */
@WebServlet(name = "ManageUsersServlet", value = "/admin/users")
public class ManageUsersServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        if ("list".equals(action) || action == null) {
            // TODO: Fetch list of users using UserDao
            System.out.println("ManageUsersServlet: Listing users.");
            // request.setAttribute("userList", userDao.findAll());
            request.getRequestDispatcher("/admin/users_list.jsp").forward(request, response);
        } else if ("edit".equals(action)) {
            // TODO: Fetch user by ID for editing
            System.out.println("ManageUsersServlet: Showing edit form.");
            request.getRequestDispatcher("/admin/user_form.jsp").forward(request, response);
        } else if ("new".equals(action)) {
            System.out.println("ManageUsersServlet: Showing new user form.");
            request.setAttribute("isNew", true); // Flag for the form
            request.getRequestDispatcher("/admin/user_form.jsp").forward(request, response);
        }
        // TODO: Handle other actions if needed
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        if ("create".equals(action)) {
            // TODO: Get parameters, validate, create User object, call UserDao.create()
            System.out.println("ManageUsersServlet: Creating user.");
        } else if ("update".equals(action)) {
            // TODO: Get parameters, validate, create User object, call UserDao.update()
            System.out.println("ManageUsersServlet: Updating user.");
        } else if ("delete".equals(action)) {
            // TODO: Get user ID, call UserDao.delete()
            System.out.println("ManageUsersServlet: Deleting user.");
        }
        response.sendRedirect(request.getContextPath() + "/admin/users?action=list"); // Redirect after action
    }
}