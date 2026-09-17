package org.example.learn.java.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * 自调用不走代理：@Transactional / @Async 失效的同一根因。
 */
public final class SelfInvokeDemo {

    public static void run() {
        Bank raw = new BankImpl();
        Bank proxy = (Bank) Proxy.newProxyInstance(
                Bank.class.getClassLoader(),
                new Class<?>[]{Bank.class},
                new TxHandler(raw));

        System.out.println("-- 通过代理调 outer --");
        proxy.outer();
        System.out.println("-- 通过代理调 inner --");
        proxy.inner();
        System.out.println("  outer 里面 this.inner() 打在实现类上，拦截器进不去。");
        System.out.println("  修法：注入自己（代理）、AopContext.currentProxy()、或把 inner 拆到另一个 Bean。");
    }

    public interface Bank {
        void outer();

        void inner();
    }

    static final class BankImpl implements Bank {
        @Override
        public void outer() {
            System.out.println("  业务 outer，接着 this.inner()");
            inner();
        }

        @Override
        public void inner() {
            System.out.println("  业务 inner");
        }
    }

    static final class TxHandler implements InvocationHandler {
        private final Object target;

        TxHandler(Object target) {
            this.target = target;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            System.out.println("[代理] 开启事务 around " + method.getName());
            try {
                return method.invoke(target, args);
            } finally {
                System.out.println("[代理] 提交 " + method.getName());
            }
        }
    }
}
