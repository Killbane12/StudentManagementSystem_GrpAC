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
                display: flex;
                flex-direction: column;
                align-items: center;
                color: #343A40;
            }

            .controls {
                margin-bottom: 15px;
                display: flex;
                gap: 10px;
                align-items: center;
            }

            .controls input[type="text"],
            select {
                padding: 8px;
                border: 1px solid #ccc;
                border-radius: 4px;
                box-sizing: border-box;
            }

            .controls button {
                padding: 10px 15px;
                background-color: #343A40;
                color: white;
                border: none;
                border-radius: 4px;
                cursor: pointer;
                font-size: 1em;
            }

            .controls button:hover {
                opacity: 0.8;
            }

            table {
                width: 100%;
                border-collapse: collapse;
            }

            th, td {
                border: 1px solid #ddd;
                padding: 8px;
                text-align: left;
            }

            th {
                background-color: #f2f2f2;
            }

            .actions a {
                text-decoration: none;
                padding: 3px 10px;
                border: none;
                border-radius: 4px;
                font-size: 0.9em;
                display: flex;
                align-items: center;
                justify-content: center;
            }

            .actions a.edit {
                background-color: #28a745;
                color: white;
            }

            .actions a.delete {
                background-color: #dc3545;
                color: white;
            }

            .actions a:hover {
                opacity: 0.8
            }

            .backDashboard{
                text-decoration: none;
            }

            .backDashboard:hover,
            .backDashboard:visited,
            .backDashboard:active {
                color: #0056B3;
            }

            .backDashboard:hover {
                text-decoration: underline;
                padding: 10px;
                border-radius: 4px;
                color: #0056B3;
            }

        </style>
    </head>
    <body>
        <%--    <h1>Display data in tabular format with options to add new, edit, or delete existing records. Should include search/filter capabilities if time permits.d</h1>--%>

        <h1>All Announcements</h1>

        <div class="controls">
            <button onclick="window.location.href='${pageContext.request.contextPath}/announcements/add'">Add New Announcement</button>
            <br>
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

        <p><a href="${pageContext.request.contextPath}/admin/dashboard" class="backDashboard">Back to Dashboard</a></p>

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
