package com.grpAC_SMS.controller.student;

import com.grpAC_SMS.dao.StudentDao;
import com.grpAC_SMS.dao.UserDao;
import com.grpAC_SMS.dao.impl.StudentDaoImpl;
import com.grpAC_SMS.dao.impl.UserDaoImpl;
import com.grpAC_SMS.model.Student;
import com.grpAC_SMS.model.User;
import com.grpAC_SMS.model.Role;
import com.grpAC_SMS.util.ApplicationConstants;

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
import java.util.Optional;

@WebServlet("/StudentProfileServlet")
public class StudentProfileServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(StudentProfileServlet.class);
    private StudentDao studentDao;
    private UserDao userDao;

    @Override
    public void init() {
        studentDao = new StudentDaoImpl();
        userDao = new UserDaoImpl();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User loggedInUser = (session != null) ? (User) session.getAttribute(ApplicationConstants.SESSION_USER) : null;

        if (loggedInUser == null || loggedInUser.getRole() != Role.STUDENT) {
            response.sendRedirect(request.getContextPath() + "/auth/login.jsp");
            return;
        }

        try {
            Optional<Student> studentOpt = studentDao.findByUserId(loggedInUser.getUserId());
            if (studentOpt.isPresent()) {
                request.setAttribute("studentProfile", studentOpt.get());
                // The studentOpt should ideally contain programName due to `findById` joining with Programs.
                // Also fetch the User object for username/email if not already fully populated in loggedInUser
                Optional<User> userProfileOpt = userDao.findById(loggedInUser.getUserId());
                userProfileOpt.ifPresent(user -> request.setAttribute("userProfile", user));

            } else {
                logger.warn("No student profile found for user ID: {}", loggedInUser.getUserId());
                request.setAttribute("errorMessage", "Your student profile could not be loaded.");
            }
        } catch (Exception e) {
            logger.error("Error fetching student profile for user {}: {}", loggedInUser.getUsername(), e.getMessage(), e);
            request.setAttribute("errorMessage", "Error loading profile: " + e.getMessage());
        }

        RequestDispatcher dispatcher = request.getRequestDispatcher("/student/my_profile.jsp");
        dispatcher.forward(request, response);
    }
}