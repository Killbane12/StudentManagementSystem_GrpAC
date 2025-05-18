<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="com.grpAC_SMS.util.DateFormatter" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>${empty enrollment.enrollmentId ? 'Add New' : 'Edit'} Enrollment | Student Management System -
                                                                 Group_AC</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/styles.css">
</head>
<body>
<jsp:include page="/common/header.jsp"/>
<div class="container main-content form-container">
    <h2>${empty enrollment.enrollmentId ? 'Add New Enrollment' : 'Edit Enrollment'}</h2>
    <c:if test="${not empty requestScope.errorMessage}">
        <p class="message error">${requestScope.errorMessage}</p>
    </c:if>
    <c:if test="${not empty sessionScope.errorMessage && empty requestScope.errorMessage}">
        <p class="message error">${sessionScope.errorMessage}</p>
        <c:remove var="errorMessage" scope="session"/>
    </c:if>

    <form action="${pageContext.request.contextPath}/ManageEnrollmentsServlet" method="post">
        <input type="hidden" name="action" value="${empty enrollment.enrollmentId ? 'create' : 'update'}">
        <c:if test="${not empty enrollment.enrollmentId}">
            <input type="hidden" name="enrollmentId" value="${enrollment.enrollmentId}">
        </c:if>

        <div class="form-group">
            <label for="studentId">Student (Required):</label>
            <select id="studentId" name="studentId" required ${not empty enrollment.enrollmentId ? 'disabled' : ''}>
                <option value="">-- Select Student --</option>
                <c:forEach var="student" items="${studentList}">
                    <option value="${student.studentId}" ${enrollment.studentId == student.studentId ? 'selected' : ''}>
                        <c:out value="${student.firstName} ${student.lastName} (${student.studentUniqueId})"/>
                    </option>
                </c:forEach>
            </select>
            <c:if test="${not empty enrollment.enrollmentId}"> <%-- If editing, studentId might not be changeable easily --%>
                <input type="hidden" name="studentId" value="${enrollment.studentId}">
                <small>Student cannot be changed for existing enrollment. To change student, delete and
                       re-create.</small>
            </c:if>
        </div>
        <div class="form-group">
            <label for="courseId">Course (Required):</label>
            <select id="courseId" name="courseId" required ${not empty enrollment.enrollmentId ? 'disabled' : ''}>
                <option value="">-- Select Course --</option>
                <c:forEach var="course" items="${courseList}">
                    <option value="${course.courseId}" ${enrollment.courseId == course.courseId ? 'selected' : ''}>
                        <c:out value="${course.courseCode} - ${course.courseName}"/>
                    </option>
                </c:forEach>
            </select>
            <c:if test="${not empty enrollment.enrollmentId}">
                <input type="hidden" name="courseId" value="${enrollment.courseId}">
            </c:if>
        </div>
        <div class="form-group">
            <label for="academicTermId">Academic Term (Required):</label>
            <select id="academicTermId" name="academicTermId"
                    required ${not empty enrollment.enrollmentId ? 'disabled' : ''}>
                <option value="">-- Select Term --</option>
                <c:forEach var="term" items="${termList}">
                    <option value="${term.academicTermId}" ${enrollment.academicTermId == term.academicTermId ? 'selected' : ''}>
                        <c:out value="${term.termName}"/>
                    </option>
                </c:forEach>
            </select>
            <c:if test="${not empty enrollment.enrollmentId}">
                <input type="hidden" name="academicTermId" value="${enrollment.academicTermId}">
            </c:if>
        </div>
        <div class="form-group">
            <label for="enrollmentDate">Enrollment Date (Required):</label>
            <input type="date" id="enrollmentDate" name="enrollmentDate"
                   value="${DateFormatter.sqlDateToString(enrollment.enrollmentDate)}" required>
        </div>
        <div class="form-group">
            <label for="status">Status (Required):</label>
            <select id="status" name="status" required>
                <c:forEach var="st"
                           items="${statusList}"> <%-- statusList defined in servlet (e.g., {"ENROLLED", "COMPLETED", "DROPPED"}) --%>
                    <option value="${st}" ${enrollment.status == st ? 'selected' : (empty enrollment.status && st == 'ENROLLED' ? 'selected' : '')}>
                        <c:out value="${st}"/>
                    </option>
                </c:forEach>
            </select>
        </div>

        <div class="form-actions">
            <button type="submit"
                    class="button">${empty enrollment.enrollmentId ? 'Create Enrollment' : 'Update Enrollment'}</button>
            <a href="${pageContext.request.contextPath}/ManageEnrollmentsServlet?action=list"
               class="button button-secondary">Cancel</a>
        </div>
    </form>
</div>
<jsp:include page="/common/footer.jsp"/>
</body>
</html>