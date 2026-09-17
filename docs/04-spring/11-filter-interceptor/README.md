# 11 过滤器、拦截器与 MVC 注解

[← 上一知识点](../10-self-invoke/README.md) | [Spring 集合](../README.md)

## 讲解

| | Filter | HandlerInterceptor |
|--|--------|---------------------|
| 规范 | Servlet | Spring MVC |
| 时机 | DispatcherServlet 之外 | 映射到 Handler 之后 |
| 依赖注入 | 弱 | 强，就是个 Bean |
| 典型用途 | 编码、CORS、鉴权包装 | 登录态、打点、权限 |

请求：Filter → DispatcherServlet → 拦截器 preHandle → Controller → postHandle → 视图/`@ResponseBody` → afterCompletion → Filter 回来。

`@RequestBody` 反序列化 JSON；`@PathVariable` 路径变量；`@ExceptionHandler` 就地处理异常。校验：`@Valid` 是 JSR，`@Validated` 是 Spring，分组更方便。

## Demo

```bash
cd java-basics-learn && ./run.sh net-filter
```

## 练习

1. 给 Filter 加「未登录直接 return，不调 chain」。  
2. 拦截器 preHandle 返回 false，看 Controller 还会不会执行。  
3. 口头讲清 `@ControllerAdvice` 和 `@ExceptionHandler` 各自拦哪一层。

## 对应题目

- 拦截器和过滤器有什么区别？  
- MVC 工作原理 / 核心组件 / 父子容器？  
- 如何定义 Controller、处理表单、视图解析器、国际化、异常？  
- @RequestBody / @ResponseBody / @PathVariable / @RequestHeader / @CookieValue / @SessionAttribute / @ModelAttribute？  
- @Validated 和 @Valid？@ExceptionHandler / @ResponseStatus？  
- Restful 是什么？WebFlux 和 MVC 的差别？  
- Spring 和 Spring MVC 的关系？JPA 和 Hibernate？
