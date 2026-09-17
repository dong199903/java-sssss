package org.example.learn.java.collection;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * fail-fast；CopyOnWriteArrayList；LinkedHashMap LRU。
 */
public final class CowFailFastDemo {

    public static void run() {
        demoFailFast();
        demoCow();
        demoLru();
    }

    private static void demoFailFast() {
        List<Integer> list = new ArrayList<>(List.of(1, 2, 3, 4));
        try {
            for (Integer n : list) {
                if (n == 1) {
                    list.remove(n);
                }
            }
            System.out.println("[fail-fast] 没抛异常（少见）");
        } catch (ConcurrentModificationException e) {
            System.out.println("[fail-fast] foreach 里结构性修改: " + e.getClass().getSimpleName());
        }
        List<Integer> safe = new ArrayList<>(List.of(1, 2, 3));
        Iterator<Integer> it = safe.iterator();
        while (it.hasNext()) {
            if (it.next() == 2) {
                it.remove();
            }
        }
        System.out.println("[iterator.remove] 合法删除后 " + safe);
    }

    private static void demoCow() {
        CopyOnWriteArrayList<String> cow = new CopyOnWriteArrayList<>(List.of("a", "b"));
        for (String s : cow) {
            cow.add("x-" + s);
        }
        System.out.println("[COW] 迭代时写不会 CME，快照遍历。结果 " + cow);
        System.out.println("  写都要复制数组，适合读多写极少（监听器列表）。不要当普通 ArrayList 用。");
    }

    private static void demoLru() {
        LinkedHashMap<Integer, String> lru = new LinkedHashMap<>(16, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<Integer, String> eldest) {
                return size() > 3;
            }
        };
        lru.put(1, "a");
        lru.put(2, "b");
        lru.put(3, "c");
        lru.get(1);
        lru.put(4, "d");
        System.out.println("[LRU] accessOrder=true，get(1) 把 1 挪到最近；容量 3 淘汰最老。现在 " + lru.keySet());
        System.out.println("  HashSet 内部就是 HashMap，value 是占位 Object。TreeMap 靠红黑树按 Comparable/Comparator 排序。");
    }
}
