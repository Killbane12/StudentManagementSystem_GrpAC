<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Course Details: ${course.courseName} | Student Management System - Group_AC</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/styles.css">
</head>
<body>
<jsp:include page="/common/header.jsp" />
<div class="container main-content">
    <h2>Enrolled Students in ${course.courseCode} - ${course.courseName} (${term.termName})</h2>
    <c:if test="${not empty enrolledStudents}">
        <table class="data-table">
            <thead><tr><th>Student ID</th><th>Name</th><th>Email</th></tr></thead>
            <tbody>
            <c:forEach var="student" items="${enrolledStudents}">
                <tr>
                    <td><c:out value="${student.studentUniqueId}"/></td>
                    <td><c:out value="${student.firstName} ${student.lastName}"/></td>
                    <td><c:out value="${student.userEmail}"/></td> <%-- Assuming Student object has userEmail --%>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </c:if>
    <c:if test="${empty enrolledStudents}"><p>No students enrolled in this course for this term.</p></c:if>
    <p><a href="${pageContext.request.contextPath}/FacultyCourseManagementServlet?action=myCourses" class="button button-secondary">Back to My Courses</a></p>
</div>
<jsp:include page="/common/footer.jsp" />
</body>
</html>
