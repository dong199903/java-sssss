# 17 同步工具与 CompletableFuture

[← 上一知识点](../16-cow-failfast/README.md) | [Java 集合](../README.md)

## 讲解

| 工具 | 干什么 |
|------|--------|
| CountDownLatch | 主线程等 N 个工人，一次性 |
| CyclicBarrier | N 个线程互相等，可循环 |
| Semaphore | 限流，同时最多 N 个 |
| CompletableFuture | 异步编排，thenCombine / thenCompose |
| ForkJoinPool | 分治递归任务 |

线程通信：共享内存 + wait/notify，或阻塞队列。控制顺序：latch / join / CF。  
`synchronized` 修饰实例锁 this，修饰静态锁 Class。和 ReentrantLock 的差别见 `07-aqs-lock`。

## Demo

```bash
cd java-basics-learn && ./run.sh java-sync
```

## 练习

1. 用 CountDownLatch 等 3 个下载任务结束再汇总。  
2. 用 Semaphore(1) 实现互斥，对比 synchronized。  
3. 两个 CF 分别查用户、查订单，thenCombine 成一个 VO。

## 对应题目

- 你使用过哪些并发工具类？Semaphore / CyclicBarrier / CountDownLatch？  
- 什么是 CompletableFuture / ForkJoinPool？  
- 如何控制多个线程的执行顺序？  
- 主线程如何知道子线程是否成功？  
- 使用过哪些阻塞队列 / 原子类？  
- 线程之间如何通信？什么是线程同步？  
- Synchronized 修饰静态方法和实例方法的区别？  
- Volatile 与 Synchronized 的区别？  
- synchronized 能不能禁止指令重排？  
- 如何创建多线程？
