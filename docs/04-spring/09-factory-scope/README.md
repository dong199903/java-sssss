# 09 FactoryBean 与 Bean 作用域

[← 上一知识点](../08-cloud/README.md) | [Spring 集合](../README.md) | [下一知识点 →](../10-self-invoke/README.md)

## 讲解

`BeanFactory` 是容器；`FactoryBean` 是一种特殊 Bean：**getBean(id)** 拿到的是 `getObject()` 的产品，**getBean("&id")** 才是工厂自己。MyBatis 的 `SqlSessionFactoryBean` 就是这个。

作用域：默认 **singleton**；**prototype** 每次 get 新对象；Web 还有 request / session。  
单例 Bean 里的可变字段有并发问题，状态放到请求对象或 ThreadLocal（用完 remove）。

`@Bean` 适合第三方类；`@Component`/`@Service`/`@Repository` 适合自己的类。`@Qualifier` / `@Primary` 处理多个实现。`@Lazy` 推迟初始化，常用来拆循环依赖。

## Demo

在 `spring-learn` 目录：

```bash
cd spring-learn
./mvnw -q -DskipTests compile exec:java \
  -Dexec.mainClass=org.example.learn.spring.FactoryScopeDemo
```

## 练习

1. 把 Instant 改成 prototype，看两次 getBean 是否还是同一对象。  
2. 给 Hello 加两个实现，用 @Primary 选一个。  
3. 口头：ObjectFactory 和 FactoryBean 差在哪。

## 对应题目

- BeanFactory / FactoryBean / ObjectFactory / ApplicationContext？  
- Bean 有几种作用域？  
- 有几种注入方式？自动装配方式？Bean 注册方式？  
- @Bean 和 @Component 区别？@Component/@Controller/@Service/@Repository？  
- @Qualifier / @Primary / @Value / @Profile / @Lazy / @Conditional / @PropertySource？  
- 单例 Bean 是否有并发安全问题？  
- IOC 容器初始化过程？Spring 的优点？
