<%@ page import="java.util.List" %>
<%@ page import="com.grpAC_SMS.model.Grade" %>

<h2>My Grades</h2>

<table border="1" cellpadding="10" style="border-collapse: collapse;">
    <tr>
        <th>Course Name</th>
        <th>Grade</th>
        <th>Assetment Type</th>
    </tr>
    <%
        List<Grade> grades = (List<Grade>) request.getAttribute("grades");
        if (grades != null && !grades.isEmpty()) {
            for (Grade g : grades) {
    %>
    <tr>
        <td><%= g.getCourseName() %></td>
        <td><%= g.getGradeValue() %></td>
        <td><%= g.getAssessmentType() %></td>
    </tr>
    <%
        }
    } else {
    %>
    <tr><td colspan="3">No grades available.</td></tr>
    <%
        }
    %>
</table>
