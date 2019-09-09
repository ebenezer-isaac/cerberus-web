-- phpMyAdmin SQL Dump
-- version 4.8.5
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1:3306
-- Generation Time: Sep 09, 2019 at 05:22 AM
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
-- Database: `attendance`
--

-- --------------------------------------------------------

--
-- Table structure for table `bca1304`
--

DROP TABLE IF EXISTS `bca1304`;
CREATE TABLE IF NOT EXISTS `bca1304` (
  `class` int(1) NOT NULL,
  `roll` int(3) NOT NULL,
  `total` int(2) NOT NULL,
  `perc` float NOT NULL,
  `2019/05/27` varchar(1) NOT NULL,
  `2019/05/28` varchar(1) NOT NULL,
  PRIMARY KEY (`class`,`roll`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

--
-- Dumping data for table `bca1304`
--

INSERT INTO `bca1304` (`class`, `roll`, `total`, `perc`, `2019/05/27`, `2019/05/28`) VALUES
(2, 10, 2, 100, '1', '1');

-- --------------------------------------------------------

--
-- Table structure for table `bca1305`
--

DROP TABLE IF EXISTS `bca1305`;
CREATE TABLE IF NOT EXISTS `bca1305` (
  `class` int(1) NOT NULL,
  `roll` int(3) NOT NULL,
  `total` int(2) NOT NULL,
  `perc` float NOT NULL,
  `2019/05/27` varchar(1) NOT NULL,
  `2019/05/28` varchar(1) NOT NULL,
  PRIMARY KEY (`class`,`roll`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

--
-- Dumping data for table `bca1305`
--

INSERT INTO `bca1305` (`class`, `roll`, `total`, `perc`, `2019/05/27`, `2019/05/28`) VALUES
(2, 10, 1, 50, '0', '1');

-- --------------------------------------------------------

--
-- Table structure for table `details_ty`
--

DROP TABLE IF EXISTS `details_ty`;
CREATE TABLE IF NOT EXISTS `details_ty` (
  `roll` int(3) NOT NULL,
  `name` varchar(120) NOT NULL,
  `bca1304` int(11) NOT NULL,
  `bca1305` int(11) NOT NULL,
  PRIMARY KEY (`roll`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

--
-- Dumping data for table `details_ty`
--

INSERT INTO `details_ty` (`roll`, `name`, `bca1304`, `bca1305`) VALUES
(10, 'Ebenezer Isaac', 1, 1);

-- --------------------------------------------------------

--
-- Table structure for table `login`
--

DROP TABLE IF EXISTS `login`;
CREATE TABLE IF NOT EXISTS `login` (
  `username` varchar(20) NOT NULL,
  `password` varchar(20) NOT NULL,
  PRIMARY KEY (`username`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

--
-- Dumping data for table `login`
--

INSERT INTO `login` (`username`, `password`) VALUES
('admin', 'admin');

-- --------------------------------------------------------

--
-- Table structure for table `subjects`
--

DROP TABLE IF EXISTS `subjects`;
CREATE TABLE IF NOT EXISTS `subjects` (
  `code` varchar(7) NOT NULL,
  `name` varchar(30) NOT NULL,
  `sem` int(1) NOT NULL,
  `no_of_labs` int(11) NOT NULL,
  PRIMARY KEY (`code`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

--
-- Dumping data for table `subjects`
--

INSERT INTO `subjects` (`code`, `name`, `sem`, `no_of_labs`) VALUES
('bca1304', 'Shell Programming', 5, 2),
('bca1305', 'Artificial Intelligence', 5, 2);

-- --------------------------------------------------------

--
-- Table structure for table `timetable1`
--

DROP TABLE IF EXISTS `timetable1`;
CREATE TABLE IF NOT EXISTS `timetable1` (
  `start_time` time NOT NULL,
  `end_time` time NOT NULL,
  `mon` varchar(7) NOT NULL,
  `tue` varchar(7) NOT NULL,
  `wed` varchar(7) NOT NULL,
  `thu` varchar(7) NOT NULL,
  `fri` varchar(7) NOT NULL,
  `sat` varchar(7) NOT NULL,
  PRIMARY KEY (`start_time`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

--
-- Dumping data for table `timetable1`
--

INSERT INTO `timetable1` (`start_time`, `end_time`, `mon`, `tue`, `wed`, `thu`, `fri`, `sat`) VALUES
('07:40:00', '09:40:00', 'bca1304', 'bca1304', 'bca1304', 'bca1304', 'bca1304', 'bca1304'),
('10:00:00', '12:00:00', 'bca1304', 'bca1304', 'bca1304', 'bca1304', 'bca1304', 'bca1304'),
('13:00:00', '15:00:00', 'bca1304', 'bca1304', 'bca1304', 'bca1304', 'bca1304', 'bca1304'),
('15:00:00', '17:00:00', 'bca1304', 'bca1304', 'bca1304', 'bca1304', 'bca1304', 'bca1304');

-- --------------------------------------------------------

--
-- Table structure for table `timetable2`
--

DROP TABLE IF EXISTS `timetable2`;
CREATE TABLE IF NOT EXISTS `timetable2` (
  `start_time` time NOT NULL,
  `end_time` time NOT NULL,
  `mon` varchar(7) NOT NULL,
  `tue` varchar(7) NOT NULL,
  `wed` varchar(7) NOT NULL,
  `thu` varchar(7) NOT NULL,
  `fri` varchar(7) NOT NULL,
  `sat` varchar(7) NOT NULL,
  PRIMARY KEY (`start_time`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

--
-- Dumping data for table `timetable2`
--

INSERT INTO `timetable2` (`start_time`, `end_time`, `mon`, `tue`, `wed`, `thu`, `fri`, `sat`) VALUES
('07:40:00', '09:40:00', 'bca1304', 'bca1304', 'bca1304', 'bca1304', 'bca1304', 'bca1304'),
('10:00:00', '12:00:00', 'bca1304', 'bca1304', 'bca1304', 'bca1304', 'bca1304', 'bca1304'),
('13:00:00', '15:00:00', 'bca1304', 'bca1304', 'bca1304', 'bca1304', 'bca1304', 'bca1304'),
('15:00:00', '17:00:00', 'bca1304', 'bca1304', 'bca1304', 'bca1304', 'bca1304', 'bca1304');

-- --------------------------------------------------------

--
-- Table structure for table `timetable3`
--

DROP TABLE IF EXISTS `timetable3`;
CREATE TABLE IF NOT EXISTS `timetable3` (
  `start_time` time NOT NULL,
  `end_time` time NOT NULL,
  `mon` varchar(7) NOT NULL,
  `tue` varchar(7) NOT NULL,
  `wed` varchar(7) NOT NULL,
  `thu` varchar(7) NOT NULL,
  `fri` varchar(7) NOT NULL,
  `sat` varchar(7) NOT NULL,
  PRIMARY KEY (`start_time`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

--
-- Dumping data for table `timetable3`
--

INSERT INTO `timetable3` (`start_time`, `end_time`, `mon`, `tue`, `wed`, `thu`, `fri`, `sat`) VALUES
('07:40:00', '09:40:00', 'bca1304', 'bca1304', 'bca1304', 'bca1304', 'bca1304', 'bca1304'),
('10:00:00', '12:00:00', 'bca1304', 'bca1304', 'bca1304', 'bca1304', 'bca1304', 'bca1304'),
('13:00:00', '15:00:00', 'bca1304', 'bca1304', 'bca1304', 'bca1304', 'bca1304', 'bca1304'),
('15:00:00', '17:00:00', 'bca1304', 'bca1304', 'bca1304', 'bca1304', 'bca1304', 'bca1304');
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
