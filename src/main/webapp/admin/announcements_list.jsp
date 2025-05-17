<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <link rel="icon" href="${pageContext.request.contextPath}/assets/img/favicon.ico" type="image/x-icon">
    <meta charset="UTF-8">
    <title>All Announcements List | Student Management System - Group_AC</title>
    <style>
        body {
            font-family: sans-serif;
            padding: 20px;
        }

    </style>
</head>
<body>
<%--    <h1>Display data in tabular format with options to add new, edit, or delete existing records. Should include search/filter capabilities if time permits.d</h1>--%>

<h1>All Announcements</h1>

<div class="controls">
    <button onclick="window.location.href='${pageContext.request.contextPath}/announcements/add'">Add New Announcement</button>
    <div>
        <label for="searchTitle">Search Title:</label>
        <input type="text" id="searchTitle" name="searchTitle" onkeyup="filterTable()">
    </div>
    <div>
        <label for="filterRole">Filter by Role:</label>
        <select id="filterRole" name="filterRole" onchange="filterTable()">
            <option value="">All Roles</option>
            <option value="ADMIN">Admin</option>
            <option value="TEACHER">Teacher</option>
            <option value="STUDENT">Student</option>
            <option value="PARENT">Parent</option>
            <option value="ALL">All</option>
        </select>
    </div>
</div>

<table>
    <thead>
    <tr>
        <th>ID</th>
        <th>Title</th>
        <th>Content</th>
        <th>Posted By User ID</th>
        <th>Target Role</th>
        <th>Expiry Date</th>
        <th>Created At</th>
        <th>Updated At</th>
        <th>Actions</th>
    </tr>
    </thead>
    <tbody>
    <c:forEach var="announcement" items="${announcements}">
        <tr>
            <td><c:out value="${announcement.announcementId}"/></td>
            <td class="title-cell"><c:out value="${announcement.title}"/></td>
            <td class="content-cell"><c:out value="${announcement.content}"/></td>
            <td><c:out value="${announcement.postedByUserId}"/></td>
            <td class="role-cell"><c:out value="${announcement.targetRole}"/></td>
            <td>
                <c:if test="${announcement.expiryDate != null}">
                    <fmt:formatDate value="${announcement.expiryDate}" pattern="yyyy-MM-dd"/>
                </c:if>
            </td>
            <td><fmt:formatDate value="${announcement.createdAt}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
            <td><fmt:formatDate value="${announcement.updatedAt}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
            <td class="actions">
                <a href="${pageContext.request.contextPath}/announcements/edit?id=${announcement.announcementId}" class="edit">Edit</a>
                <a href="${pageContext.request.contextPath}/announcements/delete?id=${announcement.announcementId}" class="delete" onclick="return confirm('Are you sure you want to delete this announcement?')">Delete</a>
            </td>
        </tr>
    </c:forEach>
    <c:if test="${empty announcements}">
        <tr><td colspan="9">No announcements found.</td></tr>
    </c:if>
    </tbody>
</table>

<script>
    function filterTable() {
        const searchInput = document.getElementById("searchTitle");
        const filterRole = document.getElementById("filterRole");
        const searchText = searchInput.value.toUpperCase();
        const selectedRole = filterRole.value.toUpperCase();
        const table = document.querySelector("table");
        const rows = table.getElementsByTagName("tr");

        for (let i = 1; i < rows.length; i++) {
            const titleCell = rows[i].querySelector(".title-cell");
            const roleCell = rows[i].querySelector(".role-cell");
            let displayRow = true;

            if (searchText && titleCell) {
                if (titleCell.textContent.toUpperCase().indexOf(searchText) === -1) {
                    displayRow = false;
                }
            }

            if (selectedRole && roleCell && displayRow) {
                if (selectedRole !== "ALL" && roleCell.textContent.toUpperCase().indexOf(selectedRole) === -1) {
                    displayRow = false;
                }
            }

            rows[i].style.display = displayRow ? "" : "none";
        }
    }
</script>
</body>
</html>
