USE learn_mysql;

-- CHAR / VARCHAR
DROP TABLE IF EXISTS t_types;
CREATE TABLE t_types (
    id     INT PRIMARY KEY AUTO_INCREMENT,
    c_fix  CHAR(8)     NOT NULL,
    c_var  VARCHAR(8)  NOT NULL,
    price  DECIMAL(10, 2) NOT NULL,
    age    INT NULL
) ENGINE=InnoDB;

INSERT INTO t_types (c_fix, c_var, price, age) VALUES
('hi', 'hi', 10.50, 20),
('hello', 'hello', 0.10, NULL);

SELECT id, CONCAT('[', c_fix, ']') AS char_val, CONCAT('[', c_var, ']') AS varchar_val,
       CHAR_LENGTH(c_fix) AS char_len, CHAR_LENGTH(c_var) AS varchar_len,
       price, age
FROM t_types;

SELECT COUNT(*) AS cnt_star, COUNT(age) AS cnt_age FROM t_types;

-- JOIN
DROP TABLE IF EXISTS t_order;
CREATE TABLE t_order (
    id      BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    amount  DECIMAL(10, 2) NOT NULL
) ENGINE=InnoDB;
INSERT INTO t_order (user_id, amount) VALUES (1, 9.9), (1, 20), (2, 5), (99, 1);

SELECT u.username, o.amount
FROM t_user u
INNER JOIN t_order o ON o.user_id = u.id;

SELECT u.username, o.amount
FROM t_user u
LEFT JOIN t_order o ON o.user_id = u.id;

EXPLAIN SELECT u.username, o.amount
FROM t_user u
INNER JOIN t_order o ON o.user_id = u.id
ORDER BY o.id DESC
LIMIT 2;
