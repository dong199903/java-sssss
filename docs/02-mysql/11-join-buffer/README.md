# 11 JOIN、分页与优化器

[← 上一知识点](../10-types/README.md) | [MySQL 集合](../README.md)

## 讲解

INNER 只保留两边都有；LEFT 保留左表，右表对不上就是 NULL。多表 JOIN 让优化器搜索空间爆炸，生产要控制表数、保证驱动表过滤强。

`LIMIT 100000000, 10` 要丢掉前 1 亿行，和 `LIMIT 10` 完全不是一个量级。深度分页用 **延迟关联** 或 **游标（WHERE id > last）**。

`EXISTS` 找到一行就停；`IN` 适合小结果集。优化器按代价选计划，不是「有索引就一定用」。

## Demo

执行 `sql/03-types-join.sql`。概念上 Buffer Pool / JOIN 选择也可对照 `08-optimize`。

## 练习

1. 用 EXPLAIN 看 INNER JOIN 和 LEFT JOIN 的 type / rows。  
2. 把 `LIMIT 100000, 10` 改成 `WHERE id > ? LIMIT 10`，对比扫描。  
3. 讲清 CDC、全量/增量同步各自适用场景。

## 对应题目

- INNER / LEFT / RIGHT JOIN 的区别？  
- 为什么不推荐多表 JOIN？  
- LIMIT 大偏移和 LIMIT 10 速度一样吗？深度分页？游标分页？  
- EXISTS 和 IN？查询优化器如何选计划？  
- 建表要注意什么？何时不该建索引？  
- 全量同步和增量同步？CDC？批量入库？  
- 不停服迁移？读写分离？主从延迟？分库分表？
