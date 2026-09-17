# 02 HashMap

[← 上一知识点](../01-list-array/README.md) | [Java 集合](../README.md) | [下一知识点：CHM →](../03-concurrent-hashmap/README.md)

## 讲解

JDK 8 的 HashMap 是 **数组 + 链表 / 红黑树**：

1. 用 `hash ^ (hash >>> 16)` 扰动，再 `(n-1) & hash` 定位桶（n 必须是 2 的幂）。  
2. 冲突先拉链；链表长度 ≥ 8 且数组长度 ≥ 64 才树化。  
3. `size > capacity * 0.75` 时扩容为 2 倍；JDK 8 用「高位是否为 1」把节点分到原下标或 `原下标+oldCap`。  
4. 尾插避免 JDK 7 多线程扩容死链（仍不是线程安全）。  
5. key 必须同时重写 `hashCode` 和 `equals`。

0.75 是空间和冲突的折中，不是唯一真理。预知数量时指定初始容量，避免反复 resize。

## Demo

```bash
cd java-basics-learn && ./run.sh java-hashmap
```

代码：`HashMapDemo.java`  
会演示：equals 正确时的覆盖、同一 hash 的碰撞链、默认 16 在第 13 个元素触发扩容。

## 练习

打开 `MiniHashMap.java`，实现 `put` 和 `resize`。

完成后去掉测试上的 `@Disabled`，在 `java-basics-learn` 执行：

```bash
./mvnw -q test -Dtest=MiniHashMapExerciseTest
```

要求：覆盖已有 key、冲突拉链、超过 0.75 后容量翻倍且旧数据还能 get 到。

## 对应题目

- 说说 Java 中 HashMap 的原理？  
- Java 中 HashMap 的扩容机制是怎样的？  
- 为什么扩容采用 2 的 n 次方倍？  
- 为什么默认负载因子是 0.75？  
- 为什么 JDK 1.8 引入红黑树？  
- JDK 1.8 对 HashMap 除了红黑树还改了什么？  
- HashMap 和 Hashtable 有什么区别？  
- HashSet 和 HashMap 有什么区别？  
- 什么是 Hash 碰撞？怎么解决？  
- 使用 HashMap 时有哪些提升性能的技巧？
