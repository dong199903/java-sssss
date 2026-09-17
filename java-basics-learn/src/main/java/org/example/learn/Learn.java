package org.example.learn;

import org.example.learn.java.aqs.ReentrantLockDemo;
import org.example.learn.java.classload.ClassLoadDemo;
import org.example.learn.java.collection.ConcurrentHashMapDemo;
import org.example.learn.java.collection.CowFailFastDemo;
import org.example.learn.java.collection.HashMapDemo;
import org.example.learn.java.collection.ListArrayDemo;
import org.example.learn.java.concurrent.SyncToolsDemo;
import org.example.learn.java.jmm.JmmCasDemo;
import org.example.learn.java.jvm.JvmMemoryDemo;
import org.example.learn.java.lang.ExceptionDemo;
import org.example.learn.java.lang.GenericsSerialDemo;
import org.example.learn.java.lang.Java8Demo;
import org.example.learn.java.lang.StringBoxDemo;
import org.example.learn.java.oop.EqualsOopDemo;
import org.example.learn.java.pool.ThreadPoolDemo;
import org.example.learn.java.proxy.JdkProxyDemo;
import org.example.learn.java.proxy.SelfInvokeDemo;
import org.example.learn.java.reflect.ReflectSpiDemo;
import org.example.learn.java.thread.ThreadSyncDemo;
import org.example.learn.mysql.SqlTypesDemo;
import org.example.learn.mysql.WalDemo;
import org.example.learn.other.FilterInterceptorDemo;
import org.example.learn.other.SingletonDemo;
import org.example.learn.other.TcpFrameDemo;
import org.example.learn.other.TokenBucketDemo;
import org.example.learn.redis.BloomUvDemo;
import org.example.learn.redis.CacheAsideDemo;
import org.example.learn.redis.DistLockDemo;
import org.example.learn.redis.RankingDemo;
import org.example.learn.redis.RedisEncodingDemo;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 知识点 Demo 入口：./run.sh [id]
 */
public final class Learn {

    private static final Map<String, Runnable> DEMOS = new LinkedHashMap<>();

    static {
        DEMOS.put("java-list", ListArrayDemo::run);
        DEMOS.put("java-hashmap", HashMapDemo::run);
        DEMOS.put("java-chm", ConcurrentHashMapDemo::run);
        DEMOS.put("java-thread", ThreadSyncDemo::run);
        DEMOS.put("java-pool", ThreadPoolDemo::run);
        DEMOS.put("java-jmm", JmmCasDemo::run);
        DEMOS.put("java-aqs", ReentrantLockDemo::run);
        DEMOS.put("java-jvm", JvmMemoryDemo::run);
        DEMOS.put("java-equals", EqualsOopDemo::run);
        DEMOS.put("java-string", StringBoxDemo::run);
        DEMOS.put("java-exception", ExceptionDemo::run);
        DEMOS.put("java-generic", GenericsSerialDemo::run);
        DEMOS.put("java-reflect", ReflectSpiDemo::run);
        DEMOS.put("java-classload", ClassLoadDemo::run);
        DEMOS.put("java-java8", Java8Demo::run);
        DEMOS.put("java-cow", CowFailFastDemo::run);
        DEMOS.put("java-sync", SyncToolsDemo::run);
        DEMOS.put("java-proxy", JdkProxyDemo::run);
        DEMOS.put("java-self", SelfInvokeDemo::run);
        DEMOS.put("mysql-wal", WalDemo::run);
        DEMOS.put("mysql-types", SqlTypesDemo::run);
        DEMOS.put("redis-rank", RankingDemo::run);
        DEMOS.put("redis-cache", CacheAsideDemo::run);
        DEMOS.put("redis-lock", DistLockDemo::run);
        DEMOS.put("redis-sds", RedisEncodingDemo::run);
        DEMOS.put("redis-bloom", BloomUvDemo::run);
        DEMOS.put("net-frame", TcpFrameDemo::run);
        DEMOS.put("net-filter", FilterInterceptorDemo::run);
        DEMOS.put("dp-singleton", SingletonDemo::run);
        DEMOS.put("scene-limit", TokenBucketDemo::run);
    }

    public static void main(String[] args) {
        if (args.length == 0 || "help".equals(args[0]) || "-h".equals(args[0])) {
            printCatalog();
            return;
        }
        if ("all".equals(args[0])) {
            DEMOS.forEach((id, demo) -> {
                System.out.println();
                System.out.println("=== " + id + " ===");
                demo.run();
            });
            return;
        }
        Runnable demo = DEMOS.get(args[0]);
        if (demo == null) {
            System.err.println("未知 Demo: " + args[0]);
            printCatalog();
            System.exit(1);
            return;
        }
        System.out.println("=== " + args[0] + " ===");
        demo.run();
    }

    private static void printCatalog() {
        System.out.println("用法: ./run.sh <id>   或  ./run.sh all");
        printGroup("Java Demo:", "java-");
        printGroup("MySQL 概念 Demo:", "mysql-");
        printGroup("Redis 概念 Demo（纯 Java 模拟）:", "redis-");
        System.out.println("其他 Demo:");
        DEMOS.keySet().stream()
                .filter(id -> !id.startsWith("java-") && !id.startsWith("redis-") && !id.startsWith("mysql-"))
                .forEach(id -> System.out.println("  " + id));
        System.out.println("文档目录: ../docs/README.md");
    }

    private static void printGroup(String title, String prefix) {
        System.out.println(title);
        DEMOS.keySet().stream().filter(id -> id.startsWith(prefix)).forEach(id -> System.out.println("  " + id));
    }
}
