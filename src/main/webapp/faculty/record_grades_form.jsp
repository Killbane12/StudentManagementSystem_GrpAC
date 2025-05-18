<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Record Grades: ${course.courseName} - ${assessmentType} | SMS - Group_AC</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/styles.css">
</head>
<body>
<jsp:include page="/common/header.jsp" />
<div class="container main-content">
    <h2>Record Grades for <c:out value="${course.courseCode} - ${course.courseName}"/></h2>
    <h3>Term: <c:out value="${term.termName}"/> | Assessment: <c:out value="${assessmentType}"/></h3>

    <c:if test="${not empty sessionScope.successMessage}">
        <p class="message success">${sessionScope.successMessage}</p>
        <c:remove var="successMessage" scope="session"/>
    </c:if>
    <c:if test="${not empty sessionScope.errorMessage}">
        <p class="message error">${sessionScope.errorMessage}</p>
        <c:remove var="errorMessage" scope="session"/>
    </c:if>

    <c:choose>
        <c:when test="${not empty enrollmentsForGrading}">
            <form action="${pageContext.request.contextPath}/FacultyRecordGradesServlet" method="post">
                <input type="hidden" name="action" value="saveGrades">
                <input type="hidden" name="courseId" value="${course.courseId}">
                <input type="hidden" name="termId" value="${term.academicTermId}">
                <input type="hidden" name="assessmentType" value="${assessmentType}">

                <table class="data-table">
                    <thead>
                    <tr>
                        <th>Student ID</th>
                        <th>Student Name</th>
                        <th>Grade Value</th>
                        <th>Remarks</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach var="enrollment" items="${enrollmentsForGrading}">
                        <input type="hidden" name="enrollmentId" value="${enrollment.enrollmentId}">
                        <c:set var="existingGrade" value="${existingGradesMap[enrollment.enrollmentId]}"/>
                        <tr>
                            <td><c:out value="${enrollment.studentUniqueId}"/></td>
                            <td><c:out value="${enrollment.studentName}"/></td>
                            <td>
                                <select name="gradeValue_${enrollment.enrollmentId}">
                                    <option value="">-- Select Grade --</option>
                                    <c:forEach var="gv" items="${validGradeValues}">
                                        <option value="${gv}" ${existingGrade.gradeValue == gv ? 'selected' : ''}>${gv}</option>
                                    </c:forEach>
                                </select>
                            </td>
                            <td>
                                <input type="text" name="remarks_${enrollment.enrollmentId}" value="<c:out value='${existingGrade.remarks}'/>" style="width: 95%;">
                            </td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
                <div class="form-actions" style="margin-top: 20px;">
                    <button type="submit" class="button">Save All Grades</button>
                    <a href="${pageContext.request.contextPath}/FacultyRecordGradesServlet?action=viewCoursesForGrading" class="button button-secondary">Back to Course Selection</a>
                </div>
            </form>
        </c:when>
        <c:otherwise>
            <p>No students enrolled in this course for this term to grade.</p>
            <a href="${pageContext.request.contextPath}/FacultyRecordGradesServlet?action=viewCoursesForGrading" class="button button-secondary">Back to Course Selection</a>
        </c:otherwise>
    </c:choose>
</div>
<jsp:include page="/common/footer.jsp" />
</body>
</html>