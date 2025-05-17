<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="com.grpAC_SMS.util.DateFormatter" %>
<%@ page import="com.grpAC_SMS.model.Role" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>${empty announcement.announcementId ? 'Add New' : 'Edit'} Announcement | Student Management System - Group_AC</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/styles.css">
</head>
<body>
<jsp:include page="/common/header.jsp" />
<div class="container main-content form-container">
    <h2>${empty announcement.announcementId ? 'Add New Announcement' : 'Edit Announcement'}</h2>
    <c:if test="${not empty requestScope.errorMessage}">
        <p class="message error">${requestScope.errorMessage}</p>
    </c:if>
    <c:if test="${not empty sessionScope.errorMessage && empty requestScope.errorMessage}">
        <p class="message error">${sessionScope.errorMessage}</p>
        <c:remove var="errorMessage" scope="session"/>
    </c:if>

    <form action="${pageContext.request.contextPath}/ManageAnnouncementsServlet" method="post">
        <input type="hidden" name="action" value="${empty announcement.announcementId ? 'create' : 'update'}">
        <c:if test="${not empty announcement.announcementId}">
            <input type="hidden" name="announcementId" value="${announcement.announcementId}">
        </c:if>

        <div class="form-group">
            <label for="title">Title (Required):</label>
            <input type="text" id="title" name="title" value="<c:out value='${announcement.title}'/>" required>
        </div>
        <div class="form-group">
            <label for="content">Content (Required):</label>
            <textarea id="content" name="content" rows="5" required><c:out value='${announcement.content}'/></textarea>
        </div>
        <div class="form-group">
            <label for="targetRole">Target Role (Required):</label>
            <select id="targetRole" name="targetRole" required>
                <option value="ALL" ${announcement.targetRole == 'ALL' ? 'selected' : (empty announcement.targetRole ? 'selected' : '')}>ALL</option>
                <c:forEach var="roleEnum" items="${targetRoles}"> <%-- targetRoles is Role.values() from servlet --%>
                    <option value="${roleEnum.name()}" ${announcement.targetRole == roleEnum.name() ? 'selected' : ''}>
                        <c:out value="${roleEnum.name()}"/>
                    </option>
                </c:forEach>
            </select>
        </div>
        <div class="form-group">
            <label for="expiryDate">Expiry Date (Optional):</label>
            <input type="date" id="expiryDate" name="expiryDate" value="${DateFormatter.sqlDateToString(announcement.expiryDate)}">
        </div>
        <%-- Image file path is for future use, not implemented for upload/display in this iteration --%>
        <%--
        <div class="form-group">
            <label for="imageFilePath">Image File Path (Optional, for future use):</label>
            <input type="text" id="imageFilePath" name="imageFilePath" value="<c:out value='${announcement.imageFilePath}'/>">
        </div>
        --%>
        <div class="form-actions">
            <button type="submit" class="button">${empty announcement.announcementId ? 'Create Announcement' : 'Update Announcement'}</button>
            <a href="${pageContext.request.contextPath}/ManageAnnouncementsServlet?action=list" class="button button-secondary">Cancel</a>
        </div>
    </form>
</div>
<jsp:include page="/common/footer.jsp" />
</body>
</html>