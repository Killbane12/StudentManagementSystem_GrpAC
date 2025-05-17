package com.grpAC_SMS.controller.admin;

import com.grpAC_SMS.dao.AnnouncementDao;
import com.grpAC_SMS.dao.impl.AnnouncementDaoImpl;
import com.grpAC_SMS.model.Announcement;
import com.grpAC_SMS.model.Role;
import com.grpAC_SMS.model.User;
import com.grpAC_SMS.exception.DataAccessException;
import com.grpAC_SMS.util.ApplicationConstants;
import com.grpAC_SMS.util.DateFormatter;
import com.grpAC_SMS.util.InputValidator;

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
import java.sql.Date;
import java.util.List;

@WebServlet("/ManageAnnouncementsServlet")
public class ManageAnnouncementsServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(ManageAnnouncementsServlet.class);
    private AnnouncementDao announcementDao;

    @Override
    public void init() {
        announcementDao = new AnnouncementDaoImpl();
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
                    deleteAnnouncement(request, response);
                    break;
                case ApplicationConstants.ACTION_LIST:
                default:
                    listAnnouncements(request, response);
                    break;
            }
        } catch (DataAccessException e) {
            logger.error("DataAccessException in Announcements GET: {}", e.getMessage());
            request.getSession().setAttribute(ApplicationConstants.SESSION_ERROR_MESSAGE, "Database operation failed: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/ManageAnnouncementsServlet?action=" + ApplicationConstants.ACTION_LIST);
        } catch (NumberFormatException e) {
            logger.error("NumberFormatException in Announcements GET: {}", e.getMessage());
            request.getSession().setAttribute(ApplicationConstants.SESSION_ERROR_MESSAGE, "Invalid ID format.");
            response.sendRedirect(request.getContextPath() + "/ManageAnnouncementsServlet?action=" + ApplicationConstants.ACTION_LIST);
        } catch (Exception e) {
            logger.error("General Exception in Announcements GET: {}", e.getMessage(), e);
            request.getSession().setAttribute(ApplicationConstants.SESSION_ERROR_MESSAGE, "An unexpected error occurred: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/ManageAnnouncementsServlet?action=" + ApplicationConstants.ACTION_LIST);
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        if (action == null) {
            response.sendRedirect(request.getContextPath() + "/ManageAnnouncementsServlet?action=" + ApplicationConstants.ACTION_LIST);
            return;
        }

        HttpSession session = request.getSession(false);
        User currentUser = (session != null) ? (User) session.getAttribute(ApplicationConstants.SESSION_USER) : null;
        if (currentUser == null || currentUser.getRole() != Role.ADMIN) {
            response.sendRedirect(request.getContextPath() + "/auth/login.jsp"); // Should be caught by filter anyway
            return;
        }

        try {
            switch (action) {
                case ApplicationConstants.ACTION_CREATE:
                    createAnnouncement(request, response, currentUser.getUserId());
                    break;
                case ApplicationConstants.ACTION_UPDATE:
                    updateAnnouncement(request, response, currentUser.getUserId());
                    break;
                default:
                    listAnnouncements(request, response);
                    break;
            }
        } catch (DataAccessException e) {
            logger.error("DataAccessException in Announcements POST for action {}: {}", action, e.getMessage());
            request.setAttribute(ApplicationConstants.REQ_ATTR_ERROR_MESSAGE, "Operation failed: " + e.getMessage());
            preserveFormDataOnError(request, action);
            if (ApplicationConstants.ACTION_CREATE.equals(action)) {
                showNewForm(request, response);
            } else if (ApplicationConstants.ACTION_UPDATE.equals(action)) {
                showEditForm(request, response);
            } else {
                response.sendRedirect(request.getContextPath() + "/ManageAnnouncementsServlet?action=" + ApplicationConstants.ACTION_LIST);
            }
        } catch (Exception e) {
            logger.error("General Exception in Announcements POST for action {}: {}", action, e.getMessage(), e);
            request.getSession().setAttribute(ApplicationConstants.SESSION_ERROR_MESSAGE, "An unexpected error occurred: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/ManageAnnouncementsServlet?action=" + ApplicationConstants.ACTION_LIST);
        }
    }

    private void preserveFormDataOnError(HttpServletRequest request, String action) {
        Announcement announcement = new Announcement();
        if (ApplicationConstants.ACTION_UPDATE.equals(action) && request.getParameter("announcementId") != null) {
            try {
                announcement.setAnnouncementId(Integer.parseInt(request.getParameter("announcementId")));
            } catch (NumberFormatException e) { /* ignore */ }
        }
        announcement.setTitle(request.getParameter("title"));
        announcement.setContent(request.getParameter("content"));
        announcement.setTargetRole(request.getParameter("targetRole"));
        if(InputValidator.isValidDate(request.getParameter("expiryDate"))) {
            announcement.setExpiryDate(DateFormatter.stringToSqlDate(request.getParameter("expiryDate")));
        }
        // image_file_path not handled in this iteration
        request.setAttribute("announcement", announcement);
        if(request.getAttribute("targetRoles") == null) request.setAttribute("targetRoles", Role.values());
    }


    private void listAnnouncements(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<Announcement> announcementList = announcementDao.findAllWithDetails(); // Get poster's username
        request.setAttribute("announcementList", announcementList);
        RequestDispatcher dispatcher = request.getRequestDispatcher("/admin/announcements_list.jsp");
        dispatcher.forward(request, response);
    }

    private void showNewForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if(request.getAttribute("announcement") == null) request.setAttribute("announcement", new Announcement());
        if(request.getAttribute("targetRoles") == null) request.setAttribute("targetRoles", Role.values()); // For dropdown
        // Add "ALL" as an option for target role
        request.setAttribute("allTargetOption", "ALL");
        RequestDispatcher dispatcher = request.getRequestDispatcher("/admin/announcement_form.jsp");
        dispatcher.forward(request, response);
    }

    private void showEditForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if(request.getAttribute("announcement") == null) {
            int id = Integer.parseInt(request.getParameter("id"));
            Announcement existingAnnouncement = announcementDao.findById(id)
                    .orElseThrow(() -> new DataAccessException("Announcement not found with ID: " + id));
            request.setAttribute("announcement", existingAnnouncement);
        }
        if(request.getAttribute("targetRoles") == null) request.setAttribute("targetRoles", Role.values());
        request.setAttribute("allTargetOption", "ALL");
        RequestDispatcher dispatcher = request.getRequestDispatcher("/admin/announcement_form.jsp");
        dispatcher.forward(request, response);
    }

    private void createAnnouncement(HttpServletRequest request, HttpServletResponse response, int postedByUserId) throws IOException, DataAccessException, ServletException {
        String title = request.getParameter("title");
        String content = request.getParameter("content");
        String targetRoleStr = request.getParameter("targetRole");
        String expiryDateStr = request.getParameter("expiryDate");
        // String imagePath = request.getParameter("imageFilePath"); // For future use

        if (InputValidator.isNullOrEmpty(title) || InputValidator.isNullOrEmpty(content) || InputValidator.isNullOrEmpty(targetRoleStr)) {
            request.setAttribute(ApplicationConstants.REQ_ATTR_ERROR_MESSAGE, "Title, Content, and Target Role are required.");
            preserveFormDataOnError(request, ApplicationConstants.ACTION_CREATE);
            showNewForm(request, response);
            return;
        }

        Date expiryDate = null;
        if (!InputValidator.isNullOrEmpty(expiryDateStr)) {
            if (!InputValidator.isValidDate(expiryDateStr)) {
                request.setAttribute(ApplicationConstants.REQ_ATTR_ERROR_MESSAGE, "Invalid Expiry Date format. Use YYYY-MM-DD.");
                preserveFormDataOnError(request, ApplicationConstants.ACTION_CREATE);
                showNewForm(request, response);
                return;
            }
            expiryDate = DateFormatter.stringToSqlDate(expiryDateStr);
        }


        Announcement announcement = new Announcement();
        announcement.setTitle(title);
        announcement.setContent(content);
        announcement.setPostedByUserId(postedByUserId);
        announcement.setTargetRole(targetRoleStr); // ENUM will handle validation if input is bad, or validate here
        announcement.setExpiryDate(expiryDate);
        // announcement.setImageFilePath(imagePath);

        announcementDao.add(announcement);
        request.getSession().setAttribute(ApplicationConstants.SESSION_SUCCESS_MESSAGE, "Announcement created successfully!");
        response.sendRedirect(request.getContextPath() + "/ManageAnnouncementsServlet?action=" + ApplicationConstants.ACTION_LIST);
    }

    private void updateAnnouncement(HttpServletRequest request, HttpServletResponse response, int postedByUserId) throws IOException, DataAccessException, ServletException {
        int id = Integer.parseInt(request.getParameter("announcementId"));
        String title = request.getParameter("title");
        String content = request.getParameter("content");
        String targetRoleStr = request.getParameter("targetRole");
        String expiryDateStr = request.getParameter("expiryDate");

        if (InputValidator.isNullOrEmpty(title) || InputValidator.isNullOrEmpty(content) || InputValidator.isNullOrEmpty(targetRoleStr)) {
            request.setAttribute(ApplicationConstants.REQ_ATTR_ERROR_MESSAGE, "Title, Content, and Target Role are required.");
            preserveFormDataOnError(request, ApplicationConstants.ACTION_UPDATE);
            showEditForm(request, response);
            return;
        }

        Date expiryDate = null;
        if (!InputValidator.isNullOrEmpty(expiryDateStr)) {
            if (!InputValidator.isValidDate(expiryDateStr)) {
                request.setAttribute(ApplicationConstants.REQ_ATTR_ERROR_MESSAGE, "Invalid Expiry Date format. Use YYYY-MM-DD.");
                preserveFormDataOnError(request, ApplicationConstants.ACTION_UPDATE);
                showEditForm(request, response);
                return;
            }
            expiryDate = DateFormatter.stringToSqlDate(expiryDateStr);
        }

        Announcement announcement = announcementDao.findById(id).orElseThrow(() -> new DataAccessException("Announcement not found for update."));
        announcement.setTitle(title);
        announcement.setContent(content);
        announcement.setPostedByUserId(postedByUserId); // Should remain the original poster or current admin
        announcement.setTargetRole(targetRoleStr);
        announcement.setExpiryDate(expiryDate);

        announcementDao.update(announcement);
        request.getSession().setAttribute(ApplicationConstants.SESSION_SUCCESS_MESSAGE, "Announcement updated successfully!");
        response.sendRedirect(request.getContextPath() + "/ManageAnnouncementsServlet?action=" + ApplicationConstants.ACTION_LIST);
    }

    private void deleteAnnouncement(HttpServletRequest request, HttpServletResponse response) throws IOException, DataAccessException {
        int id = Integer.parseInt(request.getParameter("id"));
        boolean success = announcementDao.delete(id);
        if (success) {
            request.getSession().setAttribute(ApplicationConstants.SESSION_SUCCESS_MESSAGE, "Announcement deleted successfully!");
        } else {
            request.getSession().setAttribute(ApplicationConstants.SESSION_ERROR_MESSAGE, "Announcement could not be deleted or not found.");
        }
        response.sendRedirect(request.getContextPath() + "/ManageAnnouncementsServlet?action=" + ApplicationConstants.ACTION_LIST);
    }
}