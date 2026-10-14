# 03 社区集市：秒杀与下单扣库存

[← 上一阶段：微服务拆分](../02-微服务拆分/README.md) | [项目练习](../README.md)

在同一个社区里加「集市活动」：限量名额，先到先得。用三条由浅到深的链路把一致性做完，三条都要落地，后面的链路不替换前面的代码，方便你对照着讲。

| 链路 | 场景 | 一致性手段 |
|------|------|------------|
| 甲 | 普通限量，流量不大 | 订单和库存同一库、同一本地事务 |
| 乙 | 下单服务和库存服务已经拆开 | 本地消息表 + 幂等消费 + 定时补发 |
| 丙 | 活动开始瞬间的秒杀 | Redis Lua 预扣 + 异步落库 + 延迟关单 + 对账 |

阶段一允许通知消息在提交后丢失。本阶段的订单和库存不允许靠这种做法。

## 1. 新服务与新表

新增 `market-service`，注册到 Nacos，经网关暴露。链路甲、丙的表都在库 `community_market`。链路乙为了演练拆分，再起一个仅用于乙的 `stock-service`（库 `community_stock`）和把订单留在 market。做完乙之后，丙的秒杀库存仍放在 market 自己的 Redis 和 `community_market`，不绕到 stock-service，避免一条秒杀路径同时背上两种一致性方案。

```sql
CREATE TABLE activity (
    id          BIGINT      NOT NULL PRIMARY KEY,
    title       VARCHAR(80) NOT NULL,
    price_cent  INT         NOT NULL,
    stock       INT         NOT NULL,
    sold        INT         NOT NULL DEFAULT 0,
    start_at    DATETIME    NOT NULL,
    end_at      DATETIME    NOT NULL,
    KEY idx_time (start_at, end_at)
) ENGINE=InnoDB;

CREATE TABLE activity_order (
    id           BIGINT      NOT NULL PRIMARY KEY,
    order_no     VARCHAR(32) NOT NULL,
    activity_id  BIGINT      NOT NULL,
    user_id      BIGINT      NOT NULL,
    status       TINYINT     NOT NULL COMMENT '0待支付 1已支付 2已关闭',
    request_id   VARCHAR(64) NOT NULL COMMENT '客户端幂等号',
    created_at   DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    pay_deadline DATETIME    NOT NULL,
    UNIQUE KEY uk_order_no (order_no),
    UNIQUE KEY uk_user_activity (user_id, activity_id),
    UNIQUE KEY uk_request (request_id)
) ENGINE=InnoDB;

CREATE TABLE outbox (
    id          BIGINT       NOT NULL PRIMARY KEY,
    event_id    VARCHAR(64)  NOT NULL,
    event_type  VARCHAR(32)  NOT NULL,
    payload     VARCHAR(2000) NOT NULL,
    status      TINYINT      NOT NULL DEFAULT 0 COMMENT '0待发送 1已发送',
    created_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_event (event_id)
) ENGINE=InnoDB;

CREATE TABLE consumed_event (
    event_id   VARCHAR(64) NOT NULL PRIMARY KEY,
    created_at DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

CREATE TABLE reconcile_log (
    id         BIGINT       NOT NULL PRIMARY KEY,
    activity_id BIGINT      NOT NULL,
    detail     VARCHAR(500) NOT NULL,
    created_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;
```

链路乙的 `community_stock.stock`：`activity_id` 主键，`available` 可售数量。乙的订单表可以复用 `activity_order` 的结构，放在 market 库；库存行只在 stock 库。两库之间没有外键。

`sold + 剩余库存` 在链路甲里等于活动初始库存。扣减语句必须带条件，靠影响行数判断成功，不先 SELECT 再在应用里减：

```sql
UPDATE activity
SET stock = stock - 1, sold = sold + 1
WHERE id = ? AND stock > 0;
```

影响行数为 0 则下单失败，事务回滚。这是防超卖的数据库兜底，链路丙异步落库时同样执行它。

## 2. 链路甲：同库本地事务

**S1** `POST /api/market/activities/{id}/orders`

请求体包含 `requestId`。开始前活动必须在 `start_at` 与 `end_at` 之间。

同一事务内：

1. 按上面的 `UPDATE` 扣库存。
2. 插入订单，状态为待支付，`pay_deadline = now + 15 分钟`。
3. 任一步失败整笔回滚。

**S2** 同一 `requestId` 再请求一次，返回原订单，库存不二次扣减。  
**S3** 同一用户对同一活动第二单被唯一索引拒绝，库存只扣一次。  
**S4** 库存为 0 时并发 20 个不同用户，成功订单数为 0，`stock` 不变成负数。  
**S5** 初始库存 5，30 个不同用户各下一单，成功 5 单，`stock = 0`，`sold = 5`。

甲不经过 Redis，用来证明「数量不大时，一条本地事务就够」。

## 3. 链路乙：拆服务后的本地消息表

普通下单改成两步，仅用于这一条链路的接口，例如 `POST /api/market/distributed-orders`。不要改掉链路甲的接口。

1. market 在**一个本地事务**里插入待支付订单（此时还没占库存），并插入 `outbox`，`event_type = STOCK_RESERVE`，payload 含 `eventId、orderId、activityId`。
2. 事务提交之后，发送线程读出待发送 outbox，发到 RabbitMQ，成功后把该行标为已发送。
3. 每 10 秒扫描仍为待发送且创建超过 5 秒的 outbox，再次发送。重复发送靠 `eventId` 唯一。
4. stock-service 消费：先插 `consumed_event`。插入成功才执行 `UPDATE stock SET available = available - 1 WHERE activity_id = ? AND available > 0`。影响行数为 1 则发 `STOCK_RESERVED`；为 0 则发 `STOCK_REJECTED`。这两条结果消息同样要求消费者幂等。
5. market 收到 `STOCK_RESERVED` 后把订单留在待支付（库存已占）。收到 `STOCK_REJECTED` 后把订单改为已关闭。

订单在库存确认前对用户显示「排队中」。前端乙的页面轮询订单状态即可，轮询间隔 1 秒，最多 10 次。

**B1** 杀掉 market 进程的时机用测试模拟：事务提交后不调用发送方法，只留下 `status=0` 的 outbox。启动补发任务后，stock-service 仍能收到并只扣一次库存。  
**B2** 同一 `eventId` 投递两次，`available` 只减 1。  
**B3** `available = 0` 时，订单最终变为已关闭，库存保持 0。  
**B4** stock-service 先宕机、消息堆在队列里，恢复后订单仍能离开「排队中」，不会永远卡住。需要消费者手动 ack，处理成功才 ack。

乙的订单如果最终关闭，不要求在本链路做支付。把「占库存」和「拒绝」做可靠，就是这一链路的全部。

## 4. 链路丙：秒杀

活动开始前，管理接口把 `activity.stock` 装入 Redis：

| Key | 含义 |
|-----|------|
| `seckill:stock:{activityId}` | 剩余库存整数 |
| `seckill:bought:{activityId}` | Set，member 为 userId |

**Lua（一次执行，中途不穿回应用）：**

1. 当前时间由调用方传入，脚本判断是否在活动时间内；不在则返回「未开始」或「已结束」。时间判断放在脚本外也可以，但库存和已购必须在脚本内一起改。
2. `SISMEMBER` 已购则返回「已买过」，不扣库存。
3. 库存小于 1 则返回「卖完」，不写已购集合。
4. `DECR` 库存，`SADD` 用户。
5. 返回成功。

应用只有在脚本返回成功时才发消息 `SECKILL_ORDER`，消息体含 `eventId、activityId、userId、requestId`。

market 消费该消息，在本地事务里执行第 1 节的条件更新并插入订单。数据库更新失败（库存条件不满足或唯一键冲突）则把 Redis 库存 `INCR` 回去，并把用户从已购集合移除，避免「Redis 少卖、人却没有订单」。

**支付与关单**

- 本练习的支付做成确认接口：`POST /api/market/orders/{id}/pay`，只有待支付且未过 `pay_deadline` 可以变成已支付。不接真实支付渠道。
- 下单成功后投递一条延迟消息：队列设置 TTL 15 分钟，过期后进入死信队列 `seckill.close`。
- 关单消费者：订单仍是待支付则改为已关闭，数据库 `stock+1, sold-1`（`sold > 0` 才减），Redis 库存加回，用户从已购集合移除。订单已支付则忽略该消息。

RabbitMQ 上要能指出：哪条队列负责下单削峰，哪条队列的 TTL 负责 15 分钟，死信队列叫什么。

**前端** 活动页展示剩余名额（读一个只返回 Redis 库存的接口）、开始时间、抢购按钮。结果页用订单号轮询。重复点击使用同一个 `requestId`。

### 秒杀验收

| 编号 | 条件 | 标准 |
|------|------|------|
| K1 | 库存 100，500 个不同用户并发抢 | 成功订单 ≤ 100，数据库 `stock >= 0` |
| K2 | 同一用户并发 10 次 | 只产生 1 笔订单 |
| K3 | 活动开始前调用 Lua | 不减 Redis 库存 |
| K4 | 消费者处理前 Redis 已扣、数据库尚未插入时，把消息投递两次 | 订单 1 笔，库存只扣 1 |
| K5 | 待支付超过 15 分钟（测试里把 TTL 改成 5 秒） | 订单关闭，数据库库存与 Redis 库存都加回，用户可以再次购买 |
| K6 | 已支付后再收到关单消息 | 状态仍是已支付，库存不回加 |
| K7 | 网关对秒杀下单路径做限流 | 超出的请求在网关被拒绝，到不了 Lua。阈值写进配置，压测时能看到拒绝计数 |

K1 的「不超过 100」同时由 Lua 和数据库条件更新保证。只做其中一层不算通过。

限流用网关过滤器实现令牌桶或滑动窗口均可，阈值例如每秒 50 个下单请求。算法和 [场景篇](../../05-other/06-scene/README.md) 里的令牌桶对应：突发可以过，持续超限被拒。

## 5. 对账

每分钟对每个进行中的活动写检查，发现不一致就插入 `reconcile_log`，不要静默改数（自动改数容易把支付中的单修坏）。检查三项：

- 数据库 `stock + sold` 是否等于活动创建时的初始库存
- 成功占用中的订单数（待支付 + 已支付）是否等于 `sold`
- Redis 剩余库存是否等于数据库 `stock` 加上「已预扣但消费者还没落库」的消息数；消息数拿不到精确值时，允许 Redis 库存 ≤ 数据库 `stock`，并在日志里说明这是在途预扣。若 Redis 库存大于数据库库存，记为不一致

**R1** 手工把 Redis 库存改大，跑一次任务，`reconcile_log` 出现该活动。  
**R2** 正常跑完 K1 且消费者追平后，再跑对账，不产生新的不一致记录。

阶段二那个「赞的行数 vs like_count」的检查仍保留，本阶段的对账是同一习惯用在库存上。

## 6. 和另外两条链路的关系

讲的时候按这个顺序，不要混成一个接口：

1. 甲证明库存和订单在一个库时，条件更新加事务就不会超卖，失败会一起回滚。
2. 乙证明库拆开之后，用「业务行和 outbox 同行提交」避免只下了单却没人去占库存，用 `consumed_event` 避免消息重放超卖。
3. 丙证明秒杀先把冲突挡在 Redis，数据库仍用甲的那条 `UPDATE` 做最终兜底；预扣和落库之间的空窗靠回补、关单和对账填上。

Seata AT 可以选做在链路乙上对照一次：它能把两库更新包进一个全局事务，但秒杀路径仍然用丙。对照时说清楚锁持有时间和超时回滚对热点行的影响即可，不作为出口条件。

## 7. 阶段出口

- [ ] S1～S5 自动化通过
- [ ] B1～B4 通过，能指出 outbox 哪一行在补发
- [ ] K1～K7 通过，K1 的并发结果写进下表
- [ ] K5 的延迟队列在管理台能指出 TTL 队列和死信队列
- [ ] R1、R2 通过
- [ ] 前端能对一场活动完成抢购、看到成功或卖完、对待支付单完成确认支付

| 项 | 日期 | 结果 |
|----|------|------|
| K1 库存 100 / 并发 500 | | 成功单 __，最终 stock __，是否出现负数 |
| K5 关单回补 | | |
| R2 追平后对账 | | |

出口口述：一笔秒杀从点击到待支付，Redis、消息、数据库各写了什么；用户不支付时库存如何回来；哪一种失败会超卖，是被 Lua 挡住还是被 `stock > 0` 挡住。
