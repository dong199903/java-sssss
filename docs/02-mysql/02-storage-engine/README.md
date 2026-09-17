# 02 存储引擎

[← 上一知识点](../01-sql-execution/README.md) | [MySQL 集合](../README.md) | [下一知识点 →](../03-bplus-index/README.md)

## 讲解

InnoDB（默认）：事务、行锁、MVCC、聚簇索引、崩溃恢复（redo/undo）。  
MyISAM：表锁、无事务、压缩读友好，新项目不要用业务表。

`SHOW TABLE STATUS` / `SHOW CREATE TABLE` 能看到 ENGINE。

## Demo

```sql
SHOW TABLE STATUS FROM learn_mysql LIKE 't_user'\G
SHOW ENGINES;
```

## 练习

1. 建一张 `ENGINE=MyISAM` 的表，试 `BEGIN; INSERT; ROLLBACK;` 数据是否还在。  
2. 列出 InnoDB 有而 MyISAM 没有的 4 件事。

## 对应题目

- MySQL 的存储引擎有哪些？它们之间有什么区别？
