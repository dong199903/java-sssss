# 02 微服务拆分

[← 上一阶段：社区单体](../01-社区单体/README.md) | [项目练习](../README.md) | [下一阶段：秒杀与库存 →](../03-秒杀与库存一致性/README.md)

阶段一的接口语义、缓存规则、表结构约束保持不变。这一阶段要掌握的是：按数据归属拆开之后，点赞和帖子计数不再在一个本地事务里，系统如何仍然对得上。

## 1. 进程与库

每个服务独立数据库，禁止跨库 JOIN，禁止一个服务直接连另一个服务的库。

```
浏览器 / Vue
      │
      ▼
 gateway :8080
      │
      ├── user-service        :8081   库 community_user
      ├── content-service     :8082   库 community_content
      ├── interaction-service :8083   库 community_interaction
      └── notify-service      :8084   库 community_notify

 Nacos :8848
 MySQL、Redis、RabbitMQ 继续用阶段一的实例，库和 vhost 分开即可
```

| 服务 | 拥有的表 | 对外能力 |
|------|----------|----------|
| user-service | `user`、`user_follow`、`admin_op_log` | 注册登录、资料、关注、管理日志、按 id 查用户是否存在 |
| content-service | `community`、`post`、`comment` | 发帖改帖、列表、附近、详情缓存、浏览刷库、评论、帖子计数 |
| interaction-service | `post_like`、`post_favorite` | 赞、藏、热榜 ZSET |
| notify-service | `notification` | 只消费消息，批量写通知。不对外提供发帖接口 |
| gateway | 无业务表 | 路由、把令牌继续往下传。阶段三再在网关上加秒杀限流 |

令牌仍由 user-service 签发。网关校验签名后把用户 id 放到内部请求头；下游信任该头，并只接受来自网关所在网络的调用。练习环境用 Compose 网络隔离即可，不引入完整的服务网格。

## 2. 依赖版本

在阶段一的 Boot 3.2 上增加：

- Spring Cloud 2023.0.x
- Spring Cloud Alibaba（Nacos 注册发现，与 Cloud 2023 匹配的那一版）
- OpenFeign
- Resilience4j（超时、熔断）

配置放在各服务本地 `application.yml`。本阶段不把 Nacos 当配置中心，避免同时学两件新事。

## 3. 路由

对外路径与阶段一相同，只是改由网关转发。前端的 API 根地址改为网关。

| 前缀 | 目标 |
|------|------|
| `/api/auth/**`、`/api/users/**`、`/api/admin/**` | user-service |
| `/api/posts/**`、`/api/posts/nearby` | content-service |
| `/api/posts/{id}/likes/**`、`/api/posts/{id}/favorites/**`、`/api/ranks/**` | interaction-service |
| `/api/notifications/**` | notify-service |

评论仍属于 content-service。点赞路径优先于 `/api/posts/**` 匹配，避免赞被转到内容服务。

## 4. 跨服务规则

**M1 发赞前确认帖子存在。** interaction-service 用 Feign 调 content-service。连接超时 200ms，读超时 500ms。连续失败达到阈值后熔断，熔断期间点赞接口直接返回「服务繁忙」，不把请求堆在调用线程上。

帖子不存在则不插入 `post_like`。

**M2 点赞与计数分离。** interaction 在自己的库里完成唯一插入（规则与阶段一 K-IDEM 相同）。事务提交后发送：

```json
{
  "eventId": "唯一",
  "eventType": "LIKE_CREATED",
  "postId": 1,
  "likeId": 2,
  "delta": 1
}
```

content-service 消费后：若 `eventId` 未见过，则 `like_count` 加 `delta`，并按阶段一的 C-DEL 删除详情缓存；见过则直接 ack。消费记录表 `consumed_event(event_id 主键)` 放在 `community_content`。取消点赞 `delta = -1`，同样带唯一 `eventId`。

评论计数仍在 content 本地事务里，因为评论表和帖子表在同一个库。收藏与点赞同一模式，权重仍是热榜 +4，计数消息 `delta` 为 1 或 -1。

**M3 热榜留在互动服务。** 分数在点赞/收藏事务提交后改 ZSET。内容服务不写 `rank:hot`。重建榜单时，互动服务向内容服务拉取各帖评论数，与本地赞、藏合并计算。这个拉取失败则本次重建中止，保留旧榜。

**M4 通知的接收人由互动/内容服务在发消息前定好。** notify-service 不回查业务库。作者 id 放进消息体。自己给自己点赞仍不发通知。

**M5 关注列表。** 阶段二不要求发帖通知全部粉丝（粉丝多时会把发帖接口拖住）。保持阶段一的范围：通知帖子作者。关注接口仍要可用，并为阶段三「活动开始提醒」留下 Feign：`GET /api/users/{id}/followers` 只在 user-service 内部或经网关给管理员调试，默认不给前端全量拉取。

## 5. 缓存归属

详情、列表、空值标记、浏览量 key 只由 content-service 读写。  
锁 `lock:post:{id}` 只由 content-service 持有。  
`rank:hot` 只由 interaction-service 读写。

点赞之后详情里的 `like_count` 允许在消息被消费前是旧的。消费成功并删缓存后，下一次详情读到新计数。用接口测试把「返回时计数可能仍是旧的，消费后刷新为新的」写成断言，给消息消费留固定等待。

## 6. 必须做的故障演练

| 编号 | 做法 | 通过标准 |
|------|------|----------|
| D1 | 停 content-service，再点赞 | 在读超时内返回繁忙，互动库不插入半截数据；熔断打开后后续请求不再等待满整个超时 |
| D2 | 恢复 content 后发一条赞，把进程停在「互动库已提交、消息未发出」无法稳定复现时，改为：先正常点赞，再把同一条消息手动投递两次 | `like_count` 只加 1，`consumed_event` 一行 |
| D3 | 停掉 interaction-service | 列表和详情仍可通过网关访问 |
| D4 | 从 Nacos 看实例 | 四个业务服务都注册；停掉一个后实例消失，再启动后出现 |

D1 说明调用方有超时和熔断。D2 说明计数的幂等不靠「消息中间件保证只投一次」。D3 说明读路径不依赖互动服务。

## 7. 部署

Compose 在阶段一的 MySQL、Redis、RabbitMQ 之外增加 Nacos 与五个应用容器（网关 + 四个服务）。前端仍是独立容器，API 指向网关。

阶段一「五个容器」的验收保留在阶段一记录里。阶段二的验收是：一条 Compose 把上述依赖和服务拉起来，前端主路径仍然走通，并且点赞计数最终与 `post_like` 行数一致。

提供一个对账 SQL/接口，只读：

- 某个帖子的 `post_like` 行数（互动库）
- 该帖 `like_count`（内容库）

二者相差只能是尚未消费的消息。等消费者追平后必须相等。做成一个你能反复执行的检查，阶段三的对账会沿用这个习惯。

## 8. 阶段出口

- [ ] 四个业务服务 + 网关注册、转发正确，前端只访问网关
- [ ] 无跨库 JOIN、无跨库事务
- [ ] M1 超时与熔断可演练（D1）
- [ ] 点赞、取消、收藏的计数消息幂等（D2）
- [ ] 阶段一的缓存测试在 content-service 上仍然通过
- [ ] 追平后赞的行数等于 `like_count`
- [ ] 能讲清：为什么点赞成功的响应里计数可以暂时是旧的，以及什么时候会新

口述：一次点赞现在写了哪个库、发了哪两条消息（计数一条、通知一条）、谁消费、重复投递时哪张表挡住了重复加一。
