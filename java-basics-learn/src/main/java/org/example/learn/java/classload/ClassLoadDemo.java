package org.example.learn.java.classload;

/**
 * 类加载过程与双亲委派；forName 会初始化，loadClass 默认不会。
 */
public final class ClassLoadDemo {

    public static void run() {
        demoLoaders();
        demoForNameVsLoadClass();
        demoParent();
    }

    private static void demoLoaders() {
        ClassLoader app = ClassLoadDemo.class.getClassLoader();
        System.out.println("[加载器] App: " + app);
        System.out.println("[加载器] 父(Platform): " + app.getParent());
        System.out.println("[加载器] 祖父(Bootstrap 对 Java 类是 null): " + app.getParent().getParent());
        System.out.println("  加载过程：加载 → 链接(验证/准备/解析) → 初始化(clinit)。");
    }

    private static void demoForNameVsLoadClass() {
        try {
            ClassLoadDemo.class.getClassLoader()
                    .loadClass("org.example.learn.java.classload.ClassLoadDemo$NotInitYet");
            System.out.println("[loadClass] 默认不跑 <clinit>，所以 NotInitYet 的静态块不应出现。");
            Class.forName("org.example.learn.java.classload.ClassLoadDemo$Ready");
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException(e);
        }
    }

    private static void demoParent() {
        System.out.println("[双亲委派] 先问父加载器，避免核心类被替换（自己写的 java.lang.String 不会生效）。");
        System.out.println("  破坏场景：SPI、热部署、Tomcat 每个 WebApp 一个加载器。");
        System.out.println("  字节码是给 JVM 的指令；javac 产出 .class，执行引擎解释或 JIT。");
    }

    static final class NotInitYet {
        static {
            System.out.println("[loadClass] 如果看到这行，说明被初始化了（不应该）");
        }
    }

    static final class Ready {
        static {
            System.out.println("[forName] 会执行 <clinit>，所以你会看到这行");
        }
    }
}
