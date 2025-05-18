<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>My Enrolled Courses | Student Management System - Group_AC</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/styles.css">
</head>
<body>
<jsp:include page="/common/header.jsp" />
<div class="container main-content">
    <h2>My Enrolled Courses</h2>
    <c:if test="${not empty errorMessage}"><p class="message error">${errorMessage}</p></c:if>

    <c:forEach var="termEntry" items="${enrollmentsByTerm}"> <%-- Assuming servlet prepares Map<AcademicTerm, List<Enrollment>> --%>
        <h3>Term: <c:out value="${termEntry.key.termName}"/> (<fmt:formatDate value="${termEntry.key.startDate}" pattern="MMM yyyy"/> - <fmt:formatDate value="${termEntry.key.endDate}" pattern="MMM yyyy"/>)</h3>
        <c:choose>
            <c:when test="${not empty termEntry.value}">
                <table class="data-table">
                    <thead><tr><th>Course Code</th><th>Course Name</th><th>Enrollment Date</th><th>Status</th><th>Actions</th></tr></thead>
                    <tbody>
                    <c:forEach var="enrollment" items="${termEntry.value}">
                        <tr>
                            <td><c:out value="${enrollment.courseCode}"/></td>
                            <td><c:out value="${enrollment.courseName}"/></td>
                            <td><fmt:formatDate value="${enrollment.enrollmentDate}" pattern="yyyy-MM-dd"/></td>
                            <td><c:out value="${enrollment.status}"/></td>
                            <td>
                                <a href="${pageContext.request.contextPath}/StudentViewGradesServlet?courseId=${enrollment.courseId}">View Grades</a>
                                <a href="${pageContext.request.contextPath}/StudentViewAttendanceServlet?courseId=${enrollment.courseId}">View Attendance</a>
                            </td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </c:when>
            <c:otherwise>
                <p>No courses enrolled for this term.</p>
            </c:otherwise>
        </c:choose>
    </c:forEach>
    <c:if test="${empty enrollmentsByTerm}">
        <p>You have no enrollment history.</p>
    </c:if>
</div>
<jsp:include page="/common/footer.jsp" />
</body>
</html>