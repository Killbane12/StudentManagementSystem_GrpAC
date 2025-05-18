<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Manage Students | Student Management System - Group_AC</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/styles.css">
</head>
<body>
<jsp:include page="/common/header.jsp"/>
<div class="container main-content">
    <h2>Student Profile Management</h2>
    <c:if test="${not empty sessionScope.successMessage}">
        <p class="message success">${sessionScope.successMessage}</p>
        <c:remove var="successMessage" scope="session"/>
    </c:if>
    <c:if test="${not empty sessionScope.errorMessage}">
        <p class="message error">${sessionScope.errorMessage}</p>
        <c:remove var="errorMessage" scope="session"/>
    </c:if>
    <a href="${pageContext.request.contextPath}/ManageStudentsServlet?action=add" class="button button-add">Add New
                                                                                                            Student</a>
    <table class="data-table">
        <thead>
        <tr>
            <th>Student ID</th>
            <th>Unique ID</th>
            <th>Name</th>
            <th>Email (User)</th>
            <th>Program</th>
            <th>Enroll. Date</th>
            <th>User Active</th>
            <th>Actions</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach var="student" items="${studentList}"> <%-- From ManageStudentsServlet --%>
            <tr>
                <td>${student.studentId}</td>
                <td><c:out value="${student.studentUniqueId}"/></td>
                <td><c:out value="${student.firstName} ${student.lastName}"/></td>
                <td><c:out value="${not empty student.userEmail ? student.userEmail : 'N/A'}"/></td>
                <td><c:out value="${not empty student.programName ? student.programName : 'N/A'}"/></td>
                <td><fmt:formatDate value="${student.enrollmentDate}" pattern="yyyy-MM-dd"/></td>
                <td>
                    <c:set var="userForStatus"
                           value="${userListForStatus[student.userId]}"/> <%-- Assuming userListForStatus Map<Integer, User> passed from servlet --%>
                    <c:choose>
                        <c:when test="${not empty student.userId && not empty userForStatus}">
                            ${userForStatus.active ? 'Active' : 'Inactive'}
                        </c:when>
                        <c:otherwise>No User Account</c:otherwise>
                    </c:choose>
                </td>
                <td class="actions">
                    <a href="${pageContext.request.contextPath}/ManageStudentsServlet?action=edit&id=${student.studentId}">Edit
                                                                                                                           Profile</a>
                    <c:if test="${not empty student.userId}">
                        <a href="${pageContext.request.contextPath}/ManageUsersServlet?action=edit&userId=${student.userId}"
                           title="Edit linked user account (email, password, status)">Edit User</a>
                    </c:if>
                    <a href="${pageContext.request.contextPath}/ManageStudentsServlet?action=delete&id=${student.studentId}"
                       class="delete"
                       onclick="return confirm('Are you sure you want to delete this student profile? This may also affect the linked user account.');">Delete</a>
                </td>
            </tr>
        </c:forEach>
        <c:if test="${empty studentList}">
            <tr>
                <td colspan="8">No students found.</td>
            </tr>
        </c:if>
        </tbody>
    </table>
</div>
<jsp:include page="/common/footer.jsp"/>
</body>
</html>