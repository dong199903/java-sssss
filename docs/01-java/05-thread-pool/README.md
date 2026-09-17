# 05 线程池

[← 上一知识点](../04-thread-sync/README.md) | [Java 集合](../README.md) | [下一知识点：JMM →](../06-jmm-cas/README.md)

## 讲解

`ThreadPoolExecutor` 提交任务的顺序必须记住：

1. 当前线程数 &lt; core → 建核心线程  
2. 否则进队列  
3. 队列满且 &lt; max → 建非核心线程  
4. 再满 → 拒绝策略  

常见坑：`Executors.newFixedThreadPool` 用无界队列，max 几乎永不生效，任务能把内存撑爆。

拒绝策略：Abort（默认抛异常）、CallerRuns（调用者跑，带背压）、Discard、DiscardOldest。

线程数：CPU 密集约 N+1，IO 密集可更大，**以压测为准**。

## Demo

```bash
cd java-basics-learn && ./run.sh java-pool
```

core=1, queue=2, max=2。前几个任务分别进核心、队列、第二个线程，再提交就会拒绝。

## 练习

1. 改成 `LinkedBlockingQueue`（无界）再提交 20 个任务，看 poolSize 是否永远是 1。  
2. 换成 `CallerRunsPolicy`，观察主线程是否开始执行任务。  
3. 调用 `setCorePoolSize(3)`，看运行中能否调大核心数。

## 对应题目

- 你了解 Java 线程池的原理吗？  
- 如何合理地设置 Java 线程池的线程数？  
- Java 线程池有哪些拒绝策略？  
- Java 并发库中提供了哪些线程池实现？它们有什么区别？  
- Java 创建线程池有哪些方式？  
- shutdown 与 shutdownNow 的区别？  
- 核心线程数运行中能修改吗？  
- 任务出异常后如何知道是哪个线程？  
- DelayQueue 和 ScheduledThreadPool 的区别？什么是 Timer？时间轮？
