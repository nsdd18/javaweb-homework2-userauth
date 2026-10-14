-- ============================================================
--  软件2501班 第二次作业 —— 数据库初始化脚本
--  项目：登录 / 注册 / 忘记密码（JavaWeb + Servlet + MySQL）
--  姓名：杨鑫    学号：25040500
-- ============================================================
--  说明：在 MySQL 中执行本脚本即可完成建库、建表、建应用账号。
--       执行方式（命令行）：mysql -uroot -p < init_database.sql
-- ============================================================

-- ---------- 1. 创建数据库 ----------
CREATE DATABASE IF NOT EXISTS `javaweb_homework`
    DEFAULT CHARACTER SET utf8mb4
    COLLATE utf8mb4_general_ci;

-- ---------- 2. 创建应用专用账号 ----------
--  不让程序直接用 root，最小权限原则，只授予本库的权限
CREATE USER IF NOT EXISTS 'javaweb'@'localhost'
    IDENTIFIED WITH mysql_native_password BY 'javaweb123';
ALTER USER 'javaweb'@'localhost'
    IDENTIFIED WITH mysql_native_password BY 'javaweb123';
GRANT ALL PRIVILEGES ON `javaweb_homework`.* TO 'javaweb'@'localhost';
FLUSH PRIVILEGES;

USE `javaweb_homework`;

-- ---------- 3. 创建用户表 ----------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
    `id`          INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '用户编号',
    `username`    VARCHAR(50)  NOT NULL                COMMENT '登录用户名（唯一）',
    `password`    CHAR(64)     NOT NULL                COMMENT '密码：SHA-256(盐值+密码) 的十六进制字符串',
    `salt`        CHAR(32)     NOT NULL                COMMENT '每个用户独立的随机盐值（16 字节的十六进制）',
    `email`       VARCHAR(100) NOT NULL                COMMENT '注册邮箱，用于找回密码',
    `phone`       VARCHAR(20)  DEFAULT NULL            COMMENT '手机号（选填）',
    `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间',
    `update_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP
                               ON UPDATE CURRENT_TIMESTAMP        COMMENT '最后更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    KEY `idx_email` (`email`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COMMENT = '用户表';

-- ---------- 4. 查看结果 ----------
SHOW TABLES;
DESC `user`;
