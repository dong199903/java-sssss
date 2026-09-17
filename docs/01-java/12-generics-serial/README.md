# 12 泛型、拷贝与序列化

[← 上一知识点](../11-exception/README.md) | [Java 集合](../README.md) | [下一知识点 →](../13-reflect-spi/README.md)

## 讲解

泛型是 **编译期约束**，运行时擦成上限（通常 Object）。所以 `List<String>` 和 `List<Integer>` 运行时是同一个类。  
PECS：`? extends` 生产者只取，`? super` 消费者只放。

浅拷贝复制引用，数组/集合字段会共享。深拷贝要新开内部对象。  
序列化把对象变成字节。实现 `Serializable`；敏感字段加 `transient`。跨进程、缓存、RPC 都靠它（现在更常用 JSON，但原理一样）。

## Demo

```bash
cd java-basics-learn && ./run.sh java-generic
```

## 练习

1. 写一个 `copy(List<? extends Number> src, List<? super Number> dest)`。  
2. 给 Account 去掉 transient，再序列化，看密码是否出现。  
3. 解释：为什么不能 `new T()`。

## 对应题目

- Java 泛型的作用是什么？  
- 泛型的上下界限定符？  
- 泛型擦除是什么？  
- 深拷贝和浅拷贝有什么区别？  
- 序列化和反序列化是什么？
