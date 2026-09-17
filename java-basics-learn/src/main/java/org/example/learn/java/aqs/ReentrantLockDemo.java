package org.example.learn.java.aqs;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * ReentrantLock：可中断、可限时、可重入。对比 synchronized 的「必须拿到或一直等」。
 */
public final class ReentrantLockDemo {

    public static void run() {
        ReentrantLock lock = new ReentrantLock();
        lock.lock();
        try {
            System.out.println("[重入] holdCount=" + lock.getHoldCount());
            lock.lock();
            try {
                System.out.println("[重入] 再次 lock 后 holdCount=" + lock.getHoldCount());
            } finally {
                lock.unlock();
            }
        } finally {
            lock.unlock();
        }

        ReentrantLock contended = new ReentrantLock();
        Thread holder = new Thread(() -> {
            contended.lock();
            try {
                sleep(200);
            } finally {
                contended.unlock();
            }
        }, "holder");
        holder.start();
        sleep(30);
        try {
            boolean ok = contended.tryLock(50, TimeUnit.MILLISECONDS);
            System.out.println("[tryLock] 50ms 内拿到? " + ok + " （锁被占用时应为 false）");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        try {
            holder.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        System.out.println("AQS：state + 等待队列。ReentrantLock 独占；Semaphore/CountDownLatch 走共享。");
    }

    private static void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
