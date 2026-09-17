package org.example.learn.java.reflect;

import org.example.learn.java.spi.Greeter;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Method;
import java.util.ServiceLoader;

/**
 * 反射调方法；运行时注解；SPI ServiceLoader。
 */
public final class ReflectSpiDemo {

    public static void run() {
        demoReflect();
        demoAnnotation();
        demoSpi();
    }

    private static void demoReflect() {
        try {
            Class<?> clz = Class.forName("org.example.learn.java.reflect.ReflectSpiDemo$Target");
            Object obj = clz.getDeclaredConstructor().newInstance();
            Method m = clz.getDeclaredMethod("secret", String.class);
            m.setAccessible(true);
            Object r = m.invoke(obj, "learn");
            System.out.println("[反射] 运行时拿到私有方法并调用: " + r);
            System.out.println("  Spring/MyBatis/JSON 库都靠这套。代价是慢、破坏封装、难静态分析。");
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException(e);
        }
    }

    private static void demoAnnotation() {
        Hint hint = Target.class.getAnnotation(Hint.class);
        System.out.println("[注解] @" + hint.annotationType().getSimpleName() + " value=" + hint.value());
        System.out.println("  Retention.RUNTIME 才能被反射读到；SOURCE 只给编译器（如 @Override）。");
    }

    private static void demoSpi() {
        ServiceLoader<Greeter> loader = ServiceLoader.load(Greeter.class);
        Greeter g = loader.findFirst().orElseThrow();
        System.out.println("[SPI] META-INF/services 声明实现，运行时加载: " + g.hello());
        System.out.println("  JDBC Driver、SLF4J 都是这套。打破双亲委派的典型：SPI 由启动类加载器加载接口、实现却在 AppClassLoader。");
    }

    @Hint("runtime-meta")
    public static final class Target {
        private String secret(String name) {
            return "hi " + name;
        }
    }

    @Retention(RetentionPolicy.RUNTIME)
    public @interface Hint {
        String value();
    }
}
