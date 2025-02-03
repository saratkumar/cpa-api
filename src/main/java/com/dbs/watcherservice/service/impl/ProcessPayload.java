package com.dbs.watcherservice.service.impl;

import com.dbs.watcherservice.datasource.primary.model.CpaRaw;
import com.dbs.watcherservice.datasource.primary.model.CpaRootNodeConfig;
import com.dbs.watcherservice.datasource.primary.repositories.CpaRawRepository;
import com.dbs.watcherservice.datasource.primary.repositories.CpaRootNodeConfigRepository;
import com.dbs.watcherservice.service.GeneratePathService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public abstract class ProcessPayload {

    @Autowired
    CpaRawRepository cpaRawRepository;

    @Autowired
    CpaRootNodeConfigRepository cpaRootNodeConfigRepository;

    @Autowired
    GeneratePathService generatePathService;

    @Value("${connector.system}")
    private String grafanaProfile;

    private String targetJobName = "";
    CpaRootNodeConfig cpaRootNodeConfig = null;

    @PostConstruct
    public void init() {
        cpaRootNodeConfig = getRootNodeConfig();
    }



    public void findAndTriggerCPA(List<CpaRaw> cpaRaws) {

//        cpaRawRepository.saveAll(cpaRaws);

        Optional<CpaRaw> cpaRaw = cpaRaws.stream().filter(e ->
                e.getJobName().equals(targetJobName)).findFirst();


        cpaRaw.ifPresent(e -> generateCPA());
    }


    public CpaRootNodeConfig getRootNodeConfig() {
        Optional<CpaRootNodeConfig> cpaRootNodeConfig = cpaRootNodeConfigRepository.getCpaRootNodeConfigBySystem(grafanaProfile);
        cpaRootNodeConfig.ifPresent(rootNodeConfig -> targetJobName = rootNodeConfig.getJobName());
        return cpaRootNodeConfig.get();
    }


    public void generateCPA() {
        System.out.println("CPA generate process started");
        generatePathService.generateCritictialPath();
    }


}
