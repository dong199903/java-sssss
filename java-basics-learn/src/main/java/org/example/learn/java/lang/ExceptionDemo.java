package org.example.learn.java.lang;

import java.io.IOException;

/**
 * Exception vs Error；编译期/运行时异常；finally。
 */
public final class ExceptionDemo {

    public static void run() {
        demoHierarchy();
        demoChecked();
        demoFinally();
    }

    private static void demoHierarchy() {
        System.out.println("[体系] Throwable → Error（不该捕：OOM、StackOverflow）");
        System.out.println("              → Exception → RuntimeException（NPE、IAE，可不声明）");
        System.out.println("                           → 受检异常（IOException，必须处理或声明）");
        try {
            throw new StackOverflowError("demo-only");
        } catch (Error e) {
            System.out.println("[Error] 能抓住但不该恢复: " + e.getClass().getSimpleName());
        }
    }

    private static void demoChecked() {
        try {
            mightIo();
        } catch (IOException e) {
            System.out.println("[受检] 编译器逼你处理: " + e.getMessage());
        }
        try {
            throw new IllegalStateException("runtime");
        } catch (RuntimeException e) {
            System.out.println("[运行时] 不用声明，表示编程错误: " + e.getMessage());
        }
    }

    private static void mightIo() throws IOException {
        throw new IOException("disk full");
    }

    private static void demoFinally() {
        String r = withFinally();
        System.out.println("[finally] 返回前一定跑: " + r);
        System.out.println("  final 修饰不可变；finally 是代码块；finalize 已废弃别用。");
        System.out.println("  wait 释放锁并等 notify；sleep 只让出 CPU、不释放 synchronized 锁。");
    }

    private static String withFinally() {
        try {
            return "ok";
        } finally {
            System.out.println("[finally] 在 return 之前执行");
        }
    }
}
