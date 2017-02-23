/*
Navicat MySQL Data Transfer

Source Server         : mysql
Source Server Version : 50505
Source Host           : localhost:3306
Source Database       : fileshare

Target Server Type    : MYSQL
Target Server Version : 50505
File Encoding         : 65001

Date: 2017-02-23 13:03:34
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for myfile
-- ----------------------------
DROP TABLE IF EXISTS `myfile`;
CREATE TABLE `myfile` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) CHARACTER SET utf8 DEFAULT NULL,
  `type` int(11) DEFAULT NULL,
  `fileid` int(11) DEFAULT NULL,
  `createtime` varchar(255) DEFAULT NULL,
  `uid` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=latin1;

-- ----------------------------
-- Records of myfile
-- ----------------------------
INSERT INTO `myfile` VALUES ('1', 'sort1', '0', '0', '2017-2-22 10:10', '1');
INSERT INTO `myfile` VALUES ('2', 'file1', '1', '1', '2017-2-22 10:15', '2');
INSERT INTO `myfile` VALUES ('3', 'file2', '1', '2', '2017-2-22 10:20', '3');
INSERT INTO `myfile` VALUES ('4', 'file3', '1', '3', '2017-2-22 10:30', '3');
INSERT INTO `myfile` VALUES ('6', '分类2', '0', '0', '2017-2-22 22:18', '3');
INSERT INTO `myfile` VALUES ('7', '文件4', '1', '4', '2017-2-22 22:20', '3');
INSERT INTO `myfile` VALUES ('8', '分类2-2', '0', '0', '2017-2-23 9:45', '3');
INSERT INTO `myfile` VALUES ('10', '333', '0', '0', '2017-02-23 12:41', '3');
INSERT INTO `myfile` VALUES ('11', 'fen333', '0', '0', '2017-02-23 12:45', '1');
INSERT INTO `myfile` VALUES ('13', '333', '0', '0', '2017-02-23 12:49', '1');
INSERT INTO `myfile` VALUES ('14', '333-1', '0', '0', '2017-02-23 12:49', '2');
INSERT INTO `myfile` VALUES ('15', '中文分类4', '0', '0', '2017-02-23 12:54', '2');
INSERT INTO `myfile` VALUES ('16', '分类5', '0', '0', '2017-02-23 01:01', '3');
INSERT INTO `myfile` VALUES ('17', 'fenlei6', '0', '0', '2017-02-23 01:01', '1');
INSERT INTO `myfile` VALUES ('18', 'sort777', '0', '0', '2017-02-23 01:02', '1');

-- ----------------------------
-- Table structure for myfileconn
-- ----------------------------
DROP TABLE IF EXISTS `myfileconn`;
CREATE TABLE `myfileconn` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `fatherid` int(11) DEFAULT NULL,
  `childid` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=latin1;

-- ----------------------------
-- Records of myfileconn
-- ----------------------------
INSERT INTO `myfileconn` VALUES ('1', '0', '1');
INSERT INTO `myfileconn` VALUES ('2', '0', '2');
INSERT INTO `myfileconn` VALUES ('3', '1', '3');
INSERT INTO `myfileconn` VALUES ('4', '1', '4');
INSERT INTO `myfileconn` VALUES ('5', '0', '6');
INSERT INTO `myfileconn` VALUES ('6', '0', '7');
INSERT INTO `myfileconn` VALUES ('7', '6', '8');
INSERT INTO `myfileconn` VALUES ('8', '0', '13');
INSERT INTO `myfileconn` VALUES ('9', '13', '14');
INSERT INTO `myfileconn` VALUES ('10', '0', '15');
INSERT INTO `myfileconn` VALUES ('11', '0', '16');
INSERT INTO `myfileconn` VALUES ('12', '0', '17');
INSERT INTO `myfileconn` VALUES ('13', '0', '18');

-- ----------------------------
-- Table structure for myfiledata
-- ----------------------------
DROP TABLE IF EXISTS `myfiledata`;
CREATE TABLE `myfiledata` (
  `fileid` int(11) NOT NULL AUTO_INCREMENT,
  `filedata` mediumblob,
  PRIMARY KEY (`fileid`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=latin1;

-- ----------------------------
-- Records of myfiledata
-- ----------------------------
INSERT INTO `myfiledata` VALUES ('1', null);
INSERT INTO `myfiledata` VALUES ('2', null);
INSERT INTO `myfiledata` VALUES ('3', null);
INSERT INTO `myfiledata` VALUES ('4', null);

-- ----------------------------
-- Table structure for userinfo
-- ----------------------------
DROP TABLE IF EXISTS `userinfo`;
CREATE TABLE `userinfo` (
  `uid` int(11) NOT NULL AUTO_INCREMENT,
  `uname` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `access` int(11) unsigned zerofill NOT NULL DEFAULT '00000000000',
  PRIMARY KEY (`uid`),
  UNIQUE KEY `uname` (`uname`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of userinfo
-- ----------------------------
INSERT INTO `userinfo` VALUES ('1', 'jerome2', '827ccb0eea8a706c4c34a16891f84e7b', '00000000002');
INSERT INTO `userinfo` VALUES ('2', 'jerome1', '827ccb0eea8a706c4c34a16891f84e7b', '00000000001');
INSERT INTO `userinfo` VALUES ('3', 'jerome0', '827ccb0eea8a706c4c34a16891f84e7b', '00000000000');
