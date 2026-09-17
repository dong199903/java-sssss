# 05 事务隔离级别

[← 上一知识点](../04-index-use/README.md) | [MySQL 集合](../README.md) | [下一知识点 →](../06-mvcc/README.md)

## 讲解

四种隔离：读未提交、读已提交（RC）、可重复读（RR，InnoDB 默认）、串行化。

- 脏读：读到未提交  
- 不可重复读：同一行两次结果变了  
- 幻读：范围行数变了  

InnoDB RR 用 MVCC 做**快照读**，用 Next-Key 锁做**当前读**（`FOR UPDATE`）抑制幻读。很多业务库改成 RC 换并发。

二阶段提交：redo prepare → 写 binlog → commit，保证 binlog 与引擎一致。

## Demo

两个客户端跑 `sql/02-isolation.sql`：会话 A 先开 RR 事务读 id=1，会话 B 改 age 并提交，A 再读。快照读应仍是旧值。

把隔离改成 `READ COMMITTED` 再试一次，第二次读会看到 99。

## 练习

1. 用 `SELECT ... FOR UPDATE` 代替普通 SELECT，看 RR 下会话 B 的 UPDATE 是否被堵住。  
2. 查 `@@transaction_isolation`，记下你机器默认值。  
3. 用自己的话区分快照读和当前读。

## 对应题目

- 脏读、不可重复读、幻读  
- 事务隔离级别有哪些  
- 默认隔离级别是什么，为什么  
- MySQL 如何实现事务  
- 事务的二阶段提交是什么
