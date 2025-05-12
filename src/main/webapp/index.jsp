<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    // Simple redirect to the login page
    // Alternatively, could check session here or map '/' to a servlet
    String contextPath = request.getContextPath();
    response.sendRedirect(contextPath + "/auth/login.jsp");
%>
<%-- This page will likely not be displayed due to the redirect.
     Keeping a minimal HTML structure just in case. --%>
<!DOCTYPE html>
<html lang="en">
<head>
    <link rel="icon" href="${pageContext.request.contextPath}/assets/img/favicon.ico" type="image/x-icon">
    <meta charset="UTF-8">
    <title>Welcome - Student Management System</title>
    <style> body { font-family: sans-serif; padding: 20px; } </style>
</head>
<body>
<h1>Redirecting to Student Management System Login...</h1>
<p>If you are not redirected automatically, <a href="${pageContext.request.contextPath}/auth/login.jsp">click here</a>.</p>
</body>
</html>