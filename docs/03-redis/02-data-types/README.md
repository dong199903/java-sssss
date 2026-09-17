# 02 数据结构：Hash、ZSet、跳表

[← 上一知识点](../01-why-fast/README.md) | [Redis 集合](../README.md) | [下一知识点 →](../03-expire-evict/README.md)

## 讲解

String、Hash、List、Set、ZSet，外加 Bitmap、HLL、GEO、Stream。

Hash：对象局部字段更新，小 hash 用 listpack。  
ZSet：`dict` 做 member→score，`skiplist` 做有序范围。不用平衡树是因为实现简单、范围查询好走。

排行榜：`ZINCRBY` + `ZREVRANGE`。同分用时间戳当小数。

## Demo

```bash
cd java-basics-learn && ./run.sh redis-rank
```

有 Redis：

```bash
redis-cli ZADD board 10 alice 20 bob
redis-cli ZINCRBY board 15 alice
redis-cli ZREVRANGE board 0 2 WITHSCORES
```

## 练习

1. 用 Hash 存用户 `{name, age}`，只改 age，对比整段 String JSON。  
2. 给排行榜加上「同分后到的人靠后」。  
3. 画出跳表 3 层，说明 ZRANGE 怎么走。

## 对应题目

- Redis 中常见的数据类型有哪些？  
- Redis 的 hash 是什么？  
- 跳表的实现原理  
- 如何用 Redis 实现排行榜  
- 如何实现布隆过滤器（判一定不存在，有假阳）
