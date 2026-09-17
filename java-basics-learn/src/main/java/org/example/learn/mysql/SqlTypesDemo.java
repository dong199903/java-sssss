package org.example.learn.mysql;

/**
 * CHAR vs VARCHAR 的占位直觉；金额别用 float。
 */
public final class SqlTypesDemo {

    public static void run() {
        String charPad = padRight("hi", 8);
        String varchar = "hi";
        System.out.println("[CHAR(8)] 定长补空格: [" + charPad + "] 长度=" + charPad.length());
        System.out.println("[VARCHAR] 变长只存真实内容: [" + varchar + "] 长度=" + varchar.length());
        System.out.println("  DATETIME 与时区无关；TIMESTAMP 转 UTC 存，范围到 2038。金额用 DECIMAL，别用 FLOAT。");
        System.out.println("  count(*) 计行；count(col) 忽略 NULL。int(11) 的 11 只影响显示宽度，不是容量。");
        System.out.println("  0.1+0.2 浮点误差=" + (0.1 + 0.2) + "，所以价格不能用 double 累加。");
    }

    private static String padRight(String s, int n) {
        return String.format("%-" + n + "s", s);
    }
}
