package org.example.learn.java.thread;

/**
 * 打印线程状态；再用错误锁顺序制造死锁（演示后立刻中断退出，避免挂住入口）。
 */
public final class ThreadSyncDemo {

    public static void run() {
        demoStates();
        demoDeadlock();
    }

    private static void demoStates() {
        Thread parked = new Thread(() -> {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "learn-sleep");
        System.out.println("[状态] new 之后: " + parked.getState());
        parked.start();
        System.out.println("[状态] start 之后(很快会变成 TIMED_WAITING): " + parked.getState());
        try {
            parked.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        System.out.println("[状态] 结束后: " + parked.getState());
        System.out.println("  Java 把 OS 的就绪/运行都叫 RUNNABLE；BLOCKED 是在等 synchronized。");
    }

    private static void demoDeadlock() {
        Object a = new Object();
        Object b = new Object();
        Thread t1 = new Thread(() -> {
            synchronized (a) {
                sleep(80);
                synchronized (b) {
                    System.out.println("t1 got both");
                }
            }
        }, "deadlock-1");
        Thread t2 = new Thread(() -> {
            synchronized (b) {
                sleep(80);
                synchronized (a) {
                    System.out.println("t2 got both");
                }
            }
        }, "deadlock-2");
        t1.setDaemon(true);
        t2.setDaemon(true);
        t1.start();
        t2.start();
        sleep(200);
        System.out.println("[死锁] t1=" + t1.getState() + " t2=" + t2.getState() + " （多为 BLOCKED 互相等待）");
        System.out.println("  四个条件：互斥、占有并等待、不可抢占、循环等待。破循环等待：统一加锁顺序。");
        t1.interrupt();
        t2.interrupt();
    }

    private static void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
