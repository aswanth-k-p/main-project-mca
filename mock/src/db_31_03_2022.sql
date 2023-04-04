/*
SQLyog Community v13.1.5  (64 bit)
MySQL - 5.7.19 : Database - 2022_mock_interview
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`2022_mock_interview` /*!40100 DEFAULT CHARACTER SET latin1 */;

USE `2022_mock_interview`;

/*Table structure for table `candidate` */

DROP TABLE IF EXISTS `candidate`;

CREATE TABLE `candidate` (
  `cid` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) DEFAULT NULL,
  `email` varchar(50) DEFAULT NULL,
  `phone` varchar(50) DEFAULT NULL,
  `dob` varchar(50) DEFAULT NULL,
  `lid` int(11) DEFAULT NULL,
  PRIMARY KEY (`cid`)
) ENGINE=MyISAM AUTO_INCREMENT=2 DEFAULT CHARSET=latin1;

/*Data for the table `candidate` */

insert  into `candidate`(`cid`,`name`,`email`,`phone`,`dob`,`lid`) values 
(1,'Rohith K','rohith@gmail.com','9087654321','199-08-23',3);

/*Table structure for table `interviewer` */

DROP TABLE IF EXISTS `interviewer`;

CREATE TABLE `interviewer` (
  `interviewer_id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) DEFAULT NULL,
  `email` varchar(50) DEFAULT NULL,
  `phone` varchar(50) DEFAULT NULL,
  `about` varchar(200) DEFAULT NULL,
  `lid` int(11) DEFAULT NULL,
  PRIMARY KEY (`interviewer_id`)
) ENGINE=MyISAM AUTO_INCREMENT=2 DEFAULT CHARSET=latin1;

/*Data for the table `interviewer` */

insert  into `interviewer`(`interviewer_id`,`name`,`email`,`phone`,`about`,`lid`) values 
(1,'San International','saninter@gmail.com','9087654321','We are a group of innovative developers...',2);

/*Table structure for table `login` */

DROP TABLE IF EXISTS `login`;

CREATE TABLE `login` (
  `lid` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(50) DEFAULT NULL,
  `password` varchar(50) DEFAULT NULL,
  `type` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`lid`)
) ENGINE=MyISAM AUTO_INCREMENT=4 DEFAULT CHARSET=latin1;

/*Data for the table `login` */

insert  into `login`(`lid`,`username`,`password`,`type`) values 
(1,'admin','admin','admin'),
(2,'saninter@gmail.com','123rew','rejected'),
(3,'rohith@gmail.com','2233','candidate');

/*Table structure for table `questions` */

DROP TABLE IF EXISTS `questions`;

CREATE TABLE `questions` (
  `qn_id` int(11) NOT NULL AUTO_INCREMENT,
  `test_id` int(11) DEFAULT NULL,
  `question` varchar(200) DEFAULT NULL,
  `answer` varchar(500) DEFAULT NULL,
  PRIMARY KEY (`qn_id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

/*Data for the table `questions` */

/*Table structure for table `score_sub` */

DROP TABLE IF EXISTS `score_sub`;

CREATE TABLE `score_sub` (
  `sub_id` int(11) NOT NULL AUTO_INCREMENT,
  `scores_id` int(11) DEFAULT NULL,
  `qn_id` int(11) DEFAULT NULL,
  `emotion` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`sub_id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

/*Data for the table `score_sub` */

/*Table structure for table `scores_main` */

DROP TABLE IF EXISTS `scores_main`;

CREATE TABLE `scores_main` (
  `score_id` int(11) NOT NULL AUTO_INCREMENT,
  `test_id` int(11) DEFAULT NULL,
  `user_id` int(11) DEFAULT NULL,
  `date` varchar(50) DEFAULT NULL,
  `score` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`score_id`)
) ENGINE=MyISAM AUTO_INCREMENT=3 DEFAULT CHARSET=latin1;

/*Data for the table `scores_main` */

insert  into `scores_main`(`score_id`,`test_id`,`user_id`,`date`,`score`) values 
(1,1,3,'2022-02-28','20'),
(2,1,3,'2022-03-01','30');

/*Table structure for table `test` */

DROP TABLE IF EXISTS `test`;

CREATE TABLE `test` (
  `test_id` int(11) NOT NULL AUTO_INCREMENT,
  `test_name` varchar(100) DEFAULT NULL,
  `description` varchar(200) DEFAULT NULL,
  `interviewer_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`test_id`)
) ENGINE=MyISAM AUTO_INCREMENT=2 DEFAULT CHARSET=latin1;

/*Data for the table `test` */

insert  into `test`(`test_id`,`test_name`,`description`,`interviewer_id`) values 
(1,'T1','D1',2);

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
