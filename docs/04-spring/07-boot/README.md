# 07 Spring Boot

[← 上一知识点](../06-mvc/README.md) | [Spring 集合](../README.md) | [下一知识点 →](../08-cloud/README.md)

## 讲解

Boot = 约定 + 自动配置 + 起步依赖 + 内嵌容器，不是新容器。

`@SpringBootApplication` = 配置 + 扫描 + `@EnableAutoConfiguration`。  
选择器按 `AutoConfiguration.imports` 装配置类，`@ConditionalOnClass` 等决定是否生效。

starter：依赖清单 + 自动配置。自己做 starter 就是拆 autoconfigure 模块。

配置优先级：命令行 > 环境变量 > 配置文件。`debug=true` 看自动配置报告。

本包只讲原理。要看自动配置报告，需要自备一个 Boot 项目并加 `debug=true`。

## Demo

对照 `IocDiDemo` 理解「容器 refresh」。starter 就是依赖清单 + 自动配置类，不必在本包启动 Web。

## 练习

1. 加 `debug=true`，找出 `DataSourceAutoConfiguration` 是否匹配。  
2. 写一个 `@ConditionalOnProperty` 的配置类。  
3. 解释 starter 和普通依赖的差别。

## 对应题目

- 什么是 Spring Boot？  
- 核心特性有哪些？  
- 如何理解 starter？  
- 如何实现自动配置？  
- main 方法如何启动 Web 项目？  
- Spring Boot 启动流程
