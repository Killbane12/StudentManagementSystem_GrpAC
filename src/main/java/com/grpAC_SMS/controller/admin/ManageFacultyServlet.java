package com.grpAC_SMS.controller.admin;

import com.grpAC_SMS.dao.DepartmentDao;
import com.grpAC_SMS.dao.FacultyDao;
import com.grpAC_SMS.dao.UserDao;
import com.grpAC_SMS.dao.impl.DepartmentDaoImpl;
import com.grpAC_SMS.dao.impl.FacultyDaoImpl;
import com.grpAC_SMS.dao.impl.UserDaoImpl;
import com.grpAC_SMS.exception.BusinessLogicException;
import com.grpAC_SMS.exception.DataAccessException;
import com.grpAC_SMS.model.Faculty;
import com.grpAC_SMS.model.Role;
import com.grpAC_SMS.model.User;
import com.grpAC_SMS.service.UserService;
import com.grpAC_SMS.service.impl.UserServiceImpl;
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

@WebServlet("/ManageFacultyServlet")
public class ManageFacultyServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(ManageFacultyServlet.class);
    private FacultyDao facultyDao;
    private DepartmentDao departmentDao;
    private UserService userService;
    private UserDao userDao;

    @Override
    public void init() {
        facultyDao = new FacultyDaoImpl();
        departmentDao = new DepartmentDaoImpl();
        userService = new UserServiceImpl();
        userDao = new UserDaoImpl();
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
                    deleteFaculty(request, response);
                    break;
                case ApplicationConstants.ACTION_LIST:
                default:
                    listFaculty(request, response);
                    break;
            }
        } catch (DataAccessException | BusinessLogicException e) {
            logger.error("Exception in Faculty GET: {}", e.getMessage());
            request.getSession().setAttribute(ApplicationConstants.SESSION_ERROR_MESSAGE, "Operation failed: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/ManageFacultyServlet?action=" + ApplicationConstants.ACTION_LIST);
        } catch (NumberFormatException e) {
            logger.error("NumberFormatException in Faculty GET: {}", e.getMessage());
            request.getSession().setAttribute(ApplicationConstants.SESSION_ERROR_MESSAGE, "Invalid ID format.");
            response.sendRedirect(request.getContextPath() + "/ManageFacultyServlet?action=" + ApplicationConstants.ACTION_LIST);
        } catch (Exception e) {
            logger.error("General Exception in Faculty GET: {}", e.getMessage(), e);
            request.getSession().setAttribute(ApplicationConstants.SESSION_ERROR_MESSAGE, "An unexpected error occurred: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/ManageFacultyServlet?action=" + ApplicationConstants.ACTION_LIST);
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        if (action == null) {
            response.sendRedirect(request.getContextPath() + "/ManageFacultyServlet?action=" + ApplicationConstants.ACTION_LIST);
            return;
        }

        try {
            switch (action) {
                case ApplicationConstants.ACTION_CREATE:
                    createFaculty(request, response);
                    break;
                case ApplicationConstants.ACTION_UPDATE:
                    updateFaculty(request, response);
                    break;
                default:
                    listFaculty(request, response);
                    break;
            }
        } catch (BusinessLogicException | DataAccessException e) {
            logger.error("Exception in Faculty POST for action {}: {}", action, e.getMessage());
            request.setAttribute(ApplicationConstants.REQ_ATTR_ERROR_MESSAGE, "Operation failed: " + e.getMessage());
            preserveFormDataOnError(request, action);
            if (ApplicationConstants.ACTION_CREATE.equals(action)) {
                showNewForm(request, response);
            } else if (ApplicationConstants.ACTION_UPDATE.equals(action)) {
                showEditForm(request, response);
            } else {
                response.sendRedirect(request.getContextPath() + "/ManageFacultyServlet?action=" + ApplicationConstants.ACTION_LIST);
            }
        } catch (Exception e) {
            logger.error("General Exception in Faculty POST for action {}: {}", action, e.getMessage(), e);
            request.getSession().setAttribute(ApplicationConstants.SESSION_ERROR_MESSAGE, "An unexpected error occurred: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/ManageFacultyServlet?action=" + ApplicationConstants.ACTION_LIST);
        }
    }

    private void loadDropdownData(HttpServletRequest request) {
        if (request.getAttribute("departmentList") == null)
            request.setAttribute("departmentList", departmentDao.findAll());
    }

    private void preserveFormDataOnError(HttpServletRequest request, String action) {
        Faculty faculty = new Faculty();
        User user = new User();

        if (ApplicationConstants.ACTION_UPDATE.equals(action) && request.getParameter("facultyMemberId") != null) {
            try {
                faculty.setFacultyMemberId(Integer.parseInt(request.getParameter("facultyMemberId")));
                if (request.getParameter("userId") != null && !request.getParameter("userId").isEmpty()) {
                    faculty.setUserId(Integer.parseInt(request.getParameter("userId")));
                }
            } catch (NumberFormatException e) { /* ignore */ }
        }
        faculty.setFacultyUniqueId(request.getParameter("facultyUniqueId"));
        faculty.setFirstName(request.getParameter("firstName"));
        faculty.setLastName(request.getParameter("lastName"));
        String deptIdStr = request.getParameter("departmentId");
        if (!InputValidator.isNullOrEmpty(deptIdStr) && InputValidator.isInteger(deptIdStr)) {
            faculty.setDepartmentId(Integer.parseInt(deptIdStr));
        }
        faculty.setOfficeLocation(request.getParameter("officeLocation"));
        faculty.setContactEmail(request.getParameter("contactEmailFaculty")); // Use a different name than user email if needed
        faculty.setPhoneNumber(request.getParameter("phoneNumber"));

        user.setEmail(request.getParameter("email")); // User login email

        request.setAttribute("faculty", faculty);
        request.setAttribute("user", user);
        loadDropdownData(request);
    }

    private void listFaculty(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<Faculty> facultyList = facultyDao.findAllWithDetails();
        request.setAttribute("facultyList", facultyList);
        RequestDispatcher dispatcher = request.getRequestDispatcher("/admin/faculty_list.jsp");
        dispatcher.forward(request, response);
    }

    private void showNewForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (request.getAttribute("faculty") == null) request.setAttribute("faculty", new Faculty());
        if (request.getAttribute("user") == null) request.setAttribute("user", new User());
        loadDropdownData(request);
        RequestDispatcher dispatcher = request.getRequestDispatcher("/admin/faculty_form.jsp");
        dispatcher.forward(request, response);
    }

    private void showEditForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (request.getAttribute("faculty") == null) {
            int id = Integer.parseInt(request.getParameter("id"));
            Faculty existingFaculty = facultyDao.findById(id)
                    .orElseThrow(() -> new DataAccessException("Faculty not found with ID: " + id));
            request.setAttribute("faculty", existingFaculty);

            User associatedUser = new User();
            if (existingFaculty.getUserId() != null) {
                associatedUser = userDao.findById(existingFaculty.getUserId()).orElse(new User());
            }
            request.setAttribute("user", associatedUser);
        }
        loadDropdownData(request);
        RequestDispatcher dispatcher = request.getRequestDispatcher("/admin/faculty_form.jsp");
        dispatcher.forward(request, response);
    }

    private void createFaculty(HttpServletRequest request, HttpServletResponse response) throws IOException, DataAccessException, BusinessLogicException, ServletException {
        // Faculty Profile Data
        String facultyUniqueId = request.getParameter("facultyUniqueId");
        String firstName = request.getParameter("firstName");
        String lastName = request.getParameter("lastName");
        String departmentIdStr = request.getParameter("departmentId");
        String officeLocation = request.getParameter("officeLocation");
        String contactEmailFaculty = request.getParameter("contactEmailFaculty"); // Specific contact email for faculty profile
        String phone = request.getParameter("phoneNumber");

        // User Account Data
        String email = request.getParameter("email"); // Login email for user account
        String password = request.getParameter("password");

        if (InputValidator.isNullOrEmpty(facultyUniqueId) || InputValidator.isNullOrEmpty(firstName) ||
                InputValidator.isNullOrEmpty(lastName) || InputValidator.isNullOrEmpty(departmentIdStr) ||
                InputValidator.isNullOrEmpty(email) || InputValidator.isNullOrEmpty(password)) {
            request.setAttribute(ApplicationConstants.REQ_ATTR_ERROR_MESSAGE, "Required fields missing (Unique ID, Name, Department, Login Email, Password).");
            preserveFormDataOnError(request, ApplicationConstants.ACTION_CREATE);
            showNewForm(request, response);
            return;
        }
        if (!InputValidator.isInteger(departmentIdStr)) {
            request.setAttribute(ApplicationConstants.REQ_ATTR_ERROR_MESSAGE, "Invalid Department ID.");
            preserveFormDataOnError(request, ApplicationConstants.ACTION_CREATE);
            showNewForm(request, response);
            return;
        }

        User newUser = null;
        try {
            newUser = userService.registerUser(email, password, email, Role.FACULTY, true);
        } catch (BusinessLogicException e) {
            logger.error("Failed to create user account for faculty: {}", e.getMessage());
            request.setAttribute(ApplicationConstants.REQ_ATTR_ERROR_MESSAGE, "Failed to create user account: " + e.getMessage());
            preserveFormDataOnError(request, ApplicationConstants.ACTION_CREATE);
            showNewForm(request, response);
            return;
        }

        Faculty faculty = new Faculty();
        faculty.setUserId(newUser.getUserId());
        faculty.setFacultyUniqueId(facultyUniqueId);
        faculty.setFirstName(firstName);
        faculty.setLastName(lastName);
        faculty.setDepartmentId(Integer.parseInt(departmentIdStr));
        faculty.setOfficeLocation(officeLocation);
        faculty.setContactEmail(contactEmailFaculty); // Can be same as login email or different
        faculty.setPhoneNumber(phone);

        try {
            facultyDao.add(faculty);
            request.getSession().setAttribute(ApplicationConstants.SESSION_SUCCESS_MESSAGE, "Faculty member created successfully with User ID: " + newUser.getUserId());
            response.sendRedirect(request.getContextPath() + "/ManageFacultyServlet?action=" + ApplicationConstants.ACTION_LIST);
        } catch (DataAccessException e) {
            logger.error("Failed to create faculty profile after user creation: {}. Attempting to clean up user.", e.getMessage());
            try {
                userService.deleteUser(newUser.getUserId());
                logger.info("User {} cleaned up after failed faculty profile creation.", newUser.getUserId());
            } catch (BusinessLogicException | DataAccessException cleanupEx) {
                logger.error("Failed to cleanup user {} after faculty profile error: {}", newUser.getUserId(), cleanupEx.getMessage());
            }
            request.setAttribute(ApplicationConstants.REQ_ATTR_ERROR_MESSAGE, "Failed to create faculty profile: " + e.getMessage());
            preserveFormDataOnError(request, ApplicationConstants.ACTION_CREATE);
            showNewForm(request, response);
        }
    }

    private void updateFaculty(HttpServletRequest request, HttpServletResponse response) throws IOException, DataAccessException, BusinessLogicException, ServletException {
        int facultyMemberId = Integer.parseInt(request.getParameter("facultyMemberId"));

        String facultyUniqueId = request.getParameter("facultyUniqueId");
        String firstName = request.getParameter("firstName");
        String lastName = request.getParameter("lastName");
        String departmentIdStr = request.getParameter("departmentId");
        String officeLocation = request.getParameter("officeLocation");
        String contactEmailFaculty = request.getParameter("contactEmailFaculty");
        String phone = request.getParameter("phoneNumber");

        if (InputValidator.isNullOrEmpty(facultyUniqueId) || InputValidator.isNullOrEmpty(firstName) ||
                InputValidator.isNullOrEmpty(lastName) || InputValidator.isNullOrEmpty(departmentIdStr)) {
            request.setAttribute(ApplicationConstants.REQ_ATTR_ERROR_MESSAGE, "Required faculty profile fields are missing.");
            preserveFormDataOnError(request, ApplicationConstants.ACTION_UPDATE);
            showEditForm(request, response);
            return;
        }
        if (!InputValidator.isInteger(departmentIdStr)) {
            request.setAttribute(ApplicationConstants.REQ_ATTR_ERROR_MESSAGE, "Invalid Department ID.");
            preserveFormDataOnError(request, ApplicationConstants.ACTION_UPDATE);
            showEditForm(request, response);
            return;
        }


        Faculty faculty = facultyDao.findById(facultyMemberId).orElseThrow(() -> new DataAccessException("Faculty not found for update."));
        faculty.setFacultyUniqueId(facultyUniqueId);
        faculty.setFirstName(firstName);
        faculty.setLastName(lastName);
        faculty.setDepartmentId(Integer.parseInt(departmentIdStr));
        faculty.setOfficeLocation(officeLocation);
        faculty.setContactEmail(contactEmailFaculty);
        faculty.setPhoneNumber(phone);

        facultyDao.update(faculty);

        // User details update (e.g., email) if faculty.getUserId() is not null would go here
        // String userEmailLogin = request.getParameter("email");
        // if (faculty.getUserId() != null && !InputValidator.isNullOrEmpty(userEmailLogin)) {
        //     User userToUpdate = userDao.findById(faculty.getUserId()).orElse(null);
        //     if (userToUpdate != null && !userToUpdate.getEmail().equals(userEmailLogin)) {
        //         userToUpdate.setEmail(userEmailLogin); //Potentially also username if they are linked
        //         userService.updateUser(userToUpdate, null); // null for password
        //     }
        // }

        request.getSession().setAttribute(ApplicationConstants.SESSION_SUCCESS_MESSAGE, "Faculty profile updated successfully!");
        response.sendRedirect(request.getContextPath() + "/ManageFacultyServlet?action=" + ApplicationConstants.ACTION_LIST);
    }

    private void deleteFaculty(HttpServletRequest request, HttpServletResponse response) throws IOException, DataAccessException, BusinessLogicException {
        int facultyMemberId = Integer.parseInt(request.getParameter("id"));
        Faculty facultyToDelete = facultyDao.findById(facultyMemberId)
                .orElseThrow(() -> new DataAccessException("Faculty to delete not found with ID: " + facultyMemberId));

        boolean facultyProfileDeleted = facultyDao.delete(facultyMemberId);

        if (facultyProfileDeleted) {
            if (facultyToDelete.getUserId() != null) {
                try {
                    boolean statusChanged = userService.changeUserStatus(facultyToDelete.getUserId(), false); // Deactivate
                    if (statusChanged)
                        logger.info("Associated user {} deactivated for deleted faculty {}.", facultyToDelete.getUserId(), facultyMemberId);
                    else
                        logger.warn("Could not deactivate associated user {} for faculty {}.", facultyToDelete.getUserId(), facultyMemberId);
                } catch (Exception e) {
                    logger.error("Error managing associated user account {} for deleted faculty {}: {}",
                            facultyToDelete.getUserId(), facultyMemberId, e.getMessage());
                }
            }
            request.getSession().setAttribute(ApplicationConstants.SESSION_SUCCESS_MESSAGE, "Faculty member deleted successfully!");
        } else {
            request.getSession().setAttribute(ApplicationConstants.SESSION_ERROR_MESSAGE, "Faculty profile could not be deleted or was not found.");
        }
        response.sendRedirect(request.getContextPath() + "/ManageFacultyServlet?action=" + ApplicationConstants.ACTION_LIST);
    }
}