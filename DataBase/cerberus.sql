-- phpMyAdmin SQL Dump
-- version 4.8.5
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1:3306
-- Generation Time: Sep 12, 2019 at 12:55 PM
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
  KEY `PRN` (`PRN`,`scheduleID`,`timeID`),
  KEY `dateID` (`dateID`),
  KEY `scheduleID` (`scheduleID`),
  KEY `timeID` (`timeID`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=latin1;

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
(1, 'Batch 1'),
(2, 'Batch 2'),
(3, 'Batch 3');

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
  `FacultyID` int(3) NOT NULL,
  `templateID` tinyint(1) NOT NULL,
  `template` blob NOT NULL,
  PRIMARY KEY (`FacultyID`,`templateID`),
  KEY `PRN` (`FacultyID`)
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
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=latin1;

--
-- Dumping data for table `otp`
--

INSERT INTO `otp` (`OTPID`, `email`, `OTP`) VALUES
(5, 'ebenezerv99@gmail.com', '7df5999ae1b0cbaaededbe7ea7bb3ddeadef6b91c16071e69069a2464f3f1809');

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
  `PRN` bigint(16) NOT NULL,
  `templateID` tinyint(1) NOT NULL,
  `template` blob NOT NULL,
  PRIMARY KEY (`PRN`,`templateID`),
  KEY `PRN` (`PRN`)
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
  `classID` tinyint(1) NOT NULL,
  PRIMARY KEY (`subjectID`,`sem`),
  KEY `classID` (`classID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `subject`
--

INSERT INTO `subject` (`subjectID`, `sem`, `subject`, `classID`) VALUES
('BCA1001', 2, 'Desktop Publishing LAB', 1),
('BCA1009', 4, 'Web Publishing', 2),
('BCA1010', 4, 'Introduction to Multimedia', 2),
('BCA1105', 1, 'PC Software and Database Lab', 1),
('BCA1106', 1, 'Programming Lab', 1),
('BCA1205', 2, 'OOP Lab', 1),
('BCA1206', 2, 'Data Structures Lab', 1),
('BCA1207', 2, 'SQL Lab', 1),
('BCA1208', 1, 'HTML-I Lab', 1),
('BCA1301', 3, 'Java Lab', 2),
('BCA1303', 4, '.NET Programming LAB', 2),
('BCA1304', 3, 'Shell Programming Lab', 2),
('BCA1305', 3, 'Database Application Programming Lab', 2),
('BCA1306', 3, 'Computer Networks-I Lab', 2),
('BCA1307', 3, 'HTML-II Lab', 2),
('BCA1308', 3, 'Data Exploration Lab', 2),
('BCA1401', 4, 'Advanced Java Programming Lab', 2),
('BCA1403', 4, 'Web technology Lab', 2),
('BCA1405', 4, 'Computer Networks-II Lab', 2),
('BCA1501', 5, 'XML Lab', 3),
('BCA1530', 5, 'Web Application Development Lab', 3),
('BCA1538', 5, 'Artificial Intelligence', 3),
('BCA1539', 5, 'Mobile Computing Lab', 3);

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
  `facultyID` int(3) NOT NULL,
  `weekID` int(2) NOT NULL,
  `dayID` varchar(3) NOT NULL,
  PRIMARY KEY (`scheduleID`),
  KEY `sessionID` (`slotID`,`labID`,`subjectID`,`batchID`,`facultyID`),
  KEY `labID` (`labID`),
  KEY `facultyID` (`facultyID`),
  KEY `batchID` (`batchID`),
  KEY `weekID` (`weekID`),
  KEY `dayID` (`dayID`),
  KEY `subjectID` (`subjectID`)
) ENGINE=InnoDB AUTO_INCREMENT=23 DEFAULT CHARSET=latin1;

--
-- Dumping data for table `timetable`
--

INSERT INTO `timetable` (`scheduleID`, `slotID`, `labID`, `subjectID`, `batchID`, `facultyID`, `weekID`, `dayID`) VALUES
(1, 1, 1, 'BCA1538', 1, 11, 1, 'mon'),
(2, 2, 1, 'BCA1301', 1, 3, 1, 'mon'),
(3, 2, 2, 'BCA1308', 2, 1, 1, 'mon'),
(4, 2, 3, 'BCA1303', 3, 5, 1, 'mon'),
(5, 3, 1, 'BCA1539', 1, 3, 1, 'mon'),
(6, 4, 1, 'BCA1105', 1, 6, 1, 'mon'),
(7, 4, 2, 'BCA1106', 2, 1, 1, 'mon'),
(8, 4, 3, 'BCA1208', 3, 5, 1, 'mon'),
(9, 5, 1, 'BCA1001', 1, 10, 1, 'mon'),
(10, 5, 2, 'BCA1001', 2, 10, 1, 'mon'),
(11, 5, 3, 'BCA1001', 3, 10, 1, 'mon'),
(12, 1, 1, 'BCA1538', 2, 11, 1, 'tue'),
(13, 1, 2, 'BCA1530', 1, 8, 1, 'tue'),
(14, 2, 1, 'BCA1304', 1, 2, 1, 'tue');

-- --------------------------------------------------------

--
-- Table structure for table `week`
--

DROP TABLE IF EXISTS `week`;
CREATE TABLE IF NOT EXISTS `week` (
  `weekID` int(2) NOT NULL AUTO_INCREMENT,
  `week` int(2) NOT NULL,
  PRIMARY KEY (`weekID`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=latin1;

--
-- Dumping data for table `week`
--

INSERT INTO `week` (`weekID`, `week`) VALUES
(1, 37);

--
-- Constraints for dumped tables
--

--
-- Constraints for table `attendance`
--
ALTER TABLE `attendance`
  ADD CONSTRAINT `attendance_ibfk_1` FOREIGN KEY (`PRN`) REFERENCES `student` (`PRN`),
  ADD CONSTRAINT `attendance_ibfk_2` FOREIGN KEY (`dateID`) REFERENCES `datedata` (`dateID`),
  ADD CONSTRAINT `attendance_ibfk_3` FOREIGN KEY (`scheduleID`) REFERENCES `timetable` (`scheduleID`),
  ADD CONSTRAINT `attendance_ibfk_4` FOREIGN KEY (`timeID`) REFERENCES `timedata` (`timeID`);

--
-- Constraints for table `facultyfingerprint`
--
ALTER TABLE `facultyfingerprint`
  ADD CONSTRAINT `facultyfingerprint_ibfk_1` FOREIGN KEY (`FacultyID`) REFERENCES `faculty` (`facultyID`);

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
  ADD CONSTRAINT `timetable_ibfk_3` FOREIGN KEY (`facultyID`) REFERENCES `faculty` (`facultyID`),
  ADD CONSTRAINT `timetable_ibfk_4` FOREIGN KEY (`batchID`) REFERENCES `batch` (`batchID`),
  ADD CONSTRAINT `timetable_ibfk_5` FOREIGN KEY (`weekID`) REFERENCES `week` (`weekID`),
  ADD CONSTRAINT `timetable_ibfk_6` FOREIGN KEY (`subjectID`) REFERENCES `subject` (`subjectID`),
  ADD CONSTRAINT `timetable_ibfk_7` FOREIGN KEY (`dayID`) REFERENCES `daydata` (`dayID`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
