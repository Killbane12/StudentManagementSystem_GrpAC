<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Unauthorized Access | Student Management System - Group_AC</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/styles.css">
    <style>
        .container {
            text-align: center;
            margin-top: 50px;
        }
    </style>
</head>
<body>
<jsp:include page="/common/header.jsp"/>
<div class="container main-content">
    <h2>Access Denied</h2>
    <p>Sorry, you do not have permission to access the requested page.</p>
    <p>
        <c:choose>
            <c:when test="${not empty sessionScope.loggedInUser}">
                <c:choose>
                    <c:when test="${sessionScope.loggedInUser.role == 'ADMIN'}">
                        <a href="${pageContext.request.contextPath}/admin/dashboard.jsp">Go to Admin Dashboard</a>
                    </c:when>
                    <c:when test="${sessionScope.loggedInUser.role == 'FACULTY'}">
                        <a href="${pageContext.request.contextPath}/faculty/dashboard.jsp">Go to Faculty Dashboard</a>
                    </c:when>
                    <c:when test="${sessionScope.loggedInUser.role == 'STUDENT'}">
                        <a href="${pageContext.request.contextPath}/student/dashboard.jsp">Go to Student Dashboard</a>
                    </c:when>
                    <c:otherwise>
                        <a href="${pageContext.request.contextPath}/auth/login.jsp">Go to Login</a>
                    </c:otherwise>
                </c:choose>
            </c:when>
            <c:otherwise>
                <a href="${pageContext.request.contextPath}/auth/login.jsp">Go to Login Page</a>
            </c:otherwise>
        </c:choose>
    </p>
</div>
<jsp:include page="/common/footer.jsp"/>
</body>
</html>