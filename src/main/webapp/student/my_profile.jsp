<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%-- This page might need a servlet to pre-fetch student and user data --%>
<c:if test="${empty studentProfile || empty userProfile}">
    <%-- Redirect to a servlet that fetches this data --%>
    <%-- Example: <c:redirect url="${pageContext.request.contextPath}/StudentProfileServlet"/> --%>
    <%-- For now, assume data is populated by StudentDashboardServlet or another dedicated servlet --%>
</c:if>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>My Profile | Student Management System - Group_AC</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/styles.css">
    <style>
        .profile-details { margin-top: 20px; }
        .profile-details p { margin: 8px 0; }
        .profile-details strong { min-width: 150px; display: inline-block; }
    </style>
</head>
<body>
<jsp:include page="/common/header.jsp" />
<div class="container main-content">
    <h2>My Profile</h2>
    <c:if test="${not empty studentProfile}">
        <div class="profile-details">
            <h3>Personal Information</h3>
            <p><strong>Full Name:</strong> <c:out value="${studentProfile.firstName} ${studentProfile.lastName}"/></p>
            <p><strong>Student ID:</strong> <c:out value="${studentProfile.studentUniqueId}"/></p>
            <p><strong>Date of Birth:</strong> <fmt:formatDate value="${studentProfile.dateOfBirth}" pattern="yyyy-MM-dd"/></p>
            <p><strong>Gender:</strong> <c:out value="${studentProfile.gender}"/></p>
            <p><strong>Address:</strong> <c:out value="${studentProfile.address}"/></p>
            <p><strong>Phone Number:</strong> <c:out value="${studentProfile.phoneNumber}"/></p>

            <h3>Academic Information</h3>
            <p><strong>Program:</strong> <c:out value="${studentProfile.programName}"/></p>
            <p><strong>Enrollment Date:</strong> <fmt:formatDate value="${studentProfile.enrollmentDate}" pattern="yyyy-MM-dd"/></p>

            <c:if test="${not empty userProfile}">
                <h3>Account Information</h3>
                <p><strong>Login Email:</strong> <c:out value="${userProfile.email}"/></p>
                <p><strong>Username:</strong> <c:out value="${userProfile.username}"/></p>
                <%-- Link to change password if feature exists --%>
            </c:if>
        </div>
    </c:if>
    <c:if test="${empty studentProfile}">
        <p class="message error">Your profile information could not be loaded.</p>
    </c:if>
</div>
<jsp:include page="/common/footer.jsp" />
</body>
</html>
