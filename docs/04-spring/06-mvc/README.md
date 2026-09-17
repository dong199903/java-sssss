# 06 Spring MVC

[← 上一知识点](../05-transaction/README.md) | [Spring 集合](../README.md) | [下一知识点 →](../07-boot/README.md)

## 讲解

`DispatcherServlet` 中央调度：

请求 → HandlerMapping 找处理器 → 拦截器 preHandle → HandlerAdapter 执行 → postHandle → 返回值处理（`@ResponseBody` 或 ViewResolver）→ afterCompletion。

过滤器是 Servlet 规范，没有 Spring 上下文也能跑；拦截器在 Spring 里，能注入 Bean。

异常：`HandlerExceptionResolver` / `@ControllerAdvice`。

## Demo

本包不包含完整 Web 工程。对照讲解画出 DispatcherServlet 调度链；过滤器 vs 拦截器可跑：

```bash
cd java-basics-learn && ./run.sh net-filter
```

## 练习

1. 写 `HandlerInterceptor`，统计每个请求耗时。  
2. 对比 Filter 和 Interceptor 各做鉴权的差别。  
3. 画 DispatcherServlet 九大组件里你用过的 4 个。

## 对应题目

- Spring MVC 具体的工作原理？核心组件？  
- 说下对 Spring MVC 的理解？父子容器？  
- Spring 和 Spring MVC 的关系？  
- 拦截器和过滤器有什么区别？（下一节 11）
