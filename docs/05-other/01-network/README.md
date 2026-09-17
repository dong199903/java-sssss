# 01 计算机网络

[← 其他集合](../README.md) | [下一知识点 →](../02-mq/README.md)

## 讲解

TCP/IP 四层：链路、网络、传输、应用。TCP 在 IP 之上提供可靠字节流：三次握手同步序号，四次挥手因全双工。TIME_WAIT 在主动关闭方，保证最后 ACK 和旧包消失。

滑动窗口做流量控制；超时重传和快速重传做可靠。UDP 无连接。

HTTP 明文；HTTPS = HTTP+TLS。2.0 多路复用；3.0 换 QUIC。Cookie 是存储机制，Session 是服务端会话，Token 常自包含。

TCP **没有消息边界** → 粘包/半包，要用长度前缀或分隔符。

## Demo

```bash
cd java-basics-learn && ./run.sh net-frame
```

## 练习

1. 用浏览器开发者工具看一次 HTTPS 请求的证书和 HTTP/2 frames（能看多少算多少）。  
2. 解释为什么两次握手不够。  
3. 短连接导致 TIME_WAIT 过多时，优先长连接而不是乱开 reuse。

## 对应题目

- TCP/IP 四层、OSI 七层  
- TCP 连接 / 解决什么问题 / 与 UDP 区别  
- 三次握手、四次挥手、TIME_WAIT  
- 超时重传、滑动窗口、粘包拆包  
- HTTP vs HTTPS、1.0 vs 2.0、2.0 vs 3.0  
- Cookie Session Token  
- 从输入网址到页面显示  
- 什么是 BIO、NIO、AIO？Channel / Selector？  
- Java 的 I/O 流是什么？什么是网络编程？
