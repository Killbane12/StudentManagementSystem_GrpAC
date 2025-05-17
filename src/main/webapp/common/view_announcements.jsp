<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>${pageTitle} | Student Management System - Group_AC</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/styles.css">
    <style>
        .announcement-item {
            border: 1px solid #ddd;
            padding: 15px;
            margin-bottom: 15px;
            border-radius: 5px;
            background-color: #f9f9f9;
        }
        .announcement-item h3 {
            margin-top: 0;
            color: #004466;
        }
        .announcement-meta {
            font-size: 0.9em;
            color: #555;
            margin-bottom: 10px;
        }
    </style>
</head>
<body>
<jsp:include page="/common/header.jsp" />
<div class="container main-content">
    <h2>${pageTitle}</h2>

    <c:if test="${not empty errorMessage}">
        <p class="message error">${errorMessage}</p>
    </c:if>

    <c:choose>
        <c:when test="${not empty announcements}">
            <c:forEach var="ann" items="${announcements}">
                <div class="announcement-item">
                    <h3><c:out value="${ann.title}"/></h3>
                    <div class="announcement-meta">
                        Posted by: <c:out value="${ann.postedByUsername}"/> on
                        <fmt:formatDate value="${ann.createdAt}" pattern="yyyy-MM-dd HH:mm"/>
                        <c:if test="${not empty ann.expiryDate}">
                            | Expires: <fmt:formatDate value="${ann.expiryDate}" pattern="yyyy-MM-dd"/>
                        </c:if>
                    </div>
                    <div class="announcement-content">
                            <%-- Naive way to handle newlines in text from DB --%>
                        <c:set var="contentWithBreaks" value="${fn:replace(ann.content, '\\n', '<br/>')}"/>
                        <p>${contentWithBreaks}</p>
                    </div>
                </div>
            </c:forEach>
        </c:when>
        <c:otherwise>
            <p>No announcements available for you at this time.</p>
        </c:otherwise>
    </c:choose>
</div>
<jsp:include page="/common/footer.jsp" />
</body>
</html>