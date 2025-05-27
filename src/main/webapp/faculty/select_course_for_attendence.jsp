<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Select Course for Attendance | Student Management System - Group_AC</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/styles.css">
</head>
<body>
<jsp:include page="/common/header.jsp" />
<div class="container main-content">
    <h2>Select Course for Attendance Marking</h2>
    <p>Current Term: <strong><c:out value="${currentTerm.termName}"/></strong></p>

    <c:if test="${not empty requestScope.errorMessage}">
        <p class="message error">${requestScope.errorMessage}</p>
    </c:if>
    <c:if test="${not empty sessionScope.errorMessage && empty requestScope.errorMessage}">
        <p class="message error">${sessionScope.errorMessage}</p>
        <c:remove var="errorMessage" scope="session"/>
    </c:if>

    <c:choose>
        <c:when test="${not empty coursesForAttendance}">
            <form action="${pageContext.request.contextPath}/FacultyRecordAttendanceServlet" method="get">
                <input type="hidden" name="action" value="selectSession">
                <input type="hidden" name="termId" value="${currentTermId}">
                <div class="form-group">
                    <label for="courseId">Select Course:</label>
                    <select id="courseId" name="courseId" required>
                        <option value="">-- Choose a Course --</option>
                        <c:forEach var="course" items="${coursesForAttendance}">
                            <option value="${course.courseId}"><c:out value="${course.courseCode} - ${course.courseName}"/></option>
                        </c:forEach>
                    </select>
                </div>
                <div class="form-actions">
                    <button type="submit" class="button">View Lecture Sessions</button>
                </div>
            </form>
        </c:when>
        <c:otherwise>
            <p>You are not assigned to teach any courses this term, or no courses are available.</p>
        </c:otherwise>
    </c:choose>
</div>
<jsp:include page="/common/footer.jsp" />
</body>
</html>