# 09 redo / undo / binlog 与 WAL

[← 上一知识点](../08-optimize/README.md) | [MySQL 集合](../README.md) | [下一知识点 →](../10-types/README.md)

## 讲解

InnoDB 靠 **先写日志再改页（WAL）** 做到 crash-safe：

| 日志 | 在哪一层 | 干什么 |
|------|----------|--------|
| redo | 引擎 | 重做已提交改动，崩溃恢复 |
| undo | 引擎 | 回滚 + MVCC 旧版本 |
| binlog | Server | 主从复制、备份、数据恢复 |

提交走 **两阶段**：redo prepare → 写 binlog → redo commit。少了任一阶段，崩溃后要对齐。  
Buffer Pool 是页缓存，数据不总是从磁盘读。Change Buffer 优化二级索引写；Doublewrite 防页写到一半。Log Buffer 是 redo 内存。

## Demo

纯 Java 模拟崩溃丢页再用 redo 恢复：

```bash
cd java-basics-learn && ./run.sh mysql-wal
```

有 MySQL 时对照：

```sql
SHOW VARIABLES LIKE 'innodb_flush_log_at_trx_commit';
SHOW VARIABLES LIKE 'sync_binlog';
```

## 练习

1. 口头走一遍：update 一行时 redo、undo、binlog 各写什么。  
2. 解释 `innodb_flush_log_at_trx_commit=1` 和 `=2` 的差别。  
3. 画两阶段提交时序（prepare / binlog / commit）。

## 对应题目

- binlog、redo log、undo log 的作用和区别？  
- 什么是 WAL？MySQL 用到了吗？  
- 事务的二阶段提交是什么？  
- 插入一条 SQL，redo 记录的是什么？  
- 从磁盘读还是 Buffer Pool？Change Buffer / Doublewrite / Log Buffer？  
- MySQL 如何实现事务？
