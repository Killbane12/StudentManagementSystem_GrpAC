<%@ page contentType="text/html;charset=UTF-8" language="java" isErrorPage="true" %> <%-- Mark as error page --%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Access Denied - NSBM SMS</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/styles.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/common-elements.css">
</head>
<body>
<jsp:include page="/common/header.jsp"/>
<div class="page-container">
    <%-- No Nav bar usually for error pages --%>
    <main class="main-content">
        <h1>Access Denied (403 Forbidden)</h1>
        <p class="message error">You do not have permission to access the requested page.</p>
        <p>Please contact the system administrator if you believe this is an error.</p>
        <p><a href="${pageContext.request.contextPath}/">Return to Home/Login</a>
        </p> <%-- Link back to index or login --%>
    </main>
</div>
<jsp:include page="/common/footer.jsp"/>
</body>
</html>