package com.dbs.watcherservice.service.impl;

import com.dbs.watcherservice.datasource.primary.model.*;
import com.dbs.watcherservice.datasource.primary.repositories.CpaEtaConfigRepository;
import com.dbs.watcherservice.datasource.primary.repositories.CpaEtaRepository;
import com.dbs.watcherservice.datasource.primary.repositories.CpaRawRepository;
import com.dbs.watcherservice.datasource.primary.repositories.CpaRootNodeConfigRepository;
import com.dbs.watcherservice.service.GeneratePathService;
import com.dbs.watcherservice.utils.AppStore;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
public abstract class ProcessPayload {

    @Autowired
    CpaRawRepository cpaRawRepository;

    @Autowired
    CpaRootNodeConfigRepository cpaRootNodeConfigRepository;

    @Autowired
    GeneratePathService generatePathService;

    @Autowired
    CpaEtaConfigRepository cpaEtaConfigRepository;

    @Autowired
    CpaEtaRepository cpaEtaRepository;

    @Autowired
    AppStore appStore;

    @Value("${connector.system}")
    private String system;

    private String targetJobName = "";

    CpaRootNodeConfig cpaRootNodeConfig = null;

    @PostConstruct
    public void init() {
        cpaRootNodeConfig = getRootNodeConfig();
    }



    public void findAndTriggerCPA(List<CpaRaw> cpaRaws) {

        cpaRawRepository.saveAll(cpaRaws);

        Optional<CpaRaw> cpaRaw = cpaRaws.stream().filter(e ->
                e.getJobName().equals(targetJobName)).findFirst();


        cpaRaw.ifPresent(e -> {
            System.out.println("CPA generate process started");
            appStore.setEntity(e.getEntity());
            generatePathService.generateCritictialPath();
        });
    }


    public CpaRootNodeConfig getRootNodeConfig() {
        Optional<CpaRootNodeConfig> cpaRootNodeConfig = cpaRootNodeConfigRepository.findBySystem(system);
        cpaRootNodeConfig.ifPresent(rootNodeConfig -> targetJobName = rootNodeConfig.getLastJob());
        return cpaRootNodeConfig.get();
    }


    public void findETA(List<CpaRaw> cpaRaws) {
        ExecutorService executor = Executors.newFixedThreadPool(1);
        executor.submit( () -> {
            try {
                List<JobDelay> jobDelays = cpaEtaRepository.getJobDelays(appStore.getBusinessDate(), appStore.getEntity(), grafanaProfile);
                /**Implementation*/
                if(jobDelays.size() > 0) {
                    JobDelay jobDelay = jobDelays.get(0);

                    if(jobDelay.getTotalDelay() != null || jobDelay.getTotalDelaySeconds() > 0) {

                        /*** add the total value to exiting value **/
                    }
                }
            } catch (Exception e) {
                executor.shutdown();
                System.out.println("Exception in finding ETA "+ e.getMessage());
            }

        });
        executor.shutdown();
    }


}
