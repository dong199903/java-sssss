package org.example.learn.java.concurrent;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * CountDownLatch / CyclicBarrier / Semaphore / CompletableFuture。
 */
public final class SyncToolsDemo {

    public static void run() {
        demoLatch();
        demoBarrier();
        demoSemaphore();
        demoFuture();
    }

    private static void demoLatch() {
        CountDownLatch latch = new CountDownLatch(3);
        AtomicInteger done = new AtomicInteger();
        for (int i = 0; i < 3; i++) {
            Thread t = new Thread(() -> {
                done.incrementAndGet();
                latch.countDown();
            });
            t.start();
        }
        boolean ok;
        try {
            ok = latch.await(2, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            ok = false;
        }
        System.out.println("[CountDownLatch] 主线程等 3 个工人 countDown，到位=" + done.get() + " await=" + ok);
        System.out.println("  Latch 一次性。CyclicBarrier 可循环，人到齐一起过。");
    }

    private static void demoBarrier() {
        AtomicInteger waves = new AtomicInteger();
        CyclicBarrier barrier = new CyclicBarrier(2, waves::incrementAndGet);
        Runnable r = () -> {
            try {
                barrier.await(2, TimeUnit.SECONDS);
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
        };
        Thread t1 = new Thread(r, "b1");
        Thread t2 = new Thread(r, "b2");
        t1.start();
        t2.start();
        join(t1);
        join(t2);
        System.out.println("[CyclicBarrier] 2 个线程到齐放行，触发回调次数=" + waves.get());
    }

    private static void demoSemaphore() {
        Semaphore sem = new Semaphore(2);
        AtomicInteger peak = new AtomicInteger();
        AtomicInteger current = new AtomicInteger();
        Thread[] ts = new Thread[6];
        for (int i = 0; i < ts.length; i++) {
            ts[i] = new Thread(() -> {
                try {
                    sem.acquire();
                    int n = current.incrementAndGet();
                    peak.accumulateAndGet(n, Math::max);
                    sleep(30);
                    current.decrementAndGet();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    sem.release();
                }
            });
            ts[i].start();
        }
        for (Thread t : ts) {
            join(t);
        }
        System.out.println("[Semaphore] 许可=2，同时进入的峰值=" + peak.get() + "（应 <=2）");
    }

    private static void demoFuture() {
        CompletableFuture<Integer> price = CompletableFuture.supplyAsync(() -> 10);
        CompletableFuture<Integer> tax = CompletableFuture.supplyAsync(() -> 2);
        int total = price.thenCombine(tax, Integer::sum).join();
        System.out.println("[CompletableFuture] 两个异步结果合并=" + total);
        System.out.println("  替代手写 Thread+Future。异常用 exceptionally；编排用 thenCompose。");
        System.out.println("  虚拟线程（Java21）：阻塞式代码也能高并发，不必再为 IO 写回调。");
    }

    private static void join(Thread t) {
        try {
            t.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private static void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
