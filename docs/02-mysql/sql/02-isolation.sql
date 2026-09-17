USE learn_mysql;

-- 会话 A
SET SESSION TRANSACTION ISOLATION LEVEL REPEATABLE READ;
BEGIN;
SELECT * FROM t_user WHERE id = 1;

-- 会话 B（另开窗口）
-- UPDATE t_user SET age = 99 WHERE id = 1; COMMIT;

-- 回到会话 A，再 SELECT 一次：RR 下快照读仍是旧值
SELECT * FROM t_user WHERE id = 1;
COMMIT;
