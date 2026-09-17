# 10 自调用：事务与 @Async 失效

[← 上一知识点](../09-factory-scope/README.md) | [Spring 集合](../README.md) | [下一知识点 →](../11-filter-interceptor/README.md)

## 讲解

`@Transactional`、`@Async`、自定义 AOP **都是代理**。同类 `this.foo()` 打在目标对象上，拦截器进不去。

失效清单（事务）：

- 自调用  
- 方法不是 public  
- 异常被吃掉 / 抛的是 checked 且没 rollbackFor  
- 库不支持（MyISAM）  
- 多线程里没把连接/事务传下去

`@Async` 同样：没 `@EnableAsync`、自调用、返回值不是 void/Future、自己的线程池没配拒绝策略。  
修法：注入自己、拆 Bean、`AopContext.currentProxy()`。

## Demo

```bash
cd java-basics-learn && ./run.sh java-self
```

看 outer 的 inner **没有**「开启事务」日志；单独调 inner 才有。

## 练习

1. 把 inner 拆到另一个类再经容器注入，确认日志出现两层事务。  
2. 列出 @Async 失效的 4 个原因。  
3. 对照 `05-transaction`：REQUIRED vs REQUIRES_NEW 在自调用时会怎样。

## 对应题目

- Spring 事务在什么情况下会失效？  
- @Async 原理？什么时候失效？如何避免内部调用失效？  
- 事务传播行为有什么用？隔离级别？  
- @PostConstruct / @PreDestroy / @Scheduled / @Cacheable / @EventListener？
