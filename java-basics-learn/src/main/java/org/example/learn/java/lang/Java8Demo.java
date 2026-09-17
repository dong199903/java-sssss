package org.example.learn.java.lang;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Lambda / Stream / Optional。Java 8 之后面试常问的「能写出来」部分。
 */
public final class Java8Demo {

    public static void run() {
        demoLambdaStream();
        demoOptional();
        demoLaterLts();
    }

    private static void demoLambdaStream() {
        List<String> names = List.of("ada", "bob", "", "cathy");
        List<String> upper = names.stream()
                .filter(s -> !s.isBlank())
                .map(String::toUpperCase)
                .collect(Collectors.toList());
        System.out.println("[Stream] filter+map: " + upper);
        System.out.println("  Lambda 是函数式接口（单一抽象方法）的实例。Stream 是延迟计算，遇到 collect 才跑。");
    }

    private static void demoOptional() {
        Optional<String> hit = find("ada");
        Optional<String> miss = find("nobody");
        System.out.println("[Optional] 有值: " + hit.orElse("x") + " ；空: " + miss.orElse("fallback"));
        System.out.println("  用来当返回值，避免 NPE。不要 Optional.get() 不检查，也不要用它当字段。");
    }

    private static Optional<String> find(String name) {
        return List.of("ada", "bob").stream().filter(name::equals).findFirst();
    }

    private static void demoLaterLts() {
        String text = """
                Java11: HttpClient / isBlank
                Java17: sealed / record / 模式匹配 instanceof
                Java21: 虚拟线程（轻量，阻塞自动让出载体线程）
                """;
        Object v = "hi";
        if (v instanceof String s) {
            System.out.println("[17+] instanceof 模式匹配长度=" + s.length());
        }
        System.out.println("[LTS 印象]\n" + text.strip());
    }
}
