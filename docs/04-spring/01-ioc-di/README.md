# 01 IoC 与 DI

[← Spring 集合](../README.md) | [下一知识点 →](../02-bean-lifecycle/README.md)

## 讲解

IoC：谁来 `new`。权利交给容器。  
DI：容器把依赖塞进对象。推荐**构造器注入**（依赖显式、易测、不可变）。

BeanFactory 是基础容器；ApplicationContext 在其上加事件、国际化、AOP。  
模块：Core / Beans / Context / AOP / JDBC / TX / Web。

`@Autowired` 按类型；`@Resource` 按名称。字段注入难测，循环依赖也更容易被「藏起来」。

## Demo

在 `spring-learn` 目录：

```bash
cd spring-learn
./mvnw -q -DskipTests compile exec:java -Dexec.mainClass=org.example.learn.spring.IocDiDemo
```

对比自己 `new` 和容器装配，并看到 `@PostConstruct` / `@PreDestroy`。

## 练习

1. 把构造器注入改成字段 `@Autowired`，再改回构造器，体会测试时哪个更好 mock。  
2. 给 `UserService` 加一个接口和两个实现，用 `@Qualifier` 注入。  
3. 口头回答：IoC 和 DI 谁包含谁。

## 对应题目

- 什么是 Spring IOC？IOC 有什么好处？  
- Spring 中的 DI 是什么？有几种注入方式？  
- Spring 由哪些重要模块组成？  
- 什么是 Spring Bean？ApplicationContext 是什么？
