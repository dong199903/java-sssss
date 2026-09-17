# 08 Spring Cloud 与治理

[← 上一知识点](../07-boot/README.md) | [Spring 集合](../README.md) | [下一知识点 →](../09-factory-scope/README.md)

## 讲解

分布式 ≠ 微服务。微服务是拆分风格，一定是分布式。

- 配置中心：Nacos / Apollo / Config  
- 网关：统一入口，路由鉴权限流，不要塞业务。Gateway 基于 WebFlux  
- RPC vs HTTP：IDL、治理、性能；gRPC 是 HTTP/2 上的 RPC。Dubbo 是 RPC，Gateway 是入口，常组合  
- 负载均衡：轮询、加权、最少连接、一致性哈希  
- 雪崩 → 超时 + 隔离 + 熔断 + 限流 + 降级  
- 分布式事务：尽量最终一致 + 可靠消息；必要时 Seata AT/TCC/Saga  

本包只讲治理模型。完整微服务工程不在这个学习包里。

## Demo

对照讲解区分：网关入口、RPC 调用、熔断降级。有现成 Cloud 工程时再自己起 Nacos / Gateway。

## 练习

1. 用自己的话区分熔断和降级。  
2. 列出 3 种可以避免分布式事务的设计。  
3. 走一遍 Gateway 路由到 order-service 的路径。

## 对应题目

- 分布式和微服务的区别  
- 配置中心 / API 网关  
- Dubbo 和 Spring Cloud Gateway 的区别  
- HTTP 与 RPC  
- 负载均衡算法  
- 服务雪崩 / 熔断 / 降级  
- 什么情况需要分布式事务，有哪些方案  
- 什么是 Seata
