# 09 布隆、UV 与 GEO

[← 上一知识点](../08-sds-encoding/README.md) | [Redis 集合](../README.md)

## 讲解

**布隆过滤器**：bit 数组 + 多哈希。能确定「一定不在」；「在」可能是假阳。用来挡缓存穿透的随机 key。Redis 可用 RedisBloom 模块或自己用 Bitmap。

**HyperLogLog**：估基数（UV），内存固定、允许误差。命令 `PFADD` / `PFCOUNT`。

**GEO**：ZSet + GeoHash，附近的人。  
**Pub/Sub**：不持久、不积压，不能当 MQ。

## Demo

```bash
cd java-basics-learn && ./run.sh redis-bloom
```

## 练习

1. 把 Bloom 的 bit 数改小，观察假阳是否变多。  
2. 用 Set 和「估基数」对比 10 万 UV 的内存直觉。  
3. 说明为什么登录态、订单流不能走 Pub/Sub。

## 对应题目

- 如何用 Redis 实现布隆过滤器？  
- 如何统计大量用户 UV？  
- GEO 是什么？  
- 订阅发布了解吗？  
- Redis 通常应用于哪些场景？  
- 和 Memcached 的区别？客户端是什么？字符串最大值？
