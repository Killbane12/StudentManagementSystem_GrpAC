<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="com.grpAC_SMS.util.DateFormatter" %>
<%@ page import="com.grpAC_SMS.util.ApplicationConstants" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>${empty student.studentId ? 'Add New' : 'Edit'} Student | Student Management System - Group_AC</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/styles.css">
</head>
<body>
<jsp:include page="/common/header.jsp" />
<div class="container main-content form-container">
    <h2>${empty student.studentId ? 'Add New Student' : 'Edit Student Profile'}</h2>
    <c:if test="${not empty requestScope.errorMessage}">
        <p class="message error">${requestScope.errorMessage}</p>
    </c:if>
    <c:if test="${not empty sessionScope.errorMessage && empty requestScope.errorMessage}">
        <p class="message error">${sessionScope.errorMessage}</p>
        <c:remove var="errorMessage" scope="session"/>
    </c:if>

    <form action="${pageContext.request.contextPath}/ManageStudentsServlet" method="post">
        <input type="hidden" name="action" value="${empty student.studentId ? 'create' : 'update'}">
        <c:if test="${not empty student.studentId}">
            <input type="hidden" name="studentId" value="${student.studentId}">
            <c:if test="${not empty student.userId}"> <%-- Pass existing userId if updating --%>
                <input type="hidden" name="userId" value="${student.userId}">
            </c:if>
        </c:if>

        <h3>Student Profile Details</h3>
        <div class="form-group">
            <label for="studentUniqueId">Student Unique ID (${ApplicationConstants.STUDENT_ID_MIN_RANGE}-${ApplicationConstants.STUDENT_ID_MAX_RANGE}):</label>
            <input type="text" id="studentUniqueId" name="studentUniqueId" value="<c:out value='${student.studentUniqueId}'/>"
                   required pattern="${ApplicationConstants.STUDENT_ID_RANGE_PATTERN}" title="${ApplicationConstants.STUDENT_ID_RANGE_TITLE}">
        </div>
        <div class="form-group">
            <label for="firstName">First Name (Required):</label>
            <input type="text" id="firstName" name="firstName" value="<c:out value='${student.firstName}'/>" required>
        </div>
        <div class="form-group">
            <label for="lastName">Last Name (Required):</label>
            <input type="text" id="lastName" name="lastName" value="<c:out value='${student.lastName}'/>" required>
        </div>
        <div class="form-group">
            <label for="dateOfBirth">Date of Birth (Required):</label>
            <input type="date" id="dateOfBirth" name="dateOfBirth" value="${DateFormatter.sqlDateToString(student.dateOfBirth)}" required>
        </div>
        <div class="form-group">
            <label for="gender">Gender:</label>
            <select id="gender" name="gender">
                <option value="">-- Select Gender --</option>
                <option value="MALE" ${student.gender == 'MALE' ? 'selected' : ''}>Male</option>
                <option value="FEMALE" ${student.gender == 'FEMALE' ? 'selected' : ''}>Female</option>
                <option value="OTHER" ${student.gender == 'OTHER' ? 'selected' : ''}>Other</option>
            </select>
        </div>
        <div class="form-group">
            <label for="address">Address:</label>
            <input type="text" id="address" name="address" value="<c:out value='${student.address}'/>">
        </div>
        <div class="form-group">
            <label for="phoneNumber">Phone Number:</label>
            <input type="tel" id="phoneNumber" name="phoneNumber" value="<c:out value='${student.phoneNumber}'/>">
        </div>
        <div class="form-group">
            <label for="enrollmentDate">Enrollment Date (Required):</label>
            <input type="date" id="enrollmentDate" name="enrollmentDate" value="${DateFormatter.sqlDateToString(student.enrollmentDate)}" required>
        </div>
        <div class="form-group">
            <label for="programId">Program (Optional):</label>
            <select id="programId" name="programId">
                <option value="0">-- Not Assigned --</option>
                <c:forEach var="prog" items="${programList}"> <%-- programList set by servlet --%>
                    <option value="${prog.programId}" ${student.programId == prog.programId ? 'selected' : ''}>
                        <c:out value="${prog.programName}"/>
                    </option>
                </c:forEach>
            </select>
        </div>

        <hr>
        <c:choose>
            <c:when test="${empty student.studentId || student.userId == null || student.userId == 0}">
                <h3>Create User Account</h3>
                <p>A user account will be created for this student to log in.</p>
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
                    This student profile is linked to User ID: <strong>${student.userId}</strong>.
                    Login Email: <strong><c:out value="${user.email}"/></strong> (Set by ManageUsersServlet for `user` attribute).
                </p>
                <p>To change user account details (email, password, active status), please use the <a href="${pageContext.request.contextPath}/ManageUsersServlet?action=edit&userId=${student.userId}" target="_blank">Manage Users</a> page for User ID ${student.userId}.</p>
                <%-- Alternatively, if you want to allow email update from here:
                <div class="form-group">
                   <label for="email">Login Email (Can be updated via Manage Users):</label>
                   <input type="email" id="email" name="email" value="<c:out value='${user.email}'/>" readonly>
                </div>
                --%>
            </c:otherwise>
        </c:choose>

        <div class="form-actions">
            <button type="submit" class="button">${empty student.studentId ? 'Create Student & User Account' : 'Update Student Profile'}</button>
            <a href="${pageContext.request.contextPath}/ManageStudentsServlet?action=list" class="button button-secondary">Cancel</a>
        </div>
    </form>
</div>
<jsp:include page="/common/footer.jsp" />
</body>
</html>