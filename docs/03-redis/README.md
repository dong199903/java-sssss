# Redis 知识点集合

[← 总目录](../README.md)

先建立「内存数据结构 + 单线程执行」模型，再学过期/持久化/集群，最后缓存问题与锁。  
概念 Demo 用纯 Java 模拟（不强制装 Redis）；有 Redis 时用文内 `redis-cli` 对照。

## 学习顺序

| 编号 | 难度 | 知识点 | Demo id | 文档 |
|------|------|--------|---------|------|
| 01 | 基础 | 为什么快、单线程、常见类型 | — | [01-why-fast](01-why-fast/README.md) |
| 02 | 基础 | Hash / ZSet 排行榜 / 跳表 | `redis-rank` | [02-data-types](02-data-types/README.md) |
| 03 | 进阶 | 过期删除与内存淘汰 | — | [03-expire-evict](03-expire-evict/README.md) |
| 04 | 进阶 | 持久化、复制、哨兵、集群 | — | [04-persist-ha](04-persist-ha/README.md) |
| 05 | 进阶 | 穿透击穿雪崩与 Cache Aside | `redis-cache` | [05-cache-problems](05-cache-problems/README.md) |
| 06 | 进阶 | 分布式锁与 Redisson | `redis-lock` | [06-dist-lock](06-dist-lock/README.md) |
| 07 | 进阶 | 大 key、热 key、事务/Lua/Pipeline | — | [07-ops](07-ops/README.md) |
| 08 | 进阶 | SDS、编码升级、Pipeline 原子性 | `redis-sds` | [08-sds-encoding](08-sds-encoding/README.md) |
| 09 | 进阶 | 布隆过滤器、UV、GEO | `redis-bloom` | [09-bloom-uv](09-bloom-uv/README.md) |

```bash
cd java-basics-learn && ./run.sh redis-rank
```
