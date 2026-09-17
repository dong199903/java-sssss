package org.example.learn.spring;

import org.example.learn.spring.ctor.CtorA;
import org.example.learn.spring.field.FieldA;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * 构造器循环依赖会失败；字段注入单例可以被三级缓存救活。
 *
 * ./mvnw -q -DskipTests compile exec:java -Dexec.mainClass=org.example.learn.spring.CircularDependencyDemo
 */
public final class CircularDependencyDemo {

    public static void main(String[] args) {
        try {
            new AnnotationConfigApplicationContext(CtorConfig.class).close();
        } catch (BeanCreationException e) {
            System.out.println("[构造器环] 失败: " + rootMessage(e));
        }

        try (AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(FieldConfig.class)) {
            FieldA a = ctx.getBean(FieldA.class);
            System.out.println("[字段环] 成功 A.b=" + a.b.getClass().getSimpleName()
                    + " B.a=" + a.b.a.getClass().getSimpleName());
            System.out.println("三级缓存：singletonFactories 延迟 getEarlyBeanReference，为的是 AOP 代理和半成品是同一个引用。");
        }
    }

    private static String rootMessage(Throwable e) {
        Throwable t = e;
        while (t.getCause() != null) {
            t = t.getCause();
        }
        return t.getMessage();
    }

    @Configuration
    @ComponentScan(basePackageClasses = CtorA.class)
    static class CtorConfig {
    }

    @Configuration
    @ComponentScan(basePackageClasses = FieldA.class)
    static class FieldConfig {
    }
}
