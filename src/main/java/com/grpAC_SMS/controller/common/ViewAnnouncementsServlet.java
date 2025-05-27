package com.grpAC_SMS.controller.common; // Common package

import com.grpAC_SMS.dao.AnnouncementDao;
import com.grpAC_SMS.dao.impl.AnnouncementDaoImpl;
import com.grpAC_SMS.model.Announcement;
import com.grpAC_SMS.model.Role;
import com.grpAC_SMS.model.User;
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
import java.util.List;

@WebServlet("/ViewAnnouncementsServlet")
public class ViewAnnouncementsServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(ViewAnnouncementsServlet.class);
    private AnnouncementDao announcementDao;

    @Override
    public void init() {
        announcementDao = new AnnouncementDaoImpl();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User loggedInUser = (session != null) ? (User) session.getAttribute(ApplicationConstants.SESSION_USER) : null;

        if (loggedInUser == null) { // Should be caught by auth filter, but defensive
            response.sendRedirect(request.getContextPath() + "/auth/login.jsp");
            return;
        }

        Role userRole = loggedInUser.getRole();
        List<Announcement> announcements;

        try {
            // Fetch announcements targeted to the user's role or 'ALL'
            // The findTargetedAnnouncements can take a limit, or we can make a findByTargetRole that gets all.
            announcements = announcementDao.findByTargetRole(userRole); // Gets ALL and specific role, not expired
            request.setAttribute("announcements", announcements);
            request.setAttribute("pageTitle", "View Announcements");

        } catch (Exception e) {
            logger.error("Error fetching announcements for role {}: {}", userRole, e.getMessage(), e);
            request.setAttribute("errorMessage", "Could not load announcements: " + e.getMessage());
        }

        RequestDispatcher dispatcher = request.getRequestDispatcher("/common/view_announcements.jsp"); // New common JSP
        dispatcher.forward(request, response);
    }
}