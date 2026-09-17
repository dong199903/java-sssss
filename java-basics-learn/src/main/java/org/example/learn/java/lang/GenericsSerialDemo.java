package org.example.learn.java.lang;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 泛型擦除与 PECS；浅拷贝/深拷贝；序列化与 transient。
 */
public final class GenericsSerialDemo {

    public static void run() {
        demoErasure();
        demoCopy();
        demoSerialize();
    }

    private static void demoErasure() {
        List<String> ss = new ArrayList<>();
        List<Integer> is = new ArrayList<>();
        System.out.println("[擦除] 运行时 List<String> 和 List<Integer> 同类? "
                + (ss.getClass() == is.getClass()));
        List<? extends Number> producer = List.of(1, 2);
        List<? super Integer> consumer = new ArrayList<>();
        consumer.add(3);
        System.out.println("[PECS] extends 只能取: " + producer.get(0)
                + " ；super 才能放: " + consumer);
        System.out.println("  泛型是编译期约束，字节码里类型参数被擦成上限（默认 Object）。");
    }

    private static void demoCopy() {
        Node orig = new Node("a", new int[]{1, 2});
        Node shallow = orig.clone();
        orig.payload[0] = 9;
        System.out.println("[浅拷贝] clone 只复制引用，payload 被连带改成 " + shallow.payload[0]);
        Node deep = orig.deepCopy();
        orig.payload[0] = 1;
        System.out.println("[深拷贝] orig 改回 " + orig.payload[0] + " ，deep 仍是 " + deep.payload[0]);
    }

    private static void demoSerialize() {
        Account acc = new Account("ada", "secret");
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            try (ObjectOutputStream out = new ObjectOutputStream(bos)) {
                out.writeObject(acc);
            }
            Account back;
            try (ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(bos.toByteArray()))) {
                back = (Account) in.readObject();
            }
            System.out.println("[序列化] name=" + back.name + " password=" + back.password + "（transient 为 null）");
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    static final class Node implements Cloneable {
        final String id;
        final int[] payload;

        Node(String id, int[] payload) {
            this.id = id;
            this.payload = payload;
        }

        @Override
        protected Node clone() {
            try {
                return (Node) super.clone();
            } catch (CloneNotSupportedException e) {
                throw new AssertionError(e);
            }
        }

        Node deepCopy() {
            return new Node(id, payload.clone());
        }
    }

    static final class Account implements Serializable {
        @java.io.Serial
        private static final long serialVersionUID = 1L;
        final String name;
        final transient String password;

        Account(String name, String password) {
            this.name = name;
            this.password = password;
        }
    }
}
