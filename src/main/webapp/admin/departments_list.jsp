<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Manage Departments | Student Management System - Group_AC</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/styles.css">
</head>
<body>
<jsp:include page="/common/header.jsp" />
<div class="container main-content">
    <h2>Department Management</h2>
    <c:if test="${not empty sessionScope.successMessage}">
        <p class="message success">${sessionScope.successMessage}</p>
        <c:remove var="successMessage" scope="session"/>
    </c:if>
    <c:if test="${not empty sessionScope.errorMessage}">
        <p class="message error">${sessionScope.errorMessage}</p>
        <c:remove var="errorMessage" scope="session"/>
    </c:if>
    <a href="${pageContext.request.contextPath}/ManageDepartmentsServlet?action=add" class="button button-add">Add New Department</a>
    <table class="data-table">
        <thead>
        <tr>
            <th>ID</th>
            <th>Department Name</th>
            <th>Created At</th>
            <th>Actions</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach var="dept" items="${departmentList}">
            <tr>
                <td>${dept.departmentId}</td>
                <td><c:out value="${dept.departmentName}"/></td>
                <td><fmt:formatDate value="${dept.createdAt}" pattern="yyyy-MM-dd HH:mm"/></td>
                <td class="actions">
                    <a href="${pageContext.request.contextPath}/ManageDepartmentsServlet?action=edit&id=${dept.departmentId}">Edit</a>
                    <a href="${pageContext.request.contextPath}/ManageDepartmentsServlet?action=delete&id=${dept.departmentId}" class="delete"
                       onclick="return confirm('Are you sure you want to delete this department? This may fail if it is referenced elsewhere.');">Delete</a>
                </td>
            </tr>
        </c:forEach>
        <c:if test="${empty departmentList}">
            <tr><td colspan="4">No departments found.</td></tr>
        </c:if>
        </tbody>
    </table>
</div>
<jsp:include page="/common/footer.jsp" />
</body>
</html>
