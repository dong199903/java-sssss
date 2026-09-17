package org.example.learn.java.collection;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * 对比数组随机访问与链表头插成本。
 */
public final class ListArrayDemo {

    public static void run() {
        int n = 50_000;

        long arrayGet = timed(() -> {
            List<Integer> list = new ArrayList<>();
            for (int i = 0; i < n; i++) {
                list.add(i);
            }
            int sum = 0;
            for (int i = 0; i < n; i++) {
                sum += list.get(i);
            }
            sink(sum);
        });

        long linkedGet = timed(() -> {
            List<Integer> list = new LinkedList<>();
            for (int i = 0; i < n; i++) {
                list.add(i);
            }
            int sum = 0;
            for (int i = 0; i < n; i++) {
                sum += list.get(i);
            }
            sink(sum);
        });

        long arrayHead = timed(() -> {
            List<Integer> list = new ArrayList<>();
            for (int i = 0; i < n; i++) {
                list.add(0, i);
            }
        });

        long linkedHead = timed(() -> {
            List<Integer> list = new LinkedList<>();
            for (int i = 0; i < n; i++) {
                list.add(0, i);
            }
        });

        System.out.printf("随机 get  %d 次  ArrayList=%d ms  LinkedList=%d ms%n", n, arrayGet, linkedGet);
        System.out.printf("头插      %d 次  ArrayList=%d ms  LinkedList=%d ms%n", n, arrayHead, linkedHead);
        System.out.println("结论: 下标访问选 ArrayList；只有明确大量头尾插入才考虑 LinkedList。");
    }

    private static long timed(Runnable task) {
        long start = System.nanoTime();
        task.run();
        return (System.nanoTime() - start) / 1_000_000;
    }

    private static void sink(int value) {
        if (value == Integer.MIN_VALUE) {
            System.out.print("");
        }
    }
}
