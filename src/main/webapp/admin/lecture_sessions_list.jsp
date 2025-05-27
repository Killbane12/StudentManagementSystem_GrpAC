<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page import="java.time.format.DateTimeFormatter" %> <%-- Import for direct formatting --%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Manage Lecture Sessions | Student Management System - Group_AC</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/styles.css">
</head>
<body>
<jsp:include page="/common/header.jsp" />
<div class="container main-content">
    <h2>Lecture Session Management</h2>
    <c:if test="${not empty sessionScope.successMessage}">
        <p class="message success">${sessionScope.successMessage}</p>
        <c:remove var="successMessage" scope="session"/>
    </c:if>
    <c:if test="${not empty sessionScope.errorMessage}">
        <p class="message error">${sessionScope.errorMessage}</p>
        <c:remove var="errorMessage" scope="session"/>
    </c:if>
    <a href="${pageContext.request.contextPath}/ManageLectureSessionsServlet?action=add" class="button button-add">Add New Lecture Session</a>
    <table class="data-table">
        <thead>
        <tr>
            <th>ID</th>
            <th>Course</th>
            <th>Faculty</th>
            <th>Term</th>
            <th>Location</th>
            <th>Start Time</th>
            <th>End Time</th>
            <th>Actions</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach var="session" items="${sessionList}">
            <tr>
                <td>${session.lectureSessionId}</td>
                <td><c:out value="${session.courseName}"/></td>
                <td><c:out value="${not empty session.facultyName ? session.facultyName : 'N/A'}"/></td>
                <td><c:out value="${session.termName}"/></td>
                <td><c:out value="${not empty session.locationName ? session.locationName : 'N/A'}"/></td>
                <td>
                    <c:if test="${not empty session.sessionStartDatetime}">
                        <%-- Directly format LocalDateTime using its methods --%>
                        ${session.sessionStartDatetime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))}
                    </c:if>
                </td>
                <td>
                    <c:if test="${not empty session.sessionEndDatetime}">
                        ${session.sessionEndDatetime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))}
                    </c:if>
                </td>
                <td class="actions">
                    <a href="${pageContext.request.contextPath}/ManageLectureSessionsServlet?action=edit&id=${session.lectureSessionId}">Edit</a>
                    <a href="${pageContext.request.contextPath}/ManageLectureSessionsServlet?action=delete&id=${session.lectureSessionId}" class="delete"
                       onclick="return confirm('Are you sure you want to delete this lecture session?');">Delete</a>
                </td>
            </tr>
        </c:forEach>
        <c:if test="${empty sessionList}">
            <tr><td colspan="8">No lecture sessions found.</td></tr>
        </c:if>
        </tbody>
    </table>
</div>
<jsp:include page="/common/footer.jsp" />
</body>
</html>