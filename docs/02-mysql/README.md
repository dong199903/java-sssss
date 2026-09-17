# MySQL 知识点集合

[← 总目录](../README.md)

从「一条 SQL 怎么走」到索引、事务锁、复制。SQL 脚本在 `sql/`，库名默认 `learn_mysql`。

## 学习顺序

| 编号 | 难度 | 知识点 | 文档 |
|------|------|--------|------|
| 01 | 基础 | 一条 SQL 的执行过程 | [01-sql-execution](01-sql-execution/README.md) |
| 02 | 基础 | 存储引擎 InnoDB vs MyISAM | [02-storage-engine](02-storage-engine/README.md) |
| 03 | 基础→进阶 | B+ 树、聚簇索引、回表 | [03-bplus-index](03-bplus-index/README.md) |
| 04 | 进阶 | 最左前缀、覆盖索引、索引下推 | [04-index-use](04-index-use/README.md) |
| 05 | 进阶 | 隔离级别与脏读/幻读 | [05-isolation](05-isolation/README.md) |
| 06 | 进阶 | MVCC、undo、长事务 | [06-mvcc](06-mvcc/README.md) |
| 07 | 进阶 | 行锁、间隙锁、死锁、乐观锁 | [07-locks](07-locks/README.md) |
| 08 | 进阶 | EXPLAIN、深度分页、主从与分库 | [08-optimize](08-optimize/README.md) |
| 09 | 进阶 | redo / undo / binlog 与 WAL | [09-redo-undo-binlog](09-redo-undo-binlog/README.md) |
| 10 | 基础 | 类型、count、金额 | [10-types](10-types/README.md) |
| 11 | 进阶 | JOIN、分页与优化器 | [11-join-buffer](11-join-buffer/README.md) |

准备：

```bash
mysql -uroot -p < docs/02-mysql/sql/00-init.sql
```

然后按文档执行 `sql/01-*.sql`。本集合按题目拆开、带练习。
