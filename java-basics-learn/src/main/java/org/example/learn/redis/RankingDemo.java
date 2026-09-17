package org.example.learn.redis;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

/**
 * 用 TreeSet 模拟 ZSet 排行榜：按分数倒序，同分按名字。
 */
public final class RankingDemo {

    record Entry(String user, long score) {
    }

    public static void run() {
        Comparator<Entry> cmp = Comparator.comparingLong(Entry::score).reversed()
                .thenComparing(Entry::user);
        TreeSet<Entry> zset = new TreeSet<>(cmp);
        Map<String, Long> dict = new HashMap<>();

        upsert(zset, dict, "alice", 10);
        upsert(zset, dict, "bob", 20);
        upsert(zset, dict, "alice", 25);
        upsert(zset, dict, "cathy", 20);

        System.out.println("[排行榜] Redis ZSet = dict(O(1) 查分) + skiplist(有序)。这里用 TreeSet 示意：");
        int rank = 1;
        for (Entry e : zset) {
            System.out.printf("  #%d %s %d%n", rank++, e.user, e.score);
        }
        System.out.println("命令对照: ZINCRBY / ZREVRANGE / ZREVRANK");
    }

    private static void upsert(TreeSet<Entry> zset, Map<String, Long> dict, String user, long score) {
        Long old = dict.put(user, score);
        if (old != null) {
            zset.remove(new Entry(user, old));
        }
        zset.add(new Entry(user, score));
    }
}
