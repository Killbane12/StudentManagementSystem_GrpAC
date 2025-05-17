<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>My Dashboard | Student Management System - Group_AC</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/styles.css">
</head>
<body>
<jsp:include page="/common/header.jsp" />

<div class="container main-content student-dashboard">
    <h2>Welcome to Your Dashboard, <c:out value="${studentProfile.firstName}"/>!</h2>
    <p>Current Academic Term: <strong><c:out value="${currentTermName}"/></strong></p>

    <c:if test="${not empty errorMessage}">
        <p class="message error">${errorMessage}</p>
    </c:if>

    <div class="section">
        <h3>My Current Courses (<c:out value="${currentTermName}"/>)</h3>
        <c:choose>
            <c:when test="${not empty enrolledCourses}">
                <ul>
                    <c:forEach var="course" items="${enrolledCourses}">
                        <li>
                            <c:out value="${course.courseCode}"/> - <c:out value="${course.courseName}"/>
                            <c:if test="${not empty attendancePercentages[course.courseId]}">
                                <div class="attendance-snippet">
                                    Attendance:
                                    <strong><fmt:formatNumber value="${attendancePercentages[course.courseId]}" maxFractionDigits="1"/>%</strong>
                                    <div class="progress-bar-container">
                                        <div class="progress-bar" style="width: ${attendancePercentages[course.courseId]}%;">
                                            <fmt:formatNumber value="${attendancePercentages[course.courseId]}" maxFractionDigits="0"/>%
                                        </div>
                                    </div>
                                </div>
                            </c:if>
                        </li>
                    </c:forEach>
                </ul>
            </c:when>
            <c:otherwise>
                <p>You are not enrolled in any courses for the current term, or term data is unavailable.</p>
            </c:otherwise>
        </c:choose>
        <a href="${pageContext.request.contextPath}/StudentViewCoursesServlet" class="button">View All Enrolled Courses</a>
    </div>

    <div class="section">
        <h3>Recent Announcements</h3>
        <c:choose>
            <c:when test="${not empty announcements}">
                <ul>
                    <c:forEach var="announcement" items="${announcements}">
                        <li>
                            <strong><c:out value="${announcement.title}"/></strong> (<fmt:formatDate value="${announcement.createdAt}" pattern="yyyy-MM-dd"/>)
                            <p><c:out value="${fn:replace(announcement.content, '\\n', '<br/>')}" escapeXml="false"/></p> <%-- Added escapeXml="false" and fn prefix --%>
                        </li>
                    </c:forEach>
                </ul>
            </c:when>
            <c:otherwise>
                <p>No recent announcements.</p>
            </c:otherwise>
        </c:choose>
        <a href="${pageContext.request.contextPath}/ViewAnnouncementsServlet" class="button">View All Announcements</a>
    </div>

    <div class="section">
        <h3>Quick Links</h3>
        <ul>
            <li><a href="${pageContext.request.contextPath}/StudentProfileServlet">View My Profile</a></li>
            <li><a href="${pageContext.request.contextPath}/StudentViewGradesServlet">View My Grades</a></li>
            <li><a href="${pageContext.request.contextPath}/StudentViewAttendanceServlet">View My Attendance Details</a></li>
        </ul>
    </div>
</div>

<jsp:include page="/common/footer.jsp" />
<script src="${pageContext.request.contextPath}/assets/js/scripts.js"></script>
</body>
</html>