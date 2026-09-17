# 08 SQL 优化、分页、复制

[← 上一知识点](../07-locks/README.md) | [MySQL 集合](../README.md) | [下一知识点 →](../09-redo-undo-binlog/README.md)

## 讲解

调优顺序：慢日志 → EXPLAIN / EXPLAIN ANALYZE → 改 SQL 或索引 → 再量。  
`LIMIT 100000,10` 会扔掉前 10 万行，改成 `WHERE id > last_id ORDER BY id LIMIT 10`。

`select *` 一千万行：mysqld 流式读页，**应用一次性 List 全接才会堆内存爆**。JDBC 设 fetchSize。

主从：binlog → relay → 重放。延迟看大事务、无主键、从库资源。  
分库分表：先垂直再水平，sharding key 选错会变成广播查询。

## Demo

```sql
EXPLAIN SELECT * FROM t_user ORDER BY id LIMIT 100000, 10;
-- 对照
EXPLAIN SELECT * FROM t_user WHERE id > 100000 ORDER BY id LIMIT 10;
```

（数据少时只看计划差异即可。）

## 练习

1. 打开慢日志，故意写一条无索引查询，把它优化到 `ref` 或 `range`。  
2. 说明 `count(*)` 在 InnoDB 为什么不能 O(1)。  
3. 列出主从延迟的三条处理手段。

## 对应题目

- 如何使用 EXPLAIN  
- 如何 SQL 调优  
- 如何解决深度分页  
- select * 一千万行内存会飙吗  
- 主从同步机制 / 如何处理延迟  
- 什么是分库分表，有哪些策略
