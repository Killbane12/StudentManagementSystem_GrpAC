<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Manage Enrollments | Student Management System - Group_AC</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/styles.css">
</head>
<body>
<jsp:include page="/common/header.jsp"/>
<div class="container main-content">
    <h2>Enrollment Management</h2>
    <c:if test="${not empty sessionScope.successMessage}">
        <p class="message success">${sessionScope.successMessage}</p>
        <c:remove var="successMessage" scope="session"/>
    </c:if>
    <c:if test="${not empty sessionScope.errorMessage}">
        <p class="message error">${sessionScope.errorMessage}</p>
        <c:remove var="errorMessage" scope="session"/>
    </c:if>
    <a href="${pageContext.request.contextPath}/ManageEnrollmentsServlet?action=add" class="button button-add">Add New
                                                                                                               Enrollment</a>
    <%-- TODO: Add filters for student, course, term --%>
    <table class="data-table">
        <thead>
        <tr>
            <th>ID</th>
            <th>Student (ID)</th>
            <th>Course (Code)</th>
            <th>Term</th>
            <th>Enrollment Date</th>
            <th>Status</th>
            <th>Actions</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach var="enrollment" items="${enrollmentList}">
            <tr>
                <td>${enrollment.enrollmentId}</td>
                <td><c:out value="${enrollment.studentName} (${enrollment.studentUniqueId})"/></td>
                <td><c:out value="${enrollment.courseName} (${enrollment.courseCode})"/></td>
                <td><c:out value="${enrollment.termName}"/></td>
                <td><fmt:formatDate value="${enrollment.enrollmentDate}" pattern="yyyy-MM-dd"/></td>
                <td><c:out value="${enrollment.status}"/></td>
                <td class="actions">
                    <a href="${pageContext.request.contextPath}/ManageEnrollmentsServlet?action=edit&id=${enrollment.enrollmentId}">Edit
                                                                                                                                    Status/Date</a>
                    <a href="${pageContext.request.contextPath}/ManageEnrollmentsServlet?action=delete&id=${enrollment.enrollmentId}"
                       class="delete"
                       onclick="return confirm('Are you sure you want to delete this enrollment record?');">Delete</a>
                </td>
            </tr>
        </c:forEach>
        <c:if test="${empty enrollmentList}">
            <tr>
                <td colspan="7">No enrollments found.</td>
            </tr>
        </c:if>
        </tbody>
    </table>
</div>
<jsp:include page="/common/footer.jsp"/>
</body>
</html>