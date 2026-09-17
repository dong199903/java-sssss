package org.example.learn.mysql;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * WAL：先写日志再改页。对应 redo / undo / binlog 的分工。
 */
public final class WalDemo {

    public static void run() {
        Engine engine = new Engine();
        engine.update(1, "bob");
        System.out.println("[crash 前] page=" + engine.page + " redo=" + engine.redo + " undo=" + engine.undo + " binlog=" + engine.binlog);

        engine.crashLosePage();
        System.out.println("[crash 丢页] page=" + engine.page);

        engine.recover();
        System.out.println("[用 redo 恢复] page=" + engine.page);
        System.out.println("  redo：物理/逻辑重做，crash-safe。undo：回滚+MVCC 旧版本。binlog：Server 层逻辑日志，主从/备份。");
        System.out.println("  提交要 redo 与 binlog 两阶段：先 prepare redo → 写 binlog → commit redo。");
    }

    static final class Engine {
        Map<Integer, String> page = new LinkedHashMap<>();
        List<String> redo = new ArrayList<>();
        List<String> undo = new ArrayList<>();
        List<String> binlog = new ArrayList<>();

        Engine() {
            page.put(1, "ada");
        }

        void update(int id, String name) {
            String old = page.get(id);
            redo.add("SET " + id + "=" + name);
            undo.add("SET " + id + "=" + old);
            page.put(id, name);
            binlog.add("UPDATE t SET name=" + name + " WHERE id=" + id);
        }

        void crashLosePage() {
            page.put(1, "ada");
        }

        void recover() {
            for (String log : redo) {
                String[] parts = log.substring(4).split("=");
                page.put(Integer.parseInt(parts[0]), parts[1]);
            }
        }
    }
}
