# 01 一条 SQL 的执行过程

[← MySQL 集合](../README.md) | [下一知识点 →](../02-storage-engine/README.md)

## 讲解

Server 层 + 引擎层：

1. **连接器**：认证、权限  
2. **解析器**：词法语法，生成 AST（8.0 已无查询缓存）  
3. **优化器**：选索引、表连接顺序  
4. **执行器**：调用 InnoDB handler 取数  

InnoDB 再碰 Buffer Pool、聚簇索引、undo/redo。要把「优化器决定怎么走」和「引擎真正读页」分开。

## Demo

执行 `sql/00-init.sql` 建表。再：

```sql
USE learn_mysql;
EXPLAIN SELECT * FROM t_user WHERE username = 'alice';
```

无索引时 `type=ALL`。这就是优化器还没能走索引的执行计划。

## 练习

1. 对照 `EXPLAIN` 字段：id、select_type、type、key、rows、Extra，用自己的话各写一句。  
2. 画一张从客户端到叶子页的箭头图（连接器→…→B+ 树）。

## 对应题目

- 详细描述一条 SQL 语句在 MySQL 中的执行过程。
