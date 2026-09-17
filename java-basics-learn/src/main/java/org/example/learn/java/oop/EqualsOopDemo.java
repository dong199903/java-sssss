package org.example.learn.java.oop;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * == vs equals；值传递；重载 vs 重写；接口 vs 抽象类。
 */
public final class EqualsOopDemo {

    public static void run() {
        demoEquals();
        demoPassByValue();
        demoOverloadOverride();
        demoInterfaceVsAbstract();
    }

    private static void demoEquals() {
        User a = new User(1, "ada");
        User b = new User(1, "ada");
        System.out.println("[==] a==b? " + (a == b) + " （比的是引用）");
        System.out.println("[equals] a.equals(b)? " + a.equals(b) + " （比的是业务相等）");
        Set<User> set = new HashSet<>();
        set.add(a);
        set.add(b);
        System.out.println("[hashCode 契约] 相等对象必须同 hash，否则 HashSet size 会变成 2。实际 size=" + set.size());
    }

    private static void demoPassByValue() {
        int n = 1;
        bump(n);
        System.out.println("[传值] 基本类型方法里改副本，外面仍是 " + n);

        User u = new User(1, "ada");
        rename(u, "bob");
        System.out.println("[传值] 引用拷贝指向同一对象，改字段生效: " + u.name);

        rebind(u);
        System.out.println("[传值] 方法里换引用不影响外面: " + u.name);
        System.out.println("  Java 只有按值传递：传的是拷贝。对象看起来像按引用，其实拷贝的是地址。");
    }

    private static void bump(int n) {
        n++;
    }

    private static void rename(User u, String name) {
        u.name = name;
    }

    private static void rebind(User u) {
        u = new User(2, "other");
    }

    private static void demoOverloadOverride() {
        Printer p = new ColorPrinter();
        p.print("hi");
        p.print("hi", 2);
        System.out.println("  重写：运行时看对象真实类型。重载：编译期看参数列表。");
    }

    private static void demoInterfaceVsAbstract() {
        Flyable bird = () -> "扑腾";
        System.out.println("[接口] 多实现、可 lambda（单抽象方法）: " + bird.fly());
        System.out.println("[抽象类] 可有字段和构造器，单继承。Java 不用类多继承，避免菱形问题；接口 default 方法冲突要自己选。");
    }

    static final class User {
        final int id;
        String name;

        User(int id, String name) {
            this.id = id;
            this.name = name;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof User other)) {
                return false;
            }
            return id == other.id && Objects.equals(name, other.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id, name);
        }
    }

    static class Printer {
        void print(String s) {
            System.out.println("[重写] Printer.print -> " + s);
        }

        void print(String s, int times) {
            System.out.println("[重载] 同名不同参: " + s.repeat(times));
        }
    }

    static class ColorPrinter extends Printer {
        @Override
        void print(String s) {
            System.out.println("[重写] ColorPrinter.print -> " + s);
        }
    }

    @FunctionalInterface
    interface Flyable {
        String fly();
    }
}
