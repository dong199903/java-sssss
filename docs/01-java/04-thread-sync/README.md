# 04 线程生命周期与死锁

[← 上一知识点](../03-concurrent-hashmap/README.md) | [Java 集合](../README.md) | [下一知识点：线程池 →](../05-thread-pool/README.md)

## 讲解

`Thread.State`：NEW → RUNNABLE → BLOCKED / WAITING / TIMED_WAITING → TERMINATED。

- `start()` 才是启动；直接 `run()` 还在当前线程。  
- BLOCKED：等 `synchronized` 监视器。  
- WAITING：`wait` / `join` / `LockSupport.park`，需要别人唤醒。  
- 一个 Thread 对象只能 `start` 一次。

死锁四条件：互斥、占有并等待、不可抢占、循环等待。破循环等待：全局锁顺序、`tryLock` 超时。

生产环境用线程池，不要到处 `new Thread`。

## Demo

```bash
cd java-basics-learn && ./run.sh java-thread
```

会打印 sleep 线程的状态变化，再用反序加锁做出 BLOCKED 对峙。死锁线程是 daemon，避免卡住进程。

## 练习

1. 把死锁改成两个线程都按 `a → b` 加锁，确认能打印 `got both`。  
2. 用 `jstack <pid>` 看 BLOCKED 和锁地址（自己另开一个会 `join` 的版本）。  
3. 用 `wait/notify` 写「两个线程交替打印 1 和 2」，类放 `org.example.learn.java.thread`。

## 对应题目

- Java 中如何创建多线程？  
- 线程的生命周期在 Java 中是如何定义的？  
- Java 中什么情况会导致死锁？如何避免？  
- Synchronized 和 ReentrantLock 有什么区别？（下一节继续）  
- Java 的 synchronized 是怎么实现的？（锁升级，结合本节 BLOCKED）
