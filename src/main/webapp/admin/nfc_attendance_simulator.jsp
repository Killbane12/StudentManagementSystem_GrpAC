<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.grpAC_SMS.dao.LectureSessionDao" %>
<%@ page import="com.grpAC_SMS.dao.impl.LectureSessionDaoImpl" %>
<%@ page import="com.grpAC_SMS.dao.CourseDao" %>
<%@ page import="com.grpAC_SMS.dao.impl.CourseDaoImpl" %>
<%@ page import="com.grpAC_SMS.dao.AcademicTermDao" %>
<%@ page import="com.grpAC_SMS.dao.impl.AcademicTermDaoImpl" %>
<%@ page import="com.grpAC_SMS.model.LectureSession" %>
<%@ page import="com.grpAC_SMS.model.Course" %>
<%@ page import="com.grpAC_SMS.model.AcademicTerm" %>
<%@ page import="java.util.List" %>
<%@ page import="java.time.LocalDateTime" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
    // This JSP is designed to be standalone. It does its own data fetching for the dropdown.
    // In a full MVC, a servlet would prepare this data.
    // For standalone, direct DAO usage in JSP (not ideal, but fits "standalone" to some extent for setup data).
    LectureSessionDao lectureSessionDao = new LectureSessionDaoImpl();
    CourseDao courseDao = new CourseDaoImpl(); // For course names
    AcademicTermDao termDao = new AcademicTermDaoImpl(); // For term names


    // Fetch only "active" or "upcoming" lecture sessions for simplicity.
    // "Active" could mean session_start_datetime <= now AND session_end_datetime >= now
    // Or simply sessions for today. For this demo, let's fetch all and let user choose.
    List<LectureSession> allSessions = lectureSessionDao.findAllWithDetails(); // Assumes method exists to get names

    String selectedLectureSessionId = request.getParameter("selectedLectureSessionId");
    String selectedCourseNameParam = request.getParameter("selectedCourseName");

    String nfcStatusMessage = (String) session.getAttribute("nfcStatusMessage");
    String nfcMessageType = (String) session.getAttribute("nfcMessageType");
    session.removeAttribute("nfcStatusMessage");
    session.removeAttribute("nfcMessageType");

    // If a session is selected via GET param, try to pre-select it
    LectureSession currentSelectedSession = null;
    if (selectedLectureSessionId != null && !selectedLectureSessionId.isEmpty()) {
        try {
            currentSelectedSession = allSessions.stream()
                    .filter(s -> String.valueOf(s.getLectureSessionId()).equals(selectedLectureSessionId))
                    .findFirst().orElse(null);
        } catch (Exception e) { /* ignore */ }
    }

    String pageTitle = "NFC Attendance Simulator";

%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><%= pageTitle %> | Student Management System - Group_AC</title>
    <%-- Minimal, self-contained styles. Not using common styles.css --%>
    <style>
        body { font-family: Arial, sans-serif; margin: 0; padding: 20px; background-color: #f4f4f4; color: #333; }
        .nfc-simulator-container { max-width: 600px; margin: 20px auto; padding: 20px; border: 1px solid #ccc; border-radius: 8px; background-color: #fff; box-shadow: 0 2px 4px rgba(0,0,0,0.1); }
        .nfc-simulator-container h2 { text-align: center; color: #333; margin-bottom:20px;}
        .nfc-simulator-container .form-group { margin-bottom: 15px; }
        .nfc-simulator-container label { display: block; margin-bottom: 5px; font-weight: bold; }
        .nfc-simulator-container select,
        .nfc-simulator-container input[type="text"],
        .nfc-simulator-container input[type="button"],
        .nfc-simulator-container button {
            width: 100%; padding: 10px; margin-bottom: 10px; border: 1px solid #ddd; border-radius: 4px; box-sizing: border-box; font-size: 1em;
        }
        .nfc-simulator-container input[readonly] { background-color: #eee; cursor: not-allowed; }
        .nfc-simulator-container .button-group { display: flex; justify-content: space-between; }
        .nfc-simulator-container .button-group button, .nfc-simulator-container .button-group input[type="button"] {
            width: 48%; /* Adjust as needed */
        }
        .nfc-simulator-container input[type="submit"] {
            background-color: #28a745; color: white; font-weight: bold;
        }
        .nfc-simulator-container input[type="submit"]:hover {
            background-color: #218838;
        }
        .nfc-simulator-container input[type="button"].randomize {
            background-color: #007bff; color: white;
        }
        .nfc-simulator-container input[type="button"].randomize:hover {
            background-color: #0056b3;
        }
        .nfc-status-message { padding: 10px; margin-top: 15px; border-radius: 4px; text-align: center; font-weight:bold;}
        .nfc-status-message.success { background-color: #d4edda; color: #155724; border: 1px solid #c3e6cb;}
        .nfc-status-message.error { background-color: #f8d7da; color: #721c24; border: 1px solid #f5c6cb;}
        .nfc-status-message.info { background-color: #d1ecf1; color: #0c5460; border: 1px solid #bee5eb;}
        .session-details { margin-bottom: 15px; padding:10px; background-color:#e9ecef; border-radius:4px; }
        .session-details p { margin: 5px 0; }
    </style>
    <%-- Using the global scripts.js from assets, assuming it's available if this page is moved with assets --%>
    <script src="${pageContext.request.contextPath}/assets/js/scripts.js"></script>
</head>
<body>
<div class="nfc-simulator-container">
    <h2>NFC Attendance Simulator</h2>
    <p>This tool simulates an NFC scanner marking student attendance for a lecture session.</p>
    <hr>

    <form id="nfcAttendanceForm" action="${pageContext.request.contextPath}/SimulatedAttendancePunchServlet" method="post">
        <div class="form-group">
            <label for="lectureSessionIdSelect">Select Active Lecture Session:</label>
            <select id="lectureSessionIdSelect" name="lectureSessionId" onchange="updateSessionDetails(this)" required>
                <option value="">-- Select a Session --</option>
                <c:forEach var="session" items="<%= allSessions %>">
                    <%
                        // Basic filtering for "active" or recent/upcoming: today +/- N days, or currently running
                        // For simplicity here, let's show sessions from last 7 days to next 7 days
                        LocalDateTime start = session.getSessionStartDatetime();
                        boolean isRecentOrUpcoming = start.isAfter(LocalDateTime.now().minusDays(7)) && start.isBefore(LocalDateTime.now().plusDays(7));
                        boolean isCurrentlySelected = currentSelectedSession != null && currentSelectedSession.getLectureSessionId() == session.getLectureSessionId();
                        if (isRecentOrUpcoming || isCurrentlySelected) {
                    %>
                    <option value="${session.lectureSessionId}"
                            data-coursename="<c:out value='${session.courseName}'/>"
                            data-facultyname="<c:out value='${session.facultyName != null ? session.facultyName : "N/A"}'/>"
                            data-starttime="<c:out value='<%= session.getSessionStartDatetime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) %>'/>"
                        ${isCurrentlySelected ? "selected" : ""}>
                        LSID: ${session.lectureSessionId} - <c:out value="${session.courseName}"/> (<c:out value="${session.termName}"/>) - <c:out value="<%= session.getSessionStartDatetime().format(DateTimeFormatter.ofPattern("EEE, MMM dd 'at' HH:mm")) %>"/>
                    </option>
                    <% } %>
                </c:forEach>
            </select>
        </div>

        <div id="selectedSessionDetails" class="session-details" style="display: none;">
            <h4>Selected Session Details:</h4>
            <p><strong>Course:</strong> <span id="detailsCourseName"></span></p>
            <p><strong>Faculty:</strong> <span id="detailsFacultyName"></span></p>
            <p><strong>Time:</strong> <span id="detailsStartTime"></span></p>
        </div>

        <%-- Hidden field to pass course name for display continuity on redirect --%>
        <input type="hidden" id="selectedCourseName" name="courseName" value="${not empty currentSelectedSession ? currentSelectedSession.courseName : (not empty selectedCourseNameParam ? selectedCourseNameParam : '') }">

        <div class="form-group">
            <label for="studentIdToMark">Student ID to Mark (Unique ID):</label>
            <input type="text" id="studentIdToMark" name="studentId" required>
        </div>

        <div class="button-group">
            <input type="button" class="randomize" value="Get Random Unmarked Student" onclick="callFetchRandomStudent()">
        </div>
        <br>
        <input type="submit" value="Mark Attendance (Simulate NFC Punch)">
    </form>

    <c:if test="${not empty nfcStatusMessage}">
        <div id="nfcStatusMessageServer" class="nfc-status-message ${nfcMessageType}">
                ${nfcStatusMessage}
        </div>
    </c:if>
    <%-- Placeholder for AJAX messages --%>
    <div id="nfcStatusMessage" class="nfc-status-message" style="display:none;"></div>


</div>

<script>
    function updateSessionDetails(selectElement) {
        const selectedOption = selectElement.options[selectElement.selectedIndex];
        const detailsDiv = document.getElementById('selectedSessionDetails');
        const studentIdField = document.getElementById('studentIdToMark');

        if (selectedOption && selectedOption.value) {
            document.getElementById('detailsCourseName').textContent = selectedOption.getAttribute('data-coursename');
            document.getElementById('detailsFacultyName').textContent = selectedOption.getAttribute('data-facultyname');
            document.getElementById('detailsStartTime').textContent = selectedOption.getAttribute('data-starttime');
            document.getElementById('selectedCourseName').value = selectedOption.getAttribute('data-coursename'); // Update hidden field
            detailsDiv.style.display = 'block';
            studentIdField.value = ''; // Clear student ID when session changes
        } else {
            detailsDiv.style.display = 'none';
            studentIdField.value = '';
            document.getElementById('selectedCourseName').value = '';
        }
        // Clear any previous AJAX status messages
        const ajaxStatusDiv = document.getElementById('nfcStatusMessage');
        ajaxStatusDiv.textContent = '';
        ajaxStatusDiv.style.display = 'none';
        ajaxStatusDiv.className = 'nfc-status-message';

        // Also clear server-side message if it exists, as user is interacting again
        const serverStatusDiv = document.getElementById('nfcStatusMessageServer');
        if(serverStatusDiv) serverStatusDiv.style.display = 'none';
    }

    function callFetchRandomStudent() {
        const lectureSessionId = document.getElementById('lectureSessionIdSelect').value;
        const contextPath = "${pageContext.request.contextPath}"; // Get context path from JSP
        fetchRandomUnmarkedStudent(lectureSessionId, contextPath); // Call the function from scripts.js
    }

    // Initialize details on page load if a session is pre-selected (e.g. after form submission)
    document.addEventListener('DOMContentLoaded', function() {
        const lectureSessionSelect = document.getElementById('lectureSessionIdSelect');
        if (lectureSessionSelect.value) {
            updateSessionDetails(lectureSessionSelect);
        }
    });
</script>
</body>
</html>
