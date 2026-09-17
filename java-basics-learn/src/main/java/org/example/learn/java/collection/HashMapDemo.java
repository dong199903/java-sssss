package org.example.learn.java.collection;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 用「永远同一 hash」的 key 把冲突、覆盖、equals 规则跑给你看。
 */
public final class HashMapDemo {

    public static void run() {
        demoEqualsAndHashCode();
        demoCollisionChain();
        demoResizeTrigger();
    }

    private static void demoEqualsAndHashCode() {
        Map<BadKey, String> map = new HashMap<>();
        BadKey a = new BadKey(1, "a");
        BadKey b = new BadKey(1, "a");
        map.put(a, "first");
        map.put(b, "second");
        System.out.println("[equals+hash] size=" + map.size() + " get=" + map.get(new BadKey(1, "a")));
        System.out.println("  同一业务相等的 key 必须同时重写 hashCode 和 equals，否则会变成多条。");
    }

    private static void demoCollisionChain() {
        Map<CollideKey, Integer> map = new HashMap<>();
        for (int i = 0; i < 6; i++) {
            map.put(new CollideKey(i), i);
        }
        System.out.println("[碰撞拉链] 6 个不同 key、同一 hashCode，size=" + map.size());
        System.out.println("  JDK8 同一桶先链表；长度到 8 且表容量>=64 才树化（本 demo 不到阈值）。");
    }

    private static void demoResizeTrigger() {
        Map<Integer, Integer> map = new HashMap<>(16);
        for (int i = 0; i < 20; i++) {
            map.put(i, i);
            if (map.size() == 13) {
                System.out.println("[扩容] 默认 16*0.75=12，放入第 13 个元素后 size=" + map.size() + "，此时已触发 resize");
            }
        }
        System.out.println("  容量保持 2 的幂，用 (n-1)&hash 代替取模。");
    }

    /** 业务上相等，hash/equals 正确。 */
    record BadKey(int id, String name) {
        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof BadKey other)) {
                return false;
            }
            return id == other.id && Objects.equals(name, other.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id, name);
        }
    }

    /** 不同对象永远进同一桶，用来观察冲突。 */
    record CollideKey(int id) {
        @Override
        public int hashCode() {
            return 1;
        }
    }
}
