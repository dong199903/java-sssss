# 03 循环依赖与三级缓存

[← 上一知识点](../02-bean-lifecycle/README.md) | [Spring 集合](../README.md) | [下一知识点 →](../04-aop/README.md)

## 讲解

A 依赖 B、B 依赖 A。

- **构造器环**：创建 A 必须先有 B，创建 B 必须先有 A → 容器直接失败。`@Lazy` 或拆类。  
- **字段/setter + 单例**：实例化后提前暴露工厂，对方先注入引用，再继续初始化。

三级缓存：

1. `singletonObjects` 成品  
2. `earlySingletonObjects` 早期曝光对象  
3. `singletonFactories` 工厂，需要时才 `getEarlyBeanReference`（生成代理）

二级不够：有 AOP 时必须延迟创建代理，否则注入的是原对象、池里是代理。  
prototype 不解决循环依赖。

## Demo

```bash
cd spring-learn
./mvnw -q -DskipTests compile exec:java -Dexec.mainClass=org.example.learn.spring.CircularDependencyDemo
```

应看到构造器环失败、字段环成功。

## 练习

1. 给 `FieldA` 加一个 `@Transactional` 或简单 AOP，debug `getEarlyBeanReference`。  
2. 把字段注入改成构造器，确认失败信息。  
3. 画三级缓存在「A 注入 B、B 注入 A」时的读写顺序。

## 对应题目

- 什么是循环依赖？  
- Spring 如何解决循环依赖？  
- 为什么需要三级缓存，二级不够吗？
