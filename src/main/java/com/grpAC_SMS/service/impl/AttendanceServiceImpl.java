package com.grpAC_SMS.service.impl;

import com.grpAC_SMS.dao.*;
import com.grpAC_SMS.dao.impl.*;
import com.grpAC_SMS.exception.BusinessLogicException;
import com.grpAC_SMS.model.*;
import com.grpAC_SMS.service.AttendanceService;
import com.grpAC_SMS.util.ApplicationConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

public class AttendanceServiceImpl implements AttendanceService {
    private static final Logger logger = LoggerFactory.getLogger(AttendanceServiceImpl.class);

    private AttendanceDao attendanceDao;
    private StudentDao studentDao;
    private LectureSessionDao lectureSessionDao;
    private EnrollmentDao enrollmentDao;
    // No CourseDao directly needed if LectureSessionDao provides courseId

    public AttendanceServiceImpl() {
        this.attendanceDao = new AttendanceDaoImpl();
        this.studentDao = new StudentDaoImpl();
        this.lectureSessionDao = new LectureSessionDaoImpl();
        this.enrollmentDao = new EnrollmentDaoImpl();
    }

    // Constructor for dependency injection (recommended)
    public AttendanceServiceImpl(AttendanceDao attendanceDao, StudentDao studentDao,
                                 LectureSessionDao lectureSessionDao, EnrollmentDao enrollmentDao) {
        this.attendanceDao = attendanceDao;
        this.studentDao = studentDao;
        this.lectureSessionDao = lectureSessionDao;
        this.enrollmentDao = enrollmentDao;
    }

    @Override
    public Student getRandomUnmarkedStudentForSession(int lectureSessionId) throws BusinessLogicException {
        Optional<LectureSession> sessionOpt = lectureSessionDao.findById(lectureSessionId);
        if (sessionOpt.isEmpty()) {
            throw new BusinessLogicException("Lecture session with ID " + lectureSessionId + " not found.");
        }
        int courseId = sessionOpt.get().getCourseId();
        List<Student> unmarkedStudents = studentDao.findUnmarkedStudentsForSession(lectureSessionId, courseId);

        if (unmarkedStudents.isEmpty()) {
            logger.info("No unmarked students found for session ID {}", lectureSessionId);
            return null; // Or throw specific exception like NoUnmarkedStudentsException
        }
        Random random = new Random();
        return unmarkedStudents.get(random.nextInt(unmarkedStudents.size()));
    }

    @Override
    public boolean markNfcAttendance(int studentId, int lectureSessionId, String deviceId) throws BusinessLogicException {
        // Validate student and session exist
        Student student = studentDao.findById(studentId)
                .orElseThrow(() -> new BusinessLogicException("Student with ID " + studentId + " not found."));
        LectureSession session = lectureSessionDao.findById(lectureSessionId)
                .orElseThrow(() -> new BusinessLogicException("Lecture session with ID " + lectureSessionId + " not found."));

        // Validate enrollment
        if (!enrollmentDao.isStudentEnrolledInCourseForTerm(student.getStudentId(), session.getCourseId(), session.getAcademicTermId())) {
            throw new BusinessLogicException("Student " + student.getStudentUniqueId() + " is not enrolled in course " +
                    session.getCourseName() + " for term " + session.getTermName() + ".");
        }

        Optional<Attendance> existingAttendanceOpt = attendanceDao.findByStudentAndSession(studentId, lectureSessionId);
        if (existingAttendanceOpt.isPresent()) {
            Attendance existingAttendance = existingAttendanceOpt.get();
            if (existingAttendance.isPresent()) {
                logger.info("Attendance already marked PRESENT for student {} in session {}. No update needed.", studentId, lectureSessionId);
                return true; // Already present, consider it a success
            } else {
                // Was marked absent or record exists without status, update to present
                existingAttendance.setPresent(true);
                existingAttendance.setPunchInTimestamp(Timestamp.valueOf(LocalDateTime.now()));
                existingAttendance.setDeviceId(deviceId != null ? deviceId : ApplicationConstants.DEFAULT_NFC_DEVICE_ID);
                existingAttendance.setRecordedByFacultyId(null); // NFC is not faculty
                attendanceDao.update(existingAttendance);
                logger.info("Updated existing attendance to PRESENT for student {} in session {} via NFC.", studentId, lectureSessionId);
                return true;
            }
        } else {
            // New attendance record
            Attendance newAttendance = new Attendance();
            newAttendance.setStudentId(studentId);
            newAttendance.setLectureSessionId(lectureSessionId);
            newAttendance.setPunchInTimestamp(Timestamp.valueOf(LocalDateTime.now()));
            newAttendance.setPresent(true);
            newAttendance.setDeviceId(deviceId != null ? deviceId : ApplicationConstants.DEFAULT_NFC_DEVICE_ID);
            // recorded_by_faculty_id is null for NFC
            attendanceDao.add(newAttendance);
            logger.info("Marked NFC attendance for student {} in session {} as PRESENT.", studentId, lectureSessionId);
            return true;
        }
    }

    @Override
    public boolean markFacultyAttendance(int studentId, int lectureSessionId, boolean isPresent, int facultyMemberId) throws BusinessLogicException {
        // Validate student and session exist
        Student student = studentDao.findById(studentId)
                .orElseThrow(() -> new BusinessLogicException("Student with ID " + studentId + " not found."));
        LectureSession session = lectureSessionDao.findById(lectureSessionId)
                .orElseThrow(() -> new BusinessLogicException("Lecture session with ID " + lectureSessionId + " not found."));

        // Validate faculty exists (optional, but good practice)
//         facultyDao.findById(facultyMemberId).orElseThrow(() -> new BusinessLogicException("Faculty with ID " + facultyMemberId + " not found."));


        // Validate enrollment
        if (!enrollmentDao.isStudentEnrolledInCourseForTerm(student.getStudentId(), session.getCourseId(), session.getAcademicTermId())) {
            throw new BusinessLogicException("Student " + student.getStudentUniqueId() + " is not enrolled in the course for this session.");
        }

        Optional<Attendance> existingAttendanceOpt = attendanceDao.findByStudentAndSession(studentId, lectureSessionId);
        Attendance attendanceRecord;
        if (existingAttendanceOpt.isPresent()) {
            attendanceRecord = existingAttendanceOpt.get();
            attendanceRecord.setPresent(isPresent);
            attendanceRecord.setPunchInTimestamp(Timestamp.valueOf(LocalDateTime.now())); // Update timestamp on change
            attendanceRecord.setRecordedByFacultyId(facultyMemberId);
            attendanceRecord.setDeviceId(null); // Faculty marking, not NFC
            attendanceDao.update(attendanceRecord);
            logger.info("Updated attendance for student {} in session {} to {} by faculty {}.", studentId, lectureSessionId, isPresent, facultyMemberId);
        } else {
            attendanceRecord = new Attendance();
            attendanceRecord.setStudentId(studentId);
            attendanceRecord.setLectureSessionId(lectureSessionId);
            attendanceRecord.setPunchInTimestamp(Timestamp.valueOf(LocalDateTime.now()));
            attendanceRecord.setPresent(isPresent);
            attendanceRecord.setRecordedByFacultyId(facultyMemberId);
            // device_id is null
            attendanceDao.add(attendanceRecord);
            logger.info("Marked attendance for student {} in session {} as {} by faculty {}.", studentId, lectureSessionId, isPresent, facultyMemberId);
        }
        return true;
    }

    @Override
    public void markMultipleStudentsAttendance(List<Attendance> attendanceList, int facultyMemberId) throws BusinessLogicException {
        if (attendanceList == null || attendanceList.isEmpty()) {
            throw new BusinessLogicException("Attendance list cannot be empty.");
        }
        // Basic validation for faculty (could be more thorough)
        // facultyDao.findById(facultyMemberId).orElseThrow(() -> new BusinessLogicException("Faculty ID " + facultyMemberId + " not found."));


        for (Attendance attInput : attendanceList) {
            // It's assumed studentId and lectureSessionId are valid and student is enrolled.
            // A more robust system might re-verify each.
            Optional<Attendance> existingAtt = attendanceDao.findByStudentAndSession(attInput.getStudentId(), attInput.getLectureSessionId());
            if (existingAtt.isPresent()) {
                Attendance dbAtt = existingAtt.get();
                dbAtt.setPresent(attInput.isPresent());
                dbAtt.setPunchInTimestamp(Timestamp.valueOf(LocalDateTime.now())); // Update time
                dbAtt.setRecordedByFacultyId(facultyMemberId);
                dbAtt.setDeviceId(null);
                attendanceDao.update(dbAtt);
            } else {
                attInput.setPunchInTimestamp(Timestamp.valueOf(LocalDateTime.now()));
                attInput.setRecordedByFacultyId(facultyMemberId);
                attInput.setDeviceId(null);
                attendanceDao.add(attInput);
            }
        }
        logger.info("Processed multiple student attendance markings by faculty {}", facultyMemberId);
    }


    @Override
    public List<Attendance> getAttendanceForStudentInCourse(int studentId, int courseId) {
        // DAO method already fetches with details
        return attendanceDao.findByStudentAndCourse(studentId, courseId);
    }

    @Override
    public Map<Integer, Double> getAttendancePercentageForStudentCourses(int studentId, List<Integer> courseIds, int academicTermId) {
        Map<Integer, Double> percentages = new HashMap<>();
        if (courseIds == null || courseIds.isEmpty()) {
            return percentages;
        }
        for (Integer courseId : courseIds) {
            double percentage = attendanceDao.calculateAttendancePercentage(studentId, courseId, academicTermId);
            percentages.put(courseId, percentage);
        }
        return percentages;
    }

    @Override
    public List<Attendance> getAttendanceForSession(int lectureSessionId) {
        // DAO method already fetches with details
        return attendanceDao.findByLectureSessionId(lectureSessionId);
    }

    @Override
    public List<Attendance> getAttendanceByStudentAndSession(int studentId, int lectureSessionId) {
        // This method seems redundant if findByStudentAndSession returns Optional<Attendance>.
        // If the intent is to always return a list (even if empty or single element):
        Optional<Attendance> optAtt = attendanceDao.findByStudentAndSession(studentId, lectureSessionId);
        return optAtt.map(List::of).orElseGet(List::of);
    }
}
