/*
Navicat MySQL Data Transfer

Source Server         : lpms
Source Server Version : 50514
Source Host           : localhost:3306
Source Database       : lpms

Target Server Type    : MYSQL
Target Server Version : 50514
File Encoding         : 65001

Date: 2013-04-01 20:49:20
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
  `last_login` date DEFAULT NULL,
  `machineName` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `belong_to` (`belong_to`),
  CONSTRAINT `server_machine_ibfk_1` FOREIGN KEY (`belong_to`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=28 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of server_machine
-- ----------------------------
INSERT INTO `server_machine` VALUES ('4', '66', '1323123123', '123', 'asdsdasde', null, 'machine', '202cb962ac59075b964b07152d234b70');
INSERT INTO `server_machine` VALUES ('5', '66', '1323123123', '123', 'asdsdasde', null, 'machine', '202cb962ac59075b964b07152d234b70');
INSERT INTO `server_machine` VALUES ('6', '66', '1323123123', '123', 'asdsdasde', null, 'machine', '202cb962ac59075b964b07152d234b70');
INSERT INTO `server_machine` VALUES ('7', '66', 'DSFASD', '12', '2323232', null, 'WWQ', '4035bbc636d3f53363b5a1209c3ffe1b');
INSERT INTO `server_machine` VALUES ('8', '66', '324234', '12', '123', null, '234', '202cb962ac59075b964b07152d234b70');
INSERT INTO `server_machine` VALUES ('9', '66', 'qwe', '12', 'wqeqwe', null, 'qwe', '76d80224611fc919a5d54f0ff9fba446');
INSERT INTO `server_machine` VALUES ('10', '66', '123', '12', 'wwqwe', null, 'qbright', '202cb962ac59075b964b07152d234b70');
INSERT INTO `server_machine` VALUES ('11', '66', '123', '12', 'wwqwe', null, 'qbright', '202cb962ac59075b964b07152d234b70');
INSERT INTO `server_machine` VALUES ('12', '66', '123', '12', 'wwqwe', null, 'qbright', '202cb962ac59075b964b07152d234b70');
INSERT INTO `server_machine` VALUES ('13', '66', '123', '12', 'wwqwe', null, 'qbright', '202cb962ac59075b964b07152d234b70');
INSERT INTO `server_machine` VALUES ('14', '66', '123', '12', 'wwqwe', null, 'qbright', '202cb962ac59075b964b07152d234b70');
INSERT INTO `server_machine` VALUES ('15', '66', '123', '23', '123123', null, '123', '1');
INSERT INTO `server_machine` VALUES ('16', '66', '123', '123', '123123', null, '33', '1');
INSERT INTO `server_machine` VALUES ('17', '66', '123', '123', '123123', null, '33', '1');
INSERT INTO `server_machine` VALUES ('18', '66', '123', '123', '123123', null, '33', '1');
INSERT INTO `server_machine` VALUES ('19', '66', '231', '123', '2323', null, '1234', '1');
INSERT INTO `server_machine` VALUES ('20', '66', 'weqwe', '123', '123123123', null, 'weqw', '1');
INSERT INTO `server_machine` VALUES ('21', '66', '123', '232', '3232313', null, 'niddd', '202cb962ac59075b964b07152d234b70');
INSERT INTO `server_machine` VALUES ('22', '66', '2323', '23', '12312312', null, 'ererer', '202cb962ac59075b964b07152d234b70');
INSERT INTO `server_machine` VALUES ('23', '66', '2323', '12', '23123', null, 'rert', '202cb962ac59075b964b07152d234b70');
INSERT INTO `server_machine` VALUES ('24', '66', '123', '232', '34234', null, 'gdfg', '202cb962ac59075b964b07152d234b70');
INSERT INTO `server_machine` VALUES ('25', '66', '232', '23', '123', null, 'fgsdf', '202cb962ac59075b964b07152d234b70');
INSERT INTO `server_machine` VALUES ('26', '66', 'd23', '343', '3434', null, '4545', '289dff07669d7a23de0ef88d2f7129e7');
INSERT INTO `server_machine` VALUES ('27', '66', '127.0.0.1', '234', '34343434', null, '433', '289dff07669d7a23de0ef88d2f7129e7');

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
INSERT INTO `user` VALUES ('66', 'user', '202cb962ac59075b964b07152d234b70', '2013-02-06', '', '0', '2', 'zhengqiguang@21cn.com');
INSERT INTO `user` VALUES ('67', 'admin', '202cb962ac59075b964b07152d234b70', '2013-02-06', '', '1', '0', 'zqbright@gmail.com');
INSERT INTO `user` VALUES ('68', 'qbright', '202cb962ac59075b964b07152d234b70', '2013-02-26', '', '0', '0', 'zhengqiguang@21cn.com');
