delimiter $$

CREATE DATABASE `fractus` /*!40100 DEFAULT CHARACTER SET latin1 */$$

delimiter $$

CREATE TABLE `Connection_tbl` (
  `ConnectionId` int(11) NOT NULL AUTO_INCREMENT,
  `UserId` int(11) NOT NULL,
  `Timestamp` datetime NOT NULL,
  PRIMARY KEY (`ConnectionId`)
) ENGINE=MyISAM AUTO_INCREMENT=1490 DEFAULT CHARSET=latin1$$

delimiter $$

CREATE TABLE `Contact_tbl` (
  `SrcUserId` int(11) NOT NULL,
  `DestUserId` int(11) NOT NULL,
  `Timestamp` datetime NOT NULL,
  PRIMARY KEY (`SrcUserId`,`DestUserId`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1$$

delimiter $$

CREATE TABLE `Event_tbl` (
  `EventId` int(11) NOT NULL AUTO_INCREMENT,
  `EventTypeId` int(11) NOT NULL,
  `TextData` text NOT NULL,
  PRIMARY KEY (`EventId`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1$$

delimiter $$

CREATE TABLE `Location_tbl` (
  `LocationId` int(11) NOT NULL AUTO_INCREMENT,
  `UserId` int(11) NOT NULL,
  `Address` char(15) NOT NULL,
  `Port` int(11) NOT NULL,
  `Timestamp` datetime NOT NULL,
  `Current` tinyint(1) NOT NULL,
  PRIMARY KEY (`LocationId`)
) ENGINE=MyISAM AUTO_INCREMENT=550 DEFAULT CHARSET=latin1$$

delimiter $$

CREATE TABLE `Proxy_tbl` (
  `LocationId` int(11) NOT NULL,
  `Timestamp` datetime NOT NULL,
  KEY `Proxy_Time_IX` (`Timestamp`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1$$

delimiter $$

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
) ENGINE=MyISAM AUTO_INCREMENT=6 DEFAULT CHARSET=latin1$$



