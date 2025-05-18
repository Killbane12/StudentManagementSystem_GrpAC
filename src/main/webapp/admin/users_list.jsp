<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Manage Users | Student Management System - Group_AC</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/styles.css">
</head>
<body>
<jsp:include page="/common/header.jsp" />

<div class="container main-content">
    <h2>User Management</h2>

    <%-- Display success or error messages from session (after redirects) --%>
    <c:if test="${not empty sessionScope.successMessage}">
        <p class="message success">${sessionScope.successMessage}</p>
        <c:remove var="successMessage" scope="session"/>
    </c:if>
    <c:if test="${not empty sessionScope.errorMessage}">
        <p class="message error">${sessionScope.errorMessage}</p>
        <c:remove var="errorMessage" scope="session"/>
    </c:if>
    <%-- Display error messages from request (after forwards, e.g., validation failure) --%>
    <c:if test="${not empty requestScope.errorMessage}">
        <p class="message error">${requestScope.errorMessage}</p>
    </c:if>


    <a href="${pageContext.request.contextPath}/ManageUsersServlet?action=add" class="button button-add">Add New
                                                                                                         User</a>

    <table class="data-table">
        <thead>
        <tr>
            <th>ID</th>
            <th>Username</th>
            <th>Email</th>
            <th>Role</th>
            <th>Status</th>
            <th>Created At</th>
            <th>Actions</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach var="user" items="${userList}"> <%-- userList is set by ManageUsersServlet --%>
            <tr>
                <td>${user.userId}</td>
                <td><c:out value="${user.username}"/></td>
                <td><c:out value="${user.email}"/></td>
                <td><c:out value="${user.role}"/></td>
                <td>
                    <c:choose>
                        <c:when test="${user.active}">Active</c:when>
                        <c:otherwise>Inactive</c:otherwise>
                    </c:choose>
                </td>
                <td><fmt:formatDate value="${user.createdAt}" pattern="yyyy-MM-dd HH:mm"/></td>
                <td class="actions">
                    <a href="${pageContext.request.contextPath}/ManageUsersServlet?action=edit&userId=${user.userId}">Edit</a>

                        <%-- Prevent admin from deactivating/deleting their own currently logged-in account --%>
                    <c:set var="loggedInUserForCheck" value="${sessionScope.loggedInUser}"/>
                    <c:if test="${loggedInUserForCheck.userId != user.userId || user.role != 'ADMIN'}">
                        <a href="${pageContext.request.contextPath}/ManageUsersServlet?action=toggleStatus&userId=${user.userId}"
                           onclick="return confirm('Are you sure you want to ${user.active ? 'deactivate' : 'activate'} this user?');">
                                ${user.active ? 'Deactivate' : 'Activate'}
                        </a>
                        <a href="${pageContext.request.contextPath}/ManageUsersServlet?action=delete&userId=${user.userId}"
                           class="delete"
                           onclick="return confirm('Are you sure you want to delete this user? This action might be irreversible if the user is linked to profiles.');">Delete</a>
                    </c:if>
                    <c:if test="${loggedInUserForCheck.userId == user.userId && user.role == 'ADMIN'}">
                        <span style="color: #777; font-size:0.9em;">(Current Admin)</span>
                    </c:if>
                </td>
            </tr>
        </c:forEach>
        <c:if test="${empty userList}">
            <tr>
                <td colspan="7">No users found.</td>
            </tr>
        </c:if>
        </tbody>
    </table>
</div>

<jsp:include page="/common/footer.jsp" />
</body>
</html>