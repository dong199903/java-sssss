# 05 MyBatis

[← 上一知识点](../04-patterns/README.md) | [其他集合](../README.md) | [下一知识点 →](../06-scene/README.md)

## 讲解

MyBatis 是 SQL 映射，SQL 你写、结果它映。Hibernate 是全 ORM。国内业务库更常见 MyBatis / MyBatis-Plus。

`#{}` 预编译防注入；`${}` 拼接，仅白名单场景（如动态表名）。

一级缓存 SqlSession 级；Spring 下 Session 短，收益有限。二级缓存跨会话，一致性难，生产常关。

插件拦截 Executor 等四类对象，分页插件就是拦 query。

Plus：`BaseMapper` + Wrapper，复杂 SQL 仍回 XML。

## Demo

对照 `#{}` 预编译：SQL 日志里应是 `?` 占位，而不是拼进去的字符串。

## 练习

1. 故意用 `${username}` 拼 LIKE，说明注入风险。  
2. 打开一级缓存：同一 SqlSession 两次相同查询，看是否只打一次 SQL（非 Spring 原生体验更明显）。  
3. 读 Plus 一个 `selectList` 生成的 SQL。

## 对应题目

- MyBatis-Plus 是什么，和 MyBatis 的区别  
- MyBatis vs Hibernate  
- `#{}` 和 `${}`  
- 缓存机制  
- 插件原理如何写插件
