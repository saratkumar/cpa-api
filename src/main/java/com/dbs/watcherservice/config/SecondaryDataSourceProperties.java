package com.dbs.watcherservice.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "spring.datasource.secondary")
public class SecondaryDataSourceProperties {
    private Map<String, DataSourceConfig> datasources;

    @Getter
    @Setter
    public static class DataSourceConfig {
        private String url;
        private String username;
        private String password;
        private String driverClassName;
    }
}
