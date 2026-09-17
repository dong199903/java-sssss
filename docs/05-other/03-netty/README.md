# 03 Netty 与 IO 模型

[← 上一知识点](../02-mq/README.md) | [其他集合](../README.md) | [下一知识点 →](../04-patterns/README.md)

## 讲解

阻塞 / 非阻塞 / IO 复用 / 异步。Java NIO 是复用。select 有上限且每次拷贝；poll 仍线性扫；epoll 就绪队列，适合海量连接。

Reactor：boss 接连接，worker 读写。Netty 默认主从 Reactor。相对裸 NIO：处理半包、空轮询 bug、内存池、线程模型。

零拷贝：组合缓冲、sendfile、堆外。Pipeline 是责任链。空轮询：统计后重建 Selector。

## Demo

拆包见 `./run.sh net-frame`。把长度前缀理解了，再看 Netty 的 `LengthFieldBasedFrameDecoder` 就不抽象。

## 练习

1. 对照画出单 Reactor 和主从 Reactor。  
2. 解释为何不要在 EventLoop 线程里做 JDBC。  
3. 列出 Netty 用到的 3 种设计模式。

## 对应题目

- 几种 I/O 模型  
- Select Poll Epoll  
- Reactor 线程模型  
- Netty 场景 / 为何不用裸 NIO / 为何快  
- 零拷贝、粘包拆包、空轮询、设计模式
