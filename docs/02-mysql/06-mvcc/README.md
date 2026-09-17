# 06 MVCC 与长事务

[← 上一知识点](../05-isolation/README.md) | [MySQL 集合](../README.md) | [下一知识点 →](../07-locks/README.md)

## 讲解

行上隐藏列 `trx_id`、`roll_ptr`。Read View 判断哪个版本对当前事务可见，老版本串在 **undo 链**上。

- RC：每个语句新 Read View  
- RR：第一次快照读创建，整事务复用  

长事务：锁不放、undo 不能清、历史链变长、备份和从库延迟。事务里禁止 RPC 和大 sleep。

## Demo

在一个事务里反复 `SELECT * FROM t_user WHERE id=1`，另一会话不断 UPDATE 同一行并提交。RR 下你一直看到同一版；`SHOW ENGINE INNODB STATUS` 里能看到事务。

## 练习

1. 开一个事务不提交，观察 `information_schema.innodb_trx`。  
2. 解释为什么「查询也会在 RR 下持有 Read View，拖住 undo」。  

## 对应题目

- MySQL 中的 MVCC 是什么？  
- 长事务可能导致哪些问题？
