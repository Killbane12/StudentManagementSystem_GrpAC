<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<li><a href="${pageContext.request.contextPath}/faculty/dashboard.jsp">Faculty Dashboard</a></li>
<li><a href="${pageContext.request.contextPath}/FacultyCourseManagementServlet?action=myCourses">My Courses</a></li>
<li><a href="${pageContext.request.contextPath}/FacultyRecordGradesServlet?action=viewCoursesForGrading">Record
                                                                                                         Grades</a></li>
<li><a href="${pageContext.request.contextPath}/FacultyRecordAttendanceServlet?action=viewSessionsForAttendance">Record
                                                                                                                 Attendance</a>
</li>
<li><a href="${pageContext.request.contextPath}/ViewAnnouncementsServlet">View Announcements</a>
</li> <%-- General announcement servlet --%>