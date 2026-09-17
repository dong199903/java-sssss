# 04 AOP 与动态代理

[← 上一知识点](../03-circular/README.md) | [Spring 集合](../README.md) | [下一知识点 →](../05-transaction/README.md)

## 讲解

把日志、事务、权限从业务方法横切出去。Spring AOP 是**运行时代理**，不是 AspectJ 编译织入。

- 有接口：JDK `Proxy`  
- 否则：CGLIB 子类（不能代理 final）  

同类 `this.foo()` 不经过代理 → `@Transactional` 失效。要注入自己或 `AopContext`。

设计模式：代理、责任链（拦截器/Advisor 链）、模板。

## Demo

```bash
cd java-basics-learn && ./run.sh java-proxy
```

看到 before/after 包住 `create`。

## 练习

1. 给代理加上「抛异常也打印 afterThrowing」。  
2. 在 `OrderServiceImpl` 的 `create` 里调另一个方法 `save`，看 save 会不会进代理（不会）。  
3. 列出 Spring 里至少 4 种设计模式及对应类。

## 对应题目

- 什么是 AOP？AOP 术语 / 通知类型？  
- Spring AOP 默认用什么动态代理，两者区别？  
- Spring AOP 和 AspectJ 有什么区别？  
- 能说说拦截链的实现吗？  
- Spring 都用到哪些设计模式？
