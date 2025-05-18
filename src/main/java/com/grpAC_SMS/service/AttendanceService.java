package com.grpAC_SMS.service;

import com.grpAC_SMS.model.Attendance;
import com.grpAC_SMS.model.Student;
import com.grpAC_SMS.exception.BusinessLogicException;
import java.util.List;
import java.util.Map;

public interface AttendanceService {
    // For NFC Simulator
    Student getRandomUnmarkedStudentForSession(int lectureSessionId) throws BusinessLogicException;
    boolean markNfcAttendance(int studentId, int lectureSessionId, String deviceId) throws BusinessLogicException;

    // For Faculty Marking
    boolean markFacultyAttendance(int studentId, int lectureSessionId, boolean isPresent, int facultyMemberId) throws BusinessLogicException;
    void markMultipleStudentsAttendance(List<Attendance> attendanceList, int facultyMemberId) throws BusinessLogicException;


    // For Viewing
    List<Attendance> getAttendanceForStudentInCourse(int studentId, int courseId);
    Map<Integer, Double> getAttendancePercentageForStudentCourses(int studentId, List<Integer> courseIds, int academicTermId);
    List<Attendance> getAttendanceForSession(int lectureSessionId);
    List<Attendance> getAttendanceByStudentAndSession(int studentId, int lectureSessionId);
}