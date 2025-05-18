package com.grpAC_SMS.controller.student;

import com.grpAC_SMS.dao.CourseDao;
import com.grpAC_SMS.dao.StudentDao;
import com.grpAC_SMS.dao.impl.CourseDaoImpl;
import com.grpAC_SMS.dao.impl.EnrollmentDaoImpl;
import com.grpAC_SMS.dao.impl.LectureSessionDaoImpl;
import com.grpAC_SMS.dao.impl.StudentDaoImpl;
import com.grpAC_SMS.model.Course;
import com.grpAC_SMS.model.Enrollment;
import com.grpAC_SMS.model.LectureSession;
import com.grpAC_SMS.model.Student;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;


@WebServlet(name = "StudentDashboardServlet", value = "/student/dashboard")
//@WebServlet("/student/dashboard")
public class StudentDashboardServlet extends HttpServlet {
    StudentDao stdao;

//    @Override
//    public void init() {
//        stdao = new StudentDao();
//    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        StudentDaoImpl stuimpl = new StudentDaoImpl();
        List<Student> students = stuimpl.selectStudents();
        req.setAttribute("student_list", students);
        stuimpl.selectStudents();

        CourseDaoImpl coimpl = new CourseDaoImpl();
        List<Course> co = coimpl.selectCourse();
        req.setAttribute("course_list", co);
        coimpl.selectCourse();

        EnrollmentDaoImpl enrollimpl = new EnrollmentDaoImpl();
        List<Enrollment> enroll = enrollimpl.selectEnrollments();
        req.setAttribute("enrollment_list", enroll);
        enrollimpl.selectEnrollments();

        LectureSessionDaoImpl lecsessionimpl = new LectureSessionDaoImpl();
        List<LectureSession> lecture_session = lecsessionimpl.selectLectureSession();
        req.setAttribute("lecturs_list", lecture_session);
       // lecsessionimpl.selectLectureSession();


        RequestDispatcher dispatcher = req.getRequestDispatcher("/student/dashboard.jsp");
        dispatcher.forward(req, resp);
    }
}

//@WebServlet(name = "StudentDashboardServlet", value = "/student/dashboard")
//public class StudentDashboardServlet extends HttpServlet {
//
//    @Override
//    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//        // TODO: Fetch overview data (counts, etc.) using DAOs
//        System.out.println("Student Dashboard doGet called ----------------------.");
//        // Set data as request attributes
//        // request.setAttribute("studentCount", studentDao.count());
//        RequestDispatcher dispatcher = request.getRequestDispatcher("/student/dashboard.jsp");
//        dispatcher.forward(request, response);
//    }
//}