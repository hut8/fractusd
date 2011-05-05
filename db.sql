-- MySQL dump 10.13  Distrib 5.1.41, for debian-linux-gnu (x86_64)
--
-- Host: localhost    Database: fractus
-- ------------------------------------------------------
-- Server version	5.1.41-3ubuntu12.10

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `Connection_tbl`
--

DROP TABLE IF EXISTS `Connection_tbl`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Connection_tbl` (
  `ConnectionId` int(11) NOT NULL AUTO_INCREMENT,
  `UserId` int(11) NOT NULL,
  `Timestamp` datetime NOT NULL,
  PRIMARY KEY (`ConnectionId`)
) ENGINE=MyISAM AUTO_INCREMENT=1490 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Connection_tbl`
--

LOCK TABLES `Connection_tbl` WRITE;
/*!40000 ALTER TABLE `Connection_tbl` DISABLE KEYS */;
/*!40000 ALTER TABLE `Connection_tbl` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Contact_tbl`
--

DROP TABLE IF EXISTS `Contact_tbl`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Contact_tbl` (
  `SrcUserId` int(11) NOT NULL,
  `DestUserId` int(11) NOT NULL,
  `Timestamp` datetime NOT NULL,
  PRIMARY KEY (`SrcUserId`,`DestUserId`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Contact_tbl`
--

LOCK TABLES `Contact_tbl` WRITE;
/*!40000 ALTER TABLE `Contact_tbl` DISABLE KEYS */;
INSERT INTO `Contact_tbl` VALUES (1,2,'2010-02-01 21:38:17'),(2,1,'2010-02-01 21:40:26'),(1,3,'2010-02-05 09:56:55'),(3,1,'2010-02-05 10:00:25'),(5,1,'2010-02-22 23:31:59'),(1,5,'2010-02-22 23:32:06'),(2,5,'2010-05-16 23:28:30');
/*!40000 ALTER TABLE `Contact_tbl` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Event_tbl`
--

DROP TABLE IF EXISTS `Event_tbl`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Event_tbl` (
  `EventId` int(11) NOT NULL AUTO_INCREMENT,
  `EventTypeId` int(11) NOT NULL,
  `TextData` text NOT NULL,
  PRIMARY KEY (`EventId`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Event_tbl`
--

LOCK TABLES `Event_tbl` WRITE;
/*!40000 ALTER TABLE `Event_tbl` DISABLE KEYS */;
/*!40000 ALTER TABLE `Event_tbl` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Location_tbl`
--

DROP TABLE IF EXISTS `Location_tbl`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Location_tbl` (
  `LocationId` int(11) NOT NULL AUTO_INCREMENT,
  `UserId` int(11) NOT NULL,
  `Address` char(15) NOT NULL,
  `Port` int(11) NOT NULL,
  `Timestamp` datetime NOT NULL,
  `Current` tinyint(1) NOT NULL,
  PRIMARY KEY (`LocationId`)
) ENGINE=MyISAM AUTO_INCREMENT=550 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Location_tbl`
--

LOCK TABLES `Location_tbl` WRITE;
/*!40000 ALTER TABLE `Location_tbl` DISABLE KEYS */;
INSERT INTO `Location_tbl` VALUES (246,1,'192.168.1.41',1337,'2010-05-16 22:21:17',1),(247,1,'192.168.1.41',1337,'2010-05-16 22:23:13',1),(248,1,'192.168.1.41',1337,'2010-05-16 22:23:15',1),(249,1,'192.168.2.15',8080,'2010-07-11 16:02:28',1),(250,1,'74.72.196.235',80,'2010-07-11 16:02:28',1),(251,1,'192.168.2.15',8080,'2010-07-11 16:11:12',1),(252,1,'74.72.196.235',80,'2010-07-11 16:11:12',1),(253,1,'192.168.2.15',8080,'2010-07-11 16:25:08',1),(254,1,'74.72.196.235',80,'2010-07-11 16:25:08',1),(255,1,'192.168.2.15',7918,'2010-07-11 16:33:07',1),(256,1,'74.72.196.235',80,'2010-07-11 16:33:07',1),(257,1,'192.168.2.15',8080,'2010-07-11 17:45:33',1),(258,1,'74.72.196.235',80,'2010-07-11 17:45:33',1),(259,1,'192.168.2.15',8080,'2010-07-11 18:58:50',1),(260,1,'192.168.2.15',8080,'2010-07-14 07:24:33',1),(261,1,'74.72.196.235',80,'2010-07-14 07:24:33',1),(262,1,'192.168.2.15',8080,'2010-07-14 07:29:02',1),(263,1,'74.72.196.235',80,'2010-07-14 07:29:03',1),(264,1,'192.168.2.15',8080,'2010-07-14 07:56:02',1),(265,1,'74.72.196.235',80,'2010-07-14 07:56:02',1),(266,1,'192.168.2.15',8080,'2010-07-14 07:58:28',1),(267,1,'74.72.196.235',80,'2010-07-14 07:58:28',1),(268,1,'192.168.2.15',8080,'2010-07-14 08:02:49',1),(269,1,'74.72.196.235',80,'2010-07-14 08:02:49',1),(270,1,'192.168.2.15',8080,'2010-07-14 08:06:11',1),(271,1,'74.72.196.235',80,'2010-07-14 08:06:11',1),(272,1,'192.168.2.15',8080,'2010-07-15 00:04:10',1),(273,1,'74.72.196.235',80,'2010-07-15 00:04:10',1),(274,1,'192.168.2.15',8080,'2010-07-30 04:30:57',1),(275,1,'74.72.196.235',80,'2010-07-30 04:30:57',1),(276,1,'192.168.2.15',8080,'2010-07-30 06:19:09',1),(277,1,'74.72.196.235',80,'2010-07-30 06:19:09',1),(278,1,'192.168.2.15',8080,'2010-07-30 06:40:51',1),(279,1,'74.72.196.235',80,'2010-07-30 06:40:51',1),(280,1,'192.168.2.15',8080,'2010-07-30 07:28:06',1),(281,1,'74.72.196.235',80,'2010-07-30 07:28:06',1),(282,1,'192.168.2.15',8080,'2010-07-30 07:29:14',1),(283,1,'74.72.196.235',80,'2010-07-30 07:29:14',1),(284,1,'192.168.2.15',8080,'2010-07-30 07:30:58',1),(285,1,'74.72.196.235',80,'2010-07-30 07:30:58',1),(286,1,'192.168.2.15',8080,'2010-07-31 16:45:21',1),(287,1,'74.72.196.235',80,'2010-07-31 16:45:21',1),(288,1,'192.168.2.15',8080,'2010-07-31 16:45:52',1),(289,1,'74.72.196.235',80,'2010-07-31 16:45:52',1),(290,1,'192.168.2.15',8080,'2010-07-31 16:58:45',1),(291,1,'74.72.196.235',80,'2010-07-31 16:58:45',1),(292,1,'192.168.2.15',8080,'2010-07-31 17:01:37',1),(293,1,'74.72.196.235',80,'2010-07-31 17:01:37',1),(294,1,'192.168.2.15',8080,'2010-07-31 17:05:51',1),(295,1,'74.72.196.235',80,'2010-07-31 17:05:51',1),(296,1,'192.168.2.15',8080,'2010-07-31 17:07:52',1),(297,1,'74.72.196.235',80,'2010-07-31 17:07:52',1),(298,1,'192.168.2.15',8080,'2010-07-31 17:10:38',1),(299,1,'74.72.196.235',80,'2010-07-31 17:10:38',1),(300,1,'192.168.2.15',8080,'2010-07-31 17:21:34',1),(301,1,'74.72.196.235',80,'2010-07-31 17:21:34',1),(302,1,'192.168.2.15',3243,'2010-07-31 17:28:46',1),(303,1,'74.72.196.235',80,'2010-07-31 17:28:46',1),(304,1,'192.168.2.15',8080,'2010-07-31 17:33:09',1),(305,1,'192.168.2.15',8080,'2010-07-31 17:42:34',1),(306,1,'74.72.196.235',80,'2010-07-31 17:42:34',1),(307,1,'192.168.2.15',8080,'2010-07-31 17:52:12',1),(308,1,'74.72.196.235',80,'2010-07-31 17:52:12',1),(309,1,'192.168.2.15',8080,'2010-07-31 17:57:48',1),(310,1,'74.72.196.235',80,'2010-07-31 17:57:48',1),(311,1,'192.168.2.15',8080,'2010-07-31 18:05:12',1),(312,1,'74.72.196.235',80,'2010-07-31 18:05:12',1),(313,1,'192.168.2.15',8080,'2010-07-31 18:10:37',1),(314,1,'74.72.196.235',80,'2010-07-31 18:10:37',1),(315,1,'192.168.2.15',8080,'2010-07-31 18:15:23',1),(316,1,'74.72.196.235',80,'2010-07-31 18:15:23',1),(317,1,'192.168.2.15',8080,'2010-07-31 18:19:25',1),(318,1,'74.72.196.235',80,'2010-07-31 18:19:25',1),(319,1,'192.168.2.15',47528,'2010-07-31 18:21:54',1),(320,1,'74.72.196.235',80,'2010-07-31 18:21:54',1),(321,1,'192.168.2.15',8080,'2010-07-31 18:28:01',1),(322,1,'74.72.196.235',80,'2010-07-31 18:28:01',1),(323,1,'192.168.2.15',8080,'2010-07-31 18:29:38',1),(324,1,'74.72.196.235',80,'2010-07-31 18:29:38',1),(325,1,'192.168.2.15',60732,'2010-07-31 18:32:27',1),(326,1,'74.72.196.235',80,'2010-07-31 18:32:27',1),(327,1,'192.168.2.15',55878,'2010-07-31 18:35:07',1),(328,1,'74.72.196.235',80,'2010-07-31 18:35:07',1),(329,1,'192.168.2.15',8080,'2010-07-31 18:48:16',1),(330,1,'74.72.196.235',80,'2010-07-31 18:48:16',1),(331,1,'192.168.2.15',8080,'2010-07-31 19:20:45',1),(332,1,'74.72.196.235',80,'2010-07-31 19:20:45',1),(333,1,'192.168.2.15',33190,'2010-07-31 19:23:20',1),(334,1,'74.72.196.235',80,'2010-07-31 19:23:20',1),(335,1,'192.168.2.15',61798,'2010-07-31 19:23:40',1),(336,1,'74.72.196.235',80,'2010-07-31 19:23:40',1),(337,1,'192.168.2.15',8080,'2010-07-31 19:23:59',1),(338,1,'74.72.196.235',80,'2010-07-31 19:23:59',1),(339,1,'192.168.2.15',8080,'2010-07-31 19:30:33',1),(340,1,'74.72.196.235',80,'2010-07-31 19:30:33',1),(341,1,'192.168.2.15',8080,'2010-07-31 19:40:41',1),(342,1,'74.72.196.235',80,'2010-07-31 19:40:41',1),(343,1,'192.168.2.15',8080,'2010-07-31 19:41:38',1),(344,1,'74.72.196.235',80,'2010-07-31 19:41:38',1),(345,1,'192.168.2.15',49988,'2010-07-31 19:44:17',1),(346,1,'74.72.196.235',80,'2010-07-31 19:44:17',1),(347,1,'192.168.2.15',8080,'2010-07-31 20:02:38',1),(348,1,'74.72.196.235',80,'2010-07-31 20:02:38',1),(349,1,'192.168.2.15',8080,'2010-07-31 20:06:30',1),(350,1,'192.168.2.15',8080,'2010-07-31 20:09:30',1),(351,1,'74.72.196.235',80,'2010-07-31 20:09:30',1),(352,1,'192.168.2.15',8080,'2010-07-31 20:11:44',1),(353,1,'74.72.196.235',80,'2010-07-31 20:11:44',1),(354,1,'192.168.2.15',8080,'2010-07-31 20:30:28',1),(355,1,'74.72.196.235',80,'2010-07-31 20:30:28',1),(356,1,'192.168.2.15',8080,'2010-07-31 20:33:08',1),(357,1,'74.72.196.235',80,'2010-07-31 20:33:08',1),(358,1,'192.168.2.15',8080,'2010-07-31 20:44:35',1),(359,1,'74.72.196.235',80,'2010-07-31 20:44:35',1),(360,1,'192.168.2.15',33933,'2010-07-31 20:45:48',1),(361,1,'74.72.196.235',80,'2010-07-31 20:45:48',1),(362,1,'192.168.2.15',8080,'2010-07-31 20:51:42',1),(363,1,'74.72.196.235',80,'2010-07-31 20:51:42',1),(364,1,'192.168.2.15',7334,'2010-07-31 20:54:46',1),(365,1,'74.72.196.235',80,'2010-07-31 20:54:46',1),(366,1,'192.168.2.15',8080,'2010-07-31 21:12:31',1),(367,1,'74.72.196.235',80,'2010-07-31 21:12:31',1),(368,1,'192.168.2.15',8080,'2010-07-31 22:04:14',1),(369,1,'74.72.196.235',80,'2010-07-31 22:04:14',1),(370,1,'192.168.2.15',8080,'2010-07-31 22:04:29',1),(371,1,'74.72.196.235',80,'2010-07-31 22:04:29',1),(372,1,'192.168.2.15',8080,'2010-07-31 22:06:12',1),(373,1,'74.72.196.235',80,'2010-07-31 22:06:12',1),(374,1,'192.168.2.15',8080,'2010-07-31 22:12:37',1),(375,1,'74.72.196.235',80,'2010-07-31 22:12:37',1),(376,1,'192.168.2.15',8080,'2010-07-31 22:15:21',1),(377,1,'74.72.196.235',80,'2010-07-31 22:15:21',1),(378,1,'192.168.2.15',8080,'2010-07-31 22:45:48',1),(379,1,'74.72.196.235',80,'2010-07-31 22:45:48',1),(380,1,'192.168.2.15',8080,'2010-07-31 22:49:10',1),(381,1,'74.72.196.235',80,'2010-07-31 22:49:10',1),(382,1,'192.168.2.15',8080,'2010-07-31 23:27:15',1),(383,1,'192.168.2.15',51400,'2010-07-31 23:30:03',1),(384,1,'74.72.196.235',80,'2010-07-31 23:30:03',1),(385,1,'192.168.2.15',38926,'2010-07-31 23:41:04',1),(386,1,'74.72.196.235',80,'2010-07-31 23:41:04',1),(387,1,'192.168.2.15',23250,'2010-07-31 23:43:21',1),(388,1,'74.72.196.235',80,'2010-07-31 23:43:21',1),(389,1,'192.168.2.15',8080,'2010-07-31 23:56:05',1),(390,1,'74.72.196.235',80,'2010-07-31 23:56:05',1),(391,1,'192.168.2.15',8080,'2010-07-31 23:57:23',1),(392,1,'74.72.196.235',80,'2010-07-31 23:57:23',1),(393,1,'192.168.2.15',8080,'2010-07-31 23:59:26',1),(394,1,'74.72.196.235',80,'2010-07-31 23:59:26',1),(395,1,'192.168.2.15',22731,'2010-08-01 00:05:38',1),(396,1,'74.72.196.235',80,'2010-08-01 00:05:38',1),(397,1,'192.168.2.15',8080,'2010-08-01 00:10:52',1),(398,1,'74.72.196.235',80,'2010-08-01 00:10:53',1),(399,1,'192.168.2.15',31358,'2010-08-01 00:14:57',1),(400,1,'74.72.196.235',80,'2010-08-01 00:14:57',1),(401,1,'192.168.2.15',18794,'2010-08-01 00:43:36',1),(402,1,'74.72.196.235',80,'2010-08-01 00:43:36',1),(403,1,'192.168.2.15',53518,'2010-08-01 00:45:29',1),(404,1,'74.72.196.235',80,'2010-08-01 00:45:29',1),(405,1,'192.168.2.15',26988,'2010-08-01 00:45:59',1),(406,1,'74.72.196.235',80,'2010-08-01 00:45:59',1),(407,1,'192.168.2.15',8080,'2010-08-01 00:47:57',1),(408,1,'74.72.196.235',80,'2010-08-01 00:47:57',1),(409,1,'192.168.2.15',8080,'2010-08-01 00:55:52',1),(410,1,'74.72.196.235',80,'2010-08-01 00:55:52',1),(411,1,'192.168.2.15',8080,'2010-08-01 02:11:30',1),(412,1,'74.72.196.235',80,'2010-08-01 02:11:30',1),(413,1,'192.168.2.15',8080,'2010-08-01 02:21:17',1),(414,1,'74.72.196.235',80,'2010-08-01 02:21:17',1),(415,1,'192.168.2.15',8080,'2010-08-01 02:31:25',1),(416,1,'74.72.196.235',80,'2010-08-01 02:31:25',1),(417,1,'192.168.2.15',8080,'2010-08-01 03:10:40',1),(418,1,'74.72.196.235',80,'2010-08-01 03:10:40',1),(419,1,'192.168.2.15',8080,'2010-08-01 04:45:34',1),(420,1,'74.72.196.235',80,'2010-08-01 04:45:34',1),(421,1,'192.168.2.15',8080,'2010-08-01 05:00:42',1),(422,1,'74.72.196.235',80,'2010-08-01 05:00:42',1),(423,1,'192.168.2.15',8080,'2010-08-01 05:03:24',1),(424,1,'74.72.196.235',80,'2010-08-01 05:03:24',1),(425,1,'192.168.2.15',8080,'2010-08-01 05:16:26',1),(426,1,'74.72.196.235',80,'2010-08-01 05:16:26',1),(427,1,'192.168.2.15',8080,'2010-08-01 05:17:18',1),(428,1,'74.72.196.235',80,'2010-08-01 05:17:18',1),(429,1,'192.168.2.15',8080,'2010-08-01 05:51:54',1),(430,1,'74.72.196.235',80,'2010-08-01 05:51:54',1),(431,1,'192.168.2.15',8080,'2010-08-01 06:24:48',1),(432,1,'74.72.196.235',80,'2010-08-01 06:24:48',1),(433,2,'192.168.2.15',40730,'2010-08-01 06:27:19',1),(434,2,'74.72.196.235',80,'2010-08-01 06:27:19',1),(435,1,'192.168.2.15',8080,'2010-08-01 07:51:17',1),(436,1,'74.72.196.235',80,'2010-08-01 07:51:17',1),(437,1,'192.168.2.15',8080,'2010-08-01 09:32:34',1),(438,1,'74.72.196.235',80,'2010-08-01 09:32:34',1),(439,1,'192.168.2.15',8080,'2010-08-01 09:35:07',1),(440,1,'74.72.196.235',80,'2010-08-01 09:35:07',1),(441,1,'192.168.2.15',8080,'2010-08-01 09:42:35',1),(442,1,'74.72.196.235',80,'2010-08-01 09:42:35',1),(443,1,'192.168.2.15',8080,'2010-08-01 09:43:56',1),(444,1,'74.72.196.235',80,'2010-08-01 09:43:56',1),(445,1,'192.168.2.15',8080,'2010-08-01 10:01:38',1),(446,1,'74.72.196.235',80,'2010-08-01 10:01:38',1),(447,1,'192.168.2.15',8080,'2010-08-01 11:04:35',1),(448,1,'74.72.196.235',80,'2010-08-01 11:04:35',1),(449,1,'192.168.2.15',8080,'2010-08-01 12:51:42',1),(450,1,'74.72.196.235',80,'2010-08-01 12:51:42',1),(451,1,'192.168.2.15',8080,'2010-08-01 13:38:03',1),(452,1,'74.72.196.235',80,'2010-08-01 13:38:03',1),(453,1,'192.168.2.15',8080,'2010-08-01 13:56:56',1),(454,1,'74.72.196.235',80,'2010-08-01 13:56:57',1),(455,1,'192.168.2.15',8080,'2010-08-01 15:49:59',1),(456,1,'74.72.196.235',80,'2010-08-01 15:49:59',1),(457,1,'192.168.2.15',8080,'2010-08-03 04:31:20',1),(458,1,'74.72.196.235',80,'2010-08-03 04:31:21',1),(459,2,'192.168.2.15',23434,'2010-08-03 04:36:21',1),(460,2,'74.72.196.235',80,'2010-08-03 04:36:21',1),(461,1,'192.168.2.15',8080,'2010-08-05 03:39:51',1),(462,1,'74.72.196.235',80,'2010-08-05 03:39:52',1),(463,1,'192.168.2.15',8080,'2010-08-05 03:55:46',1),(464,1,'74.72.196.235',80,'2010-08-05 03:55:46',1),(465,1,'192.168.2.15',8080,'2010-08-05 07:03:24',1),(466,1,'74.72.196.235',80,'2010-08-05 07:03:24',1),(467,1,'192.168.2.15',8080,'2010-08-08 21:02:51',1),(468,1,'74.72.196.235',80,'2010-08-08 21:02:51',1),(469,1,'192.168.2.15',8080,'2010-08-08 22:38:06',1),(470,1,'74.72.196.235',80,'2010-08-08 22:38:06',1),(471,1,'192.168.2.15',8080,'2010-08-08 22:45:24',1),(472,1,'74.72.196.235',80,'2010-08-08 22:45:24',1),(473,1,'192.168.2.15',8080,'2010-08-09 01:56:09',1),(474,1,'74.72.196.235',80,'2010-08-09 01:56:09',1),(475,1,'192.168.2.15',8080,'2010-08-09 20:44:23',1),(476,1,'74.72.196.235',80,'2010-08-09 20:44:23',1),(477,1,'192.168.2.15',8080,'2010-08-10 17:49:27',1),(478,1,'74.72.196.235',80,'2010-08-10 17:49:27',1),(479,1,'192.168.2.15',8080,'2010-08-13 20:34:35',1),(480,1,'74.72.196.235',80,'2010-08-13 20:34:35',1),(481,1,'192.168.2.15',8080,'2010-08-16 01:11:32',1),(482,1,'74.72.196.235',80,'2010-08-16 01:11:32',1),(483,1,'192.168.2.15',8080,'2010-08-16 01:13:28',1),(484,1,'74.72.196.235',80,'2010-08-16 01:13:28',1),(485,1,'192.168.2.15',8080,'2010-08-16 02:02:52',1),(486,1,'74.72.196.235',80,'2010-08-16 02:02:52',1),(487,1,'192.168.2.15',8080,'2010-08-16 02:17:09',1),(488,1,'74.72.196.235',80,'2010-08-16 02:17:09',1),(489,1,'192.168.2.15',8080,'2010-08-16 02:29:51',1),(490,1,'74.72.196.235',80,'2010-08-16 02:29:51',1),(491,1,'192.168.2.15',8080,'2010-08-16 02:33:40',1),(492,1,'74.72.196.235',80,'2010-08-16 02:33:40',1),(493,1,'192.168.2.15',8080,'2010-08-16 02:39:43',1),(494,1,'74.72.196.235',80,'2010-08-16 02:39:43',1),(495,1,'192.168.2.15',8080,'2010-08-16 02:46:41',1),(496,1,'74.72.196.235',80,'2010-08-16 02:46:41',1),(497,1,'192.168.2.15',8080,'2010-08-16 02:49:27',1),(498,1,'74.72.196.235',80,'2010-08-16 02:49:27',1),(499,1,'192.168.2.15',8080,'2010-08-16 03:26:33',1),(500,1,'74.72.196.235',80,'2010-08-16 03:26:33',1),(501,1,'192.168.2.15',8080,'2010-08-16 03:30:34',1),(502,1,'74.72.196.235',80,'2010-08-16 03:30:34',1),(503,1,'192.168.2.15',8080,'2010-08-16 03:33:07',1),(504,1,'74.72.196.235',80,'2010-08-16 03:33:07',1),(505,1,'192.168.2.15',8080,'2010-08-16 03:36:56',1),(506,1,'74.72.196.235',80,'2010-08-16 03:36:56',1),(507,1,'192.168.2.15',8080,'2010-08-16 04:10:52',1),(508,1,'74.72.196.235',80,'2010-08-16 04:10:52',1),(509,1,'192.168.2.15',8080,'2010-08-16 04:11:45',1),(510,1,'74.72.196.235',80,'2010-08-16 04:11:45',1),(511,1,'192.168.2.15',8080,'2010-08-16 08:49:26',1),(512,1,'74.72.196.235',80,'2010-08-16 08:49:26',1),(513,1,'192.168.2.15',8080,'2010-08-16 22:23:19',1),(514,1,'192.168.2.15',8080,'2010-08-17 00:31:08',1),(515,1,'192.168.2.15',8080,'2010-08-17 02:26:48',1),(516,1,'192.168.2.15',8080,'2010-08-17 02:34:08',1),(517,1,'192.168.2.15',8080,'2010-08-17 02:49:42',1),(518,1,'192.168.2.15',8080,'2010-08-17 03:13:10',1),(519,1,'192.168.2.15',8080,'2010-08-17 03:19:39',1),(520,1,'192.168.2.15',8080,'2010-08-17 08:12:39',1),(521,1,'192.168.1.142',37392,'2010-08-30 23:55:23',1),(522,1,'192.168.1.102',80,'2010-08-30 23:55:23',1),(523,1,'192.168.1.142',21305,'2010-08-31 00:57:53',1),(524,1,'192.168.1.102',80,'2010-08-31 00:57:53',1),(525,1,'192.168.1.142',2335,'2010-08-31 01:46:25',1),(526,1,'24.213.238.227',80,'2010-08-31 01:46:25',1),(527,1,'192.168.1.142',23043,'2010-09-03 19:49:49',1),(528,1,'192.168.1.142',55912,'2010-09-05 16:43:44',1),(529,1,'192.168.1.142',22050,'2010-09-05 18:19:48',1),(530,1,'24.213.238.227',8080,'2010-09-05 18:19:48',1),(531,1,'192.168.1.142',60560,'2010-09-05 18:52:54',1),(532,1,'24.213.238.227',36234,'2010-09-05 18:52:54',1),(533,1,'192.168.1.142',47551,'2010-09-05 19:00:53',1),(534,1,'192.168.1.142',29519,'2010-09-05 19:03:59',1),(535,1,'24.213.238.227',1892,'2010-09-05 19:03:59',1),(536,1,'192.168.1.142',60333,'2010-09-05 19:05:12',1),(537,1,'24.213.238.227',29316,'2010-09-05 19:05:12',1),(538,1,'192.168.1.142',16378,'2010-09-05 19:05:33',1),(539,1,'24.213.238.227',22250,'2010-09-05 19:05:33',1),(540,1,'192.168.1.142',28903,'2010-09-05 19:06:39',1),(541,1,'24.213.238.227',41044,'2010-09-05 19:06:39',1),(542,1,'192.168.1.142',61928,'2010-09-05 19:13:58',1),(543,1,'192.168.1.142',58605,'2010-09-05 21:19:28',1),(544,1,'192.168.1.142',54594,'2010-09-06 23:15:44',1),(545,1,'192.168.1.142',31008,'2010-09-07 01:44:04',1),(546,1,'192.168.1.142',18028,'2010-09-08 15:09:14',1),(547,1,'192.168.1.142',8080,'2010-09-17 15:38:32',1),(548,1,'24.213.238.227',25517,'2010-09-17 15:38:32',1),(549,1,'192.168.1.142',23577,'2010-09-24 12:48:41',1);
/*!40000 ALTER TABLE `Location_tbl` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Proxy_tbl`
--

DROP TABLE IF EXISTS `Proxy_tbl`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Proxy_tbl` (
  `LocationId` int(11) NOT NULL,
  `Timestamp` datetime NOT NULL,
  KEY `Proxy_Time_IX` (`Timestamp`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Proxy_tbl`
--

LOCK TABLES `Proxy_tbl` WRITE;
/*!40000 ALTER TABLE `Proxy_tbl` DISABLE KEYS */;
/*!40000 ALTER TABLE `Proxy_tbl` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `User_tbl`
--

DROP TABLE IF EXISTS `User_tbl`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `User_tbl` (
  `UserId` int(11) NOT NULL AUTO_INCREMENT,
  `Username` varchar(32) NOT NULL,
  `Password` varchar(255) NOT NULL,
  `Email` varchar(255) NOT NULL,
  `ConfirmedFlag` tinyint(1) NOT NULL,
  `RegisteredTime` datetime NOT NULL,
  `ConfirmationToken` char(64) NOT NULL,
  PRIMARY KEY (`UserId`),
  UNIQUE KEY `Username` (`Username`),
  UNIQUE KEY `ConfirmationToken` (`ConfirmationToken`)
) ENGINE=MyISAM AUTO_INCREMENT=6 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `User_tbl`
--

LOCK TABLES `User_tbl` WRITE;
/*!40000 ALTER TABLE `User_tbl` DISABLE KEYS */;
INSERT INTO `User_tbl` VALUES (1,'bowenl2','teppet','bowenl2@cs.rpi.edu',1,'2010-02-01 21:33:37','aaaaaaaaa'),(2,'snakie','teppet','jarrett.f@gmail.com',1,'2010-02-01 21:33:52','bbbbbbbbb'),(5,'saw','teppet','steve.wacks@gmail.com',1,'2010-02-22 23:31:25','1337');
/*!40000 ALTER TABLE `User_tbl` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping routines for database 'fractus'
--
/*!50003 DROP PROCEDURE IF EXISTS `AddContact_prc` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = '' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50020 DEFINER=`root`@`localhost`*/ /*!50003 PROCEDURE `AddContact_prc`(SrcUserId INT, DestUserId INT)
BEGIN
INSERT INTO Contact_tbl (SrcUserId, DestUserId, Timestamp) VALUES (SrcUserId, DestUserId, NOW());
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `AuthenticateUser_prc` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = '' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50020 DEFINER=`root`@`localhost`*/ /*!50003 PROCEDURE `AuthenticateUser_prc`(Username VARCHAR(255), Password VARCHAR(255))
BEGIN
SELECT UserId FROM User_tbl u WHERE
	u.Username = Username
	AND u.Password = Password;
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `CheckAccount_prc` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = '' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50020 DEFINER=`root`@`localhost`*/ /*!50003 PROCEDURE `CheckAccount_prc`(Username VARCHAR(255))
BEGIN

SELECT SIGN(COUNT(*)) FROM User_tbl u WHERE u.Username = Username;

END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `CountUsers_prc` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = '' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50020 DEFINER=`root`@`localhost`*/ /*!50003 PROCEDURE `CountUsers_prc`()
BEGIN
SELECT COUNT(*) FROM User_tbl u WHERE u.ConfirmedFlag=1;
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `CreateAccount_prc` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = '' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50020 DEFINER=`root`@`localhost`*/ /*!50003 PROCEDURE `CreateAccount_prc`(Username VARCHAR(32), Email VARCHAR(255), Password VARCHAR(255),
	ConfirmationToken VARCHAR(64))
BEGIN
INSERT INTO User_tbl
(Username, Email, Password, ConfirmedFlag, ConfirmationToken, RegisteredTime)
VALUES
(Username, Email, Password, 0, ConfirmationToken, NOW());
SELECT ROW_COUNT() As Success;
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `DeleteContact_prc` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = '' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50020 DEFINER=`root`@`localhost`*/ /*!50003 PROCEDURE `DeleteContact_prc`(SrcUserId INT, DestUserId INT)
BEGIN
DELETE FROM Contact_tbl
WHERE (Contact_tbl.SrcUserId = SrcUserId AND Contact_tbl.DestUserId = DestUserId);
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `FindNonreciprocalContacts_prc` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = '' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50020 DEFINER=`root`@`localhost`*/ /*!50003 PROCEDURE `FindNonreciprocalContacts_prc`(UserId INT)
BEGIN
SELECT u.Username, u.UserId
FROM Contact_tbl c
INNER JOIN User_tbl u ON c.SrcUserId = u.UserId
WHERE c.DestUserId = UserId
AND (SELECT SIGN(COUNT(*)) FROM Contact_tbl cs
	WHERE cs.SrcUserId=UserId
	AND   cs.DestUserId=u.UserId) = 0;
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `GetUserContacts_prc` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = '' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50020 DEFINER=`root`@`localhost`*/ /*!50003 PROCEDURE `GetUserContacts_prc`(UserId INT)
BEGIN
SELECT u.Username, u.UserId
FROM Contact_tbl c
INNER JOIN User_tbl u ON c.DestUserId = u.UserId
WHERE c.SrcUserId = UserId

AND (SELECT SIGN(COUNT(*)) FROM Contact_tbl cd
	WHERE cd.DestUserId=UserId
	AND cd.SrcUserId=u.UserId) = 1;

END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `IdentifyTokenOwner_prc` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = '' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50020 DEFINER=`root`@`localhost`*/ /*!50003 PROCEDURE `IdentifyTokenOwner_prc`(Token TEXT, TargetId INT)
BEGIN
SELECT uo.Username FROM Token_tbl t
INNER JOIN User_tbl u on t.TargetId = u.UserId
INNER JOIN User_tbl uo ON t.OwnerId = uo.UserId 
WHERE t.TargetId = TargetId
	AND t.Token = Token
	AND t.Used IS NULL;
-- UPDATE Token_tbl t SET t.Used = NOW()
--	WHERE t.TargetId = TargetId
--	AND t.Token = Token
--	AND t.Used IS NULL;
end */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `IdentifyUser_prc` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = '' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50020 DEFINER=`root`@`localhost`*/ /*!50003 PROCEDURE `IdentifyUser_prc`(Username VARCHAR(255))
BEGIN
SELECT u.UserId FROM User_tbl u WHERE u.Username = Username;
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `LocateUser_prc` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = '' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50020 DEFINER=`root`@`localhost`*/ /*!50003 PROCEDURE `LocateUser_prc`(UserId INT)
BEGIN

SET @lastConnectionId := (SELECT MAX(cl.ConnectionId)
FROM Connection_tbl cl
WHERE cl.UserId = UserId
AND cl.Timestamp > date_add(NOW(), INTERVAL -15 MINUTE)
);

SELECT l.Address, l.Port, r.IsProxy, c.Timestamp
FROM Route_tbl r
INNER JOIN Location_tbl l ON r.LocationId = l.LocationId 
INNER JOIN Connection_tbl c ON c.ConnectionId = r.ConnectionId
WHERE c.ConnectionId =
	case when @lastConnectionId is null then 0 else @lastConnectionId end;

END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `RecordToken_prc` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = '' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50020 DEFINER=`root`@`localhost`*/ /*!50003 PROCEDURE `RecordToken_prc`(OwnerId INT, TargetId INT, Token TEXT)
BEGIN
INSERT INTO Token_tbl (Token, OwnerId, TargetId, Created, Used)
VALUES (Token, OwnerId, TargetId, NOW(), NULL);

END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `RecordUserLocation_prc` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = '' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50020 DEFINER=`root`@`localhost`*/ /*!50003 PROCEDURE `RecordUserLocation_prc`(UserId INT, Address CHAR(15), Port INT)
BEGIN
INSERT INTO Location_tbl (UserId, Address, Port, Timestamp, Current)
VALUES (UserId, Address, Port, NOW(), 1);
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `RegisterConnection_prc` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = '' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50020 DEFINER=`root`@`localhost`*/ /*!50003 PROCEDURE `RegisterConnection_prc`(UserId INT)
BEGIN

INSERT INTO Connection_tbl (UserId, Timestamp) VALUES (UserId, NOW());
SELECT LAST_INSERT_ID() As ConnectionId;

END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `RegisterProxy_prc` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = '' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50020 DEFINER=`root`@`localhost`*/ /*!50003 PROCEDURE `RegisterProxy_prc`(Address CHAR(15), Port INT)
BEGIN

INSERT IGNORE INTO Location_tbl (Address, Port) VALUES (Address, Port);
SET @lid := (SELECT LocationId FROM Location_tbl l
WHERE l.Address = Address
AND l.Port = Port);
INSERT INTO Proxy_tbl (LocationId, Timestamp)
VALUES (@lid, NOW());

END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `SelectProxy_prc` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = '' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50020 DEFINER=`root`@`localhost`*/ /*!50003 PROCEDURE `SelectProxy_prc`()
BEGIN
SELECT l.Address, l.Port
FROM Proxy_tbl p
INNER JOIN Location_tbl l on p.LocationId = l.LocationId
WHERE p.Timestamp > DATE_ADD(NOW(), interval -5 minute)
ORDER BY RAND()
LIMIT 1;

END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `SendContactData_prc` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = '' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50020 DEFINER=`root`@`localhost`*/ /*!50003 PROCEDURE `SendContactData_prc`(UserId INT)
BEGIN

select distinct u.Username, l.Address, l.Port from User_tbl u
inner join Location_tbl l on l.UserId = u.UserId
inner join Contact_tbl c
on u.UserId = c.DestUserId
inner join Contact_tbl cd on cd.DestUserId = c.SrcUserId
where c.SrcUserId = UserId
and cd.SrcUserId = u.UserId;


END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `VerifyUser_prc` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = '' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50020 DEFINER=`root`@`localhost`*/ /*!50003 PROCEDURE `VerifyUser_prc`(Username VARCHAR(255))
BEGIN
SELECT UserId FROM User_tbl u WHERE u.Username = Username;
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2011-05-04 22:54:59