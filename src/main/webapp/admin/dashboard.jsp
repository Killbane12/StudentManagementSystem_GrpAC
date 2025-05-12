<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Admin Dashboard - NSBM SMS</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/styles.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/common-elements.css">
</head>
<body data-context-path="${pageContext.request.contextPath}">
<jsp:include page="/common/header.jsp" />
<div class="page-container">
    <jsp:include page="/common/admin_nav.jsp" />
    <main class="main-content">
        <h1>Admin Dashboard</h1>
        <p>Welcome, Admin!</p>
        <%-- TODO: Display overview statistics fetched by the servlet --%>
        <%-- Example: --%>
        <%-- <div class="dashboard-stats"> --%>
        <%--    <div class="stat-card">Students: <c:out value="${requestScope.studentCount}" /></div> --%>
        <%--    <div class="stat-card">Courses: <c:out value="${requestScope.courseCount}" /></div> --%>
        <%-- </div> --%>
        <p><i>(Dashboard overview and statistics will appear here...)</i></p>
    </main>
</div>
<jsp:include page="/common/footer.jsp" />
<script src="${pageContext.request.contextPath}/assets/js/scripts.js"></script>
</body>
</html>