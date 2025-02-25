package com.dbs.watcherservice.config;

public class DataSourceContextHolder {

    private static final ThreadLocal<String> dataSourceContext = new ThreadLocal<>();

    public static void setDataSourceContext(String dataSourceKey) {
        dataSourceContext.set(dataSourceKey);
    }

    public static String getDataSourceContext() {
        return dataSourceContext.get();
    }

    public static void clearDataSourceContext() {
        dataSourceContext.remove();
    }
}
