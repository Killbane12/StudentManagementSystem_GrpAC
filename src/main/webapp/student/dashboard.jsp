<%@ page import="com.grpAC_SMS.model.Student" %>
<%@ page import="java.util.List" %>
<%@ page import="com.grpAC_SMS.model.Course" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <link rel="icon" href="${pageContext.request.contextPath}/assets/img/favicon.ico" type="image/x-icon">
    <meta charset="UTF-8">
    <title>Student Dashboard | Student Management System - Group_AC</title>
    <style> body {
        font-family: sans-serif;
        padding: 20px;
    } </style>
</head>
<body>

<h1>Personalized student dashboard</h1>
<section style="border: 3px solid black; border-radius: 10px; ">
    <center><h1>Students Details</h1></center>
    <table>
        <tr>
            <th>Student_Id</th>
            <th>User ID</th>
            <th>Student_Unique_Id</th>
            <th>First_Name</th>
            <th>First_Name</th>
            <th>Last_Name</th>
            <th>DOB</th>
            <th>Gender</th>
            <th>Address</th>
            <th>Phone number</th>
            <th>Enrollment_Date</th>
            <th>Program_Id</th>
            <th>Created_at</th>
            <th>Updated_at</th>
        </tr>
        <% List<Student> sl = (List<Student>) request.getAttribute("student_list");
            if (sl != null && !sl.isEmpty()) {
                for (Student student : sl) {

        %>
        <tr>
            <td><%= student.getStudentId()%></td>
            <td><%= student.getUserId()%></td>
            <td><%= student.getStudentUniqueId()%></td>
            <td><%= student.getFirstName()%></td>
            <td><%= student.getLastName()%></td>
            <td><%= student.getDateOfBirth()%></td>
            <td><%= student.getGender()%></td>
            <td><%= student.getAddress()%></td>
            <td><%= student.getPhoneNumber()%></td>
            <td><%= student.getEnrollmentDate()%></td>
            <td><%= student.getProgramId()%></td>
            <td><%= student.getCreatedAt()%></td>
            <td><%= student.getUpdatedAt()%></td>
        </tr>
        <%
            }
        } else {
        %>
        <tr><td colspan="4" style="text-align: center;">No students found.</td></tr>
        <% }%>
    </table>
</section>

<section style="border: 3px solid black; border-radius: 10px; margin-top: 20px;">
    <center><h1>Course Details</h1></center>
    <table>
        <tr>
            <th>Course_Id</th>
            <th>Course_Code</th>
            <th>Course_Name</th>
            <th>Description</th>
            <th>Credits</th>
            <th>Program_Id</th>
            <th>Department_Id</th>
            <th>Created_At</th>
            <th>Updated_At</th>
        </tr>
<%--    co mean course    --%>
        <% List<Course> co = (List<Course>) request.getAttribute("course_list");
            if (co != null && !co.isEmpty()) {
                for (Course course : co) {

        %>
        <tr>
            <td><%= course.getCourseId()%></td>
            <td><%= course.getCourseCode()%></td>
            <td><%= course.getCourseName()%></td>
            <td><%= course.getDescription()%></td>
            <td><%= course.getCredits()%></td>
            <td><%= course.getProgramId()%></td>
            <td><%= course.getDepartmentId()%></td>
            <td><%= course.getCreatedAt()%></td>
            <td><%= course.getUpdatedAt()%></td>
        </tr>
        <%
            }
        } else {
        %>
        <tr><td colspan="4" style="text-align: center;">No students found.</td></tr>
        <% }%>
    </table>
</section>

</body>
</html>