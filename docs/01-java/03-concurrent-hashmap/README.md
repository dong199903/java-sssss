# 03 ConcurrentHashMap

[← 上一知识点](../02-hashmap/README.md) | [Java 集合](../README.md) | [下一知识点：线程 →](../04-thread-sync/README.md)

## 讲解

HashMap 多线程 put 会丢数据，JDK 7 还可能死循环。并发 Map 用 ConcurrentHashMap。

- **1.7**：Segment 分段锁，粒度粗。  
- **1.8**：结构对齐 HashMap（数组+链表/树）。空桶 CAS 放入；冲突时 `synchronized` 桶头。`size` 用类似 LongAdder 的 CounterCell。  
- 不允许 `null` key/value；`get` 基本无锁，靠 volatile。

不要「HashMap + 运气」。读多写少可考虑不可变拷贝，写多就 CHM。

## Demo

```bash
cd java-basics-learn && ./run.sh java-chm
```

8 个线程各 put 1000 次。CHM 的 size 应等于 8000；HashMap 经常少一截。

## 练习

1. 把 Demo 的线程数改成 32，观察 HashMap 是否开始抛异常。  
2. 用 `Collections.synchronizedMap(new HashMap<>())` 再跑一轮，对比 CHM 耗时（锁整张表 vs 锁桶）。  
3. 口头讲清：为什么 1.8 不再需要 Segment。

## 对应题目

- Java 中 ConcurrentHashMap 1.7 和 1.8 之间有哪些区别？  
- get 方法是否需要加锁？  
- 为什么不支持 key 或 value 为 null？  
- ConcurrentHashMap 和 Hashtable 的区别？  
- Java 线程安全的集合有哪些？
