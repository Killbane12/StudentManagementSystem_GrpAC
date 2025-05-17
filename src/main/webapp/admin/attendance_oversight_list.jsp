<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Attendance Oversight | Student Management System - Group_AC</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/styles.css">
</head>
<body>
<jsp:include page="/common/header.jsp" />
<div class="container main-content">
    <h2>Attendance Oversight</h2>
    <c:if test="${not empty sessionScope.successMessage}">
        <p class="message success">${sessionScope.successMessage}</p>
        <c:remove var="successMessage" scope="session"/>
    </c:if>
    <c:if test="${not empty sessionScope.errorMessage}">
        <p class="message error">${sessionScope.errorMessage}</p>
        <c:remove var="errorMessage" scope="session"/>
    </c:if>
    <%-- TODO: Add filters: Student, Course, Session, Date Range --%>
    <table class="data-table">
        <thead>
        <tr>
            <th>Att. ID</th>
            <th>Student Name (ID)</th>
            <th>Course Name</th>
            <th>Session Start</th>
            <th>Status</th>
            <th>Punch-in Time</th>
            <th>Device ID</th>
            <th>Actions</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach var="att" items="${attendanceList}"> <%-- From AdminManageAttendanceServlet --%>
            <tr>
                <td>${att.attendanceId}</td>
                <td><c:out value="${att.studentName} (${att.studentUniqueId})"/></td>
                <td><c:out value="${att.courseName}"/></td>
                <td><fmt:formatDate value="${att.sessionStartDateTime}" pattern="yyyy-MM-dd HH:mm"/></td>
                <td>${att.present ? 'Present' : 'Absent'}</td>
                <td><fmt:formatDate value="${att.punchInTimestamp}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
                <td><c:out value="${att.deviceId}"/></td>
                <td class="actions">
                        <%-- <a href="${pageContext.request.contextPath}/AdminManageAttendanceServlet?action=edit&id=${att.attendanceId}">Edit</a> --%>
                    <span style="color: #777;">No direct actions</span>
                </td>
            </tr>
        </c:forEach>
        <c:if test="${empty attendanceList}">
            <tr><td colspan="8">No attendance records found.</td></tr>
        </c:if>
        </tbody>
    </table>
</div>
<jsp:include page="/common/footer.jsp" />
</body>
</html>