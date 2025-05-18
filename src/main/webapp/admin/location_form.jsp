<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>${empty location.locationId ? 'Add New' : 'Edit'} Location | Student Management System - Group_AC</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/styles.css">
</head>
<body>
<jsp:include page="/common/header.jsp"/>
<div class="container main-content form-container">
    <h2>${empty location.locationId ? 'Add New Location' : 'Edit Location'}</h2>
    <c:if test="${not empty requestScope.errorMessage}">
        <p class="message error">${requestScope.errorMessage}</p>
    </c:if>
    <c:if test="${not empty sessionScope.errorMessage && empty requestScope.errorMessage}">
        <p class="message error">${sessionScope.errorMessage}</p>
        <c:remove var="errorMessage" scope="session"/>
    </c:if>

    <form action="${pageContext.request.contextPath}/ManageLocationsServlet" method="post">
        <input type="hidden" name="action" value="${empty location.locationId ? 'create' : 'update'}">
        <c:if test="${not empty location.locationId}">
            <input type="hidden" name="locationId" value="${location.locationId}">
        </c:if>

        <div class="form-group">
            <label for="locationName">Location Name:</label>
            <input type="text" id="locationName" name="locationName" value="<c:out value='${location.locationName}'/>"
                   required>
        </div>
        <div class="form-group">
            <label for="capacity">Capacity (Optional):</label>
            <input type="number" id="capacity" name="capacity" value="${location.capacity}" min="0">
        </div>
        <div class="form-actions">
            <button type="submit"
                    class="button">${empty location.locationId ? 'Create Location' : 'Update Location'}</button>
            <a href="${pageContext.request.contextPath}/ManageLocationsServlet?action=list"
               class="button button-secondary">Cancel</a>
        </div>
    </form>
</div>
<jsp:include page="/common/footer.jsp"/>
</body>
</html>
