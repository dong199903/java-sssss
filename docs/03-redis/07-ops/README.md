# 07 大 key、热 key、Lua、Pipeline

[← 上一知识点](../06-dist-lock/README.md) | [Redis 集合](../README.md) | [下一知识点 →](../08-sds-encoding/README.md)

## 讲解

**大 key**：网络阻塞、过期删除卡顿、主从超时。拆分、UNLINK、分批 HSCAN。  
**热 key**：单 key 打满单核。本地缓存、复制 N 份、拆请求。  
**Pipeline**：批量降低 RTT，**不原子**。要原子用 Lua 或 MULTI。  
Redis「事务」无隔离无回滚；集群跨槽会失败。Lua 期间阻塞整个实例，脚本要短。

性能：先 `SLOWLOG`，禁 `KEYS`，拆分集群，别在 Redis 里存大对象做计算。

## Demo

```bash
redis-cli SLOWLOG GET 10
redis-cli --bigkeys
# pipeline 示意（redis-cli --pipe）
```

## 练习

1. 对比循环 GET 1000 次和 Pipeline 1000 次的耗时（本地 Redis）。  
2. 解释为什么 `KEYS *` 不能在生产执行。  
3. 设计一个「用户 Feed」避免单个 List 变成大 key。

## 对应题目

- Big Key 问题如何解决  
- 如何解决热点 key  
- Redis 支持事务吗  
- Lua 脚本是什么、怎么用  
- Pipeline 是什么  
- Redis 性能瓶颈如何处理
