# 01 为什么快

[← Redis 集合](../README.md) | [下一知识点 →](../02-data-types/README.md)

## 讲解

快的原因叠在一起：数据在内存、命令执行单线程无锁、IO 多路复用、紧凑编码（SDS、listpack）、Pipeline 摊 RTT。

单线程指 **命令执行**。6.0 多线程只加速网络读写，不是多线程同时 SET。

场景：缓存、Session、锁、排行榜、计数、限流、Stream。不是所有数据都该进 Redis。

相对 Memcached：Redis 结构多、可持久化、有复制集群。

## Demo

有 Redis 时：

```bash
redis-cli PING
redis-cli SET k 1
redis-cli GET k
```

对照 `INFO` 里的 `used_memory`、`tcp_port`。没有 Redis 先读讲解，直接进入下一节排行榜 Demo。

## 练习

1. 用自己的话解释「单线程为什么反而快」。  
2. 列出 3 个不该用 Redis 的场景（大文件、强事务、复杂 join）。  
3. 读 `INFO clients`，看 connected_clients。

## 对应题目

- Redis 通常应用于哪些场景？  
- Redis 为什么这么快？  
- 为什么设计成单线程？6.0 为何引入多线程？  
- Redis 和 Memcached 有哪些区别？  
- 项目里用的 Redis 客户端是什么？（Jedis 要池，Lettuce 可共享）
