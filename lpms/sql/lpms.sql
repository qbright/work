/*
Navicat MySQL Data Transfer

Source Server         : lpms
Source Server Version : 50514
Source Host           : localhost:3306
Source Database       : lpms

Target Server Type    : MYSQL
Target Server Version : 50514
File Encoding         : 65001

Date: 2013-06-03 08:50:13
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `server_machine`
-- ----------------------------
DROP TABLE IF EXISTS `server_machine`;
CREATE TABLE `server_machine` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `belong_to` bigint(20) NOT NULL,
  `connection_ip` varchar(255) DEFAULT NULL,
  `connection_port` varchar(255) DEFAULT NULL,
  `system` varchar(255) DEFAULT NULL,
  `last_login` datetime DEFAULT NULL,
  `machineName` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `belong_to` (`belong_to`),
  CONSTRAINT `server_machine_ibfk_1` FOREIGN KEY (`belong_to`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=36 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of server_machine
-- ----------------------------
INSERT INTO `server_machine` VALUES ('29', '66', '10.10.108.203', '8081', 'dfdfd', null, 'test2', '202cb962ac59075b964b07152d234b70');
INSERT INTO `server_machine` VALUES ('30', '66', '192.168.128.132', '8081', 'linux', null, 'test3', 'b08bc7e714b051062787ded63929922b');
INSERT INTO `server_machine` VALUES ('31', '66', '127.0.0.1', '8081', '34343', null, 'test4', '202cb962ac59075b964b07152d234b70');
INSERT INTO `server_machine` VALUES ('33', '66', '127.0.0.1', '8081', 'window', '2013-05-29 15:13:59', 'test', '202cb962ac59075b964b07152d234b70');
INSERT INTO `server_machine` VALUES ('34', '66', '192.168.128.132', '8081', 'linux', '2013-05-29 15:13:31', 'test5', '202cb962ac59075b964b07152d234b70');
INSERT INTO `server_machine` VALUES ('35', '68', '127.0.0.1', '8081', 'linux', null, 'qbright_test', '202cb962ac59075b964b07152d234b70');

-- ----------------------------
-- Table structure for `user`
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` bigint(10) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `create_date` date DEFAULT NULL,
  `status` bit(1) DEFAULT NULL,
  `root` bigint(20) DEFAULT NULL,
  `manager_num` int(11) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=69 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES ('66', 'user', '202cb962ac59075b964b07152d234b70', '2013-02-06', '', '0', '9', 'zhengqiguang@21cn.com');
INSERT INTO `user` VALUES ('67', 'admin', '202cb962ac59075b964b07152d234b70', '2013-02-06', '', '1', '0', 'zqbright@gmail.com');
INSERT INTO `user` VALUES ('68', 'qbright', '202cb962ac59075b964b07152d234b70', '2013-05-29', '', '0', '1', 'zqbright@gmail.com');
