<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>${empty user.userId ? 'Add New' : 'Edit'} User | Student Management System - Group_AC</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/styles.css">
</head>
<body>
<jsp:include page="/common/header.jsp"/>

<div class="container main-content form-container">
    <h2>${empty user.userId ? 'Add New User' : 'Edit User'}</h2>

    <c:if test="${not empty requestScope.errorMessage}">
        <p class="message error">${requestScope.errorMessage}</p>
    </c:if>

    <form action="${pageContext.request.contextPath}/ManageUsersServlet" method="post">
        <c:if test="${not empty user.userId}">
            <input type="hidden" name="userId" value="${user.userId}">
            <input type="hidden" name="action" value="update">
        </c:if>
        <c:if test="${empty user.userId}">
            <input type="hidden" name="action" value="create">
        </c:if>


        <div class="form-group">
            <label for="username">Username:</label>
            <input type="text" id="username" name="username" value="<c:out value='${user.username}'/>" required>
        </div>

        <div class="form-group">
            <label for="email">Email:</label>
            <input type="email" id="email" name="email" value="<c:out value='${user.email}'/>" required>
        </div>

        <div class="form-group">
            <label for="password">Password:</label>
            <input type="password" id="password" name="password" ${empty user.userId ? 'required' : ''}>
            <c:if test="${not empty user.userId}">
                <small>Leave blank to keep current password.</small>
            </c:if>
        </div>

        <div class="form-group">
            <label for="role">Role:</label>
            <select id="role" name="role" required>
                <c:forEach var="roleEnum" items="${roles}">
                    <option value="${roleEnum}" ${user.role == roleEnum ? 'selected' : ''}>
                        <c:out value="${roleEnum}"/>
                    </option>
                </c:forEach>
            </select>
        </div>

        <div class="form-group">
            <label for="isActive">Active:</label>
            <input type="checkbox" id="isActive" name="isActive" ${user.active || empty user.userId ? 'checked' : ''}>
        </div>

        <div class="form-actions">
            <button type="submit" class="button">${empty user.userId ? 'Create User' : 'Update User'}</button>
            <a href="${pageContext.request.contextPath}/ManageUsersServlet?action=list" class="button button-secondary">Cancel</a>
        </div>
    </form>
</div>

<jsp:include page="/common/footer.jsp"/>
</body>
</html>