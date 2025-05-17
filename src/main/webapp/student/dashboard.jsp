<%--<%@ page contentType="text/html;charset=UTF-8" language="java" %>--%>
<%--<!DOCTYPE html>--%>
<%--<html lang="en">--%>
<%--<head>--%>
<%--    <link rel="icon" href="${pageContext.request.contextPath}/assets/img/favicon.ico" type="image/x-icon">--%>
<%--    <meta charset="UTF-8">--%>
<%--    <title>Student Dashboard | Student Management System - Group_AC</title>--%>
<%--    <style> body {--%>
<%--        font-family: sans-serif;--%>
<%--        padding: 20px;--%>
<%--    } </style>--%>
<%--</head>--%>
<%--<body>--%>
<%--<h1>Personalized student dashboard</h1>--%>
<%--</body>--%>
<%--</html>--%>
<%@ page import="com.grpAC_SMS.model.Student" %>
<%@ page import="java.util.List" %>
<%@ page import="com.grpAC_SMS.model.Course" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Student Dashboard</title>
    <!-- Bootstrap CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>
        .student-profile {
            padding: 20px;
            background-color: #f8f9fa;
            border-radius: 10px;
            margin-bottom: 20px;
        }
        .profile-header {
            border-bottom: 2px solid #dee2e6;
            padding-bottom: 15px;
            margin-bottom: 20px;
        }
        .profile-section {
            margin-bottom: 30px;
        }
        .sidebar {
            background-color: #343a40;
            color: white;
            height: 100vh;
            padding-top: 20px;
        }
        .sidebar a {
            color: white;
            text-decoration: none;
            display: block;
            padding: 10px 15px;
            transition: background-color 0.3s;
        }
        .sidebar a:hover {
            background-color: #495057;
        }
        .sidebar .active {
            background-color: #0d6efd;
        }
        .welcome-section {
            background-color: #e9ecef;
            padding: 20px;
            border-radius: 5px;
            margin-bottom: 20px;
        }
    </style>
</head>
<body>
<div class="container-fluid">
    <div class="row">
        <!-- Sidebar -->
        <div class="col-md-3 col-lg-2 d-md-block sidebar collapse">
            <div class="position-sticky">
                <h5 class="text-center mb-4">Student Panel</h5>
                <ul class="nav flex-column">
                    <li class="nav-item">
                        <a href="#" class="active">Dashboard</a>
                    </li>
                    <li class="nav-item">
                        <a href="../student/my_enrolled_courses">Courses</a>
                    </li>
                    <li class="nav-item">
                        <a href="#">Grades</a>
                    </li>
                    <li class="nav-item">
                        <a href="#">Timetable</a>
                    </li>
                    <li class="nav-item">
                        <a href="#">Settings</a>
                    </li>
                    <li class="nav-item mt-5">
                        <a href="${pageContext.request.contextPath}/LogoutServlet">Logout</a>
                    </li>
                </ul>
            </div>
        </div>

        <!-- Main Content Area -->
        <div class="col-md-9 ms-sm-auto col-lg-10 px-md-4">
            <div class="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center pt-3 pb-2 mb-3 border-bottom">
                <h1 class="h2">Student Dashboard</h1>
                <div class="btn-toolbar mb-2 mb-md-0">
                    <div class="btn-group me-2">
                        <button type="button" class="btn btn-sm btn-outline-secondary">Share</button>
                        <button type="button" class="btn btn-sm btn-outline-secondary">Export</button>
                    </div>
                </div>
            </div>

            <!-- Welcome Section -->
            <div class="welcome-section">
                <h3>Welcome, ${student.firstName} ${student.lastName}</h3>
                <p>Student ID: ${student.studentUniqueId}</p>
            </div>

            <!-- Student Profile Section -->
            <div class="student-profile">
                <div class="profile-header">
                    <h3>Student Profile</h3>
                </div>

                <div class="profile-section">
                    <div class="row">
                        <div class="col-md-4">
                            <h5>Personal Information</h5>
                            <dl class="row">
                                <dt class="col-sm-5">Full Name:</dt>
                                <dd class="col-sm-7">${student.firstName} ${student.lastName}</dd>

                                <dt class="col-sm-5">Student ID:</dt>
                                <dd class="col-sm-7">${student.studentUniqueId}</dd>

                                <dt class="col-sm-5">Date of Birth:</dt>
                                <dd class="col-sm-7">${student.dateOfBirth}</dd>

                                <dt class="col-sm-5">Gender:</dt>
                                <dd class="col-sm-7">${student.gender}</dd>
                            </dl>
                        </div>
                        <div class="col-md-4">
                            <h5>Contact Information</h5>
                            <dl class="row">
                                <dt class="col-sm-5">Address:</dt>
                                <dd class="col-sm-7">${student.address}</dd>

                                <dt class="col-sm-5">Phone:</dt>
                                <dd class="col-sm-7">${student.phoneNumber}</dd>

                                <%-- If you add email back to your model --%>
                                <dt class="col-sm-5">Email:</dt>
                                <dd class="col-sm-7">${user.email}</dd>
                            </dl>
                        </div>
                        <div class="col-md-4">
                            <h5>System Information</h5>
                            <dl class="row">
                                <dt class="col-sm-5">Student ID:</dt>
                                <dd class="col-sm-7">${student.studentId}</dd>

                                <dt class="col-sm-5">User ID:</dt>
                                <dd class="col-sm-7">${student.userId}</dd>
                            </dl>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Additional Dashboard Widgets -->
            <div class="row mt-4">
                <div class="col-md-6">
                    <div class="card">
                        <div class="card-header">
                            Upcoming Assignments
                        </div>
                        <div class="card-body">
                            <p class="card-text">No upcoming assignments at this time.</p>
                        </div>
                    </div>
                </div>
                <div class="col-md-6">
                    <div class="card">
                        <div class="card-header">
                            Recent Announcements
                        </div>
                        <div class="card-body">
                            <p class="card-text">No recent announcements at this time.</p>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<!-- Bootstrap JS Bundle with Popper -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
