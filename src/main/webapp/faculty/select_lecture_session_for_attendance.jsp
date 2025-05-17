<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Select Lecture Session for Attendance | Student Management System - Group_AC</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/styles.css">
</head>
<body>
<jsp:include page="/common/header.jsp" />
<div class="container main-content">
    <h2>Select Lecture Session for <c:out value="${course.courseCode} - ${course.courseName}"/></h2>
    <p>Term: <strong><c:out value="${term.termName}"/></strong></p>

    <c:if test="${not empty requestScope.errorMessage}">
        <p class="message error">${requestScope.errorMessage}</p>
    </c:if>
    <c:if test="${not empty sessionScope.errorMessage && empty requestScope.errorMessage}">
        <p class="message error">${sessionScope.errorMessage}</p>
        <c:remove var="errorMessage" scope="session"/>
    </c:if>

    <c:choose>
        <c:when test="${not empty lectureSessions}">
            <form action="${pageContext.request.contextPath}/FacultyRecordAttendanceServlet" method="get">
                <input type="hidden" name="action" value="recordAttendanceForm">
                <div class="form-group">
                    <label for="lectureSessionId">Select Session:</label>
                    <select id="lectureSessionId" name="lectureSessionId" required>
                        <option value="">-- Choose a Session --</option>
                        <c:forEach var="session" items="${lectureSessions}">
                            <option value="${session.lectureSessionId}">
                                <fmt:formatDate value="${session.sessionStartDatetime}" pattern="yyyy-MM-dd HH:mm"/> -
                                <fmt:formatDate value="${session.sessionEndDatetime}" pattern="HH:mm"/>
                                                                                                                     (<c:out value="${not empty session.locationName ? session.locationName : 'N/A'}"/>)
                            </option>
                        </c:forEach>
                    </select>
                </div>
                <div class="form-actions">
                    <button type="submit" class="button">Proceed to Mark Attendance</button>
                    <a href="${pageContext.request.contextPath}/FacultyRecordAttendanceServlet?action=viewSessionsForAttendance" class="button button-secondary">Back to Course Selection</a>
                </div>
            </form>
        </c:when>
        <c:otherwise>
            <p>No lecture sessions found for this course and term that you are assigned to.</p>
            <a href="${pageContext.request.contextPath}/FacultyRecordAttendanceServlet?action=viewSessionsForAttendance" class="button button-secondary">Back to Course Selection</a>
        </c:otherwise>
    </c:choose>
</div>
<jsp:include page="/common/footer.jsp" />
</body>
</html>