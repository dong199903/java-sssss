package org.example.learn.java.collection;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 多线程 put：HashMap 可能丢数据；ConcurrentHashMap 最终 size 正确。
 */
public final class ConcurrentHashMapDemo {

    public static void run() {
        int threads = 8;
        int perThread = 1000;
        int expected = threads * perThread;

        int hashMapSize = concurrentPut(new HashMap<>(), threads, perThread);
        int chmSize = concurrentPut(new ConcurrentHashMap<>(), threads, perThread);

        System.out.printf("期望 size=%d%n", expected);
        System.out.printf("HashMap            size=%d  (可能小于期望，结构损坏时甚至抛异常)%n", hashMapSize);
        System.out.printf("ConcurrentHashMap  size=%d%n", chmSize);
        System.out.println("JDK8 CHM：桶数组 + CAS 放第一个节点，冲突用 synchronized(桶头)，不再用 Segment。");
    }

    private static int concurrentPut(Map<Integer, Integer> map, int threads, int perThread) {
        ExecutorService pool = Executors.newFixedThreadPool(threads);
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(threads);
        AtomicInteger seq = new AtomicInteger();
        for (int t = 0; t < threads; t++) {
            pool.submit(() -> {
                try {
                    start.await();
                    for (int i = 0; i < perThread; i++) {
                        int k = seq.getAndIncrement();
                        map.put(k, k);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    done.countDown();
                }
            });
        }
        start.countDown();
        try {
            done.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        pool.shutdownNow();
        return map.size();
    }
}
