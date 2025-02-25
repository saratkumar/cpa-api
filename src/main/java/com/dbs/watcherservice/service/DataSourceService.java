package com.dbs.watcherservice.service;

import com.dbs.watcherservice.config.DataSourceContextHolder;
import org.springframework.stereotype.Service;

@Service
public class DataSourceService {

    public void switchToDataSource(String dataSourceKey) {
        DataSourceContextHolder.setDataSourceContext(dataSourceKey);
    }

    public void clearDataSource() {
        DataSourceContextHolder.clearDataSourceContext();
    }
}
