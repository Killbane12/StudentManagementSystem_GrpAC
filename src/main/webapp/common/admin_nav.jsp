<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<li><a href="${pageContext.request.contextPath}/admin/dashboard.jsp">Admin Dashboard</a></li>
<li><a href="${pageContext.request.contextPath}/ManageUsersServlet?action=list">Manage Users</a></li>
<li><a href="${pageContext.request.contextPath}/admin/students_list.jsp">Manage Students</a>
</li> <%-- Or /ManageStudentsServlet?action=list --%>
<li><a href="${pageContext.request.contextPath}/admin/faculty_list.jsp">Manage Faculty</a>
</li>   <%-- Or /ManageFacultyServlet?action=list --%>
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