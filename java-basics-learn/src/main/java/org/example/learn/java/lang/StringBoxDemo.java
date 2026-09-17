package org.example.learn.java.lang;

/**
 * String 不可变 / intern；StringBuilder；装箱与 Integer 缓存。
 */
public final class StringBoxDemo {

    public static void run() {
        demoImmutableAndIntern();
        demoBuilder();
        demoBoxingCache();
        demoNewString();
    }

    private static void demoImmutableAndIntern() {
        String a = "java";
        String b = "java";
        String c = new String("java");
        String d = c.intern();
        System.out.println("[intern] a==b? " + (a == b) + " （字面量进常量池，同一引用）");
        System.out.println("[intern] a==c? " + (a == c) + " （new 在堆上另建）");
        System.out.println("[intern] a==d? " + (a == d) + " （intern 拿回池里那份）");
        String s = "ab";
        s.concat("c");
        System.out.println("[不可变] concat 不改原串，s 仍是 " + s + "。String 才能当 HashMap key 且线程安全共享。");
    }

    private static void demoBuilder() {
        String plus = "";
        for (int i = 0; i < 3; i++) {
            plus = plus + i;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 3; i++) {
            sb.append(i);
        }
        System.out.println("[Builder] + 循环每次新 String；StringBuilder 一块可变 char/byte 数组。结果都是 " + sb);
        System.out.println("  StringBuffer 方法带 synchronized，单线程用 Builder。JDK9 起 String 用 byte[] + coder 省拉丁字符内存。");
    }

    private static void demoBoxingCache() {
        Integer x = 127;
        Integer y = 127;
        Integer p = 128;
        Integer q = 128;
        System.out.println("[Integer 缓存] 127==127? " + (x == y) + " ；128==128? " + (p == q));
        System.out.println("  valueOf 默认缓存 -128~127。比较数值用 equals，不要 ==");
        int unbox = p;
        System.out.println("[拆箱] Integer -> int 自动调用 intValue，拆箱成 " + unbox + "。空 Integer 拆箱会 NPE。");
    }

    private static void demoNewString() {
        String heap = new String("yupi");
        System.out.println("[new String] 字面量 \"yupi\" 已在常量池；new 再在堆建 1 个对象。heap==\"yupi\"? " + (heap == "yupi"));
        System.out.println("  浮点别直接 ==：0.1+0.2 有误差。金额用 BigDecimal 字符串构造。");
        System.out.println("  0.1+0.2=" + (0.1 + 0.2) + "  equal-to-0.3? " + ((0.1 + 0.2) == 0.3));
    }
}
