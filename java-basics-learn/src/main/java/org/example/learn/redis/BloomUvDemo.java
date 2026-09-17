package org.example.learn.redis;

import java.util.BitSet;
import java.util.HashSet;
import java.util.Set;

/**
 * 布隆过滤器（可能误判存在）；用 Set 精确 UV 对比「估基数」的取舍。
 */
public final class BloomUvDemo {

    public static void run() {
        demoBloom();
        demoUv();
    }

    private static void demoBloom() {
        Bloom bloom = new Bloom(256, 3);
        bloom.add("user:1");
        bloom.add("user:2");
        System.out.println("[Bloom] 放过已加入的 user:1? " + bloom.mightContain("user:1"));
        System.out.println("[Bloom] 没加过的 user:9 也可能被误判，本次=" + bloom.mightContain("user:9"));
        System.out.println("  判「一定不存在」靠谱；判「存在」有假阳。缓存穿透用它挡随机 key。");
    }

    private static void demoUv() {
        Set<String> exact = new HashSet<>();
        HyperLogLogLike hll = new HyperLogLogLike();
        for (int i = 0; i < 1000; i++) {
            String uid = "u" + (i % 800);
            exact.add(uid);
            hll.add(uid);
        }
        System.out.println("[UV] 精确 Set=" + exact.size() + " ；HLL 估=" + hll.estimate() + " （省内存，允许误差）");
        System.out.println("  Redis: PFADD/PFCOUNT。GEO 是 ZSet+GeoHash。Pub/Sub 不持久，消息队列别靠它。");
    }

    static final class Bloom {
        private final BitSet bits = new BitSet();
        private final int hashes;
        private final int size;

        Bloom(int size, int hashes) {
            this.size = size;
            this.hashes = hashes;
        }

        void add(String key) {
            for (int i = 0; i < hashes; i++) {
                bits.set(idx(key, i));
            }
        }

        boolean mightContain(String key) {
            for (int i = 0; i < hashes; i++) {
                if (!bits.get(idx(key, i))) {
                    return false;
                }
            }
            return true;
        }

        private int idx(String key, int salt) {
            int h = (key + "#" + salt).hashCode();
            return Math.floorMod(h, size);
        }
    }

    /** 极简基数估计：取 hash 前导零，不是真 HLL，只建立「用统计换内存」的模型。 */
    static final class HyperLogLogLike {
        private int maxZeros;

        void add(String key) {
            int h = key.hashCode();
            int zeros = Integer.numberOfLeadingZeros(h == 0 ? 1 : h);
            if (zeros > maxZeros) {
                maxZeros = zeros;
            }
        }

        int estimate() {
            return (1 << Math.min(maxZeros, 16));
        }
    }
}
