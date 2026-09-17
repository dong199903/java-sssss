CREATE DATABASE IF NOT EXISTS learn_mysql DEFAULT CHARSET utf8mb4;
USE learn_mysql;

DROP TABLE IF EXISTS t_order;
DROP TABLE IF EXISTS t_user;

CREATE TABLE t_user (
    id         BIGINT PRIMARY KEY AUTO_INCREMENT,
    username   VARCHAR(32) NOT NULL,
    phone      VARCHAR(20),
    age        INT,
    status     TINYINT NOT NULL DEFAULT 1,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

INSERT INTO t_user (username, phone, age, status) VALUES
('alice',  '13800000001', 21, 1),
('bob',    '13800000002', 30, 1),
('cathy',  '13800000003', 25, 0),
('david',  '13800000004', 30, 1),
('eva',    '13800000005', 28, 1);

-- 先不建二级索引，后面实验亲手加
SELECT id, username, age, status FROM t_user;
