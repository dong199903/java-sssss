package org.example.learn.redis;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * SDS 带长度；小 List 用紧凑编码；Pipeline 降 RTT 但不原子。
 */
public final class RedisEncodingDemo {

    public static void run() {
        demoSds();
        demoCompactList();
        demoPipeline();
    }

    private static void demoSds() {
        byte[] raw = "hello".getBytes(StandardCharsets.UTF_8);
        Sds sds = new Sds(raw);
        System.out.println("[SDS] len=" + sds.len + " bytes=" + sds.buf.length + "  O(1) 取长度，可存 \\0");
        System.out.println("  短字符串 EMBSTR 对象头和 buf 一次分配。历史阈值 39→44 与 redisObject 对齐有关。");
    }

    private static void demoCompactList() {
        List<String> ziplistLike = new ArrayList<>(List.of("a", "b", "c"));
        System.out.println("[listpack/ziplist] 小元素连续紧凑: " + ziplistLike + " ，元素多了改成 quicklist/listpack 链。");
        System.out.println("  Hash/ZSet 也有「小用紧凑、大用哈希/跳表」的编码升级。");
    }

    private static void demoPipeline() {
        FakeRedis redis = new FakeRedis();
        long t0 = System.nanoTime();
        for (int i = 0; i < 50; i++) {
            redis.getWithRtt("k" + i);
        }
        long seq = System.nanoTime() - t0;
        t0 = System.nanoTime();
        List<String> keys = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            keys.add("k" + i);
        }
        redis.pipelineGet(keys);
        long pipe = System.nanoTime() - t0;
        System.out.println("[Pipeline] 50 次往返 ns=" + seq + " ；打包一次 ns=" + pipe);
        System.out.println("  Pipeline 不原子；MGET 单命令原子但只能同类。要原子用 Lua / MULTI。MULTI 无隔离无回滚。");
    }

    static final class Sds {
        final int len;
        final byte[] buf;

        Sds(byte[] buf) {
            this.buf = buf;
            this.len = buf.length;
        }
    }

    static final class FakeRedis {
        final Map<String, String> kv = new LinkedHashMap<>();

        FakeRedis() {
            for (int i = 0; i < 50; i++) {
                kv.put("k" + i, "v" + i);
            }
        }

        String getWithRtt(String key) {
            sleepMicros(200);
            return kv.get(key);
        }

        List<String> pipelineGet(List<String> keys) {
            sleepMicros(200);
            List<String> out = new ArrayList<>();
            for (String k : keys) {
                out.add(kv.get(k));
            }
            return out;
        }

        private static void sleepMicros(int micros) {
            long end = System.nanoTime() + micros * 1_000L;
            while (System.nanoTime() < end) {
                Thread.onSpinWait();
            }
        }
    }
}
