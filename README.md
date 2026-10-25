# Java 后端知识点学习包

独立复制包：文档 + 可运行 Demo。需要 **JDK 17+**。Maven 可用自带 `mvnw`，不必本机预装 。

克隆后从本目录开始：

```bash
git clone <你的仓库地址>
cd knowledge-learn
```

## 目录

```
knowledge-learn/
├── docs/                  讲解 / 练习 / 对应题目（按分类）
│   └── 06-项目练习/       社区单体 → 微服务拆分 → 秒杀与库存一致性
├── java-basics-learn/     Java、MySQL 概念、Redis 概念、网络拆包等 Demo
└── spring-learn/          IoC、循环依赖、FactoryBean Demo（不启动 Web）
```

学习顺序看 [docs/README.md](docs/README.md)。建议从 [docs/01-java/01-list-array](docs/01-java/01-list-array/README.md) 起。  
项目需求在 [docs/06-项目练习](docs/06-项目练习/README.md)，原理没把握时先回到 01～05。  
题库怎么拆进分类：[docs/题库分析.md](docs/题库分析.md)。

## 跑 Java Demo

```bash
cd java-basics-learn
chmod +x mvnw run.sh          # Linux / macOS 第一次
./run.sh                      # 列出全部 id
./run.sh java-hashmap
./run.sh all                  # 一次跑完全部
./run.sh learn java-hashmap   # 两种写法都可以
```

Windows：`run.cmd java-hashmap`

## 跑 Spring Demo

```bash
cd spring-learn
chmod +x mvnw
./mvnw -q -DskipTests compile exec:java -Dexec.mainClass=org.example.learn.spring.IocDiDemo
./mvnw -q -DskipTests compile exec:java -Dexec.mainClass=org.example.learn.spring.CircularDependencyDemo
./mvnw -q -DskipTests compile exec:java -Dexec.mainClass=org.example.learn.spring.FactoryScopeDemo
```

## MySQL 脚本

```bash
mysql -uroot -p < docs/02-mysql/sql/00-init.sql
```

每个知识点都是：**Demo → 讲解 → 练习 → 文末对应题目**。
