package com.grpAC_SMS.controller.admin;

import com.grpAC_SMS.dao.LocationDao;
import com.grpAC_SMS.dao.impl.LocationDaoImpl;
import com.grpAC_SMS.exception.DataAccessException;
import com.grpAC_SMS.model.Location;
import com.grpAC_SMS.util.ApplicationConstants;
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

@WebServlet("/ManageLocationsServlet")
public class ManageLocationsServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(ManageLocationsServlet.class);
    private LocationDao locationDao;

    @Override
    public void init() {
        locationDao = new LocationDaoImpl();
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
                    deleteLocation(request, response);
                    break;
                case ApplicationConstants.ACTION_LIST:
                default:
                    listLocations(request, response);
                    break;
            }
        } catch (DataAccessException e) {
            logger.error("DataAccessException in Locations GET: {}", e.getMessage());
            request.getSession().setAttribute(ApplicationConstants.SESSION_ERROR_MESSAGE, "Database operation failed: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/ManageLocationsServlet?action=" + ApplicationConstants.ACTION_LIST);
        } catch (NumberFormatException e) {
            logger.error("NumberFormatException in Locations GET: {}", e.getMessage());
            request.getSession().setAttribute(ApplicationConstants.SESSION_ERROR_MESSAGE, "Invalid ID format.");
            response.sendRedirect(request.getContextPath() + "/ManageLocationsServlet?action=" + ApplicationConstants.ACTION_LIST);
        } catch (Exception e) {
            logger.error("General Exception in Locations GET: {}", e.getMessage(), e);
            request.getSession().setAttribute(ApplicationConstants.SESSION_ERROR_MESSAGE, "An unexpected error occurred: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/ManageLocationsServlet?action=" + ApplicationConstants.ACTION_LIST);
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        if (action == null) {
            response.sendRedirect(request.getContextPath() + "/ManageLocationsServlet?action=" + ApplicationConstants.ACTION_LIST);
            return;
        }

        try {
            switch (action) {
                case ApplicationConstants.ACTION_CREATE:
                    createLocation(request, response);
                    break;
                case ApplicationConstants.ACTION_UPDATE:
                    updateLocation(request, response);
                    break;
                default:
                    listLocations(request, response);
                    break;
            }
        } catch (DataAccessException e) {
            logger.error("DataAccessException in Locations POST for action {}: {}", action, e.getMessage());
            request.setAttribute(ApplicationConstants.REQ_ATTR_ERROR_MESSAGE, "Operation failed: " + e.getMessage());
            preserveFormDataOnError(request, action);
            if (ApplicationConstants.ACTION_CREATE.equals(action)) {
                showNewForm(request, response);
            } else if (ApplicationConstants.ACTION_UPDATE.equals(action)) {
                showEditForm(request, response);
            } else {
                response.sendRedirect(request.getContextPath() + "/ManageLocationsServlet?action=" + ApplicationConstants.ACTION_LIST);
            }
        } catch (Exception e) {
            logger.error("General Exception in Locations POST for action {}: {}", action, e.getMessage(), e);
            request.getSession().setAttribute(ApplicationConstants.SESSION_ERROR_MESSAGE, "An unexpected error occurred: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/ManageLocationsServlet?action=" + ApplicationConstants.ACTION_LIST);
        }
    }

    private void preserveFormDataOnError(HttpServletRequest request, String action) {
        Location location = new Location();
        location.setLocationName(request.getParameter("locationName"));
        String capacityStr = request.getParameter("capacity");
        if (!InputValidator.isNullOrEmpty(capacityStr) && InputValidator.isInteger(capacityStr)) {
            location.setCapacity(Integer.parseInt(capacityStr));
        }
        if (ApplicationConstants.ACTION_UPDATE.equals(action) && request.getParameter("locationId") != null) {
            try {
                location.setLocationId(Integer.parseInt(request.getParameter("locationId")));
            } catch (NumberFormatException e) { /* ignore */ }
        }
        request.setAttribute("location", location);
    }

    private void listLocations(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<Location> locationList = locationDao.findAll();
        request.setAttribute("locationList", locationList);
        RequestDispatcher dispatcher = request.getRequestDispatcher("/admin/locations_list.jsp");
        dispatcher.forward(request, response);
    }

    private void showNewForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (request.getAttribute("location") == null) request.setAttribute("location", new Location());
        RequestDispatcher dispatcher = request.getRequestDispatcher("/admin/location_form.jsp");
        dispatcher.forward(request, response);
    }

    private void showEditForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (request.getAttribute("location") == null) {
            int id = Integer.parseInt(request.getParameter("id"));
            Location existingLocation = locationDao.findById(id)
                    .orElseThrow(() -> new DataAccessException("Location not found with ID: " + id));
            request.setAttribute("location", existingLocation);
        }
        RequestDispatcher dispatcher = request.getRequestDispatcher("/admin/location_form.jsp");
        dispatcher.forward(request, response);
    }

    private void createLocation(HttpServletRequest request, HttpServletResponse response) throws IOException, DataAccessException, ServletException {
        String locationName = request.getParameter("locationName");
        String capacityStr = request.getParameter("capacity");

        if (InputValidator.isNullOrEmpty(locationName)) {
            request.setAttribute(ApplicationConstants.REQ_ATTR_ERROR_MESSAGE, "Location Name is required.");
            preserveFormDataOnError(request, ApplicationConstants.ACTION_CREATE);
            showNewForm(request, response);
            return;
        }

        Integer capacity = null;
        if (!InputValidator.isNullOrEmpty(capacityStr)) {
            if (!InputValidator.isInteger(capacityStr) || Integer.parseInt(capacityStr) < 0) {
                request.setAttribute(ApplicationConstants.REQ_ATTR_ERROR_MESSAGE, "Capacity must be a non-negative number.");
                preserveFormDataOnError(request, ApplicationConstants.ACTION_CREATE);
                showNewForm(request, response);
                return;
            }
            capacity = Integer.parseInt(capacityStr);
        }

        Location location = new Location();
        location.setLocationName(locationName);
        location.setCapacity(capacity);

        locationDao.add(location);
        request.getSession().setAttribute(ApplicationConstants.SESSION_SUCCESS_MESSAGE, "Location created successfully!");
        response.sendRedirect(request.getContextPath() + "/ManageLocationsServlet?action=" + ApplicationConstants.ACTION_LIST);
    }

    private void updateLocation(HttpServletRequest request, HttpServletResponse response) throws IOException, DataAccessException, ServletException {
        int id = Integer.parseInt(request.getParameter("locationId"));
        String locationName = request.getParameter("locationName");
        String capacityStr = request.getParameter("capacity");

        if (InputValidator.isNullOrEmpty(locationName)) {
            request.setAttribute(ApplicationConstants.REQ_ATTR_ERROR_MESSAGE, "Location Name is required.");
            preserveFormDataOnError(request, ApplicationConstants.ACTION_UPDATE);
            showEditForm(request, response);
            return;
        }

        Integer capacity = null;
        if (!InputValidator.isNullOrEmpty(capacityStr)) {
            if (!InputValidator.isInteger(capacityStr) || Integer.parseInt(capacityStr) < 0) {
                request.setAttribute(ApplicationConstants.REQ_ATTR_ERROR_MESSAGE, "Capacity must be a non-negative number.");
                preserveFormDataOnError(request, ApplicationConstants.ACTION_UPDATE);
                showEditForm(request, response);
                return;
            }
            capacity = Integer.parseInt(capacityStr);
        }

        Location location = locationDao.findById(id).orElseThrow(() -> new DataAccessException("Location not found for update."));
        location.setLocationName(locationName);
        location.setCapacity(capacity);

        locationDao.update(location);
        request.getSession().setAttribute(ApplicationConstants.SESSION_SUCCESS_MESSAGE, "Location updated successfully!");
        response.sendRedirect(request.getContextPath() + "/ManageLocationsServlet?action=" + ApplicationConstants.ACTION_LIST);
    }

    private void deleteLocation(HttpServletRequest request, HttpServletResponse response) throws IOException, DataAccessException {
        int id = Integer.parseInt(request.getParameter("id"));
        boolean success = locationDao.delete(id);
        if (success) {
            request.getSession().setAttribute(ApplicationConstants.SESSION_SUCCESS_MESSAGE, "Location deleted successfully!");
        } else {
            request.getSession().setAttribute(ApplicationConstants.SESSION_ERROR_MESSAGE, "Location could not be deleted or not found.");
        }
        response.sendRedirect(request.getContextPath() + "/ManageLocationsServlet?action=" + ApplicationConstants.ACTION_LIST);
    }
}