# Java 知识点集合

[← 总目录](../README.md)

从「能用集合」到「能解释并发与 JVM」，再补语言基本功、Java8、同步工具。全部 Demo 在 `java-basics-learn`，用 `./run.sh <id>` 运行。

## 学习顺序

| 编号 | 难度 | 知识点 | Demo id | 文档 |
|------|------|--------|---------|------|
| 01 | 基础 | List 与数组：连续内存 vs 链表 | `java-list` | [01-list-array](01-list-array/README.md) |
| 02 | 基础→进阶 | HashMap：hash、冲突、扩容 | `java-hashmap` | [02-hashmap](02-hashmap/README.md) |
| 03 | 进阶 | ConcurrentHashMap：分段锁到 CAS | `java-chm` | [03-concurrent-hashmap](03-concurrent-hashmap/README.md) |
| 04 | 基础 | 线程生命周期与死锁 | `java-thread` | [04-thread-sync](04-thread-sync/README.md) |
| 05 | 进阶 | 线程池：核心/队列/拒绝 | `java-pool` | [05-thread-pool](05-thread-pool/README.md) |
| 06 | 进阶 | JMM、volatile、CAS、ThreadLocal | `java-jmm` | [06-jmm-cas](06-jmm-cas/README.md) |
| 07 | 进阶 | AQS 与 ReentrantLock | `java-aqs` | [07-aqs-lock](07-aqs-lock/README.md) |
| 08 | 进阶 | JVM 内存区域与 OOM 类型 | `java-jvm` | [08-jvm-gc](08-jvm-gc/README.md) |
| 09 | 基础 | equals、OOP、按值传递 | `java-equals` | [09-equals-oop](09-equals-oop/README.md) |
| 10 | 基础 | String、装箱、Integer 缓存 | `java-string` | [10-string-box](10-string-box/README.md) |
| 11 | 基础 | 异常体系 | `java-exception` | [11-exception](11-exception/README.md) |
| 12 | 进阶 | 泛型擦除、拷贝、序列化 | `java-generic` | [12-generics-serial](12-generics-serial/README.md) |
| 13 | 进阶 | 反射、注解、SPI | `java-reflect` | [13-reflect-spi](13-reflect-spi/README.md) |
| 14 | 进阶 | 类加载与双亲委派 | `java-classload` | [14-classload](14-classload/README.md) |
| 15 | 基础→进阶 | Java 8 / LTS | `java-java8` | [15-java8](15-java8/README.md) |
| 16 | 进阶 | fail-fast、COW、LRU | `java-cow` | [16-cow-failfast](16-cow-failfast/README.md) |
| 17 | 进阶 | Latch / Semaphore / CF | `java-sync` | [17-sync-tools](17-sync-tools/README.md) |

代理与自调用：`java-proxy`、`java-self`（Spring 事务/@Async 失效同源）。

## 集合怎么学

1. 01→08 是并发/JVM 主线；09→15 是语言主线，可并行。16、17 分别补集合边角和并发工具。  
2. 每个点：**跑 Demo → 读讲解 → 做练习 → 合上电脑讲给自己听 → 看对应题目**。  
3. 一次跑完全部：`./run.sh all`。
