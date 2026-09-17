package org.example.learn.spring;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import java.time.Instant;

/**
 * FactoryBean 产出对象 vs 工厂本身；单例 vs prototype；@Bean vs @Component。
 *
 * 在 spring-learn 目录：
 * ./mvnw -q -DskipTests compile exec:java -Dexec.mainClass=org.example.learn.spring.FactoryScopeDemo
 */
public final class FactoryScopeDemo {

    public static void main(String[] args) {
        try (AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(Cfg.class)) {
            Instant a = ctx.getBean(Instant.class);
            Instant b = ctx.getBean(Instant.class);
            Object factory = ctx.getBean("&nowFactory");
            System.out.println("[FactoryBean] getBean(Instant) 两次同一对象? " + (a == b));
            System.out.println("[FactoryBean] &nowFactory 才是工厂: " + factory.getClass().getSimpleName());

            Proto p1 = ctx.getBean(Proto.class);
            Proto p2 = ctx.getBean(Proto.class);
            System.out.println("[Scope] prototype 每次新实例? " + (p1 != p2));

            Hello hello = ctx.getBean(Hello.class);
            System.out.println("[@Bean] 自己 new 再交给容器: " + hello.ping());
            System.out.println("  @Bean 适合第三方类；@Component 适合自己的类。@Service/@Repository 对容器几乎一样，语义不同。");
        }
    }

    @Configuration
    static class Cfg {
        @Bean
        NowFactoryBean nowFactory() {
            return new NowFactoryBean();
        }

        @Bean
        @Scope("prototype")
        Proto proto() {
            return new Proto();
        }

        @Bean
        Hello hello() {
            return new Hello();
        }
    }

    static final class NowFactoryBean implements FactoryBean<Instant> {
        private final Instant frozen = Instant.parse("2026-01-01T00:00:00Z");

        @Override
        public Instant getObject() {
            return frozen;
        }

        @Override
        public Class<?> getObjectType() {
            return Instant.class;
        }
    }

    static class Proto {
    }

    static class Hello {
        String ping() {
            return "pong";
        }
    }
}
