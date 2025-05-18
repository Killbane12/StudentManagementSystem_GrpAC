<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>My Courses | Student Management System - Group_AC</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/styles.css">
</head>
<body>
<jsp:include page="/common/header.jsp" />
<div class="container main-content">
    <h2>My Assigned Courses</h2>
    <%-- TODO: Add term selector --%>
    <c:if test="${not empty successMessage}"><p class="message success">${successMessage}</p></c:if>
    <c:if test="${not empty errorMessage}"><p class="message error">${errorMessage}</p></c:if>

    <c:choose>
        <c:when test="${not empty assignedCourses}">
            <table class="data-table">
                <thead>
                <tr>
                    <th>Course Code</th>
                    <th>Course Name</th>
                    <th>Term</th>
                    <th>Actions</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach var="course" items="${assignedCourses}"> <%-- Populate from servlet --%>
                    <tr>
                        <td><c:out value="${course.courseCode}"/></td>
                        <td><c:out value="${course.courseName}"/></td>
                        <td><c:out value="${course.termName}"/></td> <%-- Assuming course object has term info or join --%>
                        <td class="actions">
                            <a href="${pageContext.request.contextPath}/FacultyCourseManagementServlet?action=viewStudents&courseId=${course.courseId}&termId=${course.academicTermId}">View Students</a>
                            <a href="${pageContext.request.contextPath}/FacultyRecordGradesServlet?action=selectAssessment&courseId=${course.courseId}&termId=${course.academicTermId}">Manage Grades</a>
                            <a href="${pageContext.request.contextPath}/FacultyRecordAttendanceServlet?action=selectSession&courseId=${course.courseId}&termId=${course.academicTermId}">Manage Attendance</a>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </c:when>
        <c:otherwise>
            <p>You are not assigned to any courses for the selected term, or no term is active.</p>
        </c:otherwise>
    </c:choose>
</div>
<jsp:include page="/common/footer.jsp" />
</body>
</html>
