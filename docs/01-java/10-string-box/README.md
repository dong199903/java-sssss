# 10 String、装箱与缓存

[← 上一知识点](../09-equals-oop/README.md) | [Java 集合](../README.md) | [下一知识点 →](../11-exception/README.md)

## 讲解

String **不可变**：任何修改都是新对象。字面量进常量池，`intern()` 拿池里那份。`new String("x")` 在堆上再来一个。

循环拼接用 `StringBuilder`。`StringBuffer` 只是方法加了锁。JDK 9 起内部是 `byte[]` + coder，拉丁字符省一半内存。

`Integer.valueOf` 默认缓存 **-128~127**。比较数值用 `equals`。空包装类型拆箱会 NPE。  
浮点有误差，金额用 `BigDecimal`（字符串构造）。

## Demo

```bash
cd java-basics-learn && ./run.sh java-string
```

## 练习

1. 把 Integer 比较改成 127 和 128，解释为什么一个 true 一个 false。  
2. 循环 1 万次 `s += "a"` 和 StringBuilder，感受耗时。  
3. `new BigDecimal(0.1)` 和 `new BigDecimal("0.1")` 打印出来差在哪。

## 对应题目

- String、StringBuffer、StringBuilder 的区别？  
- StringBuilder 是怎么实现的？  
- 为什么 JDK 9 把 String 的 char[] 改成 byte[]？  
- 使用 new String("yupi") 会创建多少个对象？  
- 包装类型和基本类型的区别？  
- 什么是自动装箱拆箱？  
- 什么是 Integer 缓存池？  
- 什么是不可变类？  
- BigDecimal 是什么？为什么能保证精度？  
- Float 运算后如何判断相等？  
- Java 中的基本数据类型有哪些？  
- 为什么会遇到乱码？
