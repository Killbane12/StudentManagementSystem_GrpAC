<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>My Enrolled Courses</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <!-- Bootstrap CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">

    <style>
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
        .content-area {
            padding: 20px;
        }
    </style>
</head>
<body>
<div class="container-fluid">
    <div class="row">
        <!-- Sidebar -->
        <div class="col-md-3 col-lg-2 sidebar">
            <h5 class="text-center mb-4">Student Panel</h5>
            <ul class="nav flex-column">
                <li class="nav-item">
                    <a href="${pageContext.request.contextPath}/student/dashboard">Dashboard</a>
                </li>
                <li class="nav-item">
                    <a href="#" class="active">Courses</a>
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

        <!-- Main Content -->
        <div class="col-md-9 col-lg-10 content-area">
            <div class="mb-4">
                <h1 class="h2">My Academic Program</h1>
                <div class="card">
                    <div class="card-body">
                        <h4 class="card-title">${program.programName}</h4>
                        <p class="card-text"><strong>Department:</strong> ${department.departmentName}</p>
                        <p class="card-text"><strong>Duration:</strong> ${program.durationYears} years</p>
                        <p class="card-text"><strong>Description:</strong> ${program.description}</p>
                    </div>
                </div>
            </div>

            <div>
                <h2 class="h4 mb-3">Courses in This Program</h2>
                <div class="table-responsive">
                    <table class="table table-striped table-bordered align-middle">
                        <thead class="table-dark">
                        <tr>
                            <th>Course Code</th>
                            <th>Course Name</th>
                            <th>Credits</th>
                            <th>Description</th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:forEach var="course" items="${courses}">
                            <tr>
                                <td>${course.courseCode}</td>
                                <td>${course.courseName}</td>
                                <td>${course.credits}</td>
                                <td>${course.description}</td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>
</div>

<!-- Bootstrap JS Bundle -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
