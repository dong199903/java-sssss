# 06 JMM、volatile、CAS、ThreadLocal

[← 上一知识点](../05-thread-pool/README.md) | [Java 集合](../README.md) | [下一知识点：AQS →](../07-aqs-lock/README.md)

## 讲解

JMM 规定工作内存 / 主内存和 **happens-before**，不让你直接操作 CPU 缓存。

- **原子性**：操作不可拆。`i++` 不是原子。  
- **可见性**：写之后别的线程能读到。`volatile` 读写插内存屏障。  
- **有序性**：禁止非法重排序。单线程 as-if-serial，多线程靠 HB。

CAS：内存值等于期望才换成新值。ABA 要用版本号。`AtomicInteger` 就是 CAS。

ThreadLocalMap 的 **key 弱引用、value 强引用**。线程池线程很长命，用完必须 `remove`。

## Demo

```bash
cd java-basics-learn && ./run.sh java-jmm
```

对比普通 boolean 停止标志（可能停不掉）和 volatile；对比 `++` 丢数和 AtomicInteger；提醒 ThreadLocal 泄漏。

## 练习

1. 给停止标志去掉 volatile，加 `synchronized` 读写，看能否停掉。  
2. 用 `AtomicStampedReference` 写一个会触发 ABA 再被版本号挡住的小例子。  
3. 在线程池里 `ThreadLocal.set` 大对象且不 remove，用 VisualVM 看线程持有的实例（选做）。

## 对应题目

- 什么是 Java 内存模型（JMM）？  
- 什么是 Java 中的原子性、可见性和有序性？  
- Java 中 volatile 关键字的作用是什么？  
- Java 中的 final 关键字是否能保证变量的可见性？  
- 什么是 happens-before / 指令重排？  
- 什么是 Java 的 CAS？什么是 ABA 问题？  
- 使用过哪些原子类 / 累加器？  
- 为什么需要 ThreadLocal？如何隔离？key 为何弱引用？最佳实践？缺点？  
- InheritableThreadLocal / TransmittableThreadLocal / FastThreadLocal？  
- Volatile 与 Synchronized 的区别？synchronized 能不能禁止重排？
