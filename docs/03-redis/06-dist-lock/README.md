# 06 分布式锁

[← 上一知识点](../05-cache-problems/README.md) | [Redis 集合](../README.md) | [下一知识点 →](../07-ops/README.md)

## 讲解

`SET key token NX PX ttl`，解锁用 Lua：值相等才 DEL。禁止 `GET` 后无条件 `DEL`。

问题：业务没做完锁过期、续期失败、主从切换丢锁、不可重入。  
Redisson：可重入 Hash + 看门狗默认 30s 续期。看门狗依赖客户端存活。RedLock 有争议。

不是所有互斥都要分布式锁，DB 唯一约束和状态机往往更简单。

## Demo

```bash
cd java-basics-learn && ./run.sh redis-lock
```

有 Redis：

```bash
redis-cli SET lock token-a NX PX 5000
redis-cli SET lock token-b NX PX 5000
# 应用解锁必须校验 token
```

## 练习

1. 写一段伪 Lua：`if get==token then del`。  
2. 列出锁粒度（整单 vs 库存行）对并发的影响。  
3. 对比 ZK 临时节点锁和 Redis 锁的可靠性和延迟。

## 对应题目

- Redis 中如何实现分布式锁  
- 实现时可能遇到的问题  
- Redisson 分布式锁原理  
- 分布式锁一般都怎样实现（场景题）
