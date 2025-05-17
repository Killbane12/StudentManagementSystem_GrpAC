<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<header>
    <div class="header-content">
        <img src="${pageContext.request.contextPath}/assets/img/logo.png" alt="NSBM Logo" class="logo">
        <h1>Student Management System - NSBM</h1>
        <div class="user-info">
            <c:if test="${not empty sessionScope.loggedInUser}">
                <span>Welcome, ${sessionScope.loggedInUser.username} (${sessionScope.loggedInUser.role})</span>
                <a href="${pageContext.request.contextPath}/LogoutServlet">Logout</a>
            </c:if>
        </div>
    </div>
</header>
<nav class="main-nav">
    <ul>
        <c:choose>
            <c:when test="${sessionScope.loggedInUser.role == 'ADMIN'}">
                <jsp:include page="admin_nav.jsp"/>
            </c:when>
            <c:when test="${sessionScope.loggedInUser.role == 'FACULTY'}">
                <jsp:include page="faculty_nav.jsp"/>
            </c:when>
            <c:when test="${sessionScope.loggedInUser.role == 'STUDENT'}">
                <jsp:include page="student_nav.jsp"/>
            </c:when>
        </c:choose>
    </ul>
</nav>