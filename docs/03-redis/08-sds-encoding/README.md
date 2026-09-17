# 08 SDS、编码与 Pipeline

[← 上一知识点](../07-ops/README.md) | [Redis 集合](../README.md) | [下一知识点 →](../09-bloom-uv/README.md)

## 讲解

String 底层是 **SDS**：自带 len，O(1) 取长度，可含 `\0`。短串 EMBSTR 一次分配对象头+缓冲。

小 List/Hash/ZSet 用 **ziplist / listpack** 紧凑编码，大了升级成 quicklist / hashtable / skiplist。  
ZSet 用跳表而不是红黑树：实现简单、范围扫描友好、无锁化更容易。

Pipeline 把多次往返打成一次，**不保证原子**。MGET 是单命令原子但只能一类操作。要原子用 Lua 或 MULTI（无隔离、无回滚）。

## Demo

```bash
cd java-basics-learn && ./run.sh redis-sds
```

## 练习

1. 对比 C 字符串 `strlen` 和 SDS 的 len。  
2. 解释为什么 Pipeline 不能替代事务。  
3. 画 ZSet：dict + skiplist 两套结构。

## 对应题目

- String 底层 SDS？EMBSTR 阈值为什么是 44？  
- Ziplist / Quicklist / ListPack？  
- List 常见命令？如何实现队列和栈？  
- Zset 原理？为什么用跳表不是红黑树/B+？  
- Pipeline 和 MSET/MGET 的区别？  
- 事务和关系库事务的区别？Lua？  
- 内存碎片化？虚拟内存 VM？
