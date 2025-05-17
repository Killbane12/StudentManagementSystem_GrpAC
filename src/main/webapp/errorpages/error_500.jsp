<%@ page contentType="text/html;charset=UTF-8" language="java" isErrorPage="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Internal Server Error (500) | Student Management System - Group_AC</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/styles.css">
    <style> .container { text-align: center; margin-top: 50px; } .error-details { text-align: left; max-width: 800px; margin: 20px auto; padding:10px; border:1px solid #ccc; background-color:#f9f9f9;} </style>
</head>
<body>
<div class="container">
    <h1>Oops! Something Went Wrong (Error 500)</h1>
    <p>We encountered an internal server error. Please try again later.</p>
    <p><a href="${pageContext.request.contextPath}">Go to Homepage</a></p>

    <%-- For debugging (remove or conditionalize for production) --%>
    <c:if test="${pageContext.exception != null}">
        <div class="error-details">
            <h3>Error Details:</h3>
            <p><strong>Type:</strong> ${pageContext.exception.class.name}</p>
            <p><strong>Message:</strong> ${pageContext.exception.message}</p>
            <p><strong>Stack Trace:</strong></p>
            <pre><c:forEach var="trace" items="${pageContext.exception.stackTrace}">${trace}\n</c:forEach></pre>
        </div>
    </c:if>
</div>
</body>
</html>