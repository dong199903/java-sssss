# 07 AQS 与锁

[← 上一知识点](../06-jmm-cas/README.md) | [Java 集合](../README.md) | [下一知识点：JVM →](../08-jvm-gc/README.md)

## 讲解

AQS（AbstractQueuedSynchronizer）用一个 `int state` + CLH 变体队列实现同步器。

- 独占：`ReentrantLock`  
- 共享：`Semaphore`、`CountDownLatch`  

子类实现 `tryAcquire` / `tryRelease`，排队、park/unpark、取消节点由 AQS 管。

`synchronized` 是 JVM 内置锁（对象头 + Monitor），自动释放，非公平为主。  
`ReentrantLock` 是 API：可公平、可中断、可 `tryLock`、多个 `Condition`，必须 `finally unlock`。

锁优化：缩小范围、拆分、读写锁、能无锁就 CAS。先正确再快。

## Demo

```bash
cd java-basics-learn && ./run.sh java-aqs
```

看重入 `holdCount` 增加，以及 `tryLock(50ms)` 在锁被占用时失败。

## 练习

1. 用两个 `Condition` 实现有界队列（put 满则 await，take 空则 await）。  
2. 把死锁 Demo 改成 `tryLock` + 超时放弃，确认不再死锁。  
3. 读 `ReentrantLock.Sync` 源码，对照 `state` 如何表示重入次数。

## 对应题目

- 说说 AQS 吧？  
- Synchronized 和 ReentrantLock 有什么区别？  
- ReentrantLock 的实现原理？  
- 你了解读写锁吗？什么是 StampedLock？  
- Java 的 synchronized 是怎么实现的？  
- 轻量级锁是否会自旋？自适应自旋？升级到重量级后还会降吗？  
- 如何优化锁的使用？
