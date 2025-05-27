<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>${empty department.departmentId ? 'Add New' : 'Edit'} Department | Student Management System - Group_AC</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/styles.css">
</head>
<body>
<jsp:include page="/common/header.jsp" />
<div class="container main-content form-container">
    <h2>${empty department.departmentId ? 'Add New Department' : 'Edit Department'}</h2>
    <c:if test="${not empty sessionScope.errorMessage}">
        <p class="message error">${sessionScope.errorMessage}</p>
        <c:remove var="errorMessage" scope="session"/>
    </c:if>
    <c:if test="${not empty requestScope.errorMessage}"> <%-- For direct errors from servlet --%>
        <p class="message error">${requestScope.errorMessage}</p>
    </c:if>

    <form action="${pageContext.request.contextPath}/ManageDepartmentsServlet" method="post">
        <input type="hidden" name="action" value="${empty department.departmentId ? 'create' : 'update'}">
        <c:if test="${not empty department.departmentId}">
            <input type="hidden" name="departmentId" value="${department.departmentId}">
        </c:if>
        <div class="form-group">
            <label for="departmentName">Department Name:</label>
            <input type="text" id="departmentName" name="departmentName" value="<c:out value='${department.departmentName}'/>" required>
        </div>
        <div class="form-actions">
            <button type="submit" class="button">${empty department.departmentId ? 'Create' : 'Update'}</button>
            <a href="${pageContext.request.contextPath}/ManageDepartmentsServlet?action=list" class="button button-secondary">Cancel</a>
        </div>
    </form>
</div>
<jsp:include page="/common/footer.jsp" />
</body>
</html>
