# 13 反射、注解与 SPI

[← 上一知识点](../12-generics-serial/README.md) | [Java 集合](../README.md) | [下一知识点 →](../14-classload/README.md)

## 讲解

反射：运行时拿到 `Class` / `Method` / `Field`，再 `newInstance` / `invoke`。框架靠它，业务代码少用。

注解是元数据。`SOURCE` 给编译器，`RUNTIME` 才能被反射读到。Spring 的 `@Autowired` 就是运行时注解 + 反射/字节码。

SPI：接口在核心包，实现写在 `META-INF/services/全限定名`。`ServiceLoader` 加载。JDBC 驱动是经典例子。这会 **打破双亲委派**（接口由启动类加载器加载，实现在应用加载器）。

动态代理见 `java-proxy`；自调用失效见 `java-self`。

## Demo

```bash
cd java-basics-learn && ./run.sh java-reflect
```

## 练习

1. 用反射读一个 private 字段并改掉。  
2. 自己加一个 Greeter 实现，写进 `META-INF/services`，看 ServiceLoader 能否找到。  
3. 对比 JDK 代理和 CGLIB：谁要接口、谁能代理类。

## 对应题目

- 你使用过反射吗？如何应用？  
- 注解原理是什么？  
- 什么是 SPI？  
- 什么是动态代理？  
- JDK 动态代理和 CGLIB 的区别？  
- 内部类是什么？有什么作用？
