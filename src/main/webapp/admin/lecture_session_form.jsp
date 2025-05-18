<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="com.grpAC_SMS.util.DateFormatter" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>${empty session.lectureSessionId ? 'Add New' : 'Edit'} Lecture Session | Student Management System -
                                                                  Group_AC</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/styles.css">
</head>
<body>
<jsp:include page="/common/header.jsp"/>
<div class="container main-content form-container">
    <h2>${empty session.lectureSessionId ? 'Add New Lecture Session' : 'Edit Lecture Session'}</h2>
    <c:if test="${not empty requestScope.errorMessage}">
        <p class="message error">${requestScope.errorMessage}</p>
    </c:if>
    <c:if test="${not empty sessionScope.errorMessage && empty requestScope.errorMessage}">
        <p class="message error">${sessionScope.errorMessage}</p>
        <c:remove var="errorMessage" scope="session"/>
    </c:if>

    <form action="${pageContext.request.contextPath}/ManageLectureSessionsServlet" method="post">
        <input type="hidden" name="action" value="${empty session.lectureSessionId ? 'create' : 'update'}">
        <c:if test="${not empty session.lectureSessionId}">
            <input type="hidden" name="lectureSessionId" value="${session.lectureSessionId}">
        </c:if>

        <div class="form-group">
            <label for="courseId">Course (Required):</label>
            <select id="courseId" name="courseId" required>
                <option value="">-- Select Course --</option>
                <c:forEach var="course" items="${courseList}">
                    <option value="${course.courseId}" ${session.courseId == course.courseId ? 'selected' : ''}>
                        <c:out value="${course.courseCode} - ${course.courseName}"/>
                    </option>
                </c:forEach>
            </select>
        </div>
        <div class="form-group">
            <label for="academicTermId">Academic Term (Required):</label>
            <select id="academicTermId" name="academicTermId" required>
                <option value="">-- Select Term --</option>
                <c:forEach var="term" items="${termList}">
                    <option value="${term.academicTermId}" ${session.academicTermId == term.academicTermId ? 'selected' : ''}>
                        <c:out value="${term.termName}"/>
                    </option>
                </c:forEach>
            </select>
        </div>
        <div class="form-group">
            <label for="facultyMemberId">Faculty (Optional):</label>
            <select id="facultyMemberId" name="facultyMemberId">
                <option value="0">-- Not Assigned / Guest --</option>
                <c:forEach var="faculty" items="${facultyList}">
                    <option value="${faculty.facultyMemberId}" ${session.facultyMemberId == faculty.facultyMemberId ? 'selected' : ''}>
                        <c:out value="${faculty.firstName} ${faculty.lastName} (${faculty.facultyUniqueId})"/>
                    </option>
                </c:forEach>
            </select>
        </div>
        <div class="form-group">
            <label for="locationId">Location (Optional):</label>
            <select id="locationId" name="locationId">
                <option value="0">-- Not Specified --</option>
                <c:forEach var="loc" items="${locationList}">
                    <option value="${loc.locationId}" ${session.locationId == loc.locationId ? 'selected' : ''}>
                        <c:out value="${loc.locationName}"/>
                    </option>
                </c:forEach>
            </select>
        </div>
        <div class="form-group">
            <label for="sessionStartDatetime">Start Date & Time:</label>
            <input type="datetime-local" id="sessionStartDatetime" name="sessionStartDatetime"
                   value="${DateFormatter.localDateTimeToHtmlDateTimeLocalString(session.sessionStartDatetime)}"
                   required>
        </div>
        <div class="form-group">
            <label for="sessionEndDatetime">End Date & Time:</label>
            <input type="datetime-local" id="sessionEndDatetime" name="sessionEndDatetime"
                   value="${DateFormatter.localDateTimeToHtmlDateTimeLocalString(session.sessionEndDatetime)}" required>
        </div>
        <div class="form-actions">
            <button type="submit"
                    class="button">${empty session.lectureSessionId ? 'Create Session' : 'Update Session'}</button>
            <a href="${pageContext.request.contextPath}/ManageLectureSessionsServlet?action=list"
               class="button button-secondary">Cancel</a>
        </div>
    </form>
</div>
<jsp:include page="/common/footer.jsp"/>
</body>
</html>
