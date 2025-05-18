<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="com.grpAC_SMS.util.DateFormatter" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>${empty term.academicTermId ? 'Add New' : 'Edit'} Term | Student Management System - Group_AC</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/styles.css">
</head>
<body>
<jsp:include page="/common/header.jsp"/>
<div class="container main-content form-container">
    <h2>${empty term.academicTermId ? 'Add New Academic Term' : 'Edit Academic Term'}</h2>
    <c:if test="${not empty requestScope.errorMessage}">
        <p class="message error">${requestScope.errorMessage}</p>
    </c:if>
    <c:if test="${not empty sessionScope.errorMessage && empty requestScope.errorMessage}">
        <p class="message error">${sessionScope.errorMessage}</p>
        <c:remove var="errorMessage" scope="session"/>
    </c:if>

    <form action="${pageContext.request.contextPath}/ManageAcademicTermsServlet" method="post">
        <input type="hidden" name="action" value="${empty term.academicTermId ? 'create' : 'update'}">
        <c:if test="${not empty term.academicTermId}">
            <input type="hidden" name="academicTermId" value="${term.academicTermId}">
        </c:if>

        <div class="form-group">
            <label for="termName">Term Name:</label>
            <input type="text" id="termName" name="termName" value="<c:out value='${term.termName}'/>" required>
        </div>
        <div class="form-group">
            <label for="startDate">Start Date:</label>
            <input type="date" id="startDate" name="startDate" value="${DateFormatter.sqlDateToString(term.startDate)}"
                   required>
        </div>
        <div class="form-group">
            <label for="endDate">End Date:</label>
            <input type="date" id="endDate" name="endDate" value="${DateFormatter.sqlDateToString(term.endDate)}"
                   required>
        </div>
        <div class="form-actions">
            <button type="submit" class="button">${empty term.academicTermId ? 'Create Term' : 'Update Term'}</button>
            <a href="${pageContext.request.contextPath}/ManageAcademicTermsServlet?action=list"
               class="button button-secondary">Cancel</a>
        </div>
    </form>
</div>
<jsp:include page="/common/footer.jsp"/>
</body>
</html>
