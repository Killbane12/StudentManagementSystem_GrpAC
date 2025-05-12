<%@ page contentType="text/html;charset=UTF-8" language="java" isErrorPage="true" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Page Not Found (404) | Student Management System - Group_AC</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/styles.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/common-elements.css">
</head>
<body>
<jsp:include page="/common/header.jsp" />
<div class="page-container">
    <main class="main-content">
        <h1>Internal Server Error (Error 500)</h1>
        <p class="message error">Sorry, something went wrong.</p>
        <p>Try to refresh this page or feel free to contact us if the problem persists.</p>
        <p><a href="${pageContext.request.contextPath}/">Return to Home/Login</a></p>
    </main>
</div>
<jsp:include page="/common/footer.jsp" />
</body>
</html>