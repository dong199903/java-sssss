package org.example.community.config;

public final class DataSourceContext {

    private static final ThreadLocal<DataSourceType> HOLDER = new ThreadLocal<>();

    private DataSourceContext() {
    }

    public static void useSlave() {
        HOLDER.set(DataSourceType.SLAVE);
    }

    public static DataSourceType current() {
        DataSourceType type = HOLDER.get();
        return type == null ? DataSourceType.MASTER : type;
    }

    public static void clear() {
        HOLDER.remove();
    }
}
