package org.example.learn.other;

/**
 * 枚举单例：线程安全、防反序列化多实例。对比错误的「DCL 忘了 volatile」。
 */
public final class SingletonDemo {

    enum Holder {
        INSTANCE
    }

    public static void run() {
        System.out.println("枚举单例 identity=" + System.identityHashCode(Holder.INSTANCE));
        System.out.println("DCL 必须把 instance 声明为 volatile，否则重排序会让别的线程看到未构造完的对象。");
        System.out.println("Spring 容器单例 ≠ 自己写的 Singleton 类，不要混为一谈。");
    }
}
