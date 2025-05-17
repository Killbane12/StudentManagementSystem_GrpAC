<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
    // Check if user is already logged in
    if (session.getAttribute("loggedInUser") != null) {
        com.grpAC_SMS.model.User user = (com.grpAC_SMS.model.User) session.getAttribute("loggedInUser");
        String dashboardUrl = "";
        switch (user.getRole()) {
            case ADMIN:
                dashboardUrl = request.getContextPath() + "/dashboard.jsp" /* or "/admin/dashboard.jsp"*/;
                System.out.println(request.getContextPath());
                break;
            case FACULTY:
                dashboardUrl = request.getContextPath() + "/faculty/dashboard.jsp";
                break;
            case STUDENT:
                // Could be StudentDashboardServlet or student/dashboard.jsp directly if servlet pre-populates
                dashboardUrl = request.getContextPath() + "/StudentDashboardServlet";
                break;
            default:
                dashboardUrl = request.getContextPath() + "/auth/login.jsp"; // Fallback
                break;
        }
        response.sendRedirect(dashboardUrl);
    } else {
        // If not logged in, redirect to login page
        response.sendRedirect(request.getContextPath() + "/auth/login.jsp");
    }
%>
<%-- This page will effectively be blank as it always redirects --%>
<!DOCTYPE html>
<html>
<head>
    <title>Redirecting... | Student Management System - Group_AC</title>
</head>
<body>
<p>Please wait while you are being redirected...</p>
</body>
</html>