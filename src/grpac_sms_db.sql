-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: May 12, 2025 at 09:32 PM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.2.12

SET
SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET
time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `grpac_sms_db`
--

-- --------------------------------------------------------

--
-- Table structure for table `academicterms`
--

CREATE TABLE `academicterms`
(
    `academic_term_id` int(11) NOT NULL,
    `term_name`        varchar(50) NOT NULL COMMENT 'e.g., Semester 1',
    `start_date`       date        NOT NULL,
    `end_date`         date        NOT NULL,
    `created_at`       timestamp   NOT NULL DEFAULT current_timestamp(),
    `updated_at`       timestamp   NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `academicterms`
--

INSERT INTO `academicterms` (`academic_term_id`, `term_name`, `start_date`, `end_date`, `created_at`, `updated_at`)
VALUES (1, '2024 Semester 1', '2024-02-15', '2024-06-30', '2025-05-11 19:29:56', '2025-05-11 19:29:56'),
       (2, '2024 Semester 2', '2024-07-15', '2024-11-30', '2025-05-11 19:29:56', '2025-05-11 19:29:56'),
       (3, '2025 Semester 1', '2025-02-15', '2025-06-30', '2025-05-11 19:29:56', '2025-05-11 19:29:56');

-- --------------------------------------------------------

--
-- Table structure for table `announcements`
--

CREATE TABLE `announcements`
(
    `announcement_id`   int(11) NOT NULL,
    `title`             varchar(255) NOT NULL,
    `content`           text         NOT NULL,
    `posted_by_user_id` int(11) NOT NULL COMMENT 'User (Admin) who posted',
    `target_role`       enum('ALL','STUDENT','FACULTY','ADMIN') DEFAULT 'ALL',
    `image_file_path`   varchar(255)          DEFAULT NULL COMMENT 'Path to optional image/flyer, for future use',
    `expiry_date`       date                  DEFAULT NULL,
    `created_at`        timestamp    NOT NULL DEFAULT current_timestamp(),
    `updated_at`        timestamp    NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `announcements`
--

INSERT INTO `announcements` (`announcement_id`, `title`, `content`, `posted_by_user_id`, `target_role`,
                             `image_file_path`, `expiry_date`, `created_at`, `updated_at`)
VALUES (1, 'Mid-Semester Break Reminder',
        'Dear all, please note the upcoming mid-semester break from 2025-04-10 to 2025-04-18.', 1, 'ALL', NULL,
        '2025-04-19', '2025-05-11 19:32:27', '2025-05-11 19:32:27'),
       (2, 'Call for Research Symposium Papers',
        'Faculty members are invited to submit papers for the annual research symposium. Deadline: 2025-08-01.', 1,
        'FACULTY', NULL, '2025-08-02', '2025-05-11 19:32:27', '2025-05-11 19:32:27'),
       (3, 'Library System Maintenance',
        'The library system will be down for maintenance on 2025-05-20 from 02:00 AM to 04:00 AM.', 1, 'STUDENT', NULL,
        NULL, '2025-05-11 19:32:27', '2025-05-11 19:32:27');

-- --------------------------------------------------------

--
-- Table structure for table `attendance`
--

CREATE TABLE `attendance`
(
    `attendance_id`          int(11) NOT NULL,
    `student_id`             int(11) NOT NULL,
    `lecture_session_id`     int(11) NOT NULL,
    `punch_in_timestamp`     timestamp NOT NULL DEFAULT current_timestamp(),
    `is_present`             tinyint(1) NOT NULL DEFAULT 0,
    `recorded_by_faculty_id` int(11) DEFAULT NULL COMMENT 'If faculty marked it',
    `device_id`              varchar(50)        DEFAULT NULL COMMENT 'ID of the NFC device',
    `created_at`             timestamp NOT NULL DEFAULT current_timestamp(),
    `updated_at`             timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `attendance`
--

INSERT INTO `attendance` (`attendance_id`, `student_id`, `lecture_session_id`, `punch_in_timestamp`, `is_present`,
                          `recorded_by_faculty_id`, `device_id`, `created_at`, `updated_at`)
VALUES (1, 1, 1, '2024-02-20 03:28:00', 1, NULL, 'SIM_NFC_DEVICE_01', '2025-05-11 19:31:31', '2025-05-11 19:31:31'),
       (2, 2, 2, '2024-02-21 04:25:00', 1, NULL, 'SIM_NFC_DEVICE_02', '2025-05-11 19:31:31', '2025-05-11 19:31:31'),
       (3, 1, 4, '2024-03-05 03:31:00', 1, 3, NULL, '2025-05-11 19:31:31', '2025-05-11 19:31:31'),
       (4, 2, 5, '2024-03-06 08:29:00', 1, NULL, 'SIM_NFC_DEVICE_01', '2025-05-11 19:31:31', '2025-05-11 19:31:31');

-- --------------------------------------------------------

--
-- Table structure for table `courses`
--

CREATE TABLE `courses`
(
    `course_id`     int(11) NOT NULL,
    `course_code`   varchar(20)  NOT NULL,
    `course_name`   varchar(100) NOT NULL,
    `description`   text                  DEFAULT NULL,
    `credits`       int(11) DEFAULT NULL,
    `program_id`    int(11) DEFAULT NULL,
    `department_id` int(11) NOT NULL,
    `created_at`    timestamp    NOT NULL DEFAULT current_timestamp(),
    `updated_at`    timestamp    NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `courses`
--

INSERT INTO `courses` (`course_id`, `course_code`, `course_name`, `description`, `credits`, `program_id`,
                       `department_id`, `created_at`, `updated_at`)
VALUES (1, 'BM101', 'Introduction to Management', 'Fundamental concepts of management.', 3, 2, 1, '2025-05-11 19:29:33',
        '2025-05-11 19:29:33'),
       (2, 'CS101', 'Introduction to Programming', 'Basics of programming using Python.', 3, 21, 2,
        '2025-05-11 19:29:33', '2025-05-11 19:29:33'),
       (3, 'LW101', 'Introduction to Law', 'Basic legal principles and systems.', 3, 51, 5, '2025-05-11 19:29:33',
        '2025-05-11 19:29:33'),
       (4, 'SC101', 'Principles of Biology', 'Core concepts in biological sciences.', 3, 33, 3, '2025-05-11 19:29:33',
        '2025-05-11 19:29:33'),
       (5, 'EN101', 'Engineering Mechanics', 'Statics and dynamics for engineers.', 3, 41, 4, '2025-05-11 19:29:33',
        '2025-05-11 19:29:33'),
       (6, 'BM205', 'Marketing Principles', 'Core concepts of marketing.', 3, 2, 1, '2025-05-11 19:29:33',
        '2025-05-11 19:29:33'),
       (7, 'CS202', 'Data Structures and Algorithms', 'Fundamental data structures and algorithm analysis.', 4, 21, 2,
        '2025-05-11 19:29:33', '2025-05-11 19:29:33'),
       (8, 'LW203', 'Contract Law', 'Principles of contract law.', 4, 51, 5, '2025-05-11 19:29:33',
        '2025-05-11 19:29:33');

-- --------------------------------------------------------

--
-- Table structure for table `departments`
--

CREATE TABLE `departments`
(
    `department_id`   int(11) NOT NULL,
    `department_name` varchar(100) NOT NULL COMMENT 'e.g., Faculty of Computing',
    `created_at`      timestamp    NOT NULL DEFAULT current_timestamp(),
    `updated_at`      timestamp    NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `departments`
--

INSERT INTO `departments` (`department_id`, `department_name`, `created_at`, `updated_at`)
VALUES (1, 'Faculty of Business', '2025-05-11 19:26:28', '2025-05-11 19:26:28'),
       (2, 'Faculty of Computing', '2025-05-11 19:26:28', '2025-05-11 19:26:28'),
       (3, 'Faculty of Science', '2025-05-11 19:26:28', '2025-05-11 19:26:28'),
       (4, 'Faculty of Engineering', '2025-05-11 19:26:28', '2025-05-11 19:26:28'),
       (5, 'Faculty of Law', '2025-05-11 19:26:28', '2025-05-11 19:26:28');

-- --------------------------------------------------------

--
-- Table structure for table `enrollments`
--

CREATE TABLE `enrollments`
(
    `enrollment_id`    int(11) NOT NULL,
    `student_id`       int(11) NOT NULL,
    `course_id`        int(11) NOT NULL,
    `academic_term_id` int(11) NOT NULL,
    `enrollment_date`  date      NOT NULL,
    `status`           enum('ENROLLED','COMPLETED','DROPPED') DEFAULT 'ENROLLED',
    `created_at`       timestamp NOT NULL DEFAULT current_timestamp(),
    `updated_at`       timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `enrollments`
--

INSERT INTO `enrollments` (`enrollment_id`, `student_id`, `course_id`, `academic_term_id`, `enrollment_date`, `status`,
                           `created_at`, `updated_at`)
VALUES (1, 1, 1, 1, '2024-02-10', 'COMPLETED', '2025-05-11 19:30:23', '2025-05-11 19:30:23'),
       (2, 1, 6, 1, '2024-02-10', 'COMPLETED', '2025-05-11 19:30:23', '2025-05-11 19:30:23'),
       (3, 2, 2, 1, '2024-02-10', 'COMPLETED', '2025-05-11 19:30:23', '2025-05-11 19:30:23'),
       (4, 2, 7, 1, '2024-02-10', 'ENROLLED', '2025-05-11 19:30:23', '2025-05-11 19:30:23'),
       (5, 3, 3, 2, '2024-07-10', 'ENROLLED', '2025-05-11 19:30:23', '2025-05-11 19:30:23'),
       (6, 3, 8, 3, '2025-02-10', 'ENROLLED', '2025-05-11 19:30:23', '2025-05-11 19:30:23');

-- --------------------------------------------------------

--
-- Table structure for table `faculty`
--

CREATE TABLE `faculty`
(
    `faculty_member_id` int(11) NOT NULL,
    `user_id`           int(11) DEFAULT NULL,
    `faculty_unique_id` varchar(20) NOT NULL COMMENT 'e.g., FAC001',
    `first_name`        varchar(50) NOT NULL,
    `last_name`         varchar(50) NOT NULL,
    `department_id`     int(11) NOT NULL,
    `office_location`   varchar(100)         DEFAULT NULL,
    `contact_email`     varchar(100)         DEFAULT NULL,
    `phone_number`      varchar(20)          DEFAULT NULL,
    `created_at`        timestamp   NOT NULL DEFAULT current_timestamp(),
    `updated_at`        timestamp   NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `faculty`
--

INSERT INTO `faculty` (`faculty_member_id`, `user_id`, `faculty_unique_id`, `first_name`, `last_name`, `department_id`,
                       `office_location`, `contact_email`, `phone_number`, `created_at`, `updated_at`)
VALUES (1, NULL, 'FAC001', 'Dushar', 'Dayararthna', 1, 'C1-101', 'dushar.d@nsbm.ac.lk', '0112345678',
        '2025-05-11 19:27:47', '2025-05-11 19:27:47'),
       (2, 5, 'FAC002', 'Thilini', 'De Silva', 1, 'C1-DeanOffice', 'thilini.ds@nsbm.ac.lk', '0112345679',
        '2025-05-11 19:27:47', '2025-05-11 19:27:47'),
       (3, 10, 'FAC003', 'Prasanna', 'Perera', 1, 'C1-102', 'prasanna.p@nsbm.ac.lk', '0112345680',
        '2025-05-11 19:27:47', '2025-05-11 19:27:47'),
       (4, 11, 'FAC004', 'Bhasuri', 'Bhagyani', 1, 'C1-103', 'bhasuri.b@nsbm.ac.lk', '0112345681',
        '2025-05-11 19:27:47', '2025-05-11 19:27:47'),
       (5, 12, 'FAC005', 'Anne', 'Pathiranage', 1, 'C1-104', 'anne.p@nsbm.ac.lk', '0112345682', '2025-05-11 19:27:47',
        '2025-05-11 19:27:47'),
       (6, 13, 'FAC006', 'Venura', 'Colombage', 1, 'C1-105', 'venura.c@nsbm.ac.lk', '0112345683', '2025-05-11 19:27:47',
        '2025-05-11 19:27:47'),
       (7, 14, 'FAC007', 'Sudath', 'Amarasena', 1, 'C1-106', 'sudath.a@nsbm.ac.lk', '0112345684', '2025-05-11 19:27:47',
        '2025-05-11 19:27:47'),
       (8, 6, 'FAC008', 'Rasika', 'Ranaweera', 2, 'C2-DeanOffice', 'rasika.r@nsbm.ac.lk', '0113456789',
        '2025-05-11 19:27:47', '2025-05-11 19:27:47'),
       (9, NULL, 'FAC009', 'Saravanapavan', 'Nasiketha', 2, 'C2-101', 'saravanapavan.n@nsbm.ac.lk', '0113456790',
        '2025-05-11 19:27:47', '2025-05-11 19:27:47'),
       (10, 15, 'FAC010', 'Chamindra', 'Attanayake', 2, 'C2-102', 'chamindra.a@nsbm.ac.lk', '0113456791',
        '2025-05-11 19:27:47', '2025-05-11 19:27:47'),
       (11, 16, 'FAC011', 'Mohamed', 'Shafraz', 2, 'C2-103', 'mohamed.s@nsbm.ac.lk', '0113456792',
        '2025-05-11 19:27:47', '2025-05-11 19:27:47'),
       (12, 7, 'FAC012', 'Nuwanthi', 'Katuwavila', 3, 'C3-DeanOffice', 'nuwanthi.k@nsbm.ac.lk', '0114567890',
        '2025-05-11 19:27:47', '2025-05-11 19:27:47'),
       (13, NULL, 'FAC013', 'Damayanthi', 'Dahanayake', 3, 'C3-101', 'damayanthi.d@nsbm.ac.lk', '0114567891',
        '2025-05-11 19:27:47', '2025-05-11 19:27:47'),
       (14, 8, 'FAC014', 'Chandana', 'Perera', 4, 'C3-DeanOffice-Eng', 'chandana.p@nsbm.ac.lk', '0115678901',
        '2025-05-11 19:27:48', '2025-05-11 19:27:48'),
       (15, 17, 'FAC015', 'Prabhath', 'Buddhika', 4, 'C3-102', 'prabhath.b@nsbm.ac.lk', '0115678902',
        '2025-05-11 19:27:48', '2025-05-11 19:27:48'),
       (16, 9, 'FAC016', 'Shanthi', 'Segarajasingham', 5, 'C1-LawOffice', 'shanthi.s@nsbm.ac.lk', '0116789012',
        '2025-05-11 19:27:48', '2025-05-11 19:27:48'),
       (17, NULL, 'FAC017', 'Samithri', 'Wanniachy', 5, 'C1-LawOffice2', 'samithri.w@nsbm.ac.lk', '0116789013',
        '2025-05-11 19:27:48', '2025-05-11 19:27:48');

-- --------------------------------------------------------

--
-- Table structure for table `grades`
--

CREATE TABLE `grades`
(
    `grade_id`             int(11) NOT NULL,
    `enrollment_id`        int(11) NOT NULL,
    `grade_value`          varchar(10)        DEFAULT NULL COMMENT 'e.g., A+, B, 4.0, 85%',
    `assessment_type`      varchar(50)        DEFAULT NULL COMMENT 'e.g., Midterm, Final Exam, Assignment 1',
    `graded_by_faculty_id` int(11) DEFAULT NULL,
    `graded_date`          timestamp NOT NULL DEFAULT current_timestamp(),
    `remarks`              text               DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `grades`
--

INSERT INTO `grades` (`grade_id`, `enrollment_id`, `grade_value`, `assessment_type`, `graded_by_faculty_id`,
                      `graded_date`, `remarks`)
VALUES (1, 1, 'A-', 'Final Exam', 3, '2024-07-05 04:30:00', 'Good performance.'),
       (2, 2, 'B+', 'Final Exam', 3, '2024-07-05 04:30:00', NULL),
       (3, 3, 'A', 'Final Exam', 9, '2024-07-06 05:30:00', 'Excellent work!');

-- --------------------------------------------------------

--
-- Table structure for table `lecturesessions`
--

CREATE TABLE `lecturesessions`
(
    `lecture_session_id`     int(11) NOT NULL,
    `course_id`              int(11) NOT NULL,
    `faculty_member_id`      int(11) DEFAULT NULL,
    `academic_term_id`       int(11) NOT NULL,
    `location_id`            int(11) DEFAULT NULL,
    `session_start_datetime` datetime  NOT NULL,
    `session_end_datetime`   datetime  NOT NULL,
    `created_at`             timestamp NOT NULL DEFAULT current_timestamp(),
    `updated_at`             timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `lecturesessions`
--

INSERT INTO `lecturesessions` (`lecture_session_id`, `course_id`, `faculty_member_id`, `academic_term_id`,
                               `location_id`, `session_start_datetime`, `session_end_datetime`, `created_at`,
                               `updated_at`)
VALUES (1, 1, 3, 1, 1, '2024-02-20 09:00:00', '2024-02-20 12:00:00', '2025-05-11 19:31:06', '2025-05-11 19:31:06'),
       (2, 2, 9, 1, 33, '2024-02-21 10:00:00', '2024-02-21 12:00:00', '2025-05-11 19:31:06', '2025-05-11 19:31:06'),
       (3, 3, 16, 2, 5, '2024-07-22 13:00:00', '2024-07-22 17:00:00', '2025-05-11 19:31:06', '2025-05-11 19:31:06'),
       (4, 6, 3, 1, 2, '2024-03-05 09:00:00', '2024-03-05 11:00:00', '2025-05-11 19:31:06', '2025-05-11 19:31:06'),
       (5, 7, 10, 1, 34, '2024-03-06 14:00:00', '2024-03-06 16:00:00', '2025-05-11 19:31:06', '2025-05-11 19:31:06');

-- --------------------------------------------------------

--
-- Table structure for table `locations`
--

CREATE TABLE `locations`
(
    `location_id`   int(11) NOT NULL,
    `location_name` varchar(100) NOT NULL,
    `capacity`      int(11) DEFAULT NULL,
    `created_at`    timestamp    NOT NULL DEFAULT current_timestamp(),
    `updated_at`    timestamp    NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `locations`
--

INSERT INTO `locations` (`location_id`, `location_name`, `capacity`, `created_at`, `updated_at`)
VALUES (1, 'C1-001', 100, '2025-05-11 19:43:48', '2025-05-11 19:43:48'),
       (2, 'C1-002', 100, '2025-05-11 19:43:48', '2025-05-11 19:43:48'),
       (3, 'C1-003', 150, '2025-05-11 19:43:48', '2025-05-11 19:43:48'),
       (4, 'C1-004', 600, '2025-05-11 19:43:48', '2025-05-11 19:43:48'),
       (5, 'C1-005', 100, '2025-05-11 19:43:48', '2025-05-11 19:43:48'),
       (6, 'C1-006', 80, '2025-05-11 19:43:48', '2025-05-11 19:43:48'),
       (7, 'C1-007', 80, '2025-05-11 19:43:48', '2025-05-11 19:43:48'),
       (8, 'C1-008', 120, '2025-05-11 19:43:48', '2025-05-11 19:43:48'),
       (9, 'C1-009', 100, '2025-05-11 19:43:48', '2025-05-11 19:43:48'),
       (10, 'C1-010', 80, '2025-05-11 19:43:48', '2025-05-11 19:43:48'),
       (11, 'C1-011', 150, '2025-05-11 19:43:48', '2025-05-11 19:43:48'),
       (12, 'C1-101', 60, '2025-05-11 19:43:48', '2025-05-11 19:43:48'),
       (13, 'C1-102', 60, '2025-05-11 19:43:48', '2025-05-11 19:43:48'),
       (14, 'C1-103', 60, '2025-05-11 19:43:48', '2025-05-11 19:43:48'),
       (15, 'C1-104', 50, '2025-05-11 19:43:48', '2025-05-11 19:43:48'),
       (16, 'C1-105', 50, '2025-05-11 19:43:48', '2025-05-11 19:43:48'),
       (17, 'C1-106', 50, '2025-05-11 19:43:48', '2025-05-11 19:43:48'),
       (18, 'C1-107', 40, '2025-05-11 19:43:48', '2025-05-11 19:43:48'),
       (19, 'C1-108', 40, '2025-05-11 19:43:48', '2025-05-11 19:43:48'),
       (20, 'C1 B1-L101', 50, '2025-05-11 19:43:48', '2025-05-11 19:43:48'),
       (21, 'C1 B1-L102', 50, '2025-05-11 19:43:48', '2025-05-11 19:43:48'),
       (22, 'C1 B1-L103', 50, '2025-05-11 19:43:48', '2025-05-11 19:43:48'),
       (23, 'C1 B1-L104', 50, '2025-05-11 19:43:48', '2025-05-11 19:43:48'),
       (24, 'C1 B1-L105', 50, '2025-05-11 19:43:48', '2025-05-11 19:43:48'),
       (25, 'C1 B1-L106', 40, '2025-05-11 19:43:48', '2025-05-11 19:43:48'),
       (26, 'C1 B1-L107', 40, '2025-05-11 19:43:48', '2025-05-11 19:43:48'),
       (27, 'C1 B1-L108', 40, '2025-05-11 19:43:48', '2025-05-11 19:43:48'),
       (28, 'C1 B1-L109', 40, '2025-05-11 19:43:48', '2025-05-11 19:43:48'),
       (29, 'C1 B1-L110', 40, '2025-05-11 19:43:48', '2025-05-11 19:43:48'),
       (30, 'C1 B2-L201', 30, '2025-05-11 19:43:48', '2025-05-11 19:43:48'),
       (31, 'C1 B2-L202', 30, '2025-05-11 19:43:48', '2025-05-11 19:43:48'),
       (32, 'C1 B2-L203', 30, '2025-05-11 19:43:48', '2025-05-11 19:43:48'),
       (33, 'C1 B2-L204', 30, '2025-05-11 19:43:48', '2025-05-11 19:43:48'),
       (34, 'C1 B2-L205', 30, '2025-05-11 19:43:48', '2025-05-11 19:43:48'),
       (35, 'C1 B2-L206', 30, '2025-05-11 19:43:48', '2025-05-11 19:43:48'),
       (36, 'C1 B2-L207', 30, '2025-05-11 19:43:48', '2025-05-11 19:43:48'),
       (37, 'C1 B2-L208', 30, '2025-05-11 19:43:48', '2025-05-11 19:43:48'),
       (38, 'C1 B2-L209', 30, '2025-05-11 19:43:48', '2025-05-11 19:43:48'),
       (39, 'C1 B2-L210', 30, '2025-05-11 19:43:48', '2025-05-11 19:43:48'),
       (40, 'C1 B2-L211', 30, '2025-05-11 19:43:48', '2025-05-11 19:43:48'),
       (41, 'C1 B2-L212', 30, '2025-05-11 19:43:48', '2025-05-11 19:43:48'),
       (42, 'C1 B2-L213', 30, '2025-05-11 19:43:48', '2025-05-11 19:43:48'),
       (43, 'C1 B2-L214', 30, '2025-05-11 19:43:48', '2025-05-11 19:43:48'),
       (44, 'C2-001', 80, '2025-05-11 19:43:48', '2025-05-11 19:43:48'),
       (45, 'C2-002', 80, '2025-05-11 19:43:48', '2025-05-11 19:43:48'),
       (46, 'C2-003', 80, '2025-05-11 19:43:48', '2025-05-11 19:43:48'),
       (47, 'C2-004', 80, '2025-05-11 19:43:48', '2025-05-11 19:43:48'),
       (48, 'C2-005', 80, '2025-05-11 19:43:48', '2025-05-11 19:43:48'),
       (49, 'C2-006', 80, '2025-05-11 19:43:48', '2025-05-11 19:43:48'),
       (50, 'C2-007', 80, '2025-05-11 19:43:48', '2025-05-11 19:43:48'),
       (51, 'C2-008', 80, '2025-05-11 19:43:48', '2025-05-11 19:43:48'),
       (52, 'C2-009', 400, '2025-05-11 19:43:48', '2025-05-11 19:43:48'),
       (53, 'C2-101', 40, '2025-05-11 19:43:48', '2025-05-11 19:43:48'),
       (54, 'C2-102', 40, '2025-05-11 19:43:48', '2025-05-11 19:43:48'),
       (55, 'C2-103', 40, '2025-05-11 19:43:48', '2025-05-11 19:43:48'),
       (56, 'C2 B1-L101', 30, '2025-05-11 19:43:48', '2025-05-11 19:43:48'),
       (57, 'C2 B1-L102', 30, '2025-05-11 19:43:48', '2025-05-11 19:43:48'),
       (58, 'C2 B1-L103', 30, '2025-05-11 19:43:48', '2025-05-11 19:43:48'),
       (59, 'C2 B1-L104', 30, '2025-05-11 19:43:48', '2025-05-11 19:43:48'),
       (60, 'C2 B1-L105', 30, '2025-05-11 19:43:48', '2025-05-11 19:43:48'),
       (61, 'C2 B1-L106', 30, '2025-05-11 19:43:48', '2025-05-11 19:43:48'),
       (62, 'C2 B1-L107', 30, '2025-05-11 19:43:48', '2025-05-11 19:43:48'),
       (63, 'C2 B1-L108', 30, '2025-05-11 19:43:48', '2025-05-11 19:43:48'),
       (64, 'C2 B2-L201', 30, '2025-05-11 19:43:48', '2025-05-11 19:43:48'),
       (65, 'C2 B2-L202', 30, '2025-05-11 19:43:48', '2025-05-11 19:43:48'),
       (66, 'C2 B2-L203', 30, '2025-05-11 19:43:48', '2025-05-11 19:43:48'),
       (67, 'C2 B2-L204', 30, '2025-05-11 19:43:48', '2025-05-11 19:43:48'),
       (68, 'C2 B2-L205', 30, '2025-05-11 19:43:48', '2025-05-11 19:43:48'),
       (69, 'C2 B2-L206', 30, '2025-05-11 19:43:48', '2025-05-11 19:43:48'),
       (70, 'C2 B2-L207', 30, '2025-05-11 19:43:48', '2025-05-11 19:43:48'),
       (71, 'C2 B2-L208', 30, '2025-05-11 19:43:48', '2025-05-11 19:43:48'),
       (72, 'C2 B2-L209', 30, '2025-05-11 19:43:48', '2025-05-11 19:43:48'),
       (73, 'C2 B2-L210', 30, '2025-05-11 19:43:48', '2025-05-11 19:43:48'),
       (74, 'C3-001', 100, '2025-05-11 19:43:49', '2025-05-11 19:43:49'),
       (75, 'C3-002', 100, '2025-05-11 19:43:49', '2025-05-11 19:43:49'),
       (76, 'C3-003', 100, '2025-05-11 19:43:49', '2025-05-11 19:43:49'),
       (77, 'C3-004', 100, '2025-05-11 19:43:49', '2025-05-11 19:43:49'),
       (78, 'C3-005', 100, '2025-05-11 19:43:49', '2025-05-11 19:43:49'),
       (79, 'C3-101', 60, '2025-05-11 19:43:49', '2025-05-11 19:43:49'),
       (80, 'C3-102', 60, '2025-05-11 19:43:49', '2025-05-11 19:43:49'),
       (81, 'C3-103', 60, '2025-05-11 19:43:49', '2025-05-11 19:43:49'),
       (82, 'C3-104', 60, '2025-05-11 19:43:49', '2025-05-11 19:43:49'),
       (83, 'C3-105', 60, '2025-05-11 19:43:49', '2025-05-11 19:43:49'),
       (84, 'C3-106', 60, '2025-05-11 19:43:49', '2025-05-11 19:43:49'),
       (85, 'C3-107', 60, '2025-05-11 19:43:49', '2025-05-11 19:43:49'),
       (86, 'C3 B1-L101', 40, '2025-05-11 19:43:49', '2025-05-11 19:43:49'),
       (87, 'C3 B1-L102', 40, '2025-05-11 19:43:49', '2025-05-11 19:43:49'),
       (88, 'C3 B1-L103', 40, '2025-05-11 19:43:49', '2025-05-11 19:43:49'),
       (89, 'C3 B1-L104', 40, '2025-05-11 19:43:49', '2025-05-11 19:43:49'),
       (90, 'C3 B1-L105', 40, '2025-05-11 19:43:49', '2025-05-11 19:43:49'),
       (91, 'C3 B1-L106', 40, '2025-05-11 19:43:49', '2025-05-11 19:43:49'),
       (92, 'C3 B1-L107', 40, '2025-05-11 19:43:49', '2025-05-11 19:43:49'),
       (93, 'C3 B1-L108', 40, '2025-05-11 19:43:49', '2025-05-11 19:43:49'),
       (94, 'C3 B2-L201', 40, '2025-05-11 19:43:49', '2025-05-11 19:43:49'),
       (95, 'C3 B2-L202', 40, '2025-05-11 19:43:49', '2025-05-11 19:43:49'),
       (96, 'C3 B2-L203', 40, '2025-05-11 19:43:49', '2025-05-11 19:43:49'),
       (97, 'C3 B2-L204', 40, '2025-05-11 19:43:49', '2025-05-11 19:43:49'),
       (98, 'C3 B2-L206', 40, '2025-05-11 19:43:49', '2025-05-11 19:43:49'),
       (99, 'C3 B2-L207', 40, '2025-05-11 19:43:49', '2025-05-11 19:43:49'),
       (100, 'C3 B2-L208', 40, '2025-05-11 19:43:49', '2025-05-11 19:43:49'),
       (101, 'C3 B2-L209', 40, '2025-05-11 19:43:49', '2025-05-11 19:43:49');

-- --------------------------------------------------------

--
-- Table structure for table `programs`
--

CREATE TABLE `programs`
(
    `program_id`     int(11) NOT NULL,
    `program_name`   varchar(250) NOT NULL,
    `department_id`  int(11) NOT NULL,
    `description`    text                  DEFAULT NULL,
    `duration_years` int(11) DEFAULT NULL,
    `created_at`     timestamp    NOT NULL DEFAULT current_timestamp(),
    `updated_at`     timestamp    NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `programs`
--

INSERT INTO `programs` (`program_id`, `program_name`, `department_id`, `description`, `duration_years`, `created_at`,
                        `updated_at`)
VALUES (1, 'Foundation Programme for Bachelor’s Degree-UGC', 1, 'Foundation program for degree studies at FOB.', 1,
        '2025-05-11 19:26:53', '2025-05-11 19:26:53'),
       (2, 'BSc (Hons) International Management and Business-PLY', 1,
        'Offered in collaboration with Plymouth University, UK.', 3, '2025-05-11 19:26:53', '2025-05-11 19:26:53'),
       (3, 'BM (Hons) in International Business-UGC', 1, 'UGC Approved program offered By NSBM.', 4,
        '2025-05-11 19:26:53', '2025-05-11 19:26:53'),
       (4, 'Bachelor of Business: Management and Innovation & Supply Chain and Logistics Management-VU', 1,
        'Offered in collaboration with Victoria University, Australia.', 3, '2025-05-11 19:26:53',
        '2025-05-11 19:26:53'),
       (5, 'BSc in Business Management (Human Resource Management) (Special)-UGC', 1,
        'UGC Approved Special Degree in HRM.', 4, '2025-05-11 19:26:53', '2025-05-11 19:26:53'),
       (6, 'BSc (Hons) Accounting and Finance-PLY', 1, 'Offered in collaboration with Plymouth University, UK.', 3,
        '2025-05-11 19:26:53', '2025-05-11 19:26:53'),
       (7, 'BM (Hons) in Accounting and Finance-UGC', 1, 'UGC Approved program offered By NSBM.', 4,
        '2025-05-11 19:26:53', '2025-05-11 19:26:53'),
       (8, 'BBM (Hons) Tourism, Hospitality & Events-UGC', 1, 'UGC Approved program in Tourism and Hospitality.', 4,
        '2025-05-11 19:26:53', '2025-05-11 19:26:53'),
       (9, 'BSc in Multimedia-UGC', 1, 'UGC Approved program in Multimedia.', 4, '2025-05-11 19:26:53',
        '2025-05-11 19:26:53'),
       (10, 'BM (Hons) in Marketing Management-UGC', 1, 'UGC Approved program in Marketing Management.', 4,
        '2025-05-11 19:26:53', '2025-05-11 19:26:53'),
       (11, 'BSc (Hons) Events, Tourism and Hospitality Management-PLY', 1,
        'Offered in collaboration with Plymouth University, UK.', 3, '2025-05-11 19:26:53', '2025-05-11 19:26:53'),
       (12, 'BSc (Hons) Marketing Management-PLY', 1, 'Offered in collaboration with Plymouth University, UK.', 3,
        '2025-05-11 19:26:53', '2025-05-11 19:26:53'),
       (13, 'BSc (Hons) Business Communication-PLY', 1, 'Offered in collaboration with Plymouth University, UK.', 3,
        '2025-05-11 19:26:53', '2025-05-11 19:26:53'),
       (14, 'BA in Business Communication-UGC', 1, 'UGC Approved program in Business Communication.', 4,
        '2025-05-11 19:26:53', '2025-05-11 19:26:53'),
       (15, 'BSc (Hons) Operations and Logistics Management-PLY', 1,
        'Offered in collaboration with Plymouth University, UK.', 3, '2025-05-11 19:26:53', '2025-05-11 19:26:53'),
       (16, 'BSc in Business Management (Industrial Management) (Special)-UGC', 1,
        'UGC Approved Special Degree in Industrial Management.', 4, '2025-05-11 19:26:53', '2025-05-11 19:26:53'),
       (17, 'BSc in Business Management (Project Management) (Special)-UGC', 1,
        'UGC Approved Special Degree in Project Management.', 4, '2025-05-11 19:26:53', '2025-05-11 19:26:53'),
       (18, 'BSc in Business Management (Logistics Management) (Special)-UGC', 1,
        'UGC Approved Special Degree in Logistics Management.', 4, '2025-05-11 19:26:53', '2025-05-11 19:26:53'),
       (19, 'BM (Honors) in Business Analytics-UGC', 1, 'UGC Approved Honors Degree in Business Analytics.', 4,
        '2025-05-11 19:26:53', '2025-05-11 19:26:53'),
       (20, 'BM (Honors) in Business Economics-UGC', 1, 'UGC Approved Honors Degree in Business Economics.', 4,
        '2025-05-11 19:26:53', '2025-05-11 19:26:53'),
       (21, 'BSc (Hons) Data Science-UGC', 2, 'UGC Approved program in Data Science.', 4, '2025-05-11 19:26:53',
        '2025-05-11 19:26:53'),
       (22, 'BSc (Hons) Computer Science-UGC', 2, 'UGC Approved program in Computer Science.', 4, '2025-05-11 19:26:53',
        '2025-05-11 19:26:53'),
       (23, 'BSc (Hons) Software Engineering-UGC', 2, 'UGC Approved program in Software Engineering.', 4,
        '2025-05-11 19:26:53', '2025-05-11 19:26:53'),
       (24, 'BSc (Hons) in Computer Networks-UGC', 2, 'UGC Approved program in Computer Networks.', 4,
        '2025-05-11 19:26:53', '2025-05-11 19:26:53'),
       (25, 'BSc in Management Information Systems (Special)-UGC', 2, 'UGC Approved Special Degree in MIS.', 4,
        '2025-05-11 19:26:53', '2025-05-11 19:26:53'),
       (26, 'BSc (Honours) in Data Science-PLY', 2, 'Offered in collaboration with Plymouth University, UK.', 3,
        '2025-05-11 19:26:53', '2025-05-11 19:26:53'),
       (27, 'BSc (Honours) Computer Science-PLY', 2, 'Offered in collaboration with Plymouth University, UK.', 3,
        '2025-05-11 19:26:53', '2025-05-11 19:26:53'),
       (28, 'BSc (Honours) Computer Networks-PLY', 2, 'Offered in collaboration with Plymouth University, UK.', 3,
        '2025-05-11 19:26:53', '2025-05-11 19:26:53'),
       (29, 'BSc (Honours) in Technology Management-PLY', 2, 'Offered in collaboration with Plymouth University, UK.',
        3, '2025-05-11 19:26:53', '2025-05-11 19:26:53'),
       (30, 'BSc (Honours) in Software Engineering-PLY', 2, 'Offered in collaboration with Plymouth University, UK.', 3,
        '2025-05-11 19:26:53', '2025-05-11 19:26:53'),
       (31, 'BSc (Hons) Computer Security-PLY', 2, 'Offered in collaboration with Plymouth University, UK.', 3,
        '2025-05-11 19:26:53', '2025-05-11 19:26:53'),
       (32, 'Bachelor of Information Technology (Major in Cyber Security)-VU', 2,
        'Offered in collaboration with Victoria University, Australia.', 3, '2025-05-11 19:26:53',
        '2025-05-11 19:26:53'),
       (33, 'BSc (Hons) in Biomedical Science-UGC', 3, 'UGC Approved program in Biomedical Science.', 4,
        '2025-05-11 19:26:53', '2025-05-11 19:26:53'),
       (34, 'BSc (Hons) in Pharmaceutical Science-UGC', 3, 'UGC Approved program in Pharmaceutical Science.', 4,
        '2025-05-11 19:26:53', '2025-05-11 19:26:53'),
       (35, 'BSc (Hons) Nutrition and Health-UGC', 3, 'UGC Approved program in Nutrition and Health.', 4,
        '2025-05-11 19:26:53', '2025-05-11 19:26:53'),
       (36, 'BSc (Hons) Psychology-PLY', 3, 'Offered in collaboration with Plymouth University, UK.', 3,
        '2025-05-11 19:26:53', '2025-05-11 19:26:53'),
       (37, 'BSc (Hons) Nursing-PLY', 3, 'Offered in collaboration with Plymouth University, UK.', 3,
        '2025-05-11 19:26:53', '2025-05-11 19:26:53'),
       (38, 'BSc (Hons) Biomedical Science-PLY', 3, 'Offered in collaboration with Plymouth University, UK.', 3,
        '2025-05-11 19:26:53', '2025-05-11 19:26:53'),
       (39, 'BSc (Hons) Nursing – Top up-PLY', 3,
        'Top-up program offered in collaboration with Plymouth University, UK.', 1, '2025-05-11 19:26:53',
        '2025-05-11 19:26:53'),
       (40, 'Foundation Programme for Bachelor’s Degree (Science)-UGC', 3,
        'Foundation program for degree studies at FOS.', 1, '2025-05-11 19:26:53', '2025-05-11 19:26:53'),
       (41, 'Bachelor of Science of Engineering Honours in Electrical and Electronic Engineering-UGC', 4,
        'UGC Approved Engineering program.', 4, '2025-05-11 19:26:53', '2025-05-11 19:26:53'),
       (42, 'Bachelor of Science of Engineering Honours in Computer Systems Engineering-UGC', 4,
        'UGC Approved Engineering program.', 4, '2025-05-11 19:26:53', '2025-05-11 19:26:53'),
       (43, 'BEng (Hons) Electrical, Electronics, and Communication Engineering-PLY', 4,
        'Offered in collaboration with Plymouth University, UK.', 3, '2025-05-11 19:26:53', '2025-05-11 19:26:53'),
       (44, 'Bachelor of Science of Engineering Honours in Mechatronic Engineering-UGC', 4,
        'UGC Approved Engineering program.', 4, '2025-05-11 19:26:53', '2025-05-11 19:26:53'),
       (45, 'BEng (Hons) Civil and Structural Engineering-PLY', 4,
        'Offered in collaboration with Plymouth University, UK.', 3, '2025-05-11 19:26:53', '2025-05-11 19:26:53'),
       (46, 'BEng (Hons) Mechanical and Mechatronics-PLY', 4, 'Offered in collaboration with Plymouth University, UK.',
        3, '2025-05-11 19:26:53', '2025-05-11 19:26:53'),
       (47, 'BEng (Hons) Robotics and Automation Engineering-PLY', 4,
        'Offered in collaboration with Plymouth University, UK.', 3, '2025-05-11 19:26:53', '2025-05-11 19:26:53'),
       (48, 'Bachelor of Interior Design-UGC', 4, 'UGC Approved program in Interior Design.', 4, '2025-05-11 19:26:53',
        '2025-05-11 19:26:53'),
       (49, 'BSc (Hons) Quantity Surveying-PLY', 4, 'Offered in collaboration with Plymouth University, UK.', 3,
        '2025-05-11 19:26:53', '2025-05-11 19:26:53'),
       (50, 'BA (Hons) in Interior Design-PLY', 4, 'Offered in collaboration with Plymouth University, UK.', 3,
        '2025-05-11 19:26:53', '2025-05-11 19:26:53'),
       (51, 'Bachelor of Laws (Honours)-UGC', 5, 'UGC Approved Law program.', 4, '2025-05-11 19:26:54',
        '2025-05-11 19:26:54'),
       (52, 'BM (Hons) in Law and Business Studies-UGC', 5, 'UGC Approved program combining Law and Business.', 4,
        '2025-05-11 19:26:54', '2025-05-11 19:26:54'),
       (53, 'BM (Hons.) in Law and International Trade-UGC', 5, 'UGC Approved program in Law and International Trade.',
        4, '2025-05-11 19:26:54', '2025-05-11 19:26:54'),
       (54, 'BM (Hons) in Law and E-Commerce-UGC', 5, 'UGC Approved program in Law and E-Commerce.', 4,
        '2025-05-11 19:26:54', '2025-05-11 19:26:54'),
       (55, 'LLB (Hons) Law-PLY', 5, 'Offered in collaboration with Plymouth University, UK.', 3, '2025-05-11 19:26:54',
        '2025-05-11 19:26:54');

-- --------------------------------------------------------

--
-- Table structure for table `students`
--

CREATE TABLE `students`
(
    `student_id`        int(11) NOT NULL,
    `user_id`           int(11) DEFAULT NULL,
    `student_unique_id` varchar(20) NOT NULL COMMENT 'e.g., 32855',
    `first_name`        varchar(50) NOT NULL,
    `last_name`         varchar(50) NOT NULL,
    `date_of_birth`     date                 DEFAULT NULL,
    `gender`            enum('MALE','FEMALE','OTHER') DEFAULT NULL,
    `address`           varchar(255)         DEFAULT NULL,
    `phone_number`      varchar(20)          DEFAULT NULL,
    `enrollment_date`   date                 DEFAULT NULL,
    `program_id`        int(11) DEFAULT NULL,
    `created_at`        timestamp   NOT NULL DEFAULT current_timestamp(),
    `updated_at`        timestamp   NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `students`
--

INSERT INTO `students` (`student_id`, `user_id`, `student_unique_id`, `first_name`, `last_name`, `date_of_birth`,
                        `gender`, `address`, `phone_number`, `enrollment_date`, `program_id`, `created_at`,
                        `updated_at`)
VALUES (1, 2, '32855', 'Nimal', 'Perera', '2003-05-15', 'MALE', '123 Galle Road, Colombo', '0771234567', '2023-02-10',
        2, '2025-05-11 19:27:29', '2025-05-11 19:27:29'),
       (2, 3, '32856', 'Samanthi', 'Silva', '2004-08-20', 'FEMALE', '456 Kandy Road, Kandy', '0717654321', '2023-02-10',
        21, '2025-05-11 19:27:29', '2025-05-11 19:27:29'),
       (3, 4, '32857', 'John', 'Doe', '2002-11-01', 'MALE', '789 Main St, Jaffna', '0759876543', '2022-09-15', 51,
        '2025-05-11 19:27:29', '2025-05-11 19:27:29'),
       (4, NULL, '16100', 'Kasun', 'Jayawardena', '2005-01-25', 'MALE', '321 Park Avenue, Negombo', '0701122334',
        '2024-01-20', 1, '2025-05-11 19:27:29', '2025-05-11 19:27:29'),
       (5, NULL, '18500', 'Aisha', 'Mohamed', '2003-07-12', 'FEMALE', '654 Temple Road, Galle', '0765544332',
        '2023-07-05', 33, '2025-05-11 19:27:29', '2025-05-11 19:27:29');

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE `users`
(
    `user_id`       int(11) NOT NULL,
    `username`      varchar(50)  NOT NULL,
    `password_hash` varchar(255) NOT NULL,
    `email`         varchar(100) NOT NULL,
    `role`          enum('ADMIN','STUDENT','FACULTY') NOT NULL,
    `is_active`     tinyint(1) DEFAULT 1,
    `created_at`    timestamp    NOT NULL DEFAULT current_timestamp(),
    `updated_at`    timestamp    NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`user_id`, `username`, `password_hash`, `email`, `role`, `is_active`, `created_at`, `updated_at`)
VALUES (1, 'admin01', '$2a$10$nMpKYFx5OH.Dg0b7DL3A/.2d.nBfAtG8xdwKpVjUbAmSMNDfafGXy', 'admin01@example.com', 'ADMIN', 1,
        '2025-05-10 04:30:00', '2025-05-12 18:12:02'),
       (2, 'student01', '$2a$10$nMpKYFx5OH.Dg0b7DL3A/.2d.nBfAtG8xdwKpVjUbAmSMNDfafGXy', 'student01@example.com',
        'STUDENT', 1, '2025-05-10 04:30:00', '2025-05-12 18:15:08'),
       (3, 'student02', '$2a$10$nMpKYFx5OH.Dg0b7DL3A/.2d.nBfAtG8xdwKpVjUbAmSMNDfafGXy', 'student02@example.com',
        'STUDENT', 1, '2025-05-10 04:30:00', '2025-05-12 18:15:10'),
       (4, 'student03', '$2a$10$nMpKYFx5OH.Dg0b7DL3A/.2d.nBfAtG8xdwKpVjUbAmSMNDfafGXy', 'student03@example.com',
        'STUDENT', 0, '2025-05-10 04:30:00', '2025-05-12 18:15:39'),
       (5, 'thilini.ds', '$2a$10$nMpKYFx5OH.Dg0b7DL3A/.2d.nBfAtG8xdwKpVjUbAmSMNDfafGXy', 'thilini.ds@nsbm.ac.lk',
        'FACULTY', 1, '2025-05-10 04:30:00', '2025-05-12 18:15:40'),
       (6, 'rasika.r', '$2a$10$nMpKYFx5OH.Dg0b7DL3A/.2d.nBfAtG8xdwKpVjUbAmSMNDfafGXy', 'rasika.r@nsbm.ac.lk', 'FACULTY',
        1, '2025-05-10 04:30:00', '2025-05-12 18:15:13'),
       (7, 'nuwanthi.k', '$2a$10$nMpKYFx5OH.Dg0b7DL3A/.2d.nBfAtG8xdwKpVjUbAmSMNDfafGXy', 'nuwanthi.k@nsbm.ac.lk',
        'FACULTY', 1, '2025-05-10 04:30:00', '2025-05-12 18:15:17'),
       (8, 'chandana.p', '$2a$10$nMpKYFx5OH.Dg0b7DL3A/.2d.nBfAtG8xdwKpVjUbAmSMNDfafGXy', 'chandana.p@nsbm.ac.lk',
        'FACULTY', 1, '2025-05-10 04:30:00', '2025-05-12 18:15:20'),
       (9, 'shanthi.s', '$2a$10$nMpKYFx5OH.Dg0b7DL3A/.2d.nBfAtG8xdwKpVjUbAmSMNDfafGXy', 'shanthi.s@nsbm.ac.lk',
        'FACULTY', 1, '2025-05-10 04:30:00', '2025-05-12 18:15:21'),
       (10, 'prasanna.p', '$2a$10$nMpKYFx5OH.Dg0b7DL3A/.2d.nBfAtG8xdwKpVjUbAmSMNDfafGXy', 'prasanna.p@nsbm.ac.lk',
        'FACULTY', 1, '2025-05-10 04:30:00', '2025-05-12 18:15:22'),
       (11, 'bhasuri.b', '$2a$10$nMpKYFx5OH.Dg0b7DL3A/.2d.nBfAtG8xdwKpVjUbAmSMNDfafGXy', 'bhasuri.b@nsbm.ac.lk',
        'FACULTY', 1, '2025-05-10 04:30:00', '2025-05-12 18:15:23'),
       (12, 'anne.p', '$2a$10$nMpKYFx5OH.Dg0b7DL3A/.2d.nBfAtG8xdwKpVjUbAmSMNDfafGXy', 'anne.p@nsbm.ac.lk', 'FACULTY', 1,
        '2025-05-10 04:30:00', '2025-05-12 18:15:24'),
       (13, 'venura.c', '$2a$10$nMpKYFx5OH.Dg0b7DL3A/.2d.nBfAtG8xdwKpVjUbAmSMNDfafGXy', 'venura.c@nsbm.ac.lk',
        'FACULTY', 1, '2025-05-10 04:30:00', '2025-05-12 18:15:27'),
       (14, 'sudath.a', '$2a$10$nMpKYFx5OH.Dg0b7DL3A/.2d.nBfAtG8xdwKpVjUbAmSMNDfafGXy', 'sudath.a@nsbm.ac.lk',
        'FACULTY', 1, '2025-05-10 04:30:00', '2025-05-12 18:15:28'),
       (15, 'chamindra.a', '$2a$10$nMpKYFx5OH.Dg0b7DL3A/.2d.nBfAtG8xdwKpVjUbAmSMNDfafGXy', 'chamindra.a@nsbm.ac.lk',
        'FACULTY', 1, '2025-05-10 04:30:00', '2025-05-12 18:15:30'),
       (16, 'mohamed.s', '$2a$10$nMpKYFx5OH.Dg0b7DL3A/.2d.nBfAtG8xdwKpVjUbAmSMNDfafGXy', 'mohamed.s@nsbm.ac.lk',
        'FACULTY', 1, '2025-05-10 04:30:00', '2025-05-12 18:15:31'),
       (17, 'prabhath.b', '$2a$10$nMpKYFx5OH.Dg0b7DL3A/.2d.nBfAtG8xdwKpVjUbAmSMNDfafGXy', 'prabhath.b@nsbm.ac.lk',
        'FACULTY', 1, '2025-05-10 04:30:00', '2025-05-12 18:15:35');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `academicterms`
--
ALTER TABLE `academicterms`
    ADD PRIMARY KEY (`academic_term_id`),
  ADD UNIQUE KEY `term_name` (`term_name`);

--
-- Indexes for table `announcements`
--
ALTER TABLE `announcements`
    ADD PRIMARY KEY (`announcement_id`),
  ADD KEY `posted_by_user_id` (`posted_by_user_id`);

--
-- Indexes for table `attendance`
--
ALTER TABLE `attendance`
    ADD PRIMARY KEY (`attendance_id`),
  ADD UNIQUE KEY `uk_student_lecture_session` (`student_id`,`lecture_session_id`),
  ADD KEY `lecture_session_id` (`lecture_session_id`),
  ADD KEY `recorded_by_faculty_id` (`recorded_by_faculty_id`);

--
-- Indexes for table `courses`
--
ALTER TABLE `courses`
    ADD PRIMARY KEY (`course_id`),
  ADD UNIQUE KEY `course_code` (`course_code`),
  ADD KEY `program_id` (`program_id`),
  ADD KEY `department_id` (`department_id`);

--
-- Indexes for table `departments`
--
ALTER TABLE `departments`
    ADD PRIMARY KEY (`department_id`),
  ADD UNIQUE KEY `department_name` (`department_name`);

--
-- Indexes for table `enrollments`
--
ALTER TABLE `enrollments`
    ADD PRIMARY KEY (`enrollment_id`),
  ADD UNIQUE KEY `uk_student_course_term` (`student_id`,`course_id`,`academic_term_id`),
  ADD KEY `course_id` (`course_id`),
  ADD KEY `academic_term_id` (`academic_term_id`);

--
-- Indexes for table `faculty`
--
ALTER TABLE `faculty`
    ADD PRIMARY KEY (`faculty_member_id`),
  ADD UNIQUE KEY `faculty_unique_id` (`faculty_unique_id`),
  ADD UNIQUE KEY `user_id` (`user_id`),
  ADD KEY `department_id` (`department_id`);

--
-- Indexes for table `grades`
--
ALTER TABLE `grades`
    ADD PRIMARY KEY (`grade_id`),
  ADD KEY `enrollment_id` (`enrollment_id`),
  ADD KEY `graded_by_faculty_id` (`graded_by_faculty_id`);

--
-- Indexes for table `lecturesessions`
--
ALTER TABLE `lecturesessions`
    ADD PRIMARY KEY (`lecture_session_id`),
  ADD KEY `course_id` (`course_id`),
  ADD KEY `faculty_member_id` (`faculty_member_id`),
  ADD KEY `academic_term_id` (`academic_term_id`),
  ADD KEY `location_id` (`location_id`);

--
-- Indexes for table `locations`
--
ALTER TABLE `locations`
    ADD PRIMARY KEY (`location_id`),
  ADD UNIQUE KEY `location_name` (`location_name`);

--
-- Indexes for table `programs`
--
ALTER TABLE `programs`
    ADD PRIMARY KEY (`program_id`),
  ADD UNIQUE KEY `program_name` (`program_name`),
  ADD KEY `department_id` (`department_id`);

--
-- Indexes for table `students`
--
ALTER TABLE `students`
    ADD PRIMARY KEY (`student_id`),
  ADD UNIQUE KEY `student_unique_id` (`student_unique_id`),
  ADD UNIQUE KEY `user_id` (`user_id`),
  ADD KEY `program_id` (`program_id`);

--
-- Indexes for table `users`
--
ALTER TABLE `users`
    ADD PRIMARY KEY (`user_id`),
  ADD UNIQUE KEY `username` (`username`),
  ADD UNIQUE KEY `email` (`email`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `academicterms`
--
ALTER TABLE `academicterms`
    MODIFY `academic_term_id` int (11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT for table `announcements`
--
ALTER TABLE `announcements`
    MODIFY `announcement_id` int (11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT for table `attendance`
--
ALTER TABLE `attendance`
    MODIFY `attendance_id` int (11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT for table `courses`
--
ALTER TABLE `courses`
    MODIFY `course_id` int (11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=9;

--
-- AUTO_INCREMENT for table `departments`
--
ALTER TABLE `departments`
    MODIFY `department_id` int (11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT for table `enrollments`
--
ALTER TABLE `enrollments`
    MODIFY `enrollment_id` int (11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- AUTO_INCREMENT for table `faculty`
--
ALTER TABLE `faculty`
    MODIFY `faculty_member_id` int (11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=18;

--
-- AUTO_INCREMENT for table `grades`
--
ALTER TABLE `grades`
    MODIFY `grade_id` int (11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT for table `lecturesessions`
--
ALTER TABLE `lecturesessions`
    MODIFY `lecture_session_id` int (11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT for table `locations`
--
ALTER TABLE `locations`
    MODIFY `location_id` int (11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=102;

--
-- AUTO_INCREMENT for table `programs`
--
ALTER TABLE `programs`
    MODIFY `program_id` int (11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=56;

--
-- AUTO_INCREMENT for table `students`
--
ALTER TABLE `students`
    MODIFY `student_id` int (11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT for table `users`
--
ALTER TABLE `users`
    MODIFY `user_id` int (11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=18;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `announcements`
--
ALTER TABLE `announcements`
    ADD CONSTRAINT `announcements_ibfk_1` FOREIGN KEY (`posted_by_user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `attendance`
--
ALTER TABLE `attendance`
    ADD CONSTRAINT `attendance_ibfk_1` FOREIGN KEY (`student_id`) REFERENCES `students` (`student_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `attendance_ibfk_2` FOREIGN KEY (`lecture_session_id`) REFERENCES `lecturesessions` (`lecture_session_id`) ON
DELETE
CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `attendance_ibfk_3` FOREIGN KEY (`recorded_by_faculty_id`) REFERENCES `faculty` (`faculty_member_id`) ON DELETE
SET NULL ON UPDATE CASCADE;

--
-- Constraints for table `courses`
--
ALTER TABLE `courses`
    ADD CONSTRAINT `courses_ibfk_1` FOREIGN KEY (`program_id`) REFERENCES `programs` (`program_id`) ON DELETE SET NULL ON UPDATE CASCADE,
  ADD CONSTRAINT `courses_ibfk_2` FOREIGN KEY (`department_id`) REFERENCES `departments` (`department_id`) ON
UPDATE CASCADE;

--
-- Constraints for table `enrollments`
--
ALTER TABLE `enrollments`
    ADD CONSTRAINT `enrollments_ibfk_1` FOREIGN KEY (`student_id`) REFERENCES `students` (`student_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `enrollments_ibfk_2` FOREIGN KEY (`course_id`) REFERENCES `courses` (`course_id`) ON
DELETE
CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `enrollments_ibfk_3` FOREIGN KEY (`academic_term_id`) REFERENCES `academicterms` (`academic_term_id`) ON
UPDATE CASCADE;

--
-- Constraints for table `faculty`
--
ALTER TABLE `faculty`
    ADD CONSTRAINT `faculty_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE SET NULL ON UPDATE CASCADE,
  ADD CONSTRAINT `faculty_ibfk_2` FOREIGN KEY (`department_id`) REFERENCES `departments` (`department_id`) ON
UPDATE CASCADE;

--
-- Constraints for table `grades`
--
ALTER TABLE `grades`
    ADD CONSTRAINT `grades_ibfk_1` FOREIGN KEY (`enrollment_id`) REFERENCES `enrollments` (`enrollment_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `grades_ibfk_2` FOREIGN KEY (`graded_by_faculty_id`) REFERENCES `faculty` (`faculty_member_id`) ON
DELETE
SET NULL ON UPDATE CASCADE;

--
-- Constraints for table `lecturesessions`
--
ALTER TABLE `lecturesessions`
    ADD CONSTRAINT `lecturesessions_ibfk_1` FOREIGN KEY (`course_id`) REFERENCES `courses` (`course_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `lecturesessions_ibfk_2` FOREIGN KEY (`faculty_member_id`) REFERENCES `faculty` (`faculty_member_id`) ON
DELETE
SET NULL ON UPDATE CASCADE,
  ADD CONSTRAINT `lecturesessions_ibfk_3` FOREIGN KEY (`academic_term_id`) REFERENCES `academicterms` (`academic_term_id`) ON
UPDATE CASCADE,
    ADD CONSTRAINT `lecturesessions_ibfk_4` FOREIGN KEY (`location_id`) REFERENCES `locations` (`location_id`)
ON
DELETE
SET NULL ON UPDATE CASCADE;

--
-- Constraints for table `programs`
--
ALTER TABLE `programs`
    ADD CONSTRAINT `programs_ibfk_1` FOREIGN KEY (`department_id`) REFERENCES `departments` (`department_id`) ON UPDATE CASCADE;

--
-- Constraints for table `students`
--
ALTER TABLE `students`
    ADD CONSTRAINT `students_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE SET NULL ON UPDATE CASCADE,
  ADD CONSTRAINT `students_ibfk_2` FOREIGN KEY (`program_id`) REFERENCES `programs` (`program_id`) ON
DELETE
SET NULL ON UPDATE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
