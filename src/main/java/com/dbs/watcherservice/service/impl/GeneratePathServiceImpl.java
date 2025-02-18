package com.dbs.watcherservice.service.impl;
import com.dbs.watcherservice.contants.CpaConfigConstant;
import com.dbs.watcherservice.datasource.primary.model.CpaRootNodeConfig;
import com.dbs.watcherservice.datasource.primary.model.CriticalPathAnalysisOutputData;
import com.dbs.watcherservice.datasource.primary.repositories.CpaJobStatusRepository;
import com.dbs.watcherservice.datasource.primary.repositories.CpaRootNodeConfigRepository;
import com.dbs.watcherservice.dto.CpaGeneratorRequest;
import com.dbs.watcherservice.dto.CpaJobStatusDto;
import com.dbs.watcherservice.mapper.CpaJobStatusMapper;
import com.dbs.watcherservice.datasource.primary.model.CpaJobStatus;
import com.dbs.watcherservice.service.GeneratePathService;
import com.dbs.watcherservice.utils.AppStore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import okhttp3.RequestBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class GeneratePathServiceImpl implements GeneratePathService {
    @Value("${connector.system}")
    private String grafanaProfile;

    @Value("${cpa.generator.wait.period}")
    private int cpaGeneratorWaitPeriod;

    @Value("${cpa.api-url}")
    private String apiUrl;

    @Autowired
    CpaRootNodeConfigRepository cpaRootNodeConfigRepository;

    @Autowired
    CpaJobStatusRepository cpaJobStatusRepository;

    @Autowired
    AppStore appStore;

    private Map<String, String[]> dependencies = new HashMap<>();

    private Map<String, String> dependencyJobStatus = new HashMap<>();

    Logger logger = LoggerFactory.getLogger(GeneratePathServiceImpl.class);

    OkHttpClient client = new OkHttpClient();

    ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void generateCritictialPath() {
        try {
            prepareCPAApiRequest(true);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        generateWithDependencies();
    }


    private List<CriticalPathAnalysisOutputData> prepareCPAApiRequest(Boolean isStandalone) throws JsonProcessingException {
        //Implmentation logic for triggering library program
        Optional<CpaRootNodeConfig> cpaRootNodeConfig = cpaRootNodeConfigRepository.findBySystem(grafanaProfile);
        if(cpaRootNodeConfig.isPresent()) {
            CpaGeneratorRequest cpaGeneratorRequest = new CpaGeneratorRequest();
            cpaGeneratorRequest.setSystem(isStandalone ? grafanaProfile : grafanaProfile +"-all");
            cpaGeneratorRequest.setBusinessDate(appStore.getBusinessDate());
            cpaGeneratorRequest.setJobName(cpaRootNodeConfig.get().getLastJob());
            cpaGeneratorRequest.setEntity(appStore.getEntity());
            String jsonRequest = objectMapper.writeValueAsString(cpaGeneratorRequest);
            RequestBody body = RequestBody.create(jsonRequest, CpaConfigConstant.JSON);
            Request postRequest = new Request.Builder()
                    .url(apiUrl)
                    .post(body)
                    .header("Content-Type", "application/json")
                    .build();
             try(Response response = client.newCall(postRequest).execute()) {
                if (response.isSuccessful()) {
                    System.out.println("Response: " + response.body().string());
                } else {
                    System.out.println("Error: " + response.code());
                }

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        return null;

    }


    /**
     * Need to wait until all the dependencies are avaialble
     **/
    private void generateWithDependencies() {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        Runnable task = () -> {
            try {
                List<CriticalPathAnalysisOutputData> criticalPathAnalysisOutputData =  prepareCPAApiRequest(false);
                if(criticalPathAnalysisOutputData != null && !criticalPathAnalysisOutputData.isEmpty()) {
                    scheduler.shutdown();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        };
        long initialDelay = 0;
        scheduler.scheduleAtFixedRate(task, initialDelay, cpaGeneratorWaitPeriod, TimeUnit.MINUTES);

    }

}
