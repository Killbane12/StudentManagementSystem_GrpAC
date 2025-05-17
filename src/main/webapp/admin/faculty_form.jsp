<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>${empty faculty.facultyMemberId ? 'Add New' : 'Edit'} Faculty | Student Management System - Group_AC</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/styles.css">
</head>
<body>
<jsp:include page="/common/header.jsp" />
<div class="container main-content form-container">
    <h2>${empty faculty.facultyMemberId ? 'Add New Faculty Member' : 'Edit Faculty Profile'}</h2>
    <c:if test="${not empty requestScope.errorMessage}">
        <p class="message error">${requestScope.errorMessage}</p>
    </c:if>
    <c:if test="${not empty sessionScope.errorMessage && empty requestScope.errorMessage}">
        <p class="message error">${sessionScope.errorMessage}</p>
        <c:remove var="errorMessage" scope="session"/>
    </c:if>

    <form action="${pageContext.request.contextPath}/ManageFacultyServlet" method="post">
        <input type="hidden" name="action" value="${empty faculty.facultyMemberId ? 'create' : 'update'}">
        <c:if test="${not empty faculty.facultyMemberId}">
            <input type="hidden" name="facultyMemberId" value="${faculty.facultyMemberId}">
            <c:if test="${not empty faculty.userId}">
                <input type="hidden" name="userId" value="${faculty.userId}">
            </c:if>
        </c:if>

        <h3>Faculty Profile Details</h3>
        <div class="form-group">
            <label for="facultyUniqueId">Faculty Unique ID (e.g., FAC001) (Required):</label>
            <input type="text" id="facultyUniqueId" name="facultyUniqueId" value="<c:out value='${faculty.facultyUniqueId}'/>" required>
        </div>
        <div class="form-group">
            <label for="firstName">First Name (Required):</label>
            <input type="text" id="firstName" name="firstName" value="<c:out value='${faculty.firstName}'/>" required>
        </div>
        <div class="form-group">
            <label for="lastName">Last Name (Required):</label>
            <input type="text" id="lastName" name="lastName" value="<c:out value='${faculty.lastName}'/>" required>
        </div>
        <div class="form-group">
            <label for="departmentId">Department (Required):</label>
            <select id="departmentId" name="departmentId" required>
                <option value="">-- Select Department --</option>
                <c:forEach var="dept" items="${departmentList}"> <%-- departmentList set by servlet --%>
                    <option value="${dept.departmentId}" ${faculty.departmentId == dept.departmentId ? 'selected' : ''}>
                        <c:out value="${dept.departmentName}"/>
                    </option>
                </c:forEach>
            </select>
        </div>
        <div class="form-group">
            <label for="officeLocation">Office Location:</label>
            <input type="text" id="officeLocation" name="officeLocation" value="<c:out value='${faculty.officeLocation}'/>">
        </div>
        <div class="form-group">
            <label for="contactEmailFaculty">Contact Email (Profile):</label>
            <input type="email" id="contactEmailFaculty" name="contactEmailFaculty" value="<c:out value='${faculty.contactEmail}'/>">
        </div>
        <div class="form-group">
            <label for="phoneNumber">Phone Number:</label>
            <input type="tel" id="phoneNumber" name="phoneNumber" value="<c:out value='${faculty.phoneNumber}'/>">
        </div>

        <hr>
        <c:choose>
            <c:when test="${empty faculty.facultyMemberId || faculty.userId == null || faculty.userId == 0}">
                <h3>Create User Account</h3>
                <p>A user account will be created for this faculty member to log in.</p>
                <div class="form-group">
                    <label for="email">Login Email (Required):</label>
                    <input type="email" id="email" name="email" value="<c:out value='${user.email}'/>" required>
                </div>
                <div class="form-group">
                    <label for="password">Initial Password (Required, min 6 chars):</label>
                    <input type="password" id="password" name="password" required minlength="6">
                </div>
            </c:when>
            <c:otherwise>
                <h3>Linked User Account</h3>
                <p>
                    This faculty profile is linked to User ID: <strong>${faculty.userId}</strong>.
                    Login Email: <strong><c:out value="${user.email}"/></strong>.
                </p>
                <p>To change user account details (email, password, active status), please use the <a href="${pageContext.request.contextPath}/ManageUsersServlet?action=edit&userId=${faculty.userId}" target="_blank">Manage Users</a> page for User ID ${faculty.userId}.</p>
            </c:otherwise>
        </c:choose>

        <div class="form-actions">
            <button type="submit" class="button">${empty faculty.facultyMemberId ? 'Create Faculty & User Account' : 'Update Faculty Profile'}</button>
            <a href="${pageContext.request.contextPath}/ManageFacultyServlet?action=list" class="button button-secondary">Cancel</a>
        </div>
    </form>
</div>
<jsp:include page="/common/footer.jsp" />
</body>
</html>