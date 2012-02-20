-- MySQL Administrator dump 1.4
--
-- ------------------------------------------------------
-- Server version	5.1.41-3ubuntu12.6


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;


--
-- Create schema mahout
--

CREATE DATABASE IF NOT EXISTS mahout;
USE mahout;

--
-- Definition of table `mahout`.`items`
--

DROP TABLE IF EXISTS `mahout`.`items`;
CREATE TABLE  `mahout`.`items` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `identifier` tinytext NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=9 DEFAULT CHARSET=latin1;

--
-- Definition of table `mahout`.`preference_table`
--

DROP TABLE IF EXISTS `mahout`.`preference_table`;
CREATE TABLE  `mahout`.`preference_table` (
  `user_id` bigint(20) NOT NULL,
  `item_id` bigint(20) NOT NULL,
  `preference` float NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

--
-- Definition of table `mahout`.`users`
--

DROP TABLE IF EXISTS `mahout`.`users`;
CREATE TABLE  `mahout`.`users` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `identifier` tinytext NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=7 DEFAULT CHARSET=latin1;

