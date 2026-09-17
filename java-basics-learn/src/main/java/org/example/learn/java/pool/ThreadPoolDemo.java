package org.example.learn.java.pool;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 核心线程 → 队列 → 最大线程 → 拒绝。用很小的数字把路径跑完。
 */
public final class ThreadPoolDemo {

    public static void run() {
        AtomicInteger rejects = new AtomicInteger();
        ThreadPoolExecutor pool = new ThreadPoolExecutor(
                1,
                2,
                1,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(2),
                r -> {
                    Thread t = new Thread(r);
                    t.setName("learn-pool-" + t.hashCode());
                    return t;
                },
                (r, executor) -> {
                    rejects.incrementAndGet();
                    System.out.println("[拒绝] 队列满且到达 maximumPoolSize，策略=Abort");
                    throw new RejectedExecutionException("demo-abort");
                }
        );

        Runnable slow = () -> sleep(300);
        int submitted = 0;
        int aborted = 0;
        for (int i = 0; i < 6; i++) {
            try {
                pool.execute(slow);
                submitted++;
                System.out.printf("提交 #%d  poolSize=%d  queue=%d%n",
                        i + 1, pool.getPoolSize(), pool.getQueue().size());
            } catch (RejectedExecutionException e) {
                aborted++;
            }
        }
        System.out.printf("成功提交=%d 拒绝=%d（期望拒绝>=1）%n", submitted, aborted);
        System.out.println("路径: 第1个进核心线程 → 接着进队列 → 队列满才开到 max → 再多就拒绝。");
        pool.shutdownNow();
    }

    private static void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
