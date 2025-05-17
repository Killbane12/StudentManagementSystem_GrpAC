<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Grades Oversight | Student Management System - Group_AC</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/styles.css">
</head>
<body>
<jsp:include page="/common/header.jsp" />
<div class="container main-content">
    <h2>Grades Oversight</h2>
    <c:if test="${not empty sessionScope.successMessage}">
        <p class="message success">${sessionScope.successMessage}</p>
        <c:remove var="successMessage" scope="session"/>
    </c:if>
    <c:if test="${not empty sessionScope.errorMessage}">
        <p class="message error">${sessionScope.errorMessage}</p>
        <c:remove var="errorMessage" scope="session"/>
    </c:if>
    <%-- TODO: Add filters for student, course, term, assessment type --%>
    <table class="data-table">
        <thead>
        <tr>
            <th>Grade ID</th>
            <th>Student Name</th>
            <th>Course Name</th>
            <th>Assessment</th>
            <th>Grade Value</th>
            <th>Graded By (Faculty)</th>
            <th>Graded Date</th>
            <th>Actions</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach var="grade" items="${gradeList}"> <%-- From AdminManageGradesServlet --%>
            <tr>
                <td>${grade.gradeId}</td>
                <td><c:out value="${grade.studentName}"/></td>
                <td><c:out value="${grade.courseName}"/></td>
                <td><c:out value="${grade.assessmentType}"/></td>
                <td><c:out value="${grade.gradeValue}"/></td>
                <td><c:out value="${not empty grade.facultyName ? grade.facultyName : 'N/A'}"/></td>
                <td><fmt:formatDate value="${grade.gradedDate}" pattern="yyyy-MM-dd HH:mm"/></td>
                <td class="actions">
                        <%-- <a href="${pageContext.request.contextPath}/AdminManageGradesServlet?action=edit&id=${grade.gradeId}">Edit</a> --%>
                        <%-- Edit/Delete for admin grades might be very specific use cases --%>
                    <span style="color: #777;">No direct actions</span>
                </td>
            </tr>
        </c:forEach>
        <c:if test="${empty gradeList}">
            <tr><td colspan="8">No grades found.</td></tr>
        </c:if>
        </tbody>
    </table>
</div>
<jsp:include page="/common/footer.jsp" />
</body>
</html>