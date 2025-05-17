<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
    <head>
        <link rel="icon" href="${pageContext.request.contextPath}/assets/img/favicon.ico" type="image/x-icon">
        <meta charset="UTF-8">
        <%--        <title>Add an Announcement | Student Management System - Group_AC</title>--%>
        <title>${announcement.announcementId == 0 ? 'Add New' : 'Edit'} Announcement | Student Management System - Group_AC</title>
        <style>

            body {
                font-family: sans-serif;
                padding: 20px;
                color: #343A40;
            }

            .allContent {
                padding: 30px 100px;
                background-color: #F4F4F4;
                border-radius: 4px;
                display: flex;
                flex-direction: column;
                align-items: center;
            }

            label {
                display: block;
                margin-bottom: 5px;
                font-weight: bold;
            }

            input[type="text"],
            input[type="number"],
            input[type="date"],
            textarea,
            select {
                width: 100%;
                padding: 8px;
                border: 1px solid #ccc;
                border-radius: 4px;
                box-sizing: border-box;
            }

            textarea {
                resize: vertical;
            }

            .error-message {
                color: red;
                font-size: 0.9em;
                margin-top: 5px;
            }

            button {
                padding: 10px 15px;
                background-color: #343A40;
                color: white;
                border: none;
                border-radius: 4px;
                cursor: pointer;
                font-size: 1em;
            }

            button:hover {
                opacity: 0.8;
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
        <%--        <h1>Forms for creating or editing records. Fields should correspond to the model attributes, with Clear labels and input validation feedback.</h1>--%>

        <div class = "allContent">
            <h1>${announcement.announcementId == 0 ? 'Add New' : 'Edit'} Announcement</h1>

            <form action="${pageContext.request.contextPath}/announcements/save" method="post">
                <input type="hidden" name="announcementId" value="${announcement.announcementId}">

                <div class="form-group">
                    <label for="title">Title:</label>
                    <input type="text" id="title" name="title" value="${announcement.title}" required minlength="3" maxlength="255">
                    <div class="error-message" id="titleError"></div>
                </div>

                <br>

                <div class="form-group">
                    <label for="content">Content:</label>
                    <textarea id="content" name="content" rows="5" required minlength="5">${announcement.content}</textarea>
                    <div class="error-message" id="contentError"></div>
                </div>

                <br>

                <div class="form-group">
                    <label for="postedByUserId">Posted By User ID:</label>
                    <input type="number" id="postedByUserId" name="postedByUserId" value="${announcement.postedByUserId}" required>
                    <div class="error-message" id="postedByUserIdError"></div>
                </div>

                <br>

                <div class="form-group">
                    <label for="targetRole">Target Role:</label>
                    <select id="targetRole" name="targetRole" required>
                        <option value="">-- Select Role --</option>
                        <option value="ADMIN" ${announcement.targetRole == 'ADMIN' ? 'selected' : ''}>Admin</option>
                        <option value="TEACHER" ${announcement.targetRole == 'TEACHER' ? 'selected' : ''}>Teacher</option>
                        <option value="STUDENT" ${announcement.targetRole == 'STUDENT' ? 'selected' : ''}>Student</option>
                        <option value="PARENT" ${announcement.targetRole == 'PARENT' ? 'selected' : ''}>Parent</option>
                        <option value="ALL" ${announcement.targetRole == 'ALL' ? 'selected' : ''}>All</option>
                    </select>
                    <div class="error-message" id="targetRoleError"></div>
                </div>

                <br>

                <div class="form-group">
                    <label for="imageFilePath">Image File Path (Optional):</label>
                    <input type="text" id="imageFilePath" name="imageFilePath" value="${announcement.imageFilePath}">
                </div>

                <br>

                <div class="form-group">
                    <label for="expiryDate">Expiry Date (Optional):</label>
                    <input type="date" id="expiryDate" name="expiryDate" value="${announcement.expiryDate != null ? '<c:out value="${announcement.expiryDate}"/>' : ''}">
                </div>

                <br>

                <button type="submit">${announcement.announcementId == 0 ? 'Add' : 'Update'} Announcement</button>
                <c:if test="${announcement.announcementId != 0}">
                    <button type="button" onclick="window.location.href='${pageContext.request.contextPath}/announcements'">Cancel</button>
                </c:if>

                <button type="button" onclick="window.location.href='${pageContext.request.contextPath}/announcements'">View All Announcements</button>
            </form>

            <p><a href="${pageContext.request.contextPath}/admin/dashboard" class="backDashboard">Back to Dashboard</a></p>

        </div>

        <script>
            // Basic client-side validation (you might want to enhance this)
            const form = document.querySelector('form');
            const titleInput = document.getElementById('title');
            const contentInput = document.getElementById('content');
            const postedByUserIdInput = document.getElementById('postedByUserId');
            const targetRoleSelect = document.getElementById('targetRole');

            form.addEventListener('submit', (event) => {
                let isValid = true;

                if (titleInput.value.trim() === '') {
                    document.getElementById('titleError').textContent = 'Title is required.';
                    isValid = false;
                } else if (titleInput.value.length < 3 || titleInput.value.length > 255) {
                    document.getElementById('titleError').textContent = 'Title must be between 3 and 255 characters.';
                    isValid = false;
                } else {
                    document.getElementById('titleError').textContent = '';
                }

                if (contentInput.value.trim() === '') {
                    document.getElementById('contentError').textContent = 'Content is required.';
                    isValid = false;
                } else if (contentInput.value.length < 5) {
                    document.getElementById('contentError').textContent = 'Content must be at least 5 characters.';
                    isValid = false;
                } else {
                    document.getElementById('contentError').textContent = '';
                }

                if (postedByUserIdInput.value.trim() === '' || isNaN(postedByUserIdInput.value)) {
                    document.getElementById('postedByUserIdError').textContent = 'User ID is required and must be a number.';
                    isValid = false;
                } else {
                    document.getElementById('postedByUserIdError').textContent = '';
                }

                if (targetRoleSelect.value === '') {
                    document.getElementById('targetRoleError').textContent = 'Please select a target role.';
                    isValid = false;
                } else {
                    document.getElementById('targetRoleError').textContent = '';
                }

                if (!isValid) {
                    event.preventDefault(); // Prevent form submission if validation fails
                }
            });
        </script>
    </body>
</html>