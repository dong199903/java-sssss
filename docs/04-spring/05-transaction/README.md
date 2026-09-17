# 05 事务传播

[← 上一知识点](../04-aop/README.md) | [Spring 集合](../README.md) | [下一知识点 →](../06-mvc/README.md)

## 讲解

`@Transactional` 本质是 AOP 代理。默认 `REQUIRED`：有事务就加入，没有就新建。

| 传播 | 含义 |
|------|------|
| REQUIRED | 默认 |
| REQUIRES_NEW | 挂起当前，新开，独立提交 |
| NESTED | 同一连接上的 savepoint |
| SUPPORTS / NOT_SUPPORTED / MANDATORY / NEVER | 少用，知道即可 |

默认只回滚 RuntimeException / Error，checked 要 `rollbackFor`。  
自调用不走代理。事务应放在 Service，不要放在只读组装逻辑里长时间占连接。

结合 MySQL 二阶段：Spring 提交时同步刷连接，和 binlog 不是一回事，但「事务边界」要短。

## Demo

打开已有用户模块（第 7/9 章）里带 `@Transactional` 的 Service，或自己写：

```java
@Transactional
public void a() { b(); } // 同类 b 上的 REQUIRED 不会新开事务
```

对照 `docs/02-mysql/07-locks` 在事务里持锁。

## 练习

1. 造一个 checked Exception，观察默认会不会回滚。  
2. 两个 Service 方法，内层 `REQUIRES_NEW`，外层抛错，看内层是否已提交。  
3. 把 `@Transactional` 放到 private 方法上，解释为何无效。

## 对应题目

- Spring 有哪几种事务传播行为？有什么用？  
- Spring 事务有几个隔离级别？  
- 事务在什么情况下会失效？
