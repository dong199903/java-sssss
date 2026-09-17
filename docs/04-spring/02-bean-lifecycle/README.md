# 02 Bean 生命周期与启动

[← 上一知识点](../01-ioc-di/README.md) | [Spring 集合](../README.md) | [下一知识点 →](../03-circular/README.md)

## 讲解

单例 Bean 主路径：

实例化 → 属性填充 → Aware 回调 → `BeanPostProcessor` 前置 → `InitializingBean` / `@PostConstruct` / init-method → 后置（**AOP 代理通常在这里**）→ 放入单例池 → 使用 → destroy。

启动主线是 `AbstractApplicationContext.refresh()`：准备工厂 → BeanFactoryPostProcessor（解析 `@Configuration`）→ 注册 BeanPostProcessor → 实例化非懒加载单例 → `finishRefresh`。

Boot 的 `SpringApplication.run` 在 refresh 外包了一层：推断 Web 类型、Environment、内嵌服务器。

## Demo

再跑一次 `IocDiDemo`，盯生命周期两行日志。  
Boot 的 `SpringApplication.run` 在 refresh 外包一层：推断 Web 类型、Environment、内嵌服务器。

## 练习

1. 写一个 `BeanPostProcessor`，在 `postProcessAfterInitialization` 打印 beanName。  
2. 给某个 Bean `@Lazy`，看启动时是否还打印 PostConstruct。  
3. 按顺序列出 refresh 里你记得的 6 步。

## 对应题目

- 说下 Spring Bean 的生命周期？  
- 说说 Spring 启动过程？  
- 说说 Spring Boot 的启动流程？  
- Spring Boot 如何通过 main 启动 Web 项目？
