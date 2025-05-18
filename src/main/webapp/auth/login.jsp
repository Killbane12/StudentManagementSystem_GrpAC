<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <link rel="icon" href="${pageContext.request.contextPath}/assets/img/favicon.ico" type="image/x-icon">
    <meta charset="UTF-8">
    <title>Login | Student Management System - Group_AC</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/styles.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/common-elements.css">
    <style>
        /* Simple centering for login form */
        body {
            display: flex;
            flex-direction: column;
            align-items: center;
            justify-content: center;
            min-height: 100vh;
        }

        .login-container {
            width: 100%;
            max-width: 400px;
            margin-top: 20px;
        }

        /* Let header/footer take space */
        header.main-header, footer.main-footer {
            width: 100%;
            flex-shrink: 0;
        }

        /* Ensure header/footer span width */
        main.main-content {
            width: 100%;
            box-shadow: none;
            background: transparent;
            margin: 0;
            padding: 0;
        }

        /* Remove default main styles */
    </style>
</head>
<body> <%-- Added data attribute for JS --%>
<jsp:include page="/common/header.jsp"/>

<main class="main-content login-container">
    <h1>Login</h1>
    <c:if test="${not empty requestScope.errorMessage}">
        <p class="message error">${requestScope.errorMessage}</p>
    </c:if>
    <c:if test="${param.logout == 'success'}"> <%-- Check for logout message param --%>
        <p class="message success">You have been logged out successfully.</p>
    </c:if>

    <form action="${pageContext.request.contextPath}/LoginServlet" method="post">
        <div>
            <label for="username">Username:</label>
            <input type="text" id="username" name="username" required placeholder="Enter username">
        </div>
        <div>
            <label for="password">Password:</label>
            <input type="password" id="password" name="password" required placeholder="Enter password">
        </div>
        <button type="submit">Login</button>
    </form>
</main>

<jsp:include page="/common/footer.jsp"/>
<%-- No extra scripts needed for basic login --%>
</body>
</html>