package com.dbs.watcherservice.config;

import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
    basePackages = "com.dbs.watcherservice.datasource.secondary.repositories", // Adjust to your package
    entityManagerFactoryRef = "secondaryEntityManagerFactory",
    transactionManagerRef = "secondaryTransactionManager"
)
public class SecondaryDataSourceConfig {
    @Autowired
    private SecondaryDataSourceProperties secondaryDataSourceProperties;

    @Bean(name = "secondaryDataSource")
    public DataSource secondaryDataSource() {
        DynamicRoutingDataSource dynamicRoutingDataSource = new DynamicRoutingDataSource();
        Map<Object, Object> dataSourceMap = new HashMap<>();

        //Load dynamic data from properties
        for(Map.Entry<String, SecondaryDataSourceProperties.DataSourceConfig> entry: secondaryDataSourceProperties.getDatasources().entrySet()) {
            SecondaryDataSourceProperties.DataSourceConfig config = entry.getValue();

            DataSource dataSource = DataSourceBuilder.create()
                    .url(config.getUrl())
                    .username(config.getUsername())
                    .password(config.getPassword())
                    .driverClassName(config.getDriverClassName())
                    .build();
            dataSourceMap.put(entry.getKey(), dataSource);
        }
        dynamicRoutingDataSource.setTargetDataSources(dataSourceMap);
        dynamicRoutingDataSource.setDefaultTargetDataSource(dataSourceMap.get("DL")); //defaul value

        return dynamicRoutingDataSource;
    }

    @Bean(name = "secondaryEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(EntityManagerFactoryBuilder builder,
                                                                       @Qualifier("secondaryDataSource") DataSource dataSource) {
        return builder
                .dataSource(dataSource)
                .packages("com.dbs.watcherservice.datasource.secondary.model") // Adjust to your package
                .persistenceUnit("secondary")
                .build();
    }

    @Bean(name = "secondaryTransactionManager")
    public PlatformTransactionManager transactionManager(
            @Qualifier("secondaryEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }
}
