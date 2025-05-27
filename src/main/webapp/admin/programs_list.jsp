<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Manage Programs | Student Management System - Group_AC</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/styles.css">
</head>
<body>
<jsp:include page="/common/header.jsp" />
<div class="container main-content">
    <h2>Program Management</h2>
    <c:if test="${not empty sessionScope.successMessage}">
        <p class="message success">${sessionScope.successMessage}</p>
        <c:remove var="successMessage" scope="session"/>
    </c:if>
    <c:if test="${not empty sessionScope.errorMessage}">
        <p class="message error">${sessionScope.errorMessage}</p>
        <c:remove var="errorMessage" scope="session"/>
    </c:if>
    <a href="${pageContext.request.contextPath}/ManageProgramsServlet?action=add" class="button button-add">Add New Program</a>
    <table class="data-table">
        <thead>
        <tr>
            <th>ID</th>
            <th>Program Name</th>
            <th>Department</th>
            <th>Duration (Yrs)</th>
            <th>Description</th>
            <th>Actions</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach var="prog" items="${programList}"> <%-- programList set by servlet --%>
            <tr>
                <td>${prog.programId}</td>
                <td><c:out value="${prog.programName}"/></td>
                <td><c:out value="${prog.departmentName}"/></td> <%-- Assuming Program object has departmentName --%>
                <td><c:out value="${prog.durationYears}"/></td>
                <td><c:out value="${prog.description}"/></td>
                <td class="actions">
                    <a href="${pageContext.request.contextPath}/ManageProgramsServlet?action=edit&id=${prog.programId}">Edit</a>
                    <a href="${pageContext.request.contextPath}/ManageProgramsServlet?action=delete&id=${prog.programId}" class="delete"
                       onclick="return confirm('Are you sure you want to delete this program?');">Delete</a>
                </td>
            </tr>
        </c:forEach>
        <c:if test="${empty programList}">
            <tr><td colspan="6">No programs found.</td></tr>
        </c:if>
        </tbody>
    </table>
</div>
<jsp:include page="/common/footer.jsp" />
</body>
</html>

