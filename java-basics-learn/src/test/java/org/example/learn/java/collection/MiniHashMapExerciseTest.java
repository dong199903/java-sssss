package org.example.learn.java.collection;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@Disabled("完成 MiniHashMap.put / resize 后去掉本注解")
class MiniHashMapExerciseTest {

    @Test
    void putGetAndOverride() {
        MiniHashMap<String, Integer> map = new MiniHashMap<>();
        assertNull(map.put("a", 1));
        assertEquals(1, map.put("a", 2));
        assertEquals(2, map.get("a"));
        assertEquals(1, map.size());
    }

    @Test
    void collideThenResizeKeepsValues() {
        MiniHashMap<Key, Integer> map = new MiniHashMap<>(4);
        for (int i = 0; i < 8; i++) {
            map.put(new Key(i), i);
        }
        assertEquals(8, map.size());
        for (int i = 0; i < 8; i++) {
            assertEquals(i, map.get(new Key(i)));
        }
    }

    record Key(int id) {
        @Override
        public int hashCode() {
            return 3;
        }
    }
}
