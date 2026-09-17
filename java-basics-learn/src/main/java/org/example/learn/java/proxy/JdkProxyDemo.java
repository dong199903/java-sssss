package org.example.learn.java.proxy;

import java.lang.reflect.Proxy;

/**
 * JDK 动态代理：有接口才行。这就是 Spring AOP 默认路径之一。
 */
public final class JdkProxyDemo {

    public interface OrderService {
        String create(String id);
    }

    public static final class OrderServiceImpl implements OrderService {
        @Override
        public String create(String id) {
            return "created-" + id;
        }
    }

    public static void run() {
        OrderService raw = new OrderServiceImpl();
        OrderService proxy = (OrderService) Proxy.newProxyInstance(
                OrderService.class.getClassLoader(),
                new Class<?>[] {OrderService.class},
                (p, method, args) -> {
                    System.out.println("[AOP] before " + method.getName());
                    Object result = method.invoke(raw, args);
                    System.out.println("[AOP] after, result=" + result);
                    return result;
                }
        );
        System.out.println("业务调用: " + proxy.create("1001"));
        System.out.println("无接口则走 CGLIB 子类。this.create() 内部调用不经过代理 → 事务/AOP 失效。");
    }
}
