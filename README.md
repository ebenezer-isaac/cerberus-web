<h1 align="center">
	<img width="80" src="https://raw.githubusercontent.com/Cerberus-Biometric/cerberus-web/master/Logo/logo-circle.png">
	<br>
	C E R B E R U S
	<br>
	Remote Fingerprint Attendance System
</h1>

Before reading this kindly read cerberus-ard (and cerberus-rpi) repo.

### Architecture of the system ###
<h1 align="center">
	<img width="600" src="https://raw.githubusercontent.com/Cerberus-Biometric/cerberus-web/master/Documentation/Architecture.jpg">
	<br>
	<br>
</h1>

This is server-side coding of the whole attendance system.

### Tools & Technology Used: ###
* Netbeans
* PHPMyAdmin
* Git

### Languages ###
* Java (Servlets)
* AJAX
* PHP
* MySQL (for Database)


### Highlights ###
* The raspberryPi will collect and store attendance according to lab session(Stores Time, Date, Student Roll Number)
* The raspberryPi will forward the collected data to server (and then to database)
* Database file is provided in database folder (cerberus.sql)
* WebApp is the main netbeans project.
* WebApp Prototype is without GUI. (It's Database file is attendance.sql)
* Students can login and view their respective attendance.
* Teacher(here admins) can login and manipulate attendance, add timetable, manage subjects, manage student_details, manage admin.
* The WebApp is in alpha state currently.

### ScreenShot of index.html ###
<h1 align="center">
	<img width="600" src="https://raw.githubusercontent.com/Cerberus-Biometric/cerberus-web/master/Documentation/index.PNG">
	<br>
	<br>
</h1>

To know more about hardware-side kindly check cerberus-ard (and cerberus-rpi) repo.

[![forthebadge](https://forthebadge.com/images/badges/made-with-java.svg)](https://forthebadge.com)
[![forthebadge](https://forthebadge.com/images/badges/uses-js.svg)](https://forthebadge.com)
[![Uses SQL](https://img.shields.io/badge/uses-SQL-yellowgreen)](http://shields.io/#your-badge)
