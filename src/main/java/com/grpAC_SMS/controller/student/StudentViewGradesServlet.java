package com.grpAC_SMS.controller.student;

import com.grpAC_SMS.dao.GradeDao;
import com.grpAC_SMS.dao.impl.GradeDaoImpl;
import com.grpAC_SMS.model.Grade;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.List;

@WebServlet("/StudentViewGrades")
public class StudentViewGradesServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);

        if (session != null && session.getAttribute("studentId") != null) {
            int studentId = (Integer) session.getAttribute("studentId");
            System.out.println("Servlet triggered");
            System.out.println("Student ID: " + studentId);
            GradeDao gradeDao = new GradeDaoImpl();
            List<Grade> grades = gradeDao.getGradesByStudentId(studentId);
            System.out.println("Grades found: " + grades.size());
            req.setAttribute("grades", grades);
            req.getRequestDispatcher("student/my_grades.jsp").forward(req, resp);
        } else {
            resp.sendRedirect("student_login.jsp");
        }
    }
}
