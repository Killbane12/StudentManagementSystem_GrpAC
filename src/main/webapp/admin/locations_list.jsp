<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Manage Locations | Student Management System - Group_AC</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/styles.css">
</head>
<body>
<jsp:include page="/common/header.jsp"/>
<div class="container main-content">
    <h2>Location Management</h2>
    <c:if test="${not empty sessionScope.successMessage}">
        <p class="message success">${sessionScope.successMessage}</p>
        <c:remove var="successMessage" scope="session"/>
    </c:if>
    <c:if test="${not empty sessionScope.errorMessage}">
        <p class="message error">${sessionScope.errorMessage}</p>
        <c:remove var="errorMessage" scope="session"/>
    </c:if>
    <a href="${pageContext.request.contextPath}/ManageLocationsServlet?action=add" class="button button-add">Add New
                                                                                                             Location</a>
    <table class="data-table">
        <thead>
        <tr>
            <th>ID</th>
            <th>Location Name</th>
            <th>Capacity</th>
            <th>Actions</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach var="loc" items="${locationList}">
            <tr>
                <td>${loc.locationId}</td>
                <td><c:out value="${loc.locationName}"/></td>
                <td><c:out value="${loc.capacity}"/></td>
                <td class="actions">
                    <a href="${pageContext.request.contextPath}/ManageLocationsServlet?action=edit&id=${loc.locationId}">Edit</a>
                    <a href="${pageContext.request.contextPath}/ManageLocationsServlet?action=delete&id=${loc.locationId}"
                       class="delete"
                       onclick="return confirm('Are you sure you want to delete this location?');">Delete</a>
                </td>
            </tr>
        </c:forEach>
        <c:if test="${empty locationList}">
            <tr>
                <td colspan="4">No locations found.</td>
            </tr>
        </c:if>
        </tbody>
    </table>
</div>
<jsp:include page="/common/footer.jsp"/>
</body>
</html>