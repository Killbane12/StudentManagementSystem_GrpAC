<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Manage Announcements | Student Management System - Group_AC</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/styles.css">
</head>
<body>
<jsp:include page="/common/header.jsp" />
<div class="container main-content">
    <h2>Announcement Management</h2>
    <c:if test="${not empty sessionScope.successMessage}">
        <p class="message success">${sessionScope.successMessage}</p>
        <c:remove var="successMessage" scope="session"/>
    </c:if>
    <c:if test="${not empty sessionScope.errorMessage}">
        <p class="message error">${sessionScope.errorMessage}</p>
        <c:remove var="errorMessage" scope="session"/>
    </c:if>
    <a href="${pageContext.request.contextPath}/ManageAnnouncementsServlet?action=add" class="button button-add">Add New Announcement</a>
    <table class="data-table">
        <thead>
        <tr>
            <th>ID</th>
            <th>Title</th>
            <th>Target Role</th>
            <th>Posted By</th>
            <th>Expiry Date</th>
            <th>Created At</th>
            <th>Actions</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach var="ann" items="${announcementList}">
            <tr>
                <td>${ann.announcementId}</td>
                <td><c:out value="${ann.title}"/></td>
                <td><c:out value="${ann.targetRole}"/></td>
                <td><c:out value="${ann.postedByUsername}"/></td>
                <td><fmt:formatDate value="${ann.expiryDate}" pattern="yyyy-MM-dd"/></td>
                <td><fmt:formatDate value="${ann.createdAt}" pattern="yyyy-MM-dd HH:mm"/></td>
                <td class="actions">
                    <a href="${pageContext.request.contextPath}/ManageAnnouncementsServlet?action=edit&id=${ann.announcementId}">Edit</a>
                    <a href="${pageContext.request.contextPath}/ManageAnnouncementsServlet?action=delete&id=${ann.announcementId}" class="delete"
                       onclick="return confirm('Are you sure you want to delete this announcement?');">Delete</a>
                </td>
            </tr>
        </c:forEach>
        <c:if test="${empty announcementList}">
            <tr><td colspan="7">No announcements found.</td></tr>
        </c:if>
        </tbody>
    </table>
</div>
<jsp:include page="/common/footer.jsp" />
</body>
</html>