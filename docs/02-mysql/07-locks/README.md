# 07 锁、死锁、乐观锁

[← 上一知识点](../06-mvcc/README.md) | [MySQL 集合](../README.md) | [下一知识点 →](../08-optimize/README.md)

## 讲解

InnoDB 行锁打在**索引记录**上。还有意向锁、间隙锁、Next-Key、插入意向锁。

RR 范围更新容易间隙锁死锁。InnoDB 检测到环会回滚代价小的一方。

悲观：`SELECT ... FOR UPDATE`。  
乐观：`UPDATE ... SET version=version+1 WHERE id=? AND version=?`，冲突则重试。

## Demo

两窗口：

```sql
-- A
BEGIN;
UPDATE t_user SET age = age + 1 WHERE id = 1;

-- B
BEGIN;
UPDATE t_user SET age = age + 1 WHERE id = 2;
UPDATE t_user SET age = age + 1 WHERE id = 1;  -- 等 A

-- A
UPDATE t_user SET age = age + 1 WHERE id = 2;  -- 与 B 环等待 → 死锁
```

看 B 或 A 之一报 `Deadlock found`。`SHOW ENGINE INNODB STATUS` 有 LATEST DETECTED DEADLOCK。

## 练习

1. 改成两个事务都按 id 升序更新，确认不再死锁。  
2. 给 `t_user` 加 `version` 列，写一条乐观锁 UPDATE。  
3. 应用层对死锁错误码做一次重试。

## 对应题目

- MySQL 中有哪些锁类型？  
- 乐观锁和悲观锁  
- 发生死锁如何解决？
