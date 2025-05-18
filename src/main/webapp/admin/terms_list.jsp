<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Manage Academic Terms | Student Management System - Group_AC</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/styles.css">
</head>
<body>
<jsp:include page="/common/header.jsp"/>
<div class="container main-content">
    <h2>Academic Term Management</h2>
    <c:if test="${not empty sessionScope.successMessage}">
        <p class="message success">${sessionScope.successMessage}</p>
        <c:remove var="successMessage" scope="session"/>
    </c:if>
    <c:if test="${not empty sessionScope.errorMessage}">
        <p class="message error">${sessionScope.errorMessage}</p>
        <c:remove var="errorMessage" scope="session"/>
    </c:if>
    <a href="${pageContext.request.contextPath}/ManageAcademicTermsServlet?action=add" class="button button-add">Add New
                                                                                                                 Term</a>
    <table class="data-table">
        <thead>
        <tr>
            <th>ID</th>
            <th>Term Name</th>
            <th>Start Date</th>
            <th>End Date</th>
            <th>Actions</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach var="term" items="${termList}">
            <tr>
                <td>${term.academicTermId}</td>
                <td><c:out value="${term.termName}"/></td>
                <td><fmt:formatDate value="${term.startDate}" pattern="yyyy-MM-dd"/></td>
                <td><fmt:formatDate value="${term.endDate}" pattern="yyyy-MM-dd"/></td>
                <td class="actions">
                    <a href="${pageContext.request.contextPath}/ManageAcademicTermsServlet?action=edit&id=${term.academicTermId}">Edit</a>
                    <a href="${pageContext.request.contextPath}/ManageAcademicTermsServlet?action=delete&id=${term.academicTermId}"
                       class="delete"
                       onclick="return confirm('Are you sure you want to delete this term?');">Delete</a>
                </td>
            </tr>
        </c:forEach>
        <c:if test="${empty termList}">
            <tr>
                <td colspan="5">No academic terms found.</td>
            </tr>
        </c:if>
        </tbody>
    </table>
</div>
<jsp:include page="/common/footer.jsp"/>
</body>
</html>
