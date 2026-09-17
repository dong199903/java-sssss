# Spring 知识点集合

[← 总目录](../README.md)

从 IoC 容器到循环依赖、AOP/事务、Boot 自动配置、Cloud 治理。  
Java 代理 Demo 在 `java-basics-learn`；IoC / 循环依赖 / FactoryBean Demo 在 `spring-learn`（不启动 Web）。

## 学习顺序

| 编号 | 难度 | 知识点 | 怎么跑 | 文档 |
|------|------|--------|--------|------|
| 01 | 基础 | IoC / DI / 模块 | `spring-learn` `IocDiDemo` | [01-ioc-di](01-ioc-di/README.md) |
| 02 | 进阶 | Bean 生命周期与启动 refresh | 同上，看 PostConstruct | [02-bean-lifecycle](02-bean-lifecycle/README.md) |
| 03 | 进阶 | 循环依赖与三级缓存 | `CircularDependencyDemo` | [03-circular](03-circular/README.md) |
| 04 | 进阶 | AOP 与 JDK/CGLIB 代理 | `./run.sh java-proxy` | [04-aop](04-aop/README.md) |
| 05 | 进阶 | 事务传播 | 对照现有 `@Transactional` | [05-transaction](05-transaction/README.md) |
| 06 | 进阶 | MVC 请求链 | 对照讲解画调度链 | [06-mvc](06-mvc/README.md) |
| 07 | 进阶 | Boot 自动配置与 starter | 对照讲解 | [07-boot](07-boot/README.md) |
| 08 | 进阶 | Cloud：网关、熔断、Seata | 对照讲解 | [08-cloud](08-cloud/README.md) |
| 09 | 进阶 | FactoryBean 与作用域 | `spring-learn` `FactoryScopeDemo` | [09-factory-scope](09-factory-scope/README.md) |
| 10 | 进阶 | 自调用：事务 / @Async 失效 | `java-self` | [10-self-invoke](10-self-invoke/README.md) |
| 11 | 进阶 | 过滤器 vs 拦截器 | `net-filter` | [11-filter-interceptor](11-filter-interceptor/README.md) |
