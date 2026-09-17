# 16 fail-fast、COW 与 LinkedHashMap

[← 上一知识点](../15-java8/README.md) | [Java 集合](../README.md) | [下一知识点 →](../17-sync-tools/README.md)

## 讲解

`ArrayList` 迭代时结构性修改会 `ConcurrentModificationException`（fail-fast，依赖 modCount，不保证每次都抛）。正确删法：`Iterator.remove`。

`CopyOnWriteArrayList` 写时复制整表，迭代的是快照。读多写极少才用。  
`Collections.synchronizedList` 是整表锁，写多时和 COW 场景相反。

`LinkedHashMap(accessOrder=true)` + `removeEldestEntry` 可做 LRU。  
`HashSet` 就是包了一层 HashMap。`TreeMap` 红黑树有序。

## Demo

```bash
cd java-basics-learn && ./run.sh java-cow
```

## 练习

1. foreach 里 remove 最后一个元素，看是否还抛 CME。  
2. 用 LinkedHashMap 做容量 100 的 LRU。  
3. 对比 COW 和 synchronizedList：各适合什么读写比。

## 对应题目

- 你遇到过 ConcurrentModificationException 吗？如何产生？  
- CopyOnWriteArrayList 是什么？  
- COW 和 synchronizedList 的区别？  
- LinkedHashMap / TreeMap / WeakHashMap / IdentityHashMap 是什么？  
- HashSet 和 HashMap 的区别？  
- HashMap 和 Hashtable 的区别？  
- ArrayList 扩容机制？List 有哪些实现？  
- 栈和队列的区别？  
- 线程安全的集合有哪些？
