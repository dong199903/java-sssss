package org.example.learn.redis;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Cache Aside：先更新「库」，再删缓存。演示先改缓存再改库失败会留下脏缓存。
 */
public final class CacheAsideDemo {

    static final Map<String, String> db = new ConcurrentHashMap<>();
    static final Map<String, String> cache = new ConcurrentHashMap<>();

    public static void run() {
        db.put("u1", "v1");
        System.out.println("读: " + get("u1") + " （回源并写入缓存）");
        System.out.println("再读: " + get("u1") + " （命中缓存）");

        updateWrong("u1", "v2");
        System.out.println("[错误顺序] 先改缓存再改库但库失败 → cache=" + cache.get("u1") + " db=" + db.get("u1"));

        cache.clear();
        db.put("u1", "v1");
        updateRight("u1", "v3");
        System.out.println("[正确顺序] 先改库再删缓存 → cache=" + cache.get("u1") + " db=" + db.get("u1") + " （下次读回源）");
        System.out.println("穿透: 空值也要短 TTL 或 Bloom。击穿: 热点过期加锁重建。雪崩: TTL 打散 + Redis 高可用。");
    }

    static String get(String key) {
        String v = cache.get(key);
        if (v != null) {
            return v;
        }
        v = db.get(key);
        if (v != null) {
            cache.put(key, v);
        }
        return v;
    }

    static void updateWrong(String key, String value) {
        cache.put(key, value);
        // 模拟 DB 失败
    }

    static void updateRight(String key, String value) {
        db.put(key, value);
        cache.remove(key);
    }
}
