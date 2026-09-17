package org.example.learn.spring;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 自己 new vs 容器注入。不启动 Web。
 *
 * ./mvnw -q -DskipTests compile exec:java -Dexec.mainClass=org.example.learn.spring.IocDiDemo
 */
public final class IocDiDemo {

    public static void main(String[] args) {
        OrderService manual = new OrderService(new UserService());
        System.out.println("[自己 new] " + manual.place());

        try (AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(Config.class)) {
            OrderService fromIoc = ctx.getBean(OrderService.class);
            System.out.println("[容器 DI] " + fromIoc.place());
            System.out.println("同一 UserService? " + (fromIoc.users == ctx.getBean(UserService.class)));
        }
    }

    @Configuration
    static class Config {
        @Bean
        UserService userService() {
            return new UserService();
        }

        @Bean
        OrderService orderService(UserService users) {
            return new OrderService(users);
        }
    }

    static class UserService {
        String current() {
            return "user-42";
        }

        @PostConstruct
        void init() {
            System.out.println("[生命周期] UserService @PostConstruct");
        }

        @PreDestroy
        void destroy() {
            System.out.println("[生命周期] UserService @PreDestroy");
        }
    }

    static class OrderService {
        final UserService users;

        OrderService(UserService users) {
            this.users = users;
        }

        String place() {
            return "order for " + users.current();
        }
    }
}
