# 01 List 与数组

[← Java 集合](../README.md) | [下一知识点：HashMap →](../02-hashmap/README.md)

## 讲解

`List` 是「有下标的一串元素」。Java 里最常用两个实现：

- **ArrayList**：底层 `Object[]`。内存连续，按下标 `get` 是 O(1)，中间插入要搬后面的元素。
- **LinkedList**：双向链表。头尾插入 O(1)，按下标 `get` 要从头走到第 i 个，是 O(n)。

数组还带来 CPU 缓存友好：连续读几乎不 miss。链表每个节点是单独对象，指针乱跳。

默认用 ArrayList。只有「队列式头尾操作」才考虑 LinkedList 或 `ArrayDeque`。

## Demo

```bash
cd java-basics-learn && ./run.sh java-list
```

代码：`java-basics-learn/src/main/java/org/example/learn/java/collection/ListArrayDemo.java`

看输出里 **随机 get** 和 **头插** 两组耗时差一个数量级以上，就是连续内存 vs 链表的差别。

## 练习

1. 把 Demo 里的 `n` 改成 5000 和 100000，看 LinkedList 的 `get` 是否近似平方增长。  
2. 用 `ArrayDeque` 做 5 万次 `addFirst`，和 LinkedList 头插比一次。  
3. 解释：为什么 `list.get(i)` 对 LinkedList 会慢，即使 i 已经很小？

## 对应题目

- Java 中有哪些集合类？请简单介绍  
- 数组和链表在 Java 中的区别是什么？  
- List 接口有哪些实现类？  
- ArrayList 和 LinkedList 有什么区别？  
- ArrayList 的扩容机制是什么？  
- 栈和队列在 Java 中的区别是什么？
