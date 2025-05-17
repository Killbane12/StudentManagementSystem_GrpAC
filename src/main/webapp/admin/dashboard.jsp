<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%-- Call AdminDashboardServlet to populate data before rendering --%>
<c:if test="${empty adminDashboardData}"> <%-- Assuming servlet sets this attribute --%>
    <c:redirect url="${pageContext.request.contextPath}/AdminDashboardServlet"/>
</c:if>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Admin Dashboard | Student Management System - Group_AC</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/styles.css">
</head>
<body>
<jsp:include page="/common/header.jsp" />

<div class="container main-content">
    <h2>Admin Dashboard</h2>
    <p>Welcome to the Administrator Panel. From here you can manage all aspects of the system.</p>

<%--    <jsp:include page="/common/admin_nav.jsp" />--%>

    <div class="dashboard-grid">
        <div class="dashboard-card">
            <h3>Users</h3>
            <p>${adminDashboardData.totalUsers}</p>
            <a href="${pageContext.request.contextPath}/ManageUsersServlet?action=list">Manage Users</a>
        </div>
        <div class="dashboard-card">
            <h3>Students</h3>
            <p>${adminDashboardData.totalStudents}</p> <%-- Populate from servlet --%>
            <a href="${pageContext.request.contextPath}/ManageStudentsServlet?action=list">Manage Students</a>
        </div>
        <div class="dashboard-card">
            <h3>Faculty</h3>
            <p>${adminDashboardData.totalFaculty}</p> <%-- Populate from servlet --%>
            <a href="${pageContext.request.contextPath}/ManageFacultyServlet?action=list">Manage Faculty</a>
        </div>
        <div class="dashboard-card">
            <h3>Courses</h3>
            <p>${adminDashboardData.totalCourses}</p> <%-- Populate from servlet --%>
            <a href="${pageContext.request.contextPath}/ManageCoursesServlet?action=list">Manage Courses</a>
        </div>
        <div class="dashboard-card">
            <h3>Programs</h3>
            <p>${adminDashboardData.totalPrograms}</p> <%-- Populate from servlet --%>
            <a href="${pageContext.request.contextPath}/ManageProgramsServlet?action=list">Manage Programs</a>
        </div>
        <div class="dashboard-card">
            <h3>Announcements</h3>
            <p>${adminDashboardData.totalActiveAnnouncements}</p> <%-- Populate from servlet --%>
            <a href="${pageContext.request.contextPath}/ManageAnnouncementsServlet?action=list">Manage Announcements</a>
        </div>
    </div>

    <h3>Quick Actions</h3>
    <ul>
        <li><a href="${pageContext.request.contextPath}/ManageUsersServlet?action=add" class="button">Add New User</a></li>
        <li><a href="${pageContext.request.contextPath}/ManageCoursesServlet?action=add" class="button">Add New Course</a></li>
        <li><a href="${pageContext.request.contextPath}/ManageLectureSessionsServlet?action=add" class="button">Create Lecture Session</a></li>
        <li><a href="${pageContext.request.contextPath}/ManageAnnouncementsServlet?action=add" class="button">Post Announcement</a></li>
    </ul>
</div>

<jsp:include page="/common/footer.jsp" />
</body>
</html>
