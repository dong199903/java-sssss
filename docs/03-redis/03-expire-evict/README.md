# 03 过期删除与内存淘汰

[← 上一知识点](../02-data-types/README.md) | [Redis 集合](../README.md) | [下一知识点 →](../04-persist-ha/README.md)

## 讲解

过期：**惰性**（访问时检查）+ **定期抽样**，不是到点全表扫。大量 key 同一秒过期会形成风暴，TTL 加随机。

淘汰（`maxmemory` 满了才发生）：`noeviction`、`allkeys-lru/lfu`、`volatile-*`。缓存场景常用 `allkeys-lru`。过期和淘汰是两件事。

必须设 `maxmemory`，否则吃光机器。

## Demo

```bash
redis-cli SETEX session 2 abc
redis-cli GET session
# 等 3 秒
redis-cli GET session
redis-cli CONFIG GET maxmemory-policy
```

## 练习

1. 写 1000 个相同 TTL 的 key，观察过期是否「整齐切掉」。改成 TTL+random。  
2. 把 policy 改成 `allkeys-lru`，写到超过 maxmemory，看哪些 key 没了。

## 对应题目

- 数据过期后的删除策略  
- 有哪些内存淘汰策略  
- Redis 内存溢出怎么排查  
- 线上 Redis 机器爆了如何优化
