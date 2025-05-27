<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Manage Faculty | Student Management System - Group_AC</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/styles.css">
</head>
<body>
<jsp:include page="/common/header.jsp" />
<div class="container main-content">
    <h2>Faculty Profile Management</h2>
    <c:if test="${not empty sessionScope.successMessage}">
        <p class="message success">${sessionScope.successMessage}</p>
        <c:remove var="successMessage" scope="session"/>
    </c:if>
    <c:if test="${not empty sessionScope.errorMessage}">
        <p class="message error">${sessionScope.errorMessage}</p>
        <c:remove var="errorMessage" scope="session"/>
    </c:if>
    <a href="${pageContext.request.contextPath}/ManageFacultyServlet?action=add" class="button button-add">Add New Faculty</a>
    <table class="data-table">
        <thead>
        <tr>
            <th>Faculty ID</th>
            <th>Unique ID</th>
            <th>Name</th>
            <th>Department</th>
            <th>Email (User)</th>
            <th>User Active</th>
            <th>Actions</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach var="faculty" items="${facultyList}"> <%-- From ManageFacultyServlet --%>
            <tr>
                <td>${faculty.facultyMemberId}</td>
                <td><c:out value="${faculty.facultyUniqueId}"/></td>
                <td><c:out value="${faculty.firstName} ${faculty.lastName}"/></td>
                <td><c:out value="${faculty.departmentName}"/></td>
                <td><c:out value="${not empty faculty.userEmail ? faculty.userEmail : 'N/A'}"/></td>
                <td>
                    <c:set var="userForStatus" value="${userListForStatus[faculty.userId]}"/>
                    <c:choose>
                        <c:when test="${not empty faculty.userId && not empty userForStatus}">
                            ${userForStatus.active ? 'Active' : 'Inactive'}
                        </c:when>
                        <c:otherwise>No User Account</c:otherwise>
                    </c:choose>
                </td>
                <td class="actions">
                    <a href="${pageContext.request.contextPath}/ManageFacultyServlet?action=edit&id=${faculty.facultyMemberId}">Edit Profile</a>
                    <c:if test="${not empty faculty.userId}">
                        <a href="${pageContext.request.contextPath}/ManageUsersServlet?action=edit&userId=${faculty.userId}" title="Edit linked user account (email, password, status)">Edit User</a>
                    </c:if>
                    <a href="${pageContext.request.contextPath}/ManageFacultyServlet?action=delete&id=${faculty.facultyMemberId}" class="delete"
                       onclick="return confirm('Are you sure you want to delete this faculty profile? This may also affect the linked user account.');">Delete</a>
                </td>
            </tr>
        </c:forEach>
        <c:if test="${empty facultyList}">
            <tr><td colspan="7">No faculty members found.</td></tr>
        </c:if>
        </tbody>
    </table>
</div>
<jsp:include page="/common/footer.jsp" />
</body>
</html>