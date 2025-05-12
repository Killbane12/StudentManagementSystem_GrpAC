<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<nav class="side-nav">
    <h3>Admin Menu</h3>
    <ul>
        <li><a href="${pageContext.request.contextPath}/admin/dashboard">Dashboard</a></li>
        <li><a href="${pageContext.request.contextPath}/admin/users?action=list">Manage Users</a></li>
        <li><a href="${pageContext.request.contextPath}/admin/students?action=list">Manage Students</a></li>
        <li><a href="${pageContext.request.contextPath}/admin/faculty?action=list">Manage Faculty</a></li>
        <li><a href="${pageContext.request.contextPath}/admin/departments?action=list">Manage Departments</a></li>
        <li><a href="${pageContext.request.contextPath}/admin/programs?action=list">Manage Programs</a></li>
        <li><a href="${pageContext.request.contextPath}/admin/courses?action=list">Manage Courses</a></li>
        <li><a href="${pageContext.request.contextPath}/admin/terms?action=list">Manage Academic Terms</a></li>
        <li><a href="${pageContext.request.contextPath}/admin/locations?action=list">Manage Locations</a></li>
        <li><a href="${pageContext.request.contextPath}/admin/sessions?action=list">Manage Lecture Sessions</a></li>
        <li><a href="${pageContext.request.contextPath}/admin/enrollments?action=list">Manage Enrollments</a></li>
        <li><a href="${pageContext.request.contextPath}/admin/announcements?action=list">Manage Announcements</a></li>
        <li><a href="${pageContext.request.contextPath}/admin/nfc_attendance_simulator.jsp">NFC Attendance Sim</a></li>
        <%-- TODO: Add links for Admin Grade/Attendance oversight if separate pages created --%>
    </ul>
</nav>