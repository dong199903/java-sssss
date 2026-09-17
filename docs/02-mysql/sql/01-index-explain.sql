USE learn_mysql;

-- 无索引：全表扫
EXPLAIN SELECT * FROM t_user WHERE username = 'alice';

CREATE INDEX idx_username ON t_user (username);
EXPLAIN SELECT * FROM t_user WHERE username = 'alice';

-- 联合索引与最左
CREATE INDEX idx_status_age ON t_user (status, age);
EXPLAIN SELECT * FROM t_user WHERE status = 1 AND age = 30;
EXPLAIN SELECT * FROM t_user WHERE age = 30;              -- 通常用不上联合索引
EXPLAIN SELECT id, status, age FROM t_user WHERE status = 1; -- 可能覆盖索引

-- 对列套函数：索引失效
EXPLAIN SELECT * FROM t_user WHERE LEFT(username, 1) = 'a';
