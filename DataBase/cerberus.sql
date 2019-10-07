-- phpMyAdmin SQL Dump
-- version 4.8.5
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1:3306
-- Generation Time: Oct 07, 2019 at 01:17 PM
-- Server version: 5.7.26
-- PHP Version: 7.2.18

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET AUTOCOMMIT = 0;
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `cerberus`
--

-- --------------------------------------------------------

--
-- Table structure for table `attendance`
--

DROP TABLE IF EXISTS `attendance`;
CREATE TABLE IF NOT EXISTS `attendance` (
  `attendanceID` int(5) NOT NULL AUTO_INCREMENT,
  `PRN` bigint(16) NOT NULL,
  `scheduleID` int(4) NOT NULL,
  `dateID` int(3) NOT NULL,
  `timeID` int(5) NOT NULL,
  PRIMARY KEY (`attendanceID`),
  UNIQUE KEY `scheduleID` (`scheduleID`),
  UNIQUE KEY `dateID` (`dateID`),
  UNIQUE KEY `timeID` (`timeID`),
  KEY `PRN` (`PRN`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `batch`
--

DROP TABLE IF EXISTS `batch`;
CREATE TABLE IF NOT EXISTS `batch` (
  `batchID` tinyint(1) NOT NULL,
  `name` varchar(7) NOT NULL,
  PRIMARY KEY (`batchID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `batch`
--

INSERT INTO `batch` (`batchID`, `name`) VALUES
(1, 'Batch A'),
(2, 'Batch B'),
(3, 'Batch C');

-- --------------------------------------------------------

--
-- Table structure for table `class`
--

DROP TABLE IF EXISTS `class`;
CREATE TABLE IF NOT EXISTS `class` (
  `classID` tinyint(1) NOT NULL,
  `class` varchar(8) NOT NULL,
  PRIMARY KEY (`classID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `class`
--

INSERT INTO `class` (`classID`, `class`) VALUES
(1, 'BCA FY'),
(2, 'BCA SY'),
(3, 'BCA TY');

-- --------------------------------------------------------

--
-- Table structure for table `datedata`
--

DROP TABLE IF EXISTS `datedata`;
CREATE TABLE IF NOT EXISTS `datedata` (
  `dateID` int(3) NOT NULL AUTO_INCREMENT,
  `date` date NOT NULL,
  PRIMARY KEY (`dateID`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=latin1;

--
-- Dumping data for table `datedata`
--

INSERT INTO `datedata` (`dateID`, `date`) VALUES
(1, '2019-09-10');

-- --------------------------------------------------------

--
-- Table structure for table `daydata`
--

DROP TABLE IF EXISTS `daydata`;
CREATE TABLE IF NOT EXISTS `daydata` (
  `dayID` varchar(3) NOT NULL,
  `dayOfWeek` varchar(9) NOT NULL,
  PRIMARY KEY (`dayID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `daydata`
--

INSERT INTO `daydata` (`dayID`, `dayOfWeek`) VALUES
('fri', 'Friday'),
('mon', 'Monday'),
('sat', 'Saturday'),
('thu', 'Thursday'),
('tue', 'Tuesday'),
('wed', 'Wednesday');

-- --------------------------------------------------------

--
-- Table structure for table `faculty`
--

DROP TABLE IF EXISTS `faculty`;
CREATE TABLE IF NOT EXISTS `faculty` (
  `facultyID` int(3) NOT NULL AUTO_INCREMENT,
  `name` varchar(120) NOT NULL,
  `email` varchar(50) NOT NULL,
  `password` varchar(256) NOT NULL,
  PRIMARY KEY (`facultyID`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=latin1;

--
-- Dumping data for table `faculty`
--

INSERT INTO `faculty` (`facultyID`, `name`, `email`, `password`) VALUES
(1, 'Mr.Krishnanad', 'ebenezerv99@gmail.com', 'de786632264b9f3a79a7ec15c2460dd079554f0b0ad377cb8408edeb26947eec'),
(2, 'Mr.Ksitij', 'asdfasdfasdf@gmail.com', 'de786632264b9f3a79a7ec15c2460dd079554f0b0ad377cb8408edeb26947eec'),
(3, 'Mrs.Pooja', 'esdfgdgff@gmail.com', 'de786632264b9f3a79a7ec15c2460dd079554f0b0ad377cb8408edeb26947eec'),
(4, 'Mrs.Preeti', 'ebenadfgsadezerv99@gmail.com', 'de786632264b9f3a79a7ec15c2460dd079554f0b0ad377cb8408edeb26947eec'),
(5, 'Ms.Krishna', 'ebefgndneasdfvzerv99@gmail.com', 'de786632264b9f3a79a7ec15c2460dd079554f0b0ad377cb8408edeb26947eec'),
(6, 'Mrs.Mitali', 'ebfweneeaszdvzerv99@gmail.com', 'de786632264b9f3a79a7ec15c2460dd079554f0b0ad377cb8408edeb26947eec'),
(7, 'Mrs.Heta', 'ebedtenezexcvrv99@gmail.com', 'de786632264b9f3a79a7ec15c2460dd079554f0b0ad377cb8408edeb26947eec'),
(8, 'Mrs.Meghna', 'ebeasdfnerdeasdfczerv99@gmail.com', 'de786632264b9f3a79a7ec15c2460dd079554f0b0ad377cb8408edeb26947eec'),
(9, 'Mr.Tejas', 'ebenezerasdfcv99@gmail.com', 'de786632264b9f3a79a7ec15c2460dd079554f0b0ad377cb8408edeb26947eec'),
(10, 'Mrs.Hetal', 'ebenezxcfzerv99@gmail.com', 'de786632264b9f3a79a7ec15c2460dd079554f0b0ad377cb8408edeb26947eec'),
(11, 'Ms.Divya', 'ebendezdfcsdferv99@gmail.com', 'de786632264b9f3a79a7ec15c2460dd079554f0b0ad377cb8408edeb26947eec'),
(12, 'Ms.Riya', 'ebenezsdrerv99@gmail.com', 'de786632264b9f3a79a7ec15c2460dd079554f0b0ad377cb8408edeb26947eec'),
(13, 'Ms.Ankita', 'ebeasdfnqwerdezerv99@gmail.com', 'de786632264b9f3a79a7ec15c2460dd079554f0b0ad377cb8408edeb26947eec');

-- --------------------------------------------------------

--
-- Table structure for table `facultyfingerprint`
--

DROP TABLE IF EXISTS `facultyfingerprint`;
CREATE TABLE IF NOT EXISTS `facultyfingerprint` (
  `ffID` int(2) NOT NULL AUTO_INCREMENT,
  `facultyID` int(3) NOT NULL,
  `templateID` tinyint(1) NOT NULL,
  `template` blob NOT NULL,
  PRIMARY KEY (`ffID`),
  KEY `facultyID_2` (`facultyID`,`templateID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `facultytimetable`
--

DROP TABLE IF EXISTS `facultytimetable`;
CREATE TABLE IF NOT EXISTS `facultytimetable` (
  `scheduleID` int(4) NOT NULL,
  `facultyID` int(3) NOT NULL,
  KEY `scheduleID` (`scheduleID`,`facultyID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `lab`
--

DROP TABLE IF EXISTS `lab`;
CREATE TABLE IF NOT EXISTS `lab` (
  `labID` tinyint(1) NOT NULL,
  `name` varchar(5) NOT NULL,
  PRIMARY KEY (`labID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `lab`
--

INSERT INTO `lab` (`labID`, `name`) VALUES
(1, 'Lab 1'),
(2, 'Lab 2'),
(3, 'Lab 3');

-- --------------------------------------------------------

--
-- Table structure for table `log`
--

DROP TABLE IF EXISTS `log`;
CREATE TABLE IF NOT EXISTS `log` (
  `logID` int(4) NOT NULL AUTO_INCREMENT,
  `logTypeID` tinyint(1) NOT NULL,
  `dateID` int(3) NOT NULL,
  `timeID` int(5) NOT NULL,
  `comments` varchar(200) NOT NULL,
  PRIMARY KEY (`logID`),
  KEY `dateID` (`dateID`,`timeID`),
  KEY `logTypeID` (`logTypeID`),
  KEY `timeID` (`timeID`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=latin1;

--
-- Dumping data for table `log`
--

INSERT INTO `log` (`logID`, `logTypeID`, `dateID`, `timeID`, `comments`) VALUES
(1, 1, 1, 1, '2017033800107501-1 enroll');

-- --------------------------------------------------------

--
-- Table structure for table `logtype`
--

DROP TABLE IF EXISTS `logtype`;
CREATE TABLE IF NOT EXISTS `logtype` (
  `logTypeID` tinyint(1) NOT NULL,
  `logType` varchar(25) NOT NULL,
  PRIMARY KEY (`logTypeID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `logtype`
--

INSERT INTO `logtype` (`logTypeID`, `logType`) VALUES
(1, 'Fingerprint'),
(2, 'Manual Attendance');

-- --------------------------------------------------------

--
-- Table structure for table `otp`
--

DROP TABLE IF EXISTS `otp`;
CREATE TABLE IF NOT EXISTS `otp` (
  `OTPID` int(4) NOT NULL AUTO_INCREMENT,
  `email` varchar(50) NOT NULL,
  `OTP` varchar(256) NOT NULL,
  PRIMARY KEY (`OTPID`),
  KEY `PRN` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=latin1;

--
-- Dumping data for table `otp`
--

INSERT INTO `otp` (`OTPID`, `email`, `OTP`) VALUES
(10, 'ebenezerv99@gmail.com', '6c4513aa93220cf39fd8c051ca95e7cd7b6f4f4f3905e07b9546b27a5ff4b990');

-- --------------------------------------------------------

--
-- Table structure for table `rollcall`
--

DROP TABLE IF EXISTS `rollcall`;
CREATE TABLE IF NOT EXISTS `rollcall` (
  `classID` tinyint(1) NOT NULL,
  `rollNo` varchar(3) NOT NULL,
  `PRN` bigint(16) NOT NULL,
  PRIMARY KEY (`classID`,`rollNo`),
  KEY `PRN` (`PRN`),
  KEY `classID` (`classID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `rollcall`
--

INSERT INTO `rollcall` (`classID`, `rollNo`, `PRN`) VALUES
(3, '82', 2017033800107501);

-- --------------------------------------------------------

--
-- Table structure for table `slot`
--

DROP TABLE IF EXISTS `slot`;
CREATE TABLE IF NOT EXISTS `slot` (
  `slotID` tinyint(1) NOT NULL,
  `startTime` time NOT NULL,
  `endTime` time NOT NULL,
  PRIMARY KEY (`slotID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `slot`
--

INSERT INTO `slot` (`slotID`, `startTime`, `endTime`) VALUES
(1, '08:00:00', '10:00:00'),
(2, '10:00:00', '12:00:00'),
(3, '13:00:00', '15:00:00'),
(4, '15:00:00', '17:00:00'),
(5, '17:30:00', '19:30:00');

-- --------------------------------------------------------

--
-- Table structure for table `student`
--

DROP TABLE IF EXISTS `student`;
CREATE TABLE IF NOT EXISTS `student` (
  `PRN` bigint(16) NOT NULL,
  `name` varchar(120) NOT NULL,
  `email` varchar(50) NOT NULL,
  `password` varchar(256) NOT NULL,
  PRIMARY KEY (`PRN`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `student`
--

INSERT INTO `student` (`PRN`, `name`, `email`, `password`) VALUES
(2017033800107501, 'Vraj Kotwala', 'iamkotwala@gmail.com', 'de786632264b9f3a79a7ec15c2460dd079554f0b0ad377cb8408edeb26947eec');

-- --------------------------------------------------------

--
-- Table structure for table `studentfingerprint`
--

DROP TABLE IF EXISTS `studentfingerprint`;
CREATE TABLE IF NOT EXISTS `studentfingerprint` (
  `sfID` int(3) NOT NULL AUTO_INCREMENT,
  `PRN` bigint(16) NOT NULL,
  `templateID` tinyint(1) NOT NULL,
  `template` blob NOT NULL,
  PRIMARY KEY (`sfID`),
  KEY `PRN` (`PRN`,`templateID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `studentsubject`
--

DROP TABLE IF EXISTS `studentsubject`;
CREATE TABLE IF NOT EXISTS `studentsubject` (
  `PRN` bigint(16) NOT NULL,
  `subjectID` varchar(7) NOT NULL,
  `batchID` tinyint(1) NOT NULL,
  PRIMARY KEY (`PRN`,`subjectID`),
  KEY `batchID` (`batchID`),
  KEY `PRN` (`PRN`,`subjectID`),
  KEY `subjectID` (`subjectID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `studentsubject`
--

INSERT INTO `studentsubject` (`PRN`, `subjectID`, `batchID`) VALUES
(2017033800107501, 'BCA1538', 1);

-- --------------------------------------------------------

--
-- Table structure for table `subject`
--

DROP TABLE IF EXISTS `subject`;
CREATE TABLE IF NOT EXISTS `subject` (
  `subjectID` varchar(7) NOT NULL,
  `sem` tinyint(1) NOT NULL,
  `subject` varchar(40) NOT NULL,
  `Abbreviation` varchar(10) NOT NULL,
  `classID` tinyint(1) NOT NULL,
  PRIMARY KEY (`subjectID`,`sem`),
  KEY `classID` (`classID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `subject`
--

INSERT INTO `subject` (`subjectID`, `sem`, `subject`, `Abbreviation`, `classID`) VALUES
('BCA1001', 2, 'Desktop Publishing', 'DTP', 1),
('BCA1009', 4, 'Web Publishing', 'Web Pub', 2),
('BCA1010', 4, 'Introduction to Multimedia', 'IM', 2),
('BCA1105', 1, 'PC Software and Database', 'PCDB', 1),
('BCA1106', 1, 'Programming', 'Python', 1),
('BCA1205', 2, 'Object Oriented Programming', 'OOP', 1),
('BCA1206', 2, 'Data Structures', 'DS', 1),
('BCA1207', 2, 'Structured Query Language', 'SQL', 1),
('BCA1208', 1, 'HTML-I', 'HTML-1', 1),
('BCA1301', 3, 'Java', 'JAVA', 2),
('BCA1303', 4, '.NET Programming', '.NET', 2),
('BCA1304', 3, 'Shell Programming', 'SHELL', 2),
('BCA1305', 3, 'Database Application Programming', 'DAP', 2),
('BCA1306', 3, 'Computer Networks-I ', 'CN-1', 2),
('BCA1307', 3, 'HTML-II', 'HTML-2', 2),
('BCA1308', 3, 'Data Exploration', 'DE', 2),
('BCA1401', 4, 'Advanced Java Programming', 'Adv. JAVA', 2),
('BCA1403', 4, 'Web technology', 'WT', 2),
('BCA1405', 4, 'Computer Networks-II', 'CN-2', 2),
('BCA1501', 5, 'XML', 'XML', 3),
('BCA1530', 5, 'Web Application Development', 'WAD', 3),
('BCA1538', 5, 'Artificial Intelligence', 'AI', 3),
('BCA1539', 5, 'Mobile Computing', 'MC', 3);

-- --------------------------------------------------------

--
-- Table structure for table `timedata`
--

DROP TABLE IF EXISTS `timedata`;
CREATE TABLE IF NOT EXISTS `timedata` (
  `timeID` int(5) NOT NULL AUTO_INCREMENT,
  `time` time NOT NULL,
  PRIMARY KEY (`timeID`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=latin1;

--
-- Dumping data for table `timedata`
--

INSERT INTO `timedata` (`timeID`, `time`) VALUES
(1, '10:10:00');

-- --------------------------------------------------------

--
-- Table structure for table `timetable`
--

DROP TABLE IF EXISTS `timetable`;
CREATE TABLE IF NOT EXISTS `timetable` (
  `scheduleID` int(4) NOT NULL AUTO_INCREMENT,
  `slotID` tinyint(1) NOT NULL,
  `labID` tinyint(1) NOT NULL,
  `subjectID` varchar(7) NOT NULL,
  `batchID` tinyint(1) NOT NULL,
  `weekID` int(2) NOT NULL,
  `dayID` varchar(3) NOT NULL,
  PRIMARY KEY (`scheduleID`),
  KEY `slotID` (`slotID`),
  KEY `labID` (`labID`),
  KEY `subjectID` (`subjectID`),
  KEY `batchID` (`batchID`),
  KEY `weekID` (`weekID`),
  KEY `dayID` (`dayID`)
) ENGINE=InnoDB AUTO_INCREMENT=229 DEFAULT CHARSET=latin1;

--
-- Dumping data for table `timetable`
--

INSERT INTO `timetable` (`scheduleID`, `slotID`, `labID`, `subjectID`, `batchID`, `weekID`, `dayID`) VALUES
(1, 1, 1, 'BCA1538', 1, 1, 'mon'),
(2, 2, 1, 'BCA1301', 1, 1, 'mon'),
(3, 2, 2, 'BCA1308', 2, 1, 'mon'),
(4, 2, 3, 'BCA1303', 3, 1, 'mon'),
(5, 3, 1, 'BCA1539', 1, 1, 'mon'),
(6, 4, 1, 'BCA1105', 1, 1, 'mon'),
(7, 4, 2, 'BCA1106', 2, 1, 'mon'),
(8, 4, 3, 'BCA1208', 3, 1, 'mon'),
(9, 5, 1, 'BCA1001', 1, 1, 'mon'),
(10, 5, 2, 'BCA1001', 2, 1, 'mon'),
(11, 5, 3, 'BCA1001', 3, 1, 'mon'),
(12, 1, 1, 'BCA1538', 2, 1, 'tue'),
(13, 1, 2, 'BCA1530', 1, 1, 'tue'),
(14, 2, 1, 'BCA1304', 1, 1, 'tue'),
(15, 1, 1, 'BCA1538', 1, 2, 'mon'),
(16, 2, 1, 'BCA1301', 1, 2, 'mon'),
(17, 2, 2, 'BCA1308', 2, 2, 'mon'),
(18, 2, 3, 'BCA1303', 3, 2, 'mon'),
(19, 3, 1, 'BCA1539', 1, 2, 'mon'),
(20, 4, 1, 'BCA1105', 1, 2, 'mon'),
(21, 4, 2, 'BCA1106', 2, 2, 'mon'),
(22, 4, 3, 'BCA1208', 3, 2, 'mon'),
(23, 5, 1, 'BCA1001', 1, 2, 'mon'),
(24, 5, 2, 'BCA1001', 2, 2, 'mon'),
(25, 5, 3, 'BCA1001', 3, 2, 'mon'),
(26, 1, 1, 'BCA1538', 2, 2, 'tue'),
(27, 1, 2, 'BCA1530', 1, 2, 'tue'),
(28, 2, 1, 'BCA1304', 1, 2, 'tue'),
(200, 1, 1, 'BCA1538', 1, 5, 'mon'),
(201, 1, 1, 'BCA1538', 2, 5, 'tue'),
(202, 1, 1, 'BCA1106', 2, 5, 'thu'),
(203, 1, 1, 'BCA1106', 2, 5, 'fri'),
(204, 1, 1, 'BCA1305', 3, 5, 'sat'),
(205, 2, 1, 'BCA1301', 1, 5, 'mon'),
(206, 2, 1, 'BCA1304', 1, 5, 'tue'),
(207, 2, 1, 'BCA1307', 1, 5, 'wed'),
(208, 2, 1, 'BCA1306', 2, 5, 'thu'),
(209, 2, 1, 'BCA1305', 1, 5, 'fri'),
(210, 2, 1, 'BCA1307', 2, 5, 'sat'),
(211, 3, 1, 'BCA1539', 1, 5, 'mon'),
(212, 3, 1, 'BCA1307', 2, 5, 'tue'),
(213, 3, 1, 'BCA1305', 1, 5, 'wed'),
(214, 3, 1, 'BCA1304', 2, 5, 'thu'),
(215, 3, 1, 'BCA1208', 2, 5, 'fri'),
(216, 3, 1, 'BCA1304', 1, 5, 'sat'),
(217, 4, 1, 'BCA1105', 1, 5, 'mon'),
(218, 4, 1, 'BCA1306', 2, 5, 'tue'),
(219, 4, 1, 'BCA1305', 1, 5, 'wed'),
(220, 4, 1, 'BCA1208', 2, 5, 'thu'),
(221, 4, 1, 'BCA1106', 3, 5, 'fri'),
(222, 4, 1, 'BCA1208', 1, 5, 'sat'),
(223, 5, 1, 'BCA1301', 1, 5, 'mon'),
(224, 5, 1, 'BCA1304', 3, 5, 'tue'),
(225, 5, 1, 'BCA1304', 3, 5, 'wed'),
(226, 5, 1, 'BCA1106', 2, 5, 'thu'),
(227, 5, 1, 'BCA1304', 2, 5, 'fri'),
(228, 5, 1, 'BCA1301', 1, 5, 'sat');

-- --------------------------------------------------------

--
-- Table structure for table `week`
--

DROP TABLE IF EXISTS `week`;
CREATE TABLE IF NOT EXISTS `week` (
  `weekID` int(2) NOT NULL AUTO_INCREMENT,
  `week` int(2) NOT NULL,
  PRIMARY KEY (`weekID`),
  UNIQUE KEY `week` (`week`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=latin1;

--
-- Dumping data for table `week`
--

INSERT INTO `week` (`weekID`, `week`) VALUES
(1, 39),
(2, 40),
(5, 41);

--
-- Constraints for dumped tables
--

--
-- Constraints for table `attendance`
--
ALTER TABLE `attendance`
  ADD CONSTRAINT `attendance_ibfk_1` FOREIGN KEY (`scheduleID`) REFERENCES `timetable` (`scheduleID`),
  ADD CONSTRAINT `attendance_ibfk_2` FOREIGN KEY (`PRN`) REFERENCES `student` (`PRN`),
  ADD CONSTRAINT `attendance_ibfk_3` FOREIGN KEY (`dateID`) REFERENCES `datedata` (`dateID`),
  ADD CONSTRAINT `attendance_ibfk_4` FOREIGN KEY (`timeID`) REFERENCES `timedata` (`timeID`);

--
-- Constraints for table `facultyfingerprint`
--
ALTER TABLE `facultyfingerprint`
  ADD CONSTRAINT `facultyfingerprint_ibfk_1` FOREIGN KEY (`facultyID`) REFERENCES `faculty` (`facultyID`);

--
-- Constraints for table `log`
--
ALTER TABLE `log`
  ADD CONSTRAINT `log_ibfk_1` FOREIGN KEY (`logTypeID`) REFERENCES `logtype` (`logTypeID`),
  ADD CONSTRAINT `log_ibfk_2` FOREIGN KEY (`timeID`) REFERENCES `timedata` (`timeID`),
  ADD CONSTRAINT `log_ibfk_3` FOREIGN KEY (`dateID`) REFERENCES `datedata` (`dateID`);

--
-- Constraints for table `rollcall`
--
ALTER TABLE `rollcall`
  ADD CONSTRAINT `rollcall_ibfk_1` FOREIGN KEY (`classID`) REFERENCES `class` (`classID`),
  ADD CONSTRAINT `rollcall_ibfk_2` FOREIGN KEY (`PRN`) REFERENCES `student` (`PRN`);

--
-- Constraints for table `studentfingerprint`
--
ALTER TABLE `studentfingerprint`
  ADD CONSTRAINT `studentfingerprint_ibfk_1` FOREIGN KEY (`PRN`) REFERENCES `student` (`PRN`);

--
-- Constraints for table `studentsubject`
--
ALTER TABLE `studentsubject`
  ADD CONSTRAINT `studentsubject_ibfk_1` FOREIGN KEY (`subjectID`) REFERENCES `subject` (`subjectID`),
  ADD CONSTRAINT `studentsubject_ibfk_2` FOREIGN KEY (`batchID`) REFERENCES `batch` (`batchID`),
  ADD CONSTRAINT `studentsubject_ibfk_3` FOREIGN KEY (`PRN`) REFERENCES `student` (`PRN`);

--
-- Constraints for table `subject`
--
ALTER TABLE `subject`
  ADD CONSTRAINT `subject_ibfk_1` FOREIGN KEY (`classID`) REFERENCES `class` (`classID`);

--
-- Constraints for table `timetable`
--
ALTER TABLE `timetable`
  ADD CONSTRAINT `timetable_ibfk_1` FOREIGN KEY (`slotID`) REFERENCES `slot` (`slotID`),
  ADD CONSTRAINT `timetable_ibfk_2` FOREIGN KEY (`labID`) REFERENCES `lab` (`labID`),
  ADD CONSTRAINT `timetable_ibfk_3` FOREIGN KEY (`subjectID`) REFERENCES `subject` (`subjectID`),
  ADD CONSTRAINT `timetable_ibfk_4` FOREIGN KEY (`weekID`) REFERENCES `week` (`weekID`),
  ADD CONSTRAINT `timetable_ibfk_5` FOREIGN KEY (`batchID`) REFERENCES `batch` (`batchID`),
  ADD CONSTRAINT `timetable_ibfk_6` FOREIGN KEY (`dayID`) REFERENCES `daydata` (`dayID`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
