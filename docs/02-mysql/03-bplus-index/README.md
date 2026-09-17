# 03 B+ 树、聚簇索引、回表

[← 上一知识点](../02-storage-engine/README.md) | [MySQL 集合](../README.md) | [下一知识点 →](../04-index-use/README.md)

## 讲解

InnoDB 索引是 B+ 树：非叶只放键和指针，叶子链表方便范围扫，扇出大所以树矮，磁盘 IO 少。

- **聚簇索引**：叶子就是整行，一张表一份，通常主键。  
- **二级索引**：叶子是主键值，要整行必须再走主键树 → **回表**。  
- 主键用自增，避免 UUID 随机插入把页打碎。三层 16KB 页、短主键大约能撑千万～亿行量级（和键长有关）。

## Demo

```sql
USE learn_mysql;
SHOW INDEX FROM t_user;
EXPLAIN SELECT * FROM t_user WHERE id = 1;           -- 主键，不回表
-- 执行 sql/01-index-explain.sql 后：
EXPLAIN SELECT * FROM t_user WHERE username = 'alice'; -- 二级索引 + 回表
```

`select *` 几乎总会回表。`Extra: Using index` 表示覆盖、不必回表。

## 练习

1. 解释为什么「主键越短，二级索引也越瘦」。  
2. 估算：页 16KB，主键 8B+指针，三层大概多少行（写出假设）。  
3. 对比 `SELECT id FROM t_user WHERE username='alice'` 和 `SELECT *` 的 Extra。

## 对应题目

- 为什么 MySQL 选择 B+ 树？  
- 请详细描述 B+ 树中查询数据的全过程  
- 三层 B+ 树能存多少数据？  
- 聚簇索引和非聚簇索引的区别  
- 回表是什么？  
- 索引类型有哪些？
