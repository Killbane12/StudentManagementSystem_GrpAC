<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:if test="${empty facultyDashboardData}"> <%-- Servlet should set this --%>
    <%-- response.sendRedirect(request.getContextPath() + "/FacultyDashboardServlet"); --%>
    <%-- Or the servlet itself should always forward here, so this check might not be needed if navigation is strict --%>
</c:if>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Faculty Dashboard | Student Management System - Group_AC</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/styles.css">
</head>
<body>
<jsp:include page="/common/header.jsp" />

<div class="container main-content">
    <h2>Faculty Dashboard</h2>
    <p>Welcome, ${sessionScope.loggedInUser.username}!</p>

    <%-- TODO: Display faculty-specific dashboard information --%>
    <%-- Example Cards --%>
    <div class="dashboard-grid">
        <div class="dashboard-card">
            <h3>My Assigned Courses</h3>
            <p>${facultyDashboardData.assignedCoursesCount > 0 ? facultyDashboardData.assignedCoursesCount : 0}</p> <%-- Populate from servlet --%>
            <a href="${pageContext.request.contextPath}/FacultyCourseManagementServlet?action=myCourses">View My Courses</a>
        </div>
        <div class="dashboard-card">
            <h3>Upcoming Lectures</h3>
            <p>${facultyDashboardData.upcomingLecturesCount > 0 ? facultyDashboardData.upcomingLecturesCount : 0}</p> <%-- Populate from servlet --%>
            <a href="#">View Schedule</a> <%-- Link to a schedule page if implemented --%>
        </div>
        <div class="dashboard-card">
            <h3>Grade Submissions Due</h3>
            <p>${facultyDashboardData.gradingTasksCount > 0 ? facultyDashboardData.gradingTasksCount : 0}</p> <%-- Populate from servlet --%>
            <a href="${pageContext.request.contextPath}/FacultyRecordGradesServlet?action=viewCoursesForGrading">Record Grades</a>
        </div>
    </div>

    <h3>Recent Announcements</h3>
    <div class="section">
        <c:if test="${not empty facultyDashboardData.announcements}">
            <ul>
                <c:forEach var="announcement" items="${facultyDashboardData.announcements}" varStatus="loop" begin="0" end="4">
                    <li>
                        <strong>${announcement.title}</strong> - <fmt:formatDate value="${announcement.createdAt}" pattern="yyyy-MM-dd"/>
                        <p><c:out value="${announcement.content}"/></p>
                    </li>
                </c:forEach>
            </ul>
            <c:if test="${facultyDashboardData.announcements.size() > 5}">
                <a href="${pageContext.request.contextPath}/ViewAnnouncementsServlet">View all announcements...</a>
            </c:if>
        </c:if>
        <c:if test="${empty facultyDashboardData.announcements}">
            <p>No new announcements for faculty.</p>
        </c:if>
    </div>

</div>

<jsp:include page="/common/footer.jsp" />
</body>
</html>