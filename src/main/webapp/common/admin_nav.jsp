<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<li><a href="${pageContext.request.contextPath}/AdminDashboardServlet">Admin Dashboard</a>
</li> <%-- Should go to servlet --%>
<li><a href="${pageContext.request.contextPath}/ManageUsersServlet?action=list">Manage Users</a></li>
<li><a href="${pageContext.request.contextPath}/ManageStudentsServlet?action=list">Manage Students</a></li>
<li><a href="${pageContext.request.contextPath}/ManageFacultyServlet?action=list">Manage Faculty</a></li>
<li><a href="${pageContext.request.contextPath}/ManageDepartmentsServlet?action=list">Departments</a></li>
<li><a href="${pageContext.request.contextPath}/ManageProgramsServlet?action=list">Programs</a></li>
<li><a href="${pageContext.request.contextPath}/ManageCoursesServlet?action=list">Courses</a></li>
<li><a href="${pageContext.request.contextPath}/ManageAcademicTermsServlet?action=list">Academic Terms</a></li>
<li><a href="${pageContext.request.contextPath}/ManageLocationsServlet?action=list">Locations</a></li>
<li><a href="${pageContext.request.contextPath}/ManageLectureSessionsServlet?action=list">Lecture Sessions</a></li>
<li><a href="${pageContext.request.contextPath}/ManageEnrollmentsServlet?action=list">Enrollments</a></li>
<li><a href="${pageContext.request.contextPath}/AdminManageGradesServlet?action=list">Grades Oversight</a></li>
<li><a href="${pageContext.request.contextPath}/AdminManageAttendanceServlet?action=list">Attendance Oversight</a></li>
<li><a href="${pageContext.request.contextPath}/ManageAnnouncementsServlet?action=list">Announcements</a></li>
<li><a href="${pageContext.request.contextPath}/admin/nfc_attendance_simulator.jsp">NFC Attendance Sim</a></li>