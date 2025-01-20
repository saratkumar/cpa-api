package com.dbs.watcherservice.service;

import org.springframework.stereotype.Service;

@Service
public interface MonitoringService<T>   {

    void configWatchService();

    void generateCPA();

    void processFile(T input);

}
