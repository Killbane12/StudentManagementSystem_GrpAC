<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<link rel="icon" href="${pageContext.request.contextPath}/assets/img/favicon.ico" type="image/x-icon">
<header class="main-header">
    <div class="logo-container">
        <%-- Assuming logo.png is in assets/img/ --%>
        <img src="${pageContext.request.contextPath}/assets/img/logo.png" alt="NSBM Logo">
        <h1>NSBM Student Management System</h1>
    </div>
    <div class="user-info">
        <c:if test="${not empty sessionScope.loggedInUser}">
            <span>Welcome, <c:out value="${sessionScope.loggedInUser.username}"/> (<c:out
                    value="${sessionScope.loggedInUser.role}"/>)</span>
            <a href="${pageContext.request.contextPath}/LogoutServlet">Logout</a>
        </c:if>
        <c:if test="${empty sessionScope.loggedInUser}">
            <span>Guest</span>
            <%-- Optionally add login link if header shown on login page --%>
            <%-- <a href="${pageContext.request.contextPath}/auth/login.jsp">Login</a> --%>
        </c:if>
    </div>
</header>