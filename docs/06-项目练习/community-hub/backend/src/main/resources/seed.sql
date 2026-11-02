-- 已有功能的测试数据：小区、用户、帖子、评论、点赞、收藏。可重复执行。
USE community;

INSERT INTO community (id, name, city_code, center_geohash)
VALUES
    (1, '人民广场', '310100', 'wtw3sj'),
    (2, '天安门', '110100', 'wx4g0b')
ON DUPLICATE KEY UPDATE name = VALUES(name), center_geohash = VALUES(center_geohash);

INSERT INTO `user` (id, username, password_hash, nickname, city_code, role)
VALUES
    (1001, 'demo', '62cc2d8b4bf2d8728120d052163a77df', '小林', '310100', 0),
    (1002, 'amy', '62cc2d8b4bf2d8728120d052163a77df', '小安', '110100', 0)
ON DUPLICATE KEY UPDATE nickname = VALUES(nickname);

INSERT INTO post (
    id, community_id, author_id, title, content, latitude, longitude, geohash,
    view_count, like_count, comment_count, favorite_count, created_at
)
VALUES
    (101, 1, 1001, '人民广场晨练', '今天风不大，适合出来走走。', 31.230416, 121.473701, 'wtw3sj', 12, 1, 1, 1, DATE_SUB(NOW(), INTERVAL 30 MINUTE)),
    (102, 1, 1001, '地铁口快递柜满了', '靠近2号口的柜子已经满了，大家改去南侧。', 31.231000, 121.474200, 'wtw3sj', 8, 0, 0, 0, DATE_SUB(NOW(), INTERVAL 28 MINUTE)),
    (103, 1, 1002, '广场舞音量', '晚上八点后能不能把音响调小一点。', 31.229800, 121.472900, 'wtw3sj', 20, 0, 0, 0, DATE_SUB(NOW(), INTERVAL 26 MINUTE)),
    (104, 1, 1001, '小区门口修路', '东门这周半封闭，外卖改走西门。', 31.230100, 121.473100, 'wtw3sj', 6, 0, 0, 0, DATE_SUB(NOW(), INTERVAL 24 MINUTE)),
    (105, 1, 1002, '丢失一只橘猫', '脖子上有红项圈，看到请联系我。', 31.230800, 121.474000, 'wtw3sj', 15, 0, 0, 0, DATE_SUB(NOW(), INTERVAL 22 MINUTE)),
    (106, 1, 1001, '周末便民集市', '周六上午中心绿地有集市。', 31.229500, 121.473500, 'wtw3sj', 4, 0, 0, 0, DATE_SUB(NOW(), INTERVAL 20 MINUTE)),
    (107, 1, 1002, '楼道灯不亮', '3号楼2层灯坏了两天了。', 31.231200, 121.473800, 'wtw3sj', 3, 0, 0, 0, DATE_SUB(NOW(), INTERVAL 18 MINUTE)),
    (108, 1, 1001, '共享单车乱停', '南门堆在盲道上了。', 31.230200, 121.472500, 'wtw3sj', 9, 0, 0, 0, DATE_SUB(NOW(), INTERVAL 16 MINUTE)),
    (109, 1, 1002, '求推荐早餐', '附近有没有还在营业的豆浆铺。', 31.230600, 121.474500, 'wtw3sj', 7, 0, 0, 0, DATE_SUB(NOW(), INTERVAL 14 MINUTE)),
    (110, 1, 1001, '停水通知', '明天上午9点到11点计划停水。', 31.229900, 121.473900, 'wtw3sj', 18, 0, 0, 0, DATE_SUB(NOW(), INTERVAL 12 MINUTE)),
    (111, 1, 1002, '儿童游乐区开放', '改造完了，周末可以去。', 31.231500, 121.473200, 'wtw3sj', 5, 0, 0, 0, DATE_SUB(NOW(), INTERVAL 10 MINUTE)),
    (112, 1, 1001, '旧物交换', '把用不到的书放到门卫室。', 31.230300, 121.474800, 'wtw3sj', 2, 0, 0, 0, DATE_SUB(NOW(), INTERVAL 8 MINUTE)),
    (201, 2, 1002, '天安门看升旗', '人很多，建议早一点到。', 39.904211, 116.407395, 'wx4g0b', 30, 0, 0, 0, DATE_SUB(NOW(), INTERVAL 40 MINUTE)),
    (202, 2, 1002, '长安街骑行', '非机动车道今天比较顺。', 39.904800, 116.408100, 'wx4g0b', 11, 0, 0, 0, DATE_SUB(NOW(), INTERVAL 35 MINUTE)),
    (203, 2, 1001, '附近吃饭', '广场东侧有几家还开门。', 39.903600, 116.406800, 'wx4g0b', 6, 0, 0, 0, DATE_SUB(NOW(), INTERVAL 25 MINUTE)),
    (204, 2, 1002, '安检排队', '北入口快一些。', 39.905000, 116.407000, 'wx4g0b', 4, 0, 0, 0, DATE_SUB(NOW(), INTERVAL 15 MINUTE))
ON DUPLICATE KEY UPDATE title = VALUES(title), content = VALUES(content);

INSERT INTO comment (id, post_id, author_id, content, created_at)
VALUES (301, 101, 1002, '我也在，风确实舒服。', DATE_SUB(NOW(), INTERVAL 20 MINUTE))
ON DUPLICATE KEY UPDATE content = VALUES(content);

INSERT INTO post_like (id, post_id, user_id)
VALUES (401, 101, 1002)
ON DUPLICATE KEY UPDATE post_id = VALUES(post_id);

INSERT INTO post_favorite (id, post_id, user_id)
VALUES (501, 101, 1002)
ON DUPLICATE KEY UPDATE post_id = VALUES(post_id);
