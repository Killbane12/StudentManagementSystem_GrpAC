<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>${empty course.courseId ? 'Add New' : 'Edit'} Course | Student Management System - Group_AC</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/styles.css">
</head>
<body>
<jsp:include page="/common/header.jsp"/>
<div class="container main-content form-container">
    <h2>${empty course.courseId ? 'Add New Course' : 'Edit Course'}</h2>

    <c:if test="${not empty requestScope.errorMessage}"> <%-- For errors from servlet preserveFormData --%>
        <p class="message error">${requestScope.errorMessage}</p>
    </c:if>
    <c:if test="${not empty sessionScope.errorMessage && empty requestScope.errorMessage}"> <%-- For errors from session redirect --%>
        <p class="message error">${sessionScope.errorMessage}</p>
        <c:remove var="errorMessage" scope="session"/>
    </c:if>

    <form action="${pageContext.request.contextPath}/ManageCoursesServlet" method="post">
        <input type="hidden" name="action" value="${empty course.courseId ? 'create' : 'update'}">
        <c:if test="${not empty course.courseId}">
            <input type="hidden" name="courseId" value="${course.courseId}">
        </c:if>

        <div class="form-group">
            <label for="courseCode">Course Code:</label>
            <input type="text" id="courseCode" name="courseCode" value="<c:out value='${course.courseCode}'/>" required>
        </div>
        <div class="form-group">
            <label for="courseName">Course Name:</label>
            <input type="text" id="courseName" name="courseName" value="<c:out value='${course.courseName}'/>" required>
        </div>
        <div class="form-group">
            <label for="credits">Credits:</label>
            <input type="number" id="credits" name="credits" value="${course.credits}" min="0">
        </div>
        <div class="form-group">
            <label for="departmentId">Department (Required):</label>
            <select id="departmentId" name="departmentId" required>
                <option value="">-- Select Department --</option>
                <c:forEach var="dept" items="${departmentList}">
                    <option value="${dept.departmentId}" ${course.departmentId == dept.departmentId ? 'selected' : ''}>
                        <c:out value="${dept.departmentName}"/>
                    </option>
                </c:forEach>
            </select>
        </div>
        <div class="form-group">
            <label for="programId">Primary Program (Optional):</label>
            <select id="programId" name="programId">
                <option value="0">-- None --</option>
                <%-- Value 0 or "" for no specific program --%>
                <c:forEach var="prog" items="${programList}">
                    <option value="${prog.programId}" ${course.programId == prog.programId ? 'selected' : ''}>
                        <c:out value="${prog.programName}"/>
                    </option>
                </c:forEach>
            </select>
        </div>
        <div class="form-group">
            <label for="description">Description:</label>
            <textarea id="description" name="description"><c:out value='${course.description}'/></textarea>
        </div>
        <div class="form-actions">
            <button type="submit" class="button">${empty course.courseId ? 'Create Course' : 'Update Course'}</button>
            <a href="${pageContext.request.contextPath}/ManageCoursesServlet?action=list"
               class="button button-secondary">Cancel</a>
        </div>
    </form>
</div>
<jsp:include page="/common/footer.jsp"/>
</body>
</html>