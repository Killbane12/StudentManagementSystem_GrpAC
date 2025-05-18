<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Manage Courses | Student Management System - Group_AC</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/styles.css">
</head>
<body>
<jsp:include page="/common/header.jsp"/>
<div class="container main-content">
    <h2>Course Management</h2>
    <c:if test="${not empty sessionScope.successMessage}">
        <p class="message success">${sessionScope.successMessage}</p>
        <c:remove var="successMessage" scope="session"/>
    </c:if>
    <c:if test="${not empty sessionScope.errorMessage}">
        <p class="message error">${sessionScope.errorMessage}</p>
        <c:remove var="errorMessage" scope="session"/>
    </c:if>
    <a href="${pageContext.request.contextPath}/ManageCoursesServlet?action=add" class="button button-add">Add New
                                                                                                           Course</a>
    <table class="data-table">
        <thead>
        <tr>
            <th>ID</th>
            <th>Course Code</th>
            <th>Course Name</th>
            <th>Credits</th>
            <th>Program</th>
            <th>Department</th>
            <th>Actions</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach var="course" items="${courseList}">
            <tr>
                <td>${course.courseId}</td>
                <td><c:out value="${course.courseCode}"/></td>
                <td><c:out value="${course.courseName}"/></td>
                <td><c:out value="${course.credits}"/></td>
                <td><c:out value="${not empty course.programName ? course.programName : 'N/A'}"/></td>
                <td><c:out value="${course.departmentName}"/></td>
                <td class="actions">
                    <a href="${pageContext.request.contextPath}/ManageCoursesServlet?action=edit&id=${course.courseId}">Edit</a>
                    <a href="${pageContext.request.contextPath}/ManageCoursesServlet?action=delete&id=${course.courseId}"
                       class="delete"
                       onclick="return confirm('Are you sure you want to delete this course?');">Delete</a>
                </td>
            </tr>
        </c:forEach>
        <c:if test="${empty courseList}">
            <tr>
                <td colspan="7">No courses found.</td>
            </tr>
        </c:if>
        </tbody>
    </table>
</div>
<jsp:include page="/common/footer.jsp"/>
</body>
</html>