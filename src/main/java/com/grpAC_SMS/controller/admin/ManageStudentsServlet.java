package com.grpAC_SMS.controller.admin;

import com.grpAC_SMS.dao.ProgramDao;
import com.grpAC_SMS.dao.StudentDao;
import com.grpAC_SMS.dao.UserDao;
import com.grpAC_SMS.dao.impl.ProgramDaoImpl;
import com.grpAC_SMS.dao.impl.StudentDaoImpl;
import com.grpAC_SMS.dao.impl.UserDaoImpl;
import com.grpAC_SMS.exception.BusinessLogicException;
import com.grpAC_SMS.exception.DataAccessException;
import com.grpAC_SMS.model.Role;
import com.grpAC_SMS.model.Student;
import com.grpAC_SMS.model.User;
import com.grpAC_SMS.service.UserService;
import com.grpAC_SMS.service.impl.UserServiceImpl;
import com.grpAC_SMS.util.ApplicationConstants;
import com.grpAC_SMS.util.DateFormatter;
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

@WebServlet("/ManageStudentsServlet")
public class ManageStudentsServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(ManageStudentsServlet.class);
    private StudentDao studentDao;
    private ProgramDao programDao;
    private UserService userService; // For user account management
    private UserDao userDao; // For direct user checks if needed

    @Override
    public void init() {
        studentDao = new StudentDaoImpl();
        programDao = new ProgramDaoImpl();
        userService = new UserServiceImpl(); // Assumes UserServiceImpl uses UserDaoImpl
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
                    deleteStudent(request, response);
                    break;
                case ApplicationConstants.ACTION_LIST:
                default:
                    listStudents(request, response);
                    break;
            }
        } catch (DataAccessException | BusinessLogicException e) {
            logger.error("Exception in Students GET: {}", e.getMessage());
            request.getSession().setAttribute(ApplicationConstants.SESSION_ERROR_MESSAGE, "Operation failed: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/ManageStudentsServlet?action=" + ApplicationConstants.ACTION_LIST);
        } catch (NumberFormatException e) {
            logger.error("NumberFormatException in Students GET: {}", e.getMessage());
            request.getSession().setAttribute(ApplicationConstants.SESSION_ERROR_MESSAGE, "Invalid ID format.");
            response.sendRedirect(request.getContextPath() + "/ManageStudentsServlet?action=" + ApplicationConstants.ACTION_LIST);
        } catch (Exception e) {
            logger.error("General Exception in Students GET: {}", e.getMessage(), e);
            request.getSession().setAttribute(ApplicationConstants.SESSION_ERROR_MESSAGE, "An unexpected error occurred: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/ManageStudentsServlet?action=" + ApplicationConstants.ACTION_LIST);
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        if (action == null) {
            response.sendRedirect(request.getContextPath() + "/ManageStudentsServlet?action=" + ApplicationConstants.ACTION_LIST);
            return;
        }

        try {
            switch (action) {
                case ApplicationConstants.ACTION_CREATE:
                    createStudent(request, response);
                    break;
                case ApplicationConstants.ACTION_UPDATE:
                    updateStudent(request, response);
                    break;
                default:
                    listStudents(request, response);
                    break;
            }
        } catch (BusinessLogicException | DataAccessException e) {
            logger.error("Exception in Students POST for action {}: {}", action, e.getMessage());
            request.setAttribute(ApplicationConstants.REQ_ATTR_ERROR_MESSAGE, "Operation failed: " + e.getMessage());
            preserveFormDataOnError(request, action); // Preserve data and repopulate dropdowns
            if (ApplicationConstants.ACTION_CREATE.equals(action)) {
                showNewForm(request, response);
            } else if (ApplicationConstants.ACTION_UPDATE.equals(action)) {
                // For update, we might need to re-fetch the original student data if form is fully dynamic
                showEditForm(request, response); // Ensure ID is available for re-fetch or use preserved data
            } else {
                response.sendRedirect(request.getContextPath() + "/ManageStudentsServlet?action=" + ApplicationConstants.ACTION_LIST);
            }
        } catch (Exception e) {
            logger.error("General Exception in Students POST for action {}: {}", action, e.getMessage(), e);
            request.getSession().setAttribute(ApplicationConstants.SESSION_ERROR_MESSAGE, "An unexpected error occurred: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/ManageStudentsServlet?action=" + ApplicationConstants.ACTION_LIST);
        }
    }

    private void loadDropdownData(HttpServletRequest request) {
        if (request.getAttribute("programList") == null) request.setAttribute("programList", programDao.findAll());
    }

    private void preserveFormDataOnError(HttpServletRequest request, String action) {
        Student student = new Student();
        User user = new User(); // For user fields if creating new

        if (ApplicationConstants.ACTION_UPDATE.equals(action) && request.getParameter("studentId") != null) {
            try {
                student.setStudentId(Integer.parseInt(request.getParameter("studentId")));
                // If updating, also preserve user ID if it exists
                if (request.getParameter("userId") != null && !request.getParameter("userId").isEmpty()) {
                    student.setUserId(Integer.parseInt(request.getParameter("userId")));
                }
            } catch (NumberFormatException e) { /* ignore */ }
        }

        student.setStudentUniqueId(request.getParameter("studentUniqueId"));
        student.setFirstName(request.getParameter("firstName"));
        student.setLastName(request.getParameter("lastName"));
        student.setDateOfBirth(DateFormatter.stringToSqlDate(request.getParameter("dateOfBirth")));
        student.setGender(request.getParameter("gender"));
        student.setAddress(request.getParameter("address"));
        student.setPhoneNumber(request.getParameter("phoneNumber"));
        student.setEnrollmentDate(DateFormatter.stringToSqlDate(request.getParameter("enrollmentDate")));
        String programIdStr = request.getParameter("programId");
        if (!InputValidator.isNullOrEmpty(programIdStr) && InputValidator.isInteger(programIdStr) && Integer.parseInt(programIdStr) > 0) {
            student.setProgramId(Integer.parseInt(programIdStr));
        }

        // User details (relevant for create, or if editing user email via student form)
        user.setEmail(request.getParameter("email"));
        // Password is not preserved for obvious reasons

        request.setAttribute("student", student);
        request.setAttribute("user", user); // For user fields in form
        loadDropdownData(request);
    }


    private void listStudents(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<Student> studentList = studentDao.findAllWithDetails(); // Includes program name and user email
        request.setAttribute("studentList", studentList);
        RequestDispatcher dispatcher = request.getRequestDispatcher("/admin/students_list.jsp");
        dispatcher.forward(request, response);
    }

    private void showNewForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (request.getAttribute("student") == null) request.setAttribute("student", new Student());
        if (request.getAttribute("user") == null) request.setAttribute("user", new User()); // For email/password fields
        loadDropdownData(request);
        RequestDispatcher dispatcher = request.getRequestDispatcher("/admin/student_form.jsp");
        dispatcher.forward(request, response);
    }

    private void showEditForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (request.getAttribute("student") == null) { // Not from a failed POST
            int id = Integer.parseInt(request.getParameter("id"));
            Student existingStudent = studentDao.findById(id)
                    .orElseThrow(() -> new DataAccessException("Student not found with ID: " + id));
            request.setAttribute("student", existingStudent);

            User associatedUser = new User(); // Default empty user
            if (existingStudent.getUserId() != null) {
                associatedUser = userDao.findById(existingStudent.getUserId()).orElse(new User());
            }
            request.setAttribute("user", associatedUser);
        }
        // If it's from preserveFormDataOnError, student and user attributes are already set.
        loadDropdownData(request); // Ensure dropdowns are always populated
        RequestDispatcher dispatcher = request.getRequestDispatcher("/admin/student_form.jsp");
        dispatcher.forward(request, response);
    }

    private void createStudent(HttpServletRequest request, HttpServletResponse response) throws IOException, DataAccessException, BusinessLogicException, ServletException {
        // Student Profile Data
        String studentUniqueId = request.getParameter("studentUniqueId");
        String firstName = request.getParameter("firstName");
        String lastName = request.getParameter("lastName");
        String dobStr = request.getParameter("dateOfBirth");
        String gender = request.getParameter("gender");
        String address = request.getParameter("address");
        String phone = request.getParameter("phoneNumber");
        String enrollmentDateStr = request.getParameter("enrollmentDate");
        String programIdStr = request.getParameter("programId");

        // User Account Data
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        // Validations
        if (InputValidator.isNullOrEmpty(studentUniqueId) || InputValidator.isNullOrEmpty(firstName) ||
                InputValidator.isNullOrEmpty(lastName) || !InputValidator.isValidDate(dobStr) ||
                !InputValidator.isValidDate(enrollmentDateStr) || InputValidator.isNullOrEmpty(email) ||
                InputValidator.isNullOrEmpty(password)) {
            request.setAttribute(ApplicationConstants.REQ_ATTR_ERROR_MESSAGE, "Required fields are missing or have invalid formats (Unique ID, Name, DOB, Enrollment Date, Email, Password).");
            preserveFormDataOnError(request, ApplicationConstants.ACTION_CREATE);
            showNewForm(request, response);
            return;
        }
        if (!studentUniqueId.matches(ApplicationConstants.STUDENT_ID_RANGE_PATTERN)) {
            request.setAttribute(ApplicationConstants.REQ_ATTR_ERROR_MESSAGE, "Student Unique ID must be between " + ApplicationConstants.STUDENT_ID_MIN_RANGE + " and " + ApplicationConstants.STUDENT_ID_MAX_RANGE + ".");
            preserveFormDataOnError(request, ApplicationConstants.ACTION_CREATE);
            showNewForm(request, response);
            return;
        }


        // Create User first
        User newUser = null;
        try {
            newUser = userService.registerUser(email, password, email, Role.STUDENT, true); // Username is email for simplicity here, or generate one
        } catch (BusinessLogicException e) {
            logger.error("Failed to create user account for student: {}", e.getMessage());
            request.setAttribute(ApplicationConstants.REQ_ATTR_ERROR_MESSAGE, "Failed to create user account: " + e.getMessage());
            preserveFormDataOnError(request, ApplicationConstants.ACTION_CREATE);
            showNewForm(request, response);
            return;
        }

        // If user creation successful, proceed to create student profile
        Student student = new Student();
        student.setUserId(newUser.getUserId());
        student.setStudentUniqueId(studentUniqueId);
        student.setFirstName(firstName);
        student.setLastName(lastName);
        student.setDateOfBirth(DateFormatter.stringToSqlDate(dobStr));
        student.setGender(gender);
        student.setAddress(address);
        student.setPhoneNumber(phone);
        student.setEnrollmentDate(DateFormatter.stringToSqlDate(enrollmentDateStr));
        if (!InputValidator.isNullOrEmpty(programIdStr) && InputValidator.isInteger(programIdStr) && Integer.parseInt(programIdStr) > 0) {
            student.setProgramId(Integer.parseInt(programIdStr));
        }

        try {
            studentDao.add(student);
            request.getSession().setAttribute(ApplicationConstants.SESSION_SUCCESS_MESSAGE, "Student created successfully with User ID: " + newUser.getUserId());
            response.sendRedirect(request.getContextPath() + "/ManageStudentsServlet?action=" + ApplicationConstants.ACTION_LIST);
        } catch (DataAccessException e) {
            logger.error("Failed to create student profile after user creation: {}. Attempting to clean up user.", e.getMessage());
            // Rollback: Try to delete the created user if student profile creation fails
            try {
                userService.deleteUser(newUser.getUserId());
                logger.info("User {} cleaned up after failed student profile creation.", newUser.getUserId());
            } catch (BusinessLogicException | DataAccessException cleanupEx) {
                logger.error("Failed to cleanup user {} after student profile error: {}", newUser.getUserId(), cleanupEx.getMessage());
            }
            request.setAttribute(ApplicationConstants.REQ_ATTR_ERROR_MESSAGE, "Failed to create student profile: " + e.getMessage());
            preserveFormDataOnError(request, ApplicationConstants.ACTION_CREATE);
            showNewForm(request, response);
        }
    }

    private void updateStudent(HttpServletRequest request, HttpServletResponse response) throws IOException, DataAccessException, BusinessLogicException, ServletException {
        int studentId = Integer.parseInt(request.getParameter("studentId"));

        // Student Profile Data
        String studentUniqueId = request.getParameter("studentUniqueId");
        String firstName = request.getParameter("firstName");
        String lastName = request.getParameter("lastName");
        String dobStr = request.getParameter("dateOfBirth");
        String gender = request.getParameter("gender");
        String address = request.getParameter("address");
        String phone = request.getParameter("phoneNumber");
        String enrollmentDateStr = request.getParameter("enrollmentDate");
        String programIdStr = request.getParameter("programId");

        // User Account Data (only if being updated via this form, typically email/active status)
        // Password changes better handled by ManageUsersServlet or a dedicated "change password" form
        // String email = request.getParameter("email"); // If allowing email change from here

        if (InputValidator.isNullOrEmpty(studentUniqueId) || InputValidator.isNullOrEmpty(firstName) ||
                InputValidator.isNullOrEmpty(lastName) || !InputValidator.isValidDate(dobStr) ||
                !InputValidator.isValidDate(enrollmentDateStr)) {
            request.setAttribute(ApplicationConstants.REQ_ATTR_ERROR_MESSAGE, "Required student profile fields are missing or have invalid formats.");
            preserveFormDataOnError(request, ApplicationConstants.ACTION_UPDATE);
            showEditForm(request, response);
            return;
        }
        if (!studentUniqueId.matches(ApplicationConstants.STUDENT_ID_RANGE_PATTERN)) {
            request.setAttribute(ApplicationConstants.REQ_ATTR_ERROR_MESSAGE, "Student Unique ID must be between " + ApplicationConstants.STUDENT_ID_MIN_RANGE + " and " + ApplicationConstants.STUDENT_ID_MAX_RANGE + ".");
            preserveFormDataOnError(request, ApplicationConstants.ACTION_UPDATE);
            showEditForm(request, response);
            return;
        }


        Student student = studentDao.findById(studentId).orElseThrow(() -> new DataAccessException("Student not found for update."));
        student.setStudentUniqueId(studentUniqueId);
        student.setFirstName(firstName);
        student.setLastName(lastName);
        student.setDateOfBirth(DateFormatter.stringToSqlDate(dobStr));
        student.setGender(gender);
        student.setAddress(address);
        student.setPhoneNumber(phone);
        student.setEnrollmentDate(DateFormatter.stringToSqlDate(enrollmentDateStr));

        student.setProgramId(null); // Reset optional
        if (!InputValidator.isNullOrEmpty(programIdStr) && InputValidator.isInteger(programIdStr) && Integer.parseInt(programIdStr) > 0) {
            student.setProgramId(Integer.parseInt(programIdStr));
        }

        studentDao.update(student);

        // If user details like email are also updated from this form (simplified):
        // User associatedUser = userDao.findById(student.getUserId()).orElse(null);
        // if(associatedUser != null && !InputValidator.isNullOrEmpty(email) && !associatedUser.getEmail().equals(email)) {
        //    associatedUser.setEmail(email);
        //    userService.updateUser(associatedUser, null); // null for password means not changing
        // }

        request.getSession().setAttribute(ApplicationConstants.SESSION_SUCCESS_MESSAGE, "Student profile updated successfully!");
        response.sendRedirect(request.getContextPath() + "/ManageStudentsServlet?action=" + ApplicationConstants.ACTION_LIST);
    }

    private void deleteStudent(HttpServletRequest request, HttpServletResponse response) throws IOException, DataAccessException, BusinessLogicException {
        int studentId = Integer.parseInt(request.getParameter("id"));
        Student studentToDelete = studentDao.findById(studentId)
                .orElseThrow(() -> new DataAccessException("Student to delete not found with ID: " + studentId));

        boolean studentProfileDeleted = studentDao.delete(studentId);

        if (studentProfileDeleted) {
            // Optionally, delete or deactivate the associated user account
            if (studentToDelete.getUserId() != null) {
                try {
                    // Decide whether to delete or just deactivate user. Deactivating is safer.
                    // userService.deleteUser(studentToDelete.getUserId());
                    boolean statusChanged = userService.changeUserStatus(studentToDelete.getUserId(), false); // Deactivate
                    if (statusChanged)
                        logger.info("Associated user {} deactivated for deleted student {}.", studentToDelete.getUserId(), studentId);
                    else
                        logger.warn("Could not deactivate associated user {} for student {}.", studentToDelete.getUserId(), studentId);

                } catch (Exception e) {
                    logger.error("Error managing associated user account {} for deleted student {}: {}",
                            studentToDelete.getUserId(), studentId, e.getMessage());
                    // Continue, student profile deletion was the primary goal
                }
            }
            request.getSession().setAttribute(ApplicationConstants.SESSION_SUCCESS_MESSAGE, "Student deleted successfully!");
        } else {
            request.getSession().setAttribute(ApplicationConstants.SESSION_ERROR_MESSAGE, "Student profile could not be deleted or was not found.");
        }
        response.sendRedirect(request.getContextPath() + "/ManageStudentsServlet?action=" + ApplicationConstants.ACTION_LIST);
    }
}