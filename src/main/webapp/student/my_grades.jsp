
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>My Grades | Student Management System - Group_AC</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/styles.css">
</head>
<body>
<jsp:include page="/common/header.jsp" />
<div class="container main-content">
    <h2>My Grades</h2>
    <%-- TODO: Add course/term filter --%>
    <c:if test="${not empty studentGrades}">
        <table class="data-table">
            <thead>
            <tr>
                <th>Course Code</th>
                <th>Course Name</th>
                <th>Assessment Type</th>
                <th>Grade</th>
                <th>Graded Date</th>
                <th>Remarks</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach var="grade" items="${studentGrades}"> <%-- List<Grade> with details --%>
                <tr>
                    <td><c:out value="${grade.courseCode}"/></td> <%-- Assume Grade object has these details --%>
                    <td><c:out value="${grade.courseName}"/></td>
                    <td><c:out value="${grade.assessmentType}"/></td>
                    <td><c:out value="${grade.gradeValue}"/></td>
                    <td><fmt:formatDate value="${grade.gradedDate}" pattern="yyyy-MM-dd"/></td>
                    <td><c:out value="${grade.remarks}"/></td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </c:if>
    <c:if test="${empty studentGrades}"><p>No grades recorded yet.</p></c:if>
</div>
<jsp:include page="/common/footer.jsp" />
</body>
</html>
