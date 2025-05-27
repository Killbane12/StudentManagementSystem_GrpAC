**Student Management System (SMS) Overview**

The Student Management System (SMS) for NSBM is a web-based platform designed to centralize and manage student academic information, faculty data, course details, and related administrative processes. It provides distinct interfaces and functionalities for different user roles: Admin, Faculty, and Student, aiming to streamline operations and improve information access. Key goals include developing a robust and user-friendly application, implementing secure authentication and authorization, facilitating efficient record-keeping for grades and attendance, and incorporating unique features like a personalized student dashboard and system-wide announcements.
Technologies Used


**The project utilizes the following technologies:**

•	Backend Language: Java (JDK 21) 
•	Web Technologies: Jakarta EE (Servlets API 6.0+, JSP API 3.1+) 
•	Web Server: Apache Tomcat 11.0.6 
•	Database: MySQL (managed via phpMyAdmin) 
•	Database Connectivity: JDBC 
•	Build Tool: Apache Maven 
•	IDE: IntelliJ IDEA 
•	Styling: Custom CSS3 
•	Client-side Scripting: JavaScript (for basic DOM manipulation, validation, and AJAX) 
•	Password Hashing: jbcrypt (version 0.4) 
•	Testing: JUnit 5 (for unit testing) 
•	Logging (Optional): SLF4J (version 2.0.7+), Logback (version 1.4.11+) 
•	Connection Pooling (Recommended): HikariCP 
•	Version Control: Git (with a remote repository like GitHub/GitLab) 


**Tools Needed for Setup and Development
To set up and work on the system, you will need:**

1.	Java Development Kit (JDK): JDK 17 or later (LTS versions preferred), specifically JDK 21 as per the description.
2.	Apache Tomcat: Version 11 (Supports Jakarta EE 10 / Servlet 6.0).
3.	MySQL Database: And a tool to manage it, such as phpMyAdmin.
4.	JDBC Driver: The corresponding driver for MySQL (e.g., MySQL Connector/J).
5.	Apache Maven: For managing dependencies, the build lifecycle, and packaging the WAR file.
6.	Git: For version control.
7.	Integrated Development Environment (IDE): IntelliJ IDEA is specified, but other Java EE compatible IDEs could be used.
8.	Web Browser: Modern web browsers like Chrome, Firefox, or Edge.How to Set Up the System (Step-by-Step)


**Deployment Steps:**
 
1.	Set up the Development Environment: Install the required JDK, Apache Tomcat, MySQL, and your chosen IDE.
2.	Install Maven: Ensure Maven is installed and configured correctly.
3.	Set up Git: Install Git and configure it with your details.
4.	Clone the Repository: Clone the project's source code from the Git repository.
5.	Set up the Database: o	Create a MySQL database (e.g., grpAC_SMS_db).
o	Execute the SQL CREATE TABLE queries provided in the database structure section of the project description to create all necessary tables (Users, Departments, Programs, Students, Faculty, Courses, AcademicTerms, Enrollments, Locations, LectureSessions, Attendance, Grades, Announcements).
6.	Configure Database Connection: Update the database.properties file located in src/main/resources/db/ with your database connection URL, username, and password.
7.	Import Project into IDE: Import the Maven project into IntelliJ IDEA.
8.	Resolve Dependencies: Maven should automatically download the necessary libraries (dependencies) defined in the pom.xml file (like JDBC driver, JSTL, BCrypt).
9.	Build the Project: Use Maven to build the project, which will compile the Java code and package it into a Web Application Archive (WAR) file. This is typically done via the IDE or the Maven command line (mvn clean install).
10.	Deploy to Tomcat: Deploy the generated WAR file to the Apache Tomcat server. This usually involves copying the WAR file to the webapps directory of your Tomcat installation and starting the server.
11.	Initialize Resources (Optional but Recommended): If using a listener like ApplicationContextListener.java to initialize resources like a database connection pool, ensure it's configured correctly (e.g., in web.xml or via annotations).


**How to Use the System (After Running**
Once the application is deployed and the Tomcat server is running, you can access the system through a web browser:
1.	Access the Application:
   Open your web browser and navigate to the URL where your Tomcat server is running the application (e.g., http://localhost:8080/StudentManagementSystem/ or similar, depending on your deployment). This will likely redirect you to the login page.
3.	Login:
   Use the login interface to authenticate yourself. The system supports three roles: Admin, Faculty, and Student. You will need valid credentials for one of these roles.
5.	Navigate Based on Role:
   o	Admin: Upon logging in, an Admin will typically land on an admin dashboard. From here, they can access functionalities for user management, academic structure management (Departments, Programs, Courses, Terms, Locations), course and faculty assignments, enrollments, lecture session creation, academic records oversight (grades and attendance), announcement management, and the NFC attendance simulation interface.
  	o	Faculty: Faculty members will see a faculty dashboard upon login. They can view their assigned courses, see enrolled student lists, input and update grades for their courses, select and mark attendance for specific lecture sessions, and view announcements.
  	o	Student: Students will land on their personalized dashboard, which is a unique feature. This dashboard provides a summary of current courses, attendance snippets, and relevant announcements. Students can also view their profile, enrolled courses, grades, and detailed attendance records, and view announcements targeted to them or all users.
6.	Perform Actions:
   Interact with the system through forms, links, and buttons to perform specific actions relevant to your role (e.g., adding a new student, enrolling a student in a course, recording grades, viewing attendance). Server-side validation will be performed on user input.
8.	Logout:
   When finished, securely log out of the system.


**Group Members**
1. Pasan Akalanka
2. D.P.S.Senarethna-32814
3. R.S.D.Senarathna-32837
4. K.E.I.Kovilawaththa-32500
5. N.A.O.N .Perera-32098
6. K.L.Y.K.Lekamge-32476
7. K.L.I.Lakshani-32589
8. H.M.L.P.Herath-32648
9. B.A.H.T.Kumari-33061
10. A.P.R.Jayananda-32927
