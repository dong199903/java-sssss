-- 四个服务各用各的库。可重复执行。已有数据仍在 community，不在这里搬运。
CREATE DATABASE IF NOT EXISTS community_user DEFAULT CHARSET utf8mb4;
CREATE DATABASE IF NOT EXISTS community_content DEFAULT CHARSET utf8mb4;
CREATE DATABASE IF NOT EXISTS community_interaction DEFAULT CHARSET utf8mb4;
CREATE DATABASE IF NOT EXISTS community_notify DEFAULT CHARSET utf8mb4;

USE community_user;

CREATE TABLE IF NOT EXISTS `user` (
    id            BIGINT       NOT NULL PRIMARY KEY,
    username      VARCHAR(64)  NOT NULL,
    password_hash VARCHAR(128) NOT NULL,
    nickname      VARCHAR(64)  NOT NULL,
    city_code     VARCHAR(16)  NULL,
    role          INT          NOT NULL DEFAULT 0,
    created_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS user_follow (
    id           BIGINT   NOT NULL PRIMARY KEY,
    follower_id  BIGINT   NOT NULL,
    followee_id  BIGINT   NOT NULL,
    created_at   DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_pair (follower_id, followee_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS admin_op_log (
    id          BIGINT       NOT NULL PRIMARY KEY,
    operator_id BIGINT       NOT NULL,
    action      VARCHAR(32)  NOT NULL,
    target_type VARCHAR(32)  NOT NULL,
    target_id   BIGINT       NOT NULL,
    detail      VARCHAR(500) NOT NULL,
    created_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    KEY idx_created (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

USE community_content;

CREATE TABLE IF NOT EXISTS community (
    id             BIGINT       NOT NULL PRIMARY KEY,
    name           VARCHAR(64)  NOT NULL,
    city_code      VARCHAR(16)  NOT NULL,
    center_geohash CHAR(6)      NOT NULL,
    created_at     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    KEY idx_city (city_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS post (
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
    KEY idx_author (author_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS comment (
    id         BIGINT        NOT NULL PRIMARY KEY,
    post_id    BIGINT        NOT NULL,
    author_id  BIGINT        NOT NULL,
    content    VARCHAR(1000) NOT NULL,
    created_at DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    KEY idx_post_created (post_id, created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS consumed_event (
    event_id    VARCHAR(64) NOT NULL PRIMARY KEY,
    consumed_at DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

USE community_interaction;

CREATE TABLE IF NOT EXISTS post_like (
    id         BIGINT   NOT NULL PRIMARY KEY,
    post_id    BIGINT   NOT NULL,
    user_id    BIGINT   NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_user_post (user_id, post_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS post_favorite (
    id         BIGINT   NOT NULL PRIMARY KEY,
    post_id    BIGINT   NOT NULL,
    user_id    BIGINT   NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_user_post (user_id, post_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

USE community_notify;

CREATE TABLE IF NOT EXISTS notification (
    id          BIGINT       NOT NULL PRIMARY KEY,
    receiver_id BIGINT       NOT NULL,
    event_type  VARCHAR(16)  NOT NULL,
    biz_id      BIGINT       NOT NULL,
    content     VARCHAR(200) NOT NULL,
    read_flag   TINYINT      NOT NULL DEFAULT 0,
    created_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_dedup (receiver_id, event_type, biz_id),
    KEY idx_receiver_created (receiver_id, created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
