# 04 最左前缀、覆盖索引、索引下推

[← 上一知识点](../03-bplus-index/README.md) | [MySQL 集合](../README.md) | [下一知识点 →](../05-isolation/README.md)

## 讲解

联合索引 `(status, age)`：能用 `status`、`status+age`；**只写 age 用不了最左**。范围条件会截断后面的列。

**覆盖索引**：查询列都在索引里，不回表。  
**索引下推（ICP）**：后列即使不能用来定位，也可在引擎层先过滤再回表。`Extra: Using index condition`。

索引不是越多越好：写入维护成本、优化器枚举变慢。对列套函数、隐式类型转换会让索引失效。

## Demo

跑 `sql/01-index-explain.sql`，对比：

- `status=1 AND age=30`  
- 只有 `age=30`  
- `LEFT(username,1)='a'`  

## 练习

1. 设计 `(a,b,c)` 上哪些 WHERE 能走到索引，哪些不能，写 6 条 SQL。  
2. 给「只查 id,status,age 且按 status 过滤」做覆盖索引，用 EXPLAIN 验证 Using index。  
3. 找项目里一条慢 SQL，看有没有函数包列。

## 对应题目

- 最左前缀匹配原则  
- 覆盖索引 / 索引下推  
- 建索引要注意什么  
- 使用索引一定有效吗？如何排查  
- 索引是不是越多越好  
- count(*) / count(1) / count(列)
