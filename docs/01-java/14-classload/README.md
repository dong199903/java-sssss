# 14 类加载与双亲委派

[← 上一知识点](../13-reflect-spi/README.md) | [Java 集合](../README.md) | [下一知识点 →](../15-java8/README.md)

## 讲解

加载 → 链接（验证、准备、解析）→ 初始化（`<clinit>`）。

加载器链：Bootstrap（null）→ Platform → App。**双亲委派**：先让父加载，保证 `java.lang.String` 只有一份。

`Class.forName` 会初始化；`ClassLoader.loadClass` 默认只加载不初始化。  
破坏委派：SPI、OSGi、Tomcat 每应用一个加载器、热部署。

字节码是 `.class` 里的指令；JDK 工具：`jps` `jstack` `jmap` `javap` `jconsole`。

## Demo

```bash
cd java-basics-learn && ./run.sh java-classload
```

## 练习

1. `javap -c` 看一个简单类的字节码。  
2. 解释：为什么自己写的 `java.lang.String` 不会被用到。  
3. 列出 JDK 和 JRE 的差别，以及你用过的 JDK 工具。

## 对应题目

- Java 的类加载过程是怎样的？  
- 什么是双亲委派模型？  
- 字节码是什么？  
- JDK 和 JRE 有什么区别？  
- 你使用过哪些 JDK 工具？  
- Java 的优势是什么？面向对象和面向过程的区别？
