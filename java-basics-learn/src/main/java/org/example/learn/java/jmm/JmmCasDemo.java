package org.example.learn.java.jmm;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * volatile 可见性；CAS 解决并发 ++；ThreadLocal 在线程池里必须 remove。
 */
public final class JmmCasDemo {

    static boolean plainStop;
    static volatile boolean volatileStop;

    public static void run() {
        demoVolatile();
        demoCas();
        demoThreadLocal();
    }

    private static void demoVolatile() {
        plainStop = false;
        Thread waiter = new Thread(() -> {
            while (!plainStop) {
                // 无同步读，JIT 可能把条件提升成死循环
            }
        }, "plain-wait");
        waiter.setDaemon(true);
        waiter.start();
        sleep(50);
        plainStop = true;
        sleep(80);
        System.out.println("[可见性] 普通 boolean 停止标志: 线程还活着? " + waiter.isAlive() + "（常见仍为 true）");
        waiter.interrupt();

        volatileStop = false;
        Thread waiter2 = new Thread(() -> {
            while (!volatileStop) {
                // volatile 读不能被优化掉
            }
        }, "vol-wait");
        waiter2.setDaemon(true);
        waiter2.start();
        sleep(50);
        volatileStop = true;
        sleep(80);
        System.out.println("[可见性] volatile 停止标志: 线程还活着? " + waiter2.isAlive() + "（应为 false）");
        System.out.println("  volatile 保证可见和禁止重排序，不保证 i++ 原子。");
    }

    private static void demoCas() {
        int[] plain = {0};
        AtomicInteger atomic = new AtomicInteger();
        Thread[] ts = new Thread[8];
        for (int i = 0; i < ts.length; i++) {
            ts[i] = new Thread(() -> {
                for (int n = 0; n < 10_000; n++) {
                    plain[0]++;
                    atomic.incrementAndGet();
                }
            });
            ts[i].start();
        }
        for (Thread t : ts) {
            try {
                t.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        System.out.printf("[CAS] 普通 ++ = %d  AtomicInteger=%d  期望=%d%n",
                plain[0], atomic.get(), 8 * 10_000);
    }

    private static void demoThreadLocal() {
        ThreadLocal<byte[]> local = ThreadLocal.withInitial(() -> new byte[1024]);
        local.get();
        System.out.println("[ThreadLocal] key 是弱引用，value 仍是强引用。线程池线程很长命，用完必须 remove，否则泄漏。");
        local.remove();
    }

    private static void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
