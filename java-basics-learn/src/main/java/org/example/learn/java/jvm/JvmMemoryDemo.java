package org.example.learn.java.jvm;

/**
 * 对照运行时数据区：堆、栈、元空间。不主动打满内存。
 */
public final class JvmMemoryDemo {

    public static void run() {
        Runtime rt = Runtime.getRuntime();
        long mb = 1024 * 1024;
        System.out.printf("[堆] max=%d MB  total=%d MB  free=%d MB%n",
                rt.maxMemory() / mb, rt.totalMemory() / mb, rt.freeMemory() / mb);
        System.out.println("[线程私有] PC、Java 栈、本地方法栈 — 递归过深 → StackOverflowError");
        System.out.println("[共享] 堆（对象）方法区/元空间（类元数据）");
        System.out.println("[OOM 类型] Java heap space / Metaspace / Direct buffer memory / GC overhead / unable to create native thread");
        System.out.println("排查: jstat / jmap / HeapDumpOnOutOfMemoryError + MAT。生产 -Xms 与 -Xmx 设成一样。");
        System.out.println("GC: 新生代复制，老年代整理；JDK9+ 默认 G1，低延迟看 ZGC。");
    }
}
