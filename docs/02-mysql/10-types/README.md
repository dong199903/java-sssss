# 10 类型、count 与金额

[← 上一知识点](../09-redo-undo-binlog/README.md) | [MySQL 集合](../README.md) | [下一知识点 →](../11-join-buffer/README.md)

## 讲解

- **CHAR**：定长补空格；**VARCHAR**：变长，上限越大只影响行最大长度声明，不立刻占满。  
- **DATETIME** 与时区无关；**TIMESTAMP** 存 UTC，2038 问题。  
- **金额** 用 `DECIMAL`，不用 FLOAT/DOUBLE。  
- `count(*)` 计行；`count(col)` 忽略 NULL。`int(11)` 的 11 是显示宽度。  
- TEXT 有大小档位；自增到上限再插会报错。大文件别塞库，存对象存储路径。

## Demo

```bash
cd java-basics-learn && ./run.sh mysql-types
```

SQL 对照：`docs/02-mysql/sql/03-types-join.sql`

## 练习

1. 建一列 `CHAR(5)` 插入 `'ab'`，`LENGTH` / `CHAR_LENGTH` 各是多少。  
2. 插入 NULL 后对比 `count(*)` 和 `count(age)`。  
3. 解释为什么订单金额不能用 DOUBLE。

## 对应题目

- varchar 和 char 的区别？VARCHAR(100) 和 VARCHAR(10)？  
- DATETIME 和 TIMESTAMP？  
- count(*)、count(1)、count(字段) 的区别？  
- int(11) 的 11 表示什么？  
- 存储金额用什么类型？  
- TEXT 最大多长？AUTO_INCREMENT 到上限？  
- 一张表最多多少列？不推荐存图片音视频？  
- 三大范式？视图、游标、存储过程？  
- DELETE / DROP / TRUNCATE？逻辑删除？逻辑外键？  
- select/from/join/where 的执行顺序？
