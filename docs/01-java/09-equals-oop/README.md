# 09 equals、OOP 与传值

[← 上一知识点](../08-jvm-gc/README.md) | [Java 集合](../README.md) | [下一知识点 →](../10-string-box/README.md)

## 讲解

`==` 比引用；`equals` 比业务相等。放进 HashMap/HashSet 的 key **必须同时重写 hashCode 和 equals**，否则「看起来一样」会变成两条。

Java **只有按值传递**：基本类型拷贝值，引用类型拷贝地址。方法里改字段能看见，换引用看不见。

- **重写 override**：子类替换父类方法，运行时多态。  
- **重载 overload**：同名不同参，编译期按参数选。  
- **接口** 多实现、可函数式；**抽象类** 可有状态和构造器。Java 不用类多继承，避免菱形。

封装靠访问修饰符；继承是 is-a；多态是同一接口多种实现。

## Demo

```bash
cd java-basics-learn && ./run.sh java-equals
```

## 练习

1. 只重写 equals 不重写 hashCode，看 HashSet 是否出现两个「相等」对象。  
2. 写一个方法参数是 `User u`，在方法里 `u = null`，外面引用会变吗？  
3. 口头讲清：为什么 Java 不支持类的多重继承。

## 对应题目

- Java 中 hashCode 和 equals 方法是什么？它们与 == 有什么区别？  
- hashCode 和 equals 之间有什么关系？  
- Java 中的参数传递是按值还是按引用？  
- 什么是 Java 的多态特性？  
- 接口和抽象类有什么区别？  
- 为什么 Java 不支持多重继承？  
- 方法重载和方法重写的区别？  
- 什么是继承 / 封装？访问修饰符有哪些？  
- 静态方法和实例方法的区别？  
- Object 类中有什么方法？  
- PO、VO、BO、DTO、DAO、POJO 有什么区别？
