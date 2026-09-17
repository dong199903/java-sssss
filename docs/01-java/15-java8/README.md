# 15 Java 8 及后续 LTS

[← 上一知识点](../14-classload/README.md) | [Java 集合](../README.md) | [下一知识点 →](../16-cow-failfast/README.md)

## 讲解

Java 8 是分水岭：Lambda、函数式接口、Stream、Optional、新 Date API、元空间替换永久代。

Stream **惰性**：中间操作不跑，遇到 collect / forEach 才跑。  
Optional 当返回值，避免 NPE；不要当字段，不要乱 `get()`。

后面 LTS 记住印象即可：

- 11：标准 HttpClient、`isBlank`  
- 17：record、sealed、instanceof 模式匹配  
- 21：虚拟线程（阻塞自动让出载体线程）

## Demo

```bash
cd java-basics-learn && ./run.sh java-java8
```

## 练习

1. 用 Stream 对一组整数过滤偶数、求平方和。  
2. 把可能为 null 的返回值改成 Optional。  
3. 解释虚拟线程和协程、平台线程的差别（一句话）。

## 对应题目

- Java8 / 11 / 17 / 21 / 25 有哪些新特性？  
- Optional 类是什么？  
- 什么是协程？Java 支持吗？  
- foreach 和 for 的区别？什么是 Iterator？
