package org.example.learn.java.collection;

/**
 * 练习用极简 HashMap：数组 + 链表 + 2 倍扩容。
 * 先看 {@link HashMapDemo}，再补全标了 TODO 的方法。
 */
public class MiniHashMap<K, V> {

    static final float LOAD_FACTOR = 0.75f;

    Node<K, V>[] table;
    int size;

    @SuppressWarnings("unchecked")
    public MiniHashMap(int capacity) {
        int cap = 1;
        while (cap < capacity) {
            cap <<= 1;
        }
        table = (Node<K, V>[]) new Node[cap];
    }

    public MiniHashMap() {
        this(16);
    }

    public V get(K key) {
        Node<K, V> node = find(table[index(key)], key);
        return node == null ? null : node.value;
    }

    public int size() {
        return size;
    }

    /**
     * TODO 练习 1：实现 put。
     * 规则：算下标 → 空桶直接放 → 链表里 equals 则覆盖 → 否则尾插 → size++ 后如需则 resize。
     */
    public V put(K key, V value) {
        throw new UnsupportedOperationException("练习：实现 put");
    }

    /**
     * TODO 练习 2：实现 resize，容量 * 2，所有节点重新 index。
     */
    void resize() {
        throw new UnsupportedOperationException("练习：实现 resize");
    }

    boolean needResize() {
        return size > table.length * LOAD_FACTOR;
    }

    int index(K key) {
        int h = key == null ? 0 : key.hashCode();
        return (table.length - 1) & h;
    }

    Node<K, V> find(Node<K, V> head, K key) {
        for (Node<K, V> n = head; n != null; n = n.next) {
            if (ObjectsEquals(n.key, key)) {
                return n;
            }
        }
        return null;
    }

    static boolean ObjectsEquals(Object a, Object b) {
        return a == b || (a != null && a.equals(b));
    }

    static final class Node<K, V> {
        final K key;
        V value;
        Node<K, V> next;

        Node(K key, V value, Node<K, V> next) {
            this.key = key;
            this.value = value;
            this.next = next;
        }
    }
}
