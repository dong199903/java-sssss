package org.example.learn.other;

/**
 * 令牌桶：允许突发；对比漏桶更平滑。
 */
public final class TokenBucketDemo {

    static final class Bucket {
        final long capacity;
        final double ratePerMs;
        double tokens;
        long lastMs = System.currentTimeMillis();

        Bucket(long capacity, double ratePerSec) {
            this.capacity = capacity;
            this.ratePerMs = ratePerSec / 1000.0;
            this.tokens = capacity;
        }

        synchronized boolean tryAcquire() {
            long now = System.currentTimeMillis();
            tokens = Math.min(capacity, tokens + (now - lastMs) * ratePerMs);
            lastMs = now;
            if (tokens < 1) {
                return false;
            }
            tokens -= 1;
            return true;
        }
    }

    public static void run() {
        Bucket bucket = new Bucket(3, 5);
        int ok = 0;
        int deny = 0;
        for (int i = 0; i < 8; i++) {
            if (bucket.tryAcquire()) {
                ok++;
                System.out.println("放行 #" + (i + 1));
            } else {
                deny++;
                System.out.println("限流 #" + (i + 1));
            }
        }
        System.out.printf("放行=%d 拒绝=%d（突发容量=3）%n", ok, deny);
    }
}
