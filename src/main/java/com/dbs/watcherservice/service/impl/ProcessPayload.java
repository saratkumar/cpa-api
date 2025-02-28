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
import java.util.*;
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


    List<CpaRootNodeConfig> cpaRootNodeConfigs = new ArrayList<>();

    @PostConstruct
    public void init() {
        cpaRootNodeConfigs = getRootNodeConfigs();
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


    public List<CpaRootNodeConfig> getRootNodeConfigs() {
        cpaRootNodeConfigs = cpaRootNodeConfigRepository.findAll();
        appStore.setCpaRootNodeConfigList(cpaRootNodeConfigs);
        return cpaRootNodeConfigs;
    }


    public void findETA(List<CpaRaw> cpaRaws, String system) {
        ExecutorService executor = Executors.newFixedThreadPool(1);
        executor.submit( () -> {
            try {
                /**
                 * Fetch records from cpa_eta_configs and cpa_raw tables and calculate the differences. to make it
                 * more relevant keeping it on cpaeta repo
                 */
                List<CpaEta> cpaEtaConfigs = cpaEtaRepository.getJobDelays(appStore.getBusinessDate(), appStore.getEntity(), system);
                List<CpaEta> exitingCpaEtaConfigs = cpaEtaRepository.findByBusinessDateAndEntityAndAppCode(appStore.getBusinessDate(), appStore.getEntity(), system);
                Map<String, CpaEta> cpaEtaMap = exitingCpaEtaConfigs.stream().collect(Collectors.toMap(e -> e.getJobName(), Function.identity()));
                /**
                 * check job is already present in cpa_eta table if yes then update the time, and cpa raw value
                 */
                cpaEtaConfigs.forEach(e -> {
                    CpaEta previousVal = cpaEtaMap.get(e.getJobName());
                    if(previousVal != null) {
                        e.setId(previousVal.getId());
                        e.setCpaRaw(previousVal.getCpaRaw());
                    } else {
                        e.setBusinessDate(appStore.getBusinessDate());
                    }
                });

                /**Implementation*/
                cpaEtaRepository.saveAll(cpaEtaConfigs);
            } catch (Exception e) {
                executor.shutdown();
                System.out.println("Exception in finding ETA "+ e.getMessage());
            }

        });
        executor.shutdown();
    }


}
