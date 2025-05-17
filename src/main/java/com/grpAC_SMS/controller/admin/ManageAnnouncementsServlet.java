package com.grpAC_SMS.controller.admin;

import com.grpAC_SMS.dao.AnnouncementDao;
import com.grpAC_SMS.dao.impl.AnnouncementDaoImpl;
import com.grpAC_SMS.model.Announcement;
import com.grpAC_SMS.exception.DataAccessException;
import com.grpAC_SMS.util.InputValidator;  // Import the InputValidator class

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

@WebServlet("/announcements/*")
public class ManageAnnouncementsServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private AnnouncementDao announcementDao;

    @Override
    public void init() throws ServletException {
        super.init();
        announcementDao = new AnnouncementDaoImpl(); // Initialize your DAO
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String path = request.getPathInfo();
        if (path == null) {
            path = "/";
        }

        switch (path) {
            case "/":
                listAnnouncements(request, response);
                break;
            case "/add":
                showAddAnnouncementForm(request, response);
                break;
            case "/edit":
                showEditAnnouncementForm(request, response);
                break;
            case "/delete":
                deleteAnnouncement(request, response);
                break;
            default:
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String path = request.getPathInfo();
        if (path == null) {
            path = "/";
        }
        switch (path) {
            case "/save":
                try {
                    saveAnnouncement(request, response);
                } catch (DataAccessException e) {
                    throw new RuntimeException(e);
                }
                break;
            default:
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private void listAnnouncements(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            List<Announcement> announcements = announcementDao.getAllAnnouncements();
            request.setAttribute("announcements", announcements);
            request.getRequestDispatcher("/admin/announcements_list.jsp").forward(request, response);
        } catch (DataAccessException e) {
            // Log the error
            e.printStackTrace();
            request.setAttribute("errorMessage", "Failed to retrieve announcements: " + e.getMessage()); // Set error message
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private void showAddAnnouncementForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setAttribute("announcement", new Announcement()); // Pass an empty announcement object
        request.getRequestDispatcher("/admin/announcement_form.jsp").forward(request, response);
    }

    private void showEditAnnouncementForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int announcementId;
        try {
            announcementId = Integer.parseInt(request.getParameter("id"));
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid announcement ID");
            return;
        }

        try {
            Announcement announcement = announcementDao.getAnnouncementById(announcementId);
            if (announcement == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Announcement not found");
                return;
            }
            request.setAttribute("announcement", announcement);
            request.getRequestDispatcher("/admin/announcement_form.jsp").forward(request, response);
        } catch (DataAccessException e) {
            // Log the error
            e.printStackTrace();
            request.setAttribute("errorMessage", "Failed to retrieve announcement for editing: " + e.getMessage());
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private void saveAnnouncement(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, DataAccessException {
        // Retrieve and validate parameters
        String announcementIdParam = request.getParameter("announcementId");
        String title = request.getParameter("title");
        String content = request.getParameter("content");
        String postedByUserIdParam = request.getParameter("postedByUserId");
        String targetRole = request.getParameter("targetRole");
        String imageFilePath = request.getParameter("imageFilePath");
        String expiryDateParam = request.getParameter("expiryDate");


        boolean isNew = (announcementIdParam == null || announcementIdParam.isEmpty() || announcementIdParam.equals("0"));
        int announcementId = isNew ? 0 : Integer.parseInt(announcementIdParam);
        int postedByUserId;
        Date expiryDate = null;


        try {
            postedByUserId = Integer.parseInt(postedByUserIdParam);
        } catch (NumberFormatException e) {
            request.setAttribute("errorMessage", "Invalid User ID");
            request.setAttribute("announcement", createAnnouncementFromRequest(request));
            request.getRequestDispatcher("/admin/announcement_form.jsp").forward(request, response);
            return;
        }

        if (targetRole == null || targetRole.isEmpty()) {
            request.setAttribute("errorMessage", "Please select a target role");
            request.setAttribute("announcement", createAnnouncementFromRequest(request));
            request.getRequestDispatcher("/admin/announcement_form.jsp").forward(request, response);
            return;
        }

        if (expiryDateParam != null && !expiryDateParam.isEmpty()) {
            try {
                expiryDate = java.sql.Date.valueOf(expiryDateParam);
            } catch (IllegalArgumentException e) {
                request.setAttribute("errorMessage", "Invalid expiry date format.  Use YYYY-MM-DD");
                request.setAttribute("announcement", createAnnouncementFromRequest(request));
                request.getRequestDispatcher("/admin/announcement_form.jsp").forward(request, response);
                return;
            }
        }

        Announcement announcement;
        if (isNew) {
            announcement = new Announcement();
            announcement.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        }
        else{
            announcement = announcementDao.getAnnouncementById(announcementId);
            if(announcement == null){
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Announcement not found");
                return;
            }
        }

        announcement.setTitle(title);
        announcement.setContent(content);
        announcement.setPostedByUserId(postedByUserId);
        announcement.setTargetRole(targetRole);
        announcement.setImageFilePath(imageFilePath);
        announcement.setExpiryDate(expiryDate);
        announcement.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        announcement.setAnnouncementId(announcementId); // Make sure ID is set, especially for updates



        try {
            if (isNew) {
                announcementDao.addAnnouncement(announcement);
                request.getSession().setAttribute("message", "Announcement added successfully");
            } else {
                announcementDao.updateAnnouncement(announcement);
                request.getSession().setAttribute("message", "Announcement updated successfully");
            }
            response.sendRedirect(request.getContextPath() + "/announcements"); // Redirect to list
        } catch (DataAccessException e) {
            // Log the error
            e.printStackTrace();
            request.setAttribute("errorMessage", "Failed to save announcement: " + e.getMessage());
            request.setAttribute("announcement", announcement);  // Repopulate form with user data
            request.getRequestDispatcher("/admin/announcement_form.jsp").forward(request, response); //stay on form
        }
    }



    private void deleteAnnouncement(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int announcementId;
        try {
            announcementId = Integer.parseInt(request.getParameter("id"));
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid announcement ID");
            return;
        }

        try {
            Announcement announcement = announcementDao.getAnnouncementById(announcementId);
            if (announcement == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Announcement not found");
                return;
            }
            announcementDao.deleteAnnouncement(announcementId);
            request.getSession().setAttribute("message", "Announcement deleted successfully");
            response.sendRedirect(request.getContextPath() + "/announcements");
        } catch (DataAccessException e) {
            // Log the error
            e.printStackTrace();
            request.setAttribute("errorMessage", "Failed to delete announcement: " + e.getMessage());
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
    private Announcement createAnnouncementFromRequest(HttpServletRequest request) {
        Announcement announcement = new Announcement();
        announcement.setAnnouncementId(request.getParameter("announcementId") != null ? Integer.parseInt(request.getParameter("announcementId")) : 0);
        announcement.setTitle(request.getParameter("title"));
        announcement.setContent(request.getParameter("content"));
        announcement.setPostedByUserId(request.getParameter("postedByUserId") != null ? Integer.parseInt(request.getParameter("postedByUserId")) : 0);
        announcement.setTargetRole(request.getParameter("targetRole"));
        announcement.setImageFilePath(request.getParameter("imageFilePath"));
        String expiryDateParam = request.getParameter("expiryDate");
        if (expiryDateParam != null && !expiryDateParam.isEmpty()) {
            try {
                announcement.setExpiryDate(java.sql.Date.valueOf(expiryDateParam));
            } catch (IllegalArgumentException e) {
                //Leave null
            }
        }
        return announcement;
    }
}
