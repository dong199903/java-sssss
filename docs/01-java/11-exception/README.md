# 11 异常体系

[← 上一知识点](../10-string-box/README.md) | [Java 集合](../README.md) | [下一知识点 →](../12-generics-serial/README.md)

## 讲解

`Throwable` 分两支：

- **Error**：JVM 自身不行了（OOM、StackOverflow），不要 catch 了当没事。  
- **Exception**：业务可处理。  
  - **RuntimeException**：编程错误，可不声明。  
  - **受检异常**：编译器强迫处理（IOException）。

`try/finally` 在 return 前也会跑。`final` 是修饰符，`finalize` 已废弃。  
`wait` 释放监视器锁；`sleep` 不释放 synchronized。

## Demo

```bash
cd java-basics-learn && ./run.sh java-exception
```

## 练习

1. 在 finally 里 return，看会不会吞掉 try 里的异常。  
2. 自定义一个受检异常，强迫调用方处理。  
3. 讲清 wait / sleep / yield 三个「停一下」的差别。

## 对应题目

- Exception 和 Error 有什么区别？  
- 运行时异常和编译时异常的区别？  
- final、finally、finalize 各是什么？  
- wait() 和 sleep() 的区别？  
- Thread.sleep 和 yield 的区别？  
- 一个线程被两次 start 会发生什么？
