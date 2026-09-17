# 08 JVM 内存与 GC

[← 上一知识点](../07-aqs-lock/README.md) | [Java 集合](../README.md) | [下一知识点 →](../09-equals-oop/README.md)

## 讲解

运行时数据区：

- 线程私有：PC、虚拟机栈、本地方法栈  
- 共享：堆、方法区（JDK 8+ 元空间，本地内存）  
- 堆外：DirectByteBuffer 等  

类加载 → 执行引擎（解释 + JIT 热点编译）。GC 算法：标记清除 / 复制 / 整理；分代是策略。收集器：Parallel 吞吐，G1 平衡（JDK9 默认），ZGC 低延迟。

OOM 要对上区域：heap / Metaspace / direct / GC overhead / 无法创建线程。  
调优先看 GC 日志和 SLA，再改代码（少造对象），最后才调参。`-Xms` 与 `-Xmx` 相同，打开 HeapDumpOnOutOfMemoryError。

## Demo

```bash
cd java-basics-learn && ./run.sh java-jvm
```

打印当前堆大小并对照各区域、OOM 类型。不主动打满内存。

## 练习

1. 用 `-Xmx32m` 跑一个无限 `new byte[1024*1024]` 的小程序，看 `Java heap space`。  
2. 加 `-XX:+HeapDumpOnOutOfMemoryError`，用 MAT 看 Dominator Tree。  
3. 阅读 GC 日志（JDK17：`-Xlog:gc*`），标出 Young GC 和 Full/Mixed。

## 对应题目

- JVM 由哪些部分组成？  
- JVM 的内存区域是如何划分的？  
- 编译执行与解释执行的区别？JVM 用哪种？  
- Java 中有哪些垃圾回收算法？  
- 常见垃圾收集器有哪些？  
- 为什么 Java 8 用元空间替换永久代？  
- GC 调优目标 / 如何调优 / 常用 JVM 参数  
- 哪几种情况会 OOM？OOM 后怎么分析？  
- 怎么分析当前内存占用？
