# 04 设计模式

[← 上一知识点](../03-netty/README.md) | [其他集合](../README.md) | [下一知识点 →](../05-mybatis/README.md)

## 讲解

模式是可复用设计，不是背类图。常见：

| 模式 | 一句话 | 例子 |
|------|--------|------|
| 单例 | 全局一个实例 | 枚举、Spring Bean |
| 简单工厂 / 工厂方法 / 抽象工厂 | 谁来 new、几族产品 | Spring BeanFactory |
| 策略 | 可替换算法 | 支付渠道 |
| 模板方法 | 骨架固定步骤变 | JdbcTemplate |
| 代理 | 控制访问 | AOP |
| 责任链 | 沿链处理 | Filter、Netty |
| 观察者 | 发布订阅 | Spring Event |

DCL 单例必须 `volatile`。容器单例不是 `enum Singleton`。

## Demo

```bash
cd java-basics-learn && ./run.sh dp-singleton
```

## 练习

1. 用策略模式去掉一段 if-else 优惠计算。  
2. 实现 DCL，故意拿掉 volatile，说明风险。  
3. 对比工厂方法和抽象工厂，各举一个 JDK 例子。

## 对应题目

- 什么是设计模式  
- 最常见的几种及场景  
- 单例实现与线程安全  
- 简单工厂  
- 工厂 vs 抽象工厂  
- 策略 / 责任链 / 模板 / 观察者 / 代理
