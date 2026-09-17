package org.example.learn.redis;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * SET NX + 唯一 token 再删除。错误示范：不校验 token 会误删别人的锁。
 */
public final class DistLockDemo {

    record LockVal(String token, long deadlineMs) {
    }

    static final ConcurrentHashMap<String, LockVal> redis = new ConcurrentHashMap<>();

    public static void run() {
        String tokenA = UUID.randomUUID().toString();
        System.out.println("A 加锁: " + tryLock("order", tokenA, 200));
        System.out.println("B 加锁: " + tryLock("order", "token-b", 200) + " （应为 false）");
        sleep(250);
        System.out.println("过期后 B 加锁: " + tryLock("order", "token-b", 200));
        unlockWrong("order");
        System.out.println("错误 DEL 不看 token，可能删掉 B 的锁。正确做法: Lua 比较 token 再删。");
        System.out.println("Redisson: Hash 可重入 + 看门狗续期。主从故障转移时单节点锁仍可能丢。");
    }

    static boolean tryLock(String key, String token, long ttlMs) {
        long now = System.currentTimeMillis();
        LockVal cur = redis.get(key);
        if (cur != null && cur.deadlineMs > now) {
            return false;
        }
        redis.put(key, new LockVal(token, now + ttlMs));
        return true;
    }

    static void unlockWrong(String key) {
        redis.remove(key);
    }

    private static void sleep(long ms) {
        try {
            TimeUnit.MILLISECONDS.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
