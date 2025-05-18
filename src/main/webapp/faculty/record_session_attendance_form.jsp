<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Record Attendance | Student Management System - Group_AC</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/styles.css">
</head>
<body>
<jsp:include page="/common/header.jsp" />
<div class="container main-content">
    <h2>Record Attendance for Lecture Session</h2>
    <p>
        <strong>Course:</strong> <c:out value="${lectureSession.courseName}"/> <br>
        <strong>Session Time:</strong> <fmt:formatDate value="${lectureSession.sessionStartDatetime}" pattern="yyyy-MM-dd HH:mm"/>
        to <fmt:formatDate value="${lectureSession.sessionEndDatetime}" pattern="HH:mm"/> <br>
        <strong>Location:</strong> <c:out value="${not empty lectureSession.locationName ? lectureSession.locationName : 'N/A'}"/>
    </p>

    <c:if test="${not empty sessionScope.successMessage}">
        <p class="message success">${sessionScope.successMessage}</p>
        <c:remove var="successMessage" scope="session"/>
    </c:if>
    <c:if test="${not empty sessionScope.errorMessage}">
        <p class="message error">${sessionScope.errorMessage}</p>
        <c:remove var="errorMessage" scope="session"/>
    </c:if>

    <c:choose>
        <c:when test="${not empty enrolledStudents}">
            <form action="${pageContext.request.contextPath}/FacultyRecordAttendanceServlet" method="post">
                <input type="hidden" name="action" value="saveSessionAttendance">
                <input type="hidden" name="lectureSessionId" value="${lectureSession.lectureSessionId}">
                <table class="data-table">
                    <thead>
                    <tr>
                        <th>Student ID</th>
                        <th>Student Name</th>
                        <th>Status (Present)</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach var="student" items="${enrolledStudents}">
                        <input type="hidden" name="studentId" value="${student.studentId}">
                        <c:set var="existingAtt" value="${existingAttendanceMap[student.studentId]}"/>
                        <tr>
                            <td><c:out value="${student.studentUniqueId}"/></td>
                            <td><c:out value="${student.firstName} ${student.lastName}"/></td>
                            <td>
                                <input type="checkbox" name="isPresent_${student.studentId}"
                                    ${existingAtt != null && existingAtt.present ? 'checked' : ''}>
                            </td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
                <div class="form-actions" style="margin-top:20px;">
                    <button type="submit" class="button">Save Attendance</button>
                    <a href="${pageContext.request.contextPath}/FacultyRecordAttendanceServlet?action=selectSession&courseId=${lectureSession.courseId}&termId=${lectureSession.academicTermId}" class="button button-secondary">Back to Session Selection</a>
                </div>
            </form>
        </c:when>
        <c:otherwise>
            <p>No students enrolled in this course for this session's term.</p>
            <a href="${pageContext.request.contextPath}/FacultyRecordAttendanceServlet?action=selectSession&courseId=${lectureSession.courseId}&termId=${lectureSession.academicTermId}" class="button button-secondary">Back to Session Selection</a>
        </c:otherwise>
    </c:choose>
</div>
<jsp:include page="/common/footer.jsp" />
</body>
</html>