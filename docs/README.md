# 知识点学习总目录

> 从面试鸭「Java 热门 200」以及 Java 基础 / 集合 / 并发 / MySQL / Redis / Spring 六本题库抽知识点，**按分类成集合、由基础到进阶** 学。  
> 每个点：**Demo 跑通 → 读懂讲解 → 做练习 → 再回看对应题目**。  
> 六本题库怎么拆进分类：[题库分析](题库分析.md)

本目录是独立学习包，不依赖原来的 Spring Boot 项目。克隆仓库后从上级 [README](../README.md) 看环境要求。

## 怎么学一个知识点

每个知识点目录结构相同：

| 部分 | 做什么 |
|------|--------|
| **讲解** | 先建立模型（是什么、为什么、边界） |
| **Demo** | 可运行的最小案例，先跑再读代码 |
| **练习** | 自己改 / 补全，不改就等于没学 |
| **对应题目** | 学完再看题，用自己的话答 |

跑 Java Demo（在 `java-basics-learn` 目录）：

```bash
./run.sh              # 看目录
./run.sh java-hashmap # 跑某个 demo
./run.sh all          # 一次跑完全部
```

跑 Spring Demo（在 `spring-learn` 目录）：

```bash
./mvnw -q -DskipTests compile exec:java \
  -Dexec.mainClass=org.example.learn.spring.IocDiDemo
```

MySQL Demo 是 SQL，在客户端执行 `docs/02-mysql/sql/` 下的脚本。

## 分类集合（按这个顺序）

```
docs/
├── 01-java/     集合 → 并发 → JVM → 语言基本功
├── 02-mysql/    执行过程 → 索引 → 事务锁 → WAL
├── 03-redis/    数据结构 → 过期淘汰 → 高可用 → 缓存问题
├── 04-spring/   IoC → 生命周期 → AOP/事务 → Boot/Cloud
├── 05-other/    网络 → MQ → Netty → 设计模式 → 场景
└── 06-项目练习/  社区单体 → 微服务拆分 → 秒杀与库存一致性 → 岗位补强 → 分库分表
```

| # | 分类 | 路径 | 建议起点 |
|---|------|------|----------|
| 1 | Java | [01-java](01-java/README.md) | List vs 数组，再 HashMap |
| 2 | MySQL | [02-mysql](02-mysql/README.md) | 一条 SQL 怎么走完 |
| 3 | Redis | [03-redis](03-redis/README.md) | 为什么快、五种结构 |
| 4 | Spring | [04-spring](04-spring/README.md) | IoC/DI，再循环依赖 |
| 5 | 其他 | [05-other](05-other/README.md) | TCP 三次握手，再 MQ |
| 6 | 项目练习 | [06-项目练习](06-项目练习/README.md) | 先看 [要掌握什么](06-项目练习/汇总.md)，再做社区单体 |

同一分类内编号就是学习顺序：**小数先学**。01～05 是原理线；06 用一个社区系统把缓存、消息、拆分、秒杀和扣库存做成可以压测、可以对账的练习，并在 [04 岗位补强](06-项目练习/04-岗位补强/README.md) 和 [05 分库分表](06-项目练习/05-分库分表/README.md) 里把排查、分页、计数可靠性和分片路由补进同一个项目。

## 和题目的关系

题目在每个知识点文末「对应题目」里。先学后看题；不会的回到 Demo 改参数再跑，不要先翻答案。  
核对口径：[对应题目标准答案](对应题目标准答案.md)（面试口述版，仍建议自己先讲一遍）。
