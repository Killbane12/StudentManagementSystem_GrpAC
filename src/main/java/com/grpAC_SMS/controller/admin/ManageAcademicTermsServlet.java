package com.grpAC_SMS.controller.admin;

import com.grpAC_SMS.dao.AcademicTermDao;
import com.grpAC_SMS.dao.impl.AcademicTermDaoImpl;
import com.grpAC_SMS.model.AcademicTerm;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.Date;
import java.util.List;

@WebServlet("/ManageAcademicTermsServlet")
public class ManageAcademicTermsServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(ManageAcademicTermsServlet.class);
    private AcademicTermDao academicTermDao;

    @Override
    public void init() {
        academicTermDao = new AcademicTermDaoImpl();
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
                    deleteTerm(request, response);
                    break;
                case ApplicationConstants.ACTION_LIST:
                default:
                    listTerms(request, response);
                    break;
            }
        } catch (DataAccessException e) {
            logger.error("DataAccessException in AcademicTerms GET: {}", e.getMessage());
            request.getSession().setAttribute(ApplicationConstants.SESSION_ERROR_MESSAGE, "Database operation failed: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/ManageAcademicTermsServlet?action=" + ApplicationConstants.ACTION_LIST);
        } catch (NumberFormatException e) {
            logger.error("NumberFormatException in AcademicTerms GET: {}", e.getMessage());
            request.getSession().setAttribute(ApplicationConstants.SESSION_ERROR_MESSAGE, "Invalid ID format.");
            response.sendRedirect(request.getContextPath() + "/ManageAcademicTermsServlet?action=" + ApplicationConstants.ACTION_LIST);
        } catch (Exception e) {
            logger.error("General Exception in AcademicTerms GET: {}", e.getMessage(), e);
            request.getSession().setAttribute(ApplicationConstants.SESSION_ERROR_MESSAGE, "An unexpected error occurred: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/ManageAcademicTermsServlet?action=" + ApplicationConstants.ACTION_LIST);
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        if (action == null) {
            response.sendRedirect(request.getContextPath() + "/ManageAcademicTermsServlet?action=" + ApplicationConstants.ACTION_LIST);
            return;
        }

        try {
            switch (action) {
                case ApplicationConstants.ACTION_CREATE:
                    createTerm(request, response);
                    break;
                case ApplicationConstants.ACTION_UPDATE:
                    updateTerm(request, response);
                    break;
                default:
                    listTerms(request, response);
                    break;
            }
        } catch (DataAccessException e) {
            logger.error("DataAccessException in AcademicTerms POST for action {}: {}", action, e.getMessage());
            request.setAttribute(ApplicationConstants.REQ_ATTR_ERROR_MESSAGE, "Operation failed: " + e.getMessage());
            preserveFormDataOnError(request, action);
            if (ApplicationConstants.ACTION_CREATE.equals(action)) {
                showNewForm(request, response);
            } else if (ApplicationConstants.ACTION_UPDATE.equals(action)) {
                showEditForm(request, response);
            } else {
                response.sendRedirect(request.getContextPath() + "/ManageAcademicTermsServlet?action=" + ApplicationConstants.ACTION_LIST);
            }
        } catch (Exception e) {
            logger.error("General Exception in AcademicTerms POST for action {}: {}",action, e.getMessage(), e);
            request.getSession().setAttribute(ApplicationConstants.SESSION_ERROR_MESSAGE, "An unexpected error occurred: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/ManageAcademicTermsServlet?action=" + ApplicationConstants.ACTION_LIST);
        }
    }

    private void preserveFormDataOnError(HttpServletRequest request, String action) {
        AcademicTerm term = new AcademicTerm();
        term.setTermName(request.getParameter("termName"));
        term.setStartDate(DateFormatter.stringToSqlDate(request.getParameter("startDate")));
        term.setEndDate(DateFormatter.stringToSqlDate(request.getParameter("endDate")));

        if (ApplicationConstants.ACTION_UPDATE.equals(action) && request.getParameter("academicTermId") != null) {
            try {
                term.setAcademicTermId(Integer.parseInt(request.getParameter("academicTermId")));
            } catch (NumberFormatException e) { /* ignore */ }
        }
        request.setAttribute("term", term);
    }

    private void listTerms(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<AcademicTerm> termList = academicTermDao.findAll();
        request.setAttribute("termList", termList);
        RequestDispatcher dispatcher = request.getRequestDispatcher("/admin/terms_list.jsp");
        dispatcher.forward(request, response);
    }

    private void showNewForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if(request.getAttribute("term") == null) request.setAttribute("term", new AcademicTerm());
        RequestDispatcher dispatcher = request.getRequestDispatcher("/admin/term_form.jsp");
        dispatcher.forward(request, response);
    }

    private void showEditForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if(request.getAttribute("term") == null) {
            int id = Integer.parseInt(request.getParameter("id"));
            AcademicTerm existingTerm = academicTermDao.findById(id)
                    .orElseThrow(() -> new DataAccessException("Academic Term not found with ID: " + id));
            request.setAttribute("term", existingTerm);
        }
        RequestDispatcher dispatcher = request.getRequestDispatcher("/admin/term_form.jsp");
        dispatcher.forward(request, response);
    }

    private void createTerm(HttpServletRequest request, HttpServletResponse response) throws IOException, DataAccessException, ServletException {
        String termName = request.getParameter("termName");
        String startDateStr = request.getParameter("startDate");
        String endDateStr = request.getParameter("endDate");

        if (InputValidator.isNullOrEmpty(termName) || !InputValidator.isValidDate(startDateStr) || !InputValidator.isValidDate(endDateStr)) {
            request.setAttribute(ApplicationConstants.REQ_ATTR_ERROR_MESSAGE, "Term Name and valid Start/End Dates are required.");
            preserveFormDataOnError(request, ApplicationConstants.ACTION_CREATE);
            showNewForm(request, response);
            return;
        }

        Date startDate = DateFormatter.stringToSqlDate(startDateStr);
        Date endDate = DateFormatter.stringToSqlDate(endDateStr);

        if (startDate.after(endDate)) {
            request.setAttribute(ApplicationConstants.REQ_ATTR_ERROR_MESSAGE, "Start Date must be before End Date.");
            preserveFormDataOnError(request, ApplicationConstants.ACTION_CREATE);
            showNewForm(request, response);
            return;
        }

        AcademicTerm term = new AcademicTerm();
        term.setTermName(termName);
        term.setStartDate(startDate);
        term.setEndDate(endDate);

        academicTermDao.add(term);
        request.getSession().setAttribute(ApplicationConstants.SESSION_SUCCESS_MESSAGE, "Academic Term created successfully!");
        response.sendRedirect(request.getContextPath() + "/ManageAcademicTermsServlet?action=" + ApplicationConstants.ACTION_LIST);
    }

    private void updateTerm(HttpServletRequest request, HttpServletResponse response) throws IOException, DataAccessException, ServletException {
        int id = Integer.parseInt(request.getParameter("academicTermId"));
        String termName = request.getParameter("termName");
        String startDateStr = request.getParameter("startDate");
        String endDateStr = request.getParameter("endDate");

        if (InputValidator.isNullOrEmpty(termName) || !InputValidator.isValidDate(startDateStr) || !InputValidator.isValidDate(endDateStr)) {
            request.setAttribute(ApplicationConstants.REQ_ATTR_ERROR_MESSAGE, "Term Name and valid Start/End Dates are required.");
            preserveFormDataOnError(request, ApplicationConstants.ACTION_UPDATE);
            showEditForm(request, response);
            return;
        }

        Date startDate = DateFormatter.stringToSqlDate(startDateStr);
        Date endDate = DateFormatter.stringToSqlDate(endDateStr);

        if (startDate.after(endDate)) {
            request.setAttribute(ApplicationConstants.REQ_ATTR_ERROR_MESSAGE, "Start Date must be before End Date.");
            preserveFormDataOnError(request, ApplicationConstants.ACTION_UPDATE);
            showEditForm(request, response);
            return;
        }

        AcademicTerm term = academicTermDao.findById(id).orElseThrow(() -> new DataAccessException("Academic Term not found for update."));
        term.setTermName(termName);
        term.setStartDate(startDate);
        term.setEndDate(endDate);

        academicTermDao.update(term);
        request.getSession().setAttribute(ApplicationConstants.SESSION_SUCCESS_MESSAGE, "Academic Term updated successfully!");
        response.sendRedirect(request.getContextPath() + "/ManageAcademicTermsServlet?action=" + ApplicationConstants.ACTION_LIST);
    }

    private void deleteTerm(HttpServletRequest request, HttpServletResponse response) throws IOException, DataAccessException {
        int id = Integer.parseInt(request.getParameter("id"));
        boolean success = academicTermDao.delete(id);
        if (success) {
            request.getSession().setAttribute(ApplicationConstants.SESSION_SUCCESS_MESSAGE, "Academic Term deleted successfully!");
        } else {
            request.getSession().setAttribute(ApplicationConstants.SESSION_ERROR_MESSAGE, "Academic Term could not be deleted or not found.");
        }
        response.sendRedirect(request.getContextPath() + "/ManageAcademicTermsServlet?action=" + ApplicationConstants.ACTION_LIST);
    }
}