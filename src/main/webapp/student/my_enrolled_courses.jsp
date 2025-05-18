<%@ page import="java.util.List" %>
<%@ page import="com.grpAC_SMS.model.Course" %>

<h2>My Enrolled Courses</h2>

<table border="1" cellpadding="8" style="border-collapse: collapse;">
    <tr>
        <th>Course ID</th>
        <th>Course Code</th>
        <th>Course Name</th>
    </tr>
    <%
        List<Course> courses = (List<Course>) request.getAttribute("courses");
        if (courses != null && !courses.isEmpty()) {
            for (Course course : courses) {
    %>
    <tr>
        <td><%= course.getCourseId() %></td>
        <td><%= course.getCourseCode() %></td>
        <td><%= course.getCourseName() %></td>
    </tr>
    <%
        }
    } else {
    %>
    <tr>
        <td colspan="3">No enrolled courses found.</td>
    </tr>
    <%
        }
    %>
</table>
