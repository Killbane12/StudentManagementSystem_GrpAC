<%@ page contentType="text/html;charset=UTF-8" language="java" isErrorPage="true" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Page Not Found (404) | Student Management System - Group_AC</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/styles.css">
    <style> .container { text-align: center; margin-top: 50px; } </style>
</head>
<body>
<div class="container">
    <h1>Oops! Page Not Found (Error 404)</h1>
    <p>The page you are looking for might have been removed, had its name changed, or is temporarily unavailable.</p>
    <p>Requested URL: ${pageContext.errorData.requestURI}</p>
    <p><a href="${pageContext.request.contextPath}">Go to Homepage</a></p>
</div>
</body>
</html>