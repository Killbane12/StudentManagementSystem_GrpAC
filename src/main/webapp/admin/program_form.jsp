
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>${empty program.programId ? 'Add New' : 'Edit'} Program | Student Management System - Group_AC</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/styles.css">
</head>
<body>
<jsp:include page="/common/header.jsp"/>
<div class="container main-content form-container">
    <h2>${empty program.programId ? 'Add New Program' : 'Edit Program'}</h2>
    <c:if test="${not empty sessionScope.errorMessage}">
        <p class="message error">${sessionScope.errorMessage}</p>
        <c:remove var="errorMessage" scope="session"/>
    </c:if>
    <c:if test="${not empty requestScope.errorMessageJSP}"> <%-- Specific for JSP display from servlet's preserve --%>
        <p class="message error">${requestScope.errorMessageJSP}</p>
    </c:if>

    <form action="${pageContext.request.contextPath}/ManageProgramsServlet" method="post">
        <input type="hidden" name="action" value="${empty program.programId ? 'create' : 'update'}">
        <c:if test="${not empty program.programId}">
            <input type="hidden" name="programId" value="${program.programId}">
        </c:if>
        <div class="form-group">
            <label for="programName">Program Name:</label>
            <input type="text" id="programName" name="programName" value="<c:out value='${program.programName}'/>"
                   required>
        </div>
        <div class="form-group">
            <label for="departmentId">Department:</label>
            <select id="departmentId" name="departmentId" required>
                <option value="">-- Select Department --</option>
                <c:forEach var="dept" items="${departmentList}"> <%-- departmentList set by servlet --%>
                    <option value="${dept.departmentId}" ${program.departmentId == dept.departmentId ? 'selected' : ''}>
                        <c:out value="${dept.departmentName}"/>
                    </option>
                </c:forEach>
            </select>
        </div>
        <div class="form-group">
            <label for="durationYears">Duration (Years):</label>
            <input type="number" id="durationYears" name="durationYears" value="${program.durationYears}" min="1"
                   max="10">
        </div>
        <div class="form-group">
            <label for="description">Description:</label>
            <textarea id="description" name="description"><c:out value='${program.description}'/></textarea>
        </div>
        <div class="form-actions">
            <button type="submit" class="button">${empty program.programId ? 'Create' : 'Update'}</button>
            <a href="${pageContext.request.contextPath}/ManageProgramsServlet?action=list"
               class="button button-secondary">Cancel</a>
        </div>
    </form>
</div>
<jsp:include page="/common/footer.jsp"/>
</body>
</html>
