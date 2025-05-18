<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>My Attendance | Student Management System - Group_AC</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/styles.css">
</head>
<body>
<jsp:include page="/common/header.jsp" />
<div class="container main-content">
    <h2>My Attendance Records</h2>
    <c:if test="${not empty errorMessage}">
        <p class="message error">${errorMessage}</p>
    </c:if>

    <c:forEach var="courseView" items="${attendanceByCourseViews}">
        <div class="section">
            <h3>Course: <c:out value="${courseView.course.courseCode}"/> - <c:out value="${courseView.course.courseName}"/></h3>
            <p>Overall Attendance:
                <strong><fmt:formatNumber value="${courseView.percentage}" maxFractionDigits="1"/>%</strong>
            <div class="progress-bar-container" style="height:20px; background-color:#e0e0e0; border-radius:4px; margin-top:5px;">
                <div class="progress-bar" style="width: ${courseView.percentage}%; background-color: #4CAF50; height:100%; border-radius:4px; text-align:center; color:white; line-height:20px;">
                    <fmt:formatNumber value="${courseView.percentage}" maxFractionDigits="0"/>%
                </div>
            </div>
            </p>
            <c:if test="${not empty courseView.detailedRecords}">
                <h4>Detailed Session Records:</h4>
                <table class="data-table">
                    <thead>
                    <tr>
                        <th>Session Date & Time</th>
                        <th>Status</th>
                        <th>Device ID (If NFC)</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach var="attDetail" items="${courseView.detailedRecords}">
                        <tr>
                            <td><fmt:formatDate value="${attDetail.sessionStartDateTime}" pattern="yyyy-MM-dd HH:mm"/></td>
                            <td>${attDetail.present ? 'Present' : 'Absent'}</td>
                            <td><c:out value="${attDetail.deviceId}"/></td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </c:if>
            <c:if test="${empty courseView.detailedRecords}">
                <p>No detailed attendance data available for this course offering.</p>
            </c:if>
        </div>
    </c:forEach>
    <c:if test="${empty attendanceByCourseViews && empty errorMessage}">
        <p>No attendance records found for any of your courses.</p>
    </c:if>
</div>
<jsp:include page="/common/footer.jsp" />
</body>
</html>