-- phpMyAdmin SQL Dump
-- version 4.8.5
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1:3306
-- Generation Time: Sep 10, 2019 at 08:39 AM
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
  KEY `dateID` (`dateID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `batch`
--

DROP TABLE IF EXISTS `batch`;
CREATE TABLE IF NOT EXISTS `batch` (
  `batchID` tinyint(1) NOT NULL,
  `name` varchar(6) NOT NULL,
  PRIMARY KEY (`batchID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `class`
--

DROP TABLE IF EXISTS `class`;
CREATE TABLE IF NOT EXISTS `class` (
  `classID` tinyint(1) NOT NULL,
  `class` varchar(12) NOT NULL,
  PRIMARY KEY (`classID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `datedata`
--

DROP TABLE IF EXISTS `datedata`;
CREATE TABLE IF NOT EXISTS `datedata` (
  `dateID` int(3) NOT NULL AUTO_INCREMENT,
  `date` date NOT NULL,
  PRIMARY KEY (`dateID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

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
) ENGINE=InnoDB AUTO_INCREMENT=102 DEFAULT CHARSET=latin1;

--
-- Dumping data for table `faculty`
--

INSERT INTO `faculty` (`facultyID`, `name`, `email`, `password`) VALUES
(101, 'ebenezerv99@gmail.com', 'Vraj Kotwala', 'de786632264b9f3a79a7ec15c2460dd079554f0b0ad377cb8408edeb26947eec');

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
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `lab`
--

DROP TABLE IF EXISTS `lab`;
CREATE TABLE IF NOT EXISTS `lab` (
  `labID` tinyint(1) NOT NULL,
  `name` varchar(4) NOT NULL,
  PRIMARY KEY (`labID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

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
  KEY `logTypeID` (`logTypeID`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `logtype`
--

DROP TABLE IF EXISTS `logtype`;
CREATE TABLE IF NOT EXISTS `logtype` (
  `logTypeID` tinyint(1) NOT NULL,
  `logType` varchar(15) NOT NULL,
  PRIMARY KEY (`logTypeID`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

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
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=latin1;

--
-- Dumping data for table `otp`
--

INSERT INTO `otp` (`OTPID`, `email`, `OTP`) VALUES
(1, '2017033800107501', '223465');

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
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `studentsubject`
--

DROP TABLE IF EXISTS `studentsubject`;
CREATE TABLE IF NOT EXISTS `studentsubject` (
  `PRN` bigint(16) NOT NULL,
  `subjectID` int(7) NOT NULL,
  `batchID` tinyint(1) NOT NULL,
  PRIMARY KEY (`PRN`,`subjectID`),
  KEY `batchID` (`batchID`),
  KEY `PRN` (`PRN`,`subjectID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `subject`
--

DROP TABLE IF EXISTS `subject`;
CREATE TABLE IF NOT EXISTS `subject` (
  `subjectID` varchar(7) NOT NULL,
  `sem` tinyint(1) NOT NULL,
  `subject` varchar(40) NOT NULL,
  `classID` int(1) NOT NULL,
  PRIMARY KEY (`subjectID`,`sem`),
  KEY `classID` (`classID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `timedata`
--

DROP TABLE IF EXISTS `timedata`;
CREATE TABLE IF NOT EXISTS `timedata` (
  `timeID` int(5) NOT NULL AUTO_INCREMENT,
  `time` time NOT NULL,
  PRIMARY KEY (`timeID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `timetable`
--

DROP TABLE IF EXISTS `timetable`;
CREATE TABLE IF NOT EXISTS `timetable` (
  `scheduleID` int(4) NOT NULL AUTO_INCREMENT,
  `slotID` int(1) NOT NULL,
  `labID` int(1) NOT NULL,
  `subjectID` varchar(7) NOT NULL,
  `batchID` int(1) NOT NULL,
  `facultyID` int(3) NOT NULL,
  `weekID` int(2) NOT NULL,
  `dayID` varchar(3) NOT NULL,
  PRIMARY KEY (`scheduleID`),
  KEY `sessionID` (`slotID`,`labID`,`subjectID`,`batchID`,`facultyID`,`dayID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `week`
--

DROP TABLE IF EXISTS `week`;
CREATE TABLE IF NOT EXISTS `week` (
  `weekID` int(2) NOT NULL AUTO_INCREMENT,
  `week` int(2) NOT NULL,
  PRIMARY KEY (`weekID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
