# 04 持久化与高可用

[← 上一知识点](../03-expire-evict/README.md) | [Redis 集合](../README.md) | [下一知识点 →](../05-cache-problems/README.md)

## 讲解

- **RDB**：子进程 `fork` 快照，恢复快，可能丢最后一段。父进程继续服务，写时复制可能抬内存。不要高峰 `SAVE`。  
- **AOF**：命令日志，`everysec` 是常见折中。可混合持久化。  
- **复制**：PSYNC，全量 RDB + backlog 增量。积压不够会全量。  
- **哨兵**：监控、故障转移，至少 3 个 Sentinel。  
- **集群**：16384 slot，MOVED/ASK。相关 key 用 `{tag}` 同槽。网络分区会脑裂，`min-replicas-to-write` 可减写丢失。

## Demo

```bash
redis-cli INFO persistence
redis-cli LASTSAVE
# 集群环境才有：
redis-cli CLUSTER INFO
```

没有集群就读 `INFO replication` 看 role。

## 练习

1. 对比 RDB 丢数据和 AOF everysec 最多丢一秒。  
2. 解释 hash tag `{userId}:cart` 和 `{userId}:order` 为什么能一起事务。  
3. 哨兵和集群各解决什么，不解决什么。

## 对应题目

- 持久化机制有哪些  
- 生成 RDB 时如何处理请求  
- 主从复制原理 / 常见拓扑 / 复制延迟原因  
- 哨兵机制  
- 集群实现原理 / 如何根据键定位节点  
- Cluster 与 Sentinel 的区别  
- 集群会脑裂吗
