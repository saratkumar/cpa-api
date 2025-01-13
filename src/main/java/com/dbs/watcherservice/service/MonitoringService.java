package com.dbs.watcherservice.service;

import org.springframework.stereotype.Service;

import java.nio.file.Path;

@Service
public interface MonitoringService   {

    void configWatchService();

//    <T> void processFile(T filepath);
}
