<%@ page contentType="text/html;charset=UTF-8" language="java" isErrorPage="true" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Page Not Found (404) - NSBM SMS</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/styles.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/common-elements.css">
</head>
<body>
<jsp:include page="/common/header.jsp" />
<div class="page-container">
    <main class="main-content">
        <h1>Page Not Found (Error 404)</h1>
        <p class="message error">Sorry, the page you requested could not be found.</p>
        <p>The URL may be incorrect, or the page may have been moved.</p>
        <p><a href="${pageContext.request.contextPath}/">Return to Home/Login</a></p>
    </main>
</div>
<jsp:include page="/common/footer.jsp" />
</body>
</html>