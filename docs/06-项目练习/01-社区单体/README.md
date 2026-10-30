# 01 地方社区交流系统（单体）

[← 项目练习](../README.md) | [下一阶段：微服务拆分 →](../02-微服务拆分/README.md)

## 1. 目标

独立完成一个读多写少的社区后端和对应前端。流量集中在帖子列表、附近、详情；写集中在发帖、评论、点赞、收藏。

做完要同时满足三件事：

- 热点帖详情在缓存命中时稳定返回，数据库不被打穿。
- 缓存里的标题、正文、计数与数据库一致；允许的延迟只有浏览量（最多一个刷库周期）。
- 重复点赞、重复通知、越权操作都有数据库或接口层兜底，不靠前端禁用按钮。

参考标尺（必须自己压测后再引用）：150 并发、3000 次热点详情，回源个位数、错误为 0；混合读场景记下 QPS 与 P50/P99。历史记录里曾经出现「3000 次回源 6 次、命中率 99.8%、P99 约 2ms」和「本机约 1.4 万 QPS、P50 6ms、P99 33ms」。那是某一台机器的结果，换机器会变。

## 2. 范围

一个 Spring Boot 进程，一个 MySQL 库 `community`，一个 Redis，一个 RabbitMQ。

前端用 Vue 3 把主路径点通。管理端只做操作日志查询，不做完整后台。

本阶段不做：服务拆分、注册中心、秒杀、跨库扣库存。通知允许「事务已提交但消息没发出」这种丢失，阶段三用本地消息表补上。浏览量允许刷库前进程崩溃带来的少量丢失；点赞、评论、收藏计数不允许丢。

## 3. 角色与主路径

| 角色 | 能做的事 |
|------|----------|
| 游客 | 看列表、附近、详情、热榜。看详情也计浏览 |
| 登录用户 | 注册登录、发帖改自己的帖、评论、点赞、收藏、关注、看自己的通知 |
| 管理员 | 上面全部，外加查询操作日志。管理员删除帖子必须写日志 |

主路径：

1. 注册并登录，拿到令牌。
2. 在某个小区发帖。
3. 列表、附近、详情能看到这篇帖。
4. 另一用户点赞、评论、收藏。作者收到三条通知；热榜分数按权重上升。
5. 作者修改标题。详情和该小区列表不再返回旧标题。
6. 同一用户对同一帖狂点赞，库里仍只有一行，点赞数只加一。

## 4. 技术约束

| 项 | 要求 |
|----|------|
| JDK | 21 |
| Spring Boot | 3.2.x |
| ORM | MyBatis-Plus，主键雪花 |
| 数据库 | MySQL 8，库名 `community`，引擎 InnoDB，字符集 utf8mb4 |
| 缓存 | Redis 7 |
| 锁 | Redisson |
| 消息 | RabbitMQ 4.x，Spring AMQP，手动 ack |
| 前端 | Vue 3 + Vite + Vue Router + Pinia |
| 部署 | Docker Compose 五个容器：`mysql`、`redis`、`rabbitmq`、`api`、`web` |
| 接口 | JSON，统一 `code / message / data`。校验失败 400，未登录 401，越权 403，资源不存在 404 |
| 密码 | BCrypt。日志和响应里不出现明文密码和密码哈希 |
| 时间 | 数据库 `DATETIME`，应用用 `LocalDateTime`，时区 Asia/Shanghai |

## 5. 表（恰好 9 张）

```sql
CREATE TABLE user (
    id            BIGINT       NOT NULL PRIMARY KEY,
    username      VARCHAR(32)  NOT NULL,
    password_hash VARCHAR(100) NOT NULL,
    nickname      VARCHAR(32)  NOT NULL,
    city_code     VARCHAR(16)  NOT NULL,
    role          TINYINT      NOT NULL DEFAULT 0 COMMENT '0 用户 1 管理员',
    created_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_username (username)
) ENGINE=InnoDB;

CREATE TABLE community (
    id             BIGINT       NOT NULL PRIMARY KEY,
    name           VARCHAR(64)  NOT NULL,
    city_code      VARCHAR(16)  NOT NULL,
    center_geohash CHAR(6)      NOT NULL,
    created_at     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    KEY idx_city (city_code)
) ENGINE=InnoDB;

CREATE TABLE post (
    id              BIGINT        NOT NULL PRIMARY KEY,
    community_id    BIGINT        NOT NULL,
    author_id       BIGINT        NOT NULL,
    title           VARCHAR(80)   NOT NULL,
    content         VARCHAR(4000) NOT NULL,
    latitude        DECIMAL(9,6)  NOT NULL,
    longitude       DECIMAL(9,6)  NOT NULL,
    geohash         CHAR(6)       NOT NULL,
    view_count      BIGINT        NOT NULL DEFAULT 0,
    like_count      INT           NOT NULL DEFAULT 0,
    comment_count   INT           NOT NULL DEFAULT 0,
    favorite_count  INT           NOT NULL DEFAULT 0,
    created_at      DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    KEY idx_community_created (community_id, created_at),
    KEY idx_geohash (geohash),
    KEY idx_author (auth
) ENGINE=InnoDB;

CREATE TABLE comment (
    id         BIGINT        NOT NULL PRIMARY KEY,
    post_id    BIGINT        NOT NULL,
    author_id  BIGINT        NOT NULL,
    content    VARCHAR(1000) NOT NULL,
    created_at DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    KEY idx_post_created (post_id, created_at)
) ENGINE=InnoDB;

CREATE TABLE post_like (
    id         BIGINT   NOT NULL PRIMARY KEY,
    post_id    BIGINT   NOT NULL,
    user_id    BIGINT   NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_user_post (user_id, post_id)
) ENGINE=InnoDB;

CREATE TABLE post_favorite (
    id         BIGINT   NOT NULL PRIMARY KEY,
    post_id    BIGINT   NOT NULL,
    user_id    BIGINT   NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_user_post (user_id, post_id)
) ENGINE=InnoDB;

CREATE TABLE user_follow (
    id           BIGINT   NOT NULL PRIMARY KEY,
    follower_id  BIGINT   NOT NULL,
    followee_id  BIGINT   NOT NULL,
    created_at   DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_pair (follower_id, followee_id)
) ENGINE=InnoDB;

CREATE TABLE notification (
    id          BIGINT       NOT NULL PRIMARY KEY,
    receiver_id BIGINT       NOT NULL,
    event_type  VARCHAR(16)  NOT NULL COMMENT 'LIKE COMMENT FAVORITE',
    biz_id      BIGINT       NOT NULL COMMENT '点赞/评论/收藏行的 id',
    content     VARCHAR(200) NOT NULL,
    read_flag   TINYINT      NOT NULL DEFAULT 0,
    created_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_dedup (receiver_id, event_type, biz_id),
    KEY idx_receiver_created (receiver_id, created_at)
) ENGINE=InnoDB;

CREATE TABLE admin_op_log (
    id          BIGINT       NOT NULL PRIMARY KEY,
    operator_id BIGINT       NOT NULL,
    action      VARCHAR(32)  NOT NULL,
    target_type VARCHAR(32)  NOT NULL,
    target_id   BIGINT       NOT NULL,
    detail      VARCHAR(500) NOT NULL,
    created_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    KEY idx_created (created_at)
) ENGINE=InnoDB;
```

种子数据至少 2 个小区、20 个用户、200 篇帖，其中 1 篇固定为压测热点帖。

### 索引必须亲眼看过

- **L1** 列表：`WHERE community_id = ? ORDER BY created_at DESC` 的 `EXPLAIN` 使用 `idx_community_created`，`type` 不是 `ALL`。
- **L2** 附近：用 geohash 前缀做范围查询（`geohash >= ? AND geohash < ?`），走 `idx_geohash`。禁止 `LIKE '%xx'`。
- **L3** 练习一次结构漂移再修回来：先去掉 `idx_geohash`，确认附近查询变成全表扫描；去掉 `notification.uk_dedup`，确认重复消息会插入两行；不建 `admin_op_log` 时管理日志接口不可用。修回后三条都恢复。这次练习对应「上线前对账发现缺索引、缺唯一约束、缺表」。

## 6. 接口

除注册、登录、列表、附近、详情、热榜外，都要带登录令牌。

| 编号 | 方法与路径 | 行为 |
|------|------------|------|
| A1 | `POST /api/auth/register` | 用户名唯一，重复返回明确业务错误 |
| A2 | `POST /api/auth/login` | 返回令牌 |
| P1 | `POST /api/posts` | 作者为当前用户，经纬度算出 6 位 geohash |
| P2 | `PUT /api/posts/{id}` | 只有作者能改 |
| P3 | `DELETE /api/posts/{id}` | 作者可删；管理员可删且写 `admin_op_log` |
| P4 | `GET /api/posts` | `communityId` + 页码，走缓存 |
| P5 | `GET /api/posts/nearby` | geohash 前缀 + 页码 |
| P6 | `GET /api/posts/{id}` | 详情缓存；浏览计数走 Redis |
| C1 | `POST /api/posts/{id}/comments` | 同事务插评论、`comment_count+1`；提交后删缓存并发通知消息 |
| C2 | `GET /api/posts/{id}/comments` | 按时间倒序分页 |
| K1 | `POST /api/posts/{id}/likes` | 见第 8 节 |
| K2 | `DELETE /api/posts/{id}/likes` | 删掉自己的赞，计数减 1，热榜减 2 分 |
| F1 | `POST /api/posts/{id}/favorites` | 与点赞相同的幂等规则，热榜权重 +4 |
| F2 | `DELETE /api/posts/{id}/favorites` | 取消收藏，热榜减 4 分 |
| R1 | `GET /api/ranks/hot` | 读 ZSET，返回前 20 及分数 |
| N1 | `GET /api/notifications` | 只返回当前用户的 |
| N2 | `POST /api/notifications/{id}/read` | 只能标自己的 |
| U1 | `POST /api/users/{id}/follow` | 不能关注自己；重复关注不报 500 |
| G1 | `GET /api/admin/op-logs` | 仅管理员 |

发帖、改帖、删帖、评论成功后，删除该帖详情缓存，并用 `SCAN` 删除该 `communityId` 下的列表缓存。

## 7. 缓存

### Key

| Key | 类型 | 内容 | TTL |
|-----|------|------|-----|
| `post:detail:{id}` | String | 详情 JSON（含各计数，不含是否已赞） | 30 分钟 + 0～300 秒随机 |
| `post:detail:nil:{id}` | String | 空值标记，值为 `1` | 60 秒 |
| `post:list:{communityId}:{page}` | String | 该页列表 | 2 分钟 + 0～30 秒随机 |
| `post:view:{id}` | String | 未刷库的浏览增量 | 无固定 TTL，刷库成功后删除 |
| `post:view:dirty` | Set | 有未刷增量的帖子 id | 不过期 |
| `rank:hot` | ZSET | member=postId，score=热度 | 不过期，靠衰减任务清理 |
| `lock:post:{id}` | Redisson 锁 | 重建详情缓存 | 租约 10 秒 |

是否已赞、已藏跟当前用户有关，不要放进详情缓存。

### 读详情（击穿、穿透、雪崩都在这条路径）

1. 读 `post:detail:{id}`，命中直接返回。
2. 空值标记存在则按不存在返回，不再查库。
3. 抢 `lock:post:{id}`。
4. 抢到：再读一次缓存（双重检查）。仍没有则查库。查到就写入详情并带随机 TTL；查不到就写空值标记。释放锁。
5. 没抢到：隔 50ms 再读缓存，最多 3 次。期间被重建则返回缓存。仍然没有则**直接查库返回，并且不写缓存**，避免没拿到锁的请求一起写。

**C-NIL** 连续请求一个不存在的 id，数据库只在第一次被访问。  
**C-LOCK** 删掉热点详情 key 后立刻并发 50 个请求，回源建缓存的次数为 1。可以用日志计数断言。  
**C-TTL** 抽 10 个详情 key，TTL 不完全相等。

### 写

**C-DEL** 改帖、改计数都是：数据库事务提交成功之后再删缓存。删除放在事务同步的 `afterCommit`。事务回滚则不删。

禁止先改缓存再改库。删详情 key 失败时记错误日志，并有一次定时补偿：按最近更新的帖子再删一次详情 key。补偿可以简单做成每分钟扫描 `updated_at` 在最近 2 分钟的帖子。

**C-SCAN** 清理某个小区的列表缓存使用 `SCAN`，`MATCH post:list:{communityId}:`*，`COUNT` 100，然后删除扫到的 key。代码和运行日志里都不允许出现 `KEYS`。

## 8. 点赞、评论、收藏

**K-IDEM** `post_like` 以 `(user_id, post_id)` 唯一。插入成功才 `like_count = like_count + 1`，并且这两步在同一个事务里。唯一约束冲突时事务只回滚这一次插入尝试，接口返回「已点赞」，计数不变。

用测试或脚本对同一用户同一帖发起 20 个并发点赞：表中 1 行，`like_count` 增加 1。

评论、收藏同样：关系行（或评论行）与计数加减同一事务。收藏的唯一键是 `(user_id, post_id)`。

取消点赞：删除行成功才减计数。减计数使用 `like_count = like_count - 1` 且 `like_count > 0`，避免减成负数。

热榜在事务提交后更新，更新失败只影响榜单，不回滚点赞：

| 事件 | `ZINCRBY rank:hot` |
|------|-------------------|
| 点赞 / 取消 | +2 / -2 |
| 评论（本阶段不支持删评论） | +3 |
| 收藏 / 取消 | +4 / -4 |

**R-DECAY** 每 30 分钟：取出 `rank:hot` 全部成员（练习数据量下全取；超过 1000 个时只处理分数最高的 1000 个），分数乘 0.8，小于 1 的移除。

**R-REBUILD** 提供管理员接口或启动后可手动触发的任务：用数据库的赞、评、藏计数重算 ZSET（赞×2 + 评×3 + 藏×4），覆盖 `rank:hot`。用于 Redis 清空之后把榜单找回来。

## 9. 浏览量

**V1** 详情接口对 `post:view:{id}` 做 `INCR`，并把帖子 id 放进 `post:view:dirty`。这次请求不更新 `post.view_count`。

**V2** 每 5 分钟：从 dirty 集合取出 id，读取增量，执行 `view_count = view_count + ?`，成功后把 Redis 增量清掉并移出 dirty。一批最多 200 个 id。

刷库过程中新进来的浏览不能丢：用「读出当前值并删除」的原子操作（Lua：GET 后 DEL，返回旧值），刷库失败则把该值加回去。

## 10. 通知

交换器 `community.topic`（topic），队列 `notify.queue`，绑定 `notify.#`。  
死信交换器 `notify.dlx`，队列 `notify.dlq`。消费失败进入死信前先重入队，同一条最多处理 3 次（用消息头计数，超过则拒绝且不重新入队）。

消息体：

```json
{
  "eventId": "雪花或UUID",
  "eventType": "LIKE",
  "bizId": 123,
  "receiverId": 9,
  "content": "有人赞了你的帖子"
}
```

自己操作自己的帖子不发通知。

**N-ASYNC** 评论/点赞/收藏接口在事务提交后发消息。接口返回前不插入 `notification`。

**N-BATCH** 消费者手动 ack，`prefetch` 100。凑满 500 条或等待满 1 秒就批量插入，先到先执行。批量插入遇到唯一键冲突的行跳过，其他行照常写入（使用 `INSERT IGNORE` 或等价写法）。

**N-DEDUP** 同一 `receiverId + eventType + bizId` 投递两次，表里只有 1 行。

消费者处理成功后 ack。批量里若数据库整体失败，这一批 nack 并按重试规则走，不在不确定是否写入时 ack。

## 11. 越权

下列用例做成自动化测试，预期状态码必须是 401 或 403，数据库无变化：

| 编号 | 操作 |
|------|------|
| Z1 | 无令牌发帖 |
| Z2 | 用户 A 修改用户 B 的帖 |
| Z3 | 用户 A 把用户 B 的通知标已读 |
| Z4 | 普通用户调用 `GET /api/admin/op-logs` |
| Z5 | 用户 A 取消用户 B 点的赞 |

`GET /api/notifications` 在服务端按当前用户过滤。只在前端隐藏别人的通知不算通过。

## 12. 前端页面

| 页面 | 接哪些接口 |
|------|------------|
| 登录 / 注册 | A1 A2 |
| 小区帖子列表 | P4，进入详情 |
| 附近 | P5 |
| 详情 | P6 C2，按钮接 K1 K2 F1 F2 C1 |
| 发帖 / 编辑 | P1 P2 |
| 热榜 | R1 |
| 通知 | N1 N2 |

令牌放在 Pinia，刷新页面后仍能从本地存储恢复。列表和详情展示标题、作者昵称、四种计数。

## 13. 测试与压测

单测覆盖：空值标记、锁抢占失败后的降级读、TTL 随机、唯一键冲突不加计数、热榜加减分、geohash 范围边界、越权服务层。  
接口测试覆盖第 6 节每个写接口的成功与失败，以及 Z1～Z5。

不为凑条数写空测试。场景覆盖完再统计数量。

压测只打已登录或公开读接口，数据用种子热点帖。

| 场景 | 做法 | 记录 |
|------|------|------|
| 热点详情 | 150 线程，共 3000 次 `GET` 同一帖 | 回源 SQL 次数、错误数、P99、命中率 |
| 混合读 | 列表与详情约 9:1，持续 60 秒 | QPS、P50、P99、错误数 |
| 点赞并发 | 20 并发同一用户同一帖 | 行数、`like_count` |

回源次数用 SQL 日志或数据源拦截器计数，不靠猜。结果写在下面。

### 验收记录

| 项 | 日期 | 机器 | 结果 |
|----|------|------|------|
| 热点详情 3000 次 | | | 回源 __ 次，错误 __，P99 __ ms |
| 混合读 60 秒 | | | QPS __，P50 __ ms，P99 __ ms，错误 __ |
| 20 并发点赞 | | | 行数 __，计数增量 __ |
| `EXPLAIN` 列表 / 附近 | | | |
| Compose 五容器 | | | |

## 14. 部署

`docker compose up -d --build` 之后：

- MySQL 8 执行建表和种子数据
- Redis 7 无密码（仅限本练习网络）
- RabbitMQ 管理台可打开，能看到 `notify.queue`
- 前端通过环境变量访问 API
- 浏览器完成第 3 节主路径

## 15. 阶段出口

全部勾选才进入微服务阶段。

- [ ] 9 张表与第 5 节约束一致，L1 L2 通过，L3 做过并修回
- [ ] C-NIL、C-LOCK、C-TTL、C-DEL、C-SCAN 通过
- [ ] K-IDEM、R-DECAY、R-REBUILD、V1、V2 通过
- [ ] N-ASYNC、N-BATCH、N-DEDUP 通过，死信队列在连续失败时能收到消息
- [ ] Z1～Z5 自动化通过
- [ ] 前端主路径点通
- [ ] 五容器一条命令启动
- [ ] 三行压测记录是你自己跑的

出口口述（不看文档讲一遍）：详情未命中时谁回源；点赞的行和计数为什么不会加两次；通知为什么可以重复投递但只有一行；浏览量为什么可以慢一个周期，点赞数为什么不行。
