package com.dbs.watcherservice.service.impl;

import com.dbs.watcherservice.JsonPrinter;
import com.dbs.watcherservice.datasource.primary.repositories.CpaRawRepository;
import com.dbs.watcherservice.datasource.primary.repositories.CpaRootNodeConfigRepository;
import com.dbs.watcherservice.datasource.secondary.repositories.CpaRawConnectorRepository;
import com.dbs.watcherservice.dto.ApiResponse;
import com.dbs.watcherservice.datasource.primary.model.CpaRaw;
import com.dbs.watcherservice.datasource.secondary.model.CpaRawSecondary;
import com.dbs.watcherservice.datasource.primary.model.CpaRootNodeConfig;
import com.dbs.watcherservice.dto.CpaRawSecondaryDto;
import com.dbs.watcherservice.mapper.CpaRawMapper;
import com.dbs.watcherservice.mapper.CpaRawSecondaryMapper;
import com.dbs.watcherservice.service.GeneratePathService;
import com.dbs.watcherservice.service.MonitoringService;
import com.dbs.watcherservice.utils.AppStore;
import jakarta.annotation.PostConstruct;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;

@Profile("Grafana-Service")
@Service
public class GrafanaWatcherService implements MonitoringService<List<CpaRawSecondary>> {

    @Autowired
    CpaRawRepository cpaRawRepository;

    @Autowired
    CpaRootNodeConfigRepository cpaRootNodeConfigRepository;

    @Autowired
    GeneratePathService generatePathService;

    @Autowired
    AppStore appStore;

    @Autowired
    CpaRawConnectorRepository cpaRawConnectorRepository;

    @Value("${connector.system}")
    private String grafanaProfile;

    @Value("${connector.entity}")
    private String entity;

    @Value("${grafana.system.wait.period}")
    private int waitPeriod;

    @Value("${grafana.system.start.date}")
    private Long startDate;

    @Value("${grafana.api.url}")
    private String grafanaApiUrl;

    private static final OkHttpClient client = new OkHttpClient();

    private final String API_URL = grafanaApiUrl; // Replace with your API URL
    private static final boolean conditionMet = false;
    private String targetJobName = "";
    private LocalDateTime lastRunTime = null;
    private LocalDateTime businessDate = null;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd'T'HH:mm:ssX");
    DateTimeFormatter businessDateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
    @PostConstruct
    public void watchGrafana() {
        getRootNodeConfig();
        configWatchService();
    }

    public void getRootNodeConfig() {
        Optional<CpaRootNodeConfig> cpaRootNodeConfig = cpaRootNodeConfigRepository.getCpaRootNodeConfigBySystem(grafanaProfile);
        cpaRootNodeConfig.ifPresent(rootNodeConfig -> targetJobName = rootNodeConfig.getJobName());
    }

    @Override
    public void configWatchService() {
        lastRunTime = LocalDateTime.now().minusDays(startDate).minusMinutes(waitPeriod);
        Runnable task = () -> {
            LocalDateTime startTime = lastRunTime.plusSeconds(waitPeriod + 1); // Start time for the next range
//            LocalDateTime startTime = lastRunTime;
            LocalDateTime endTime = startTime.plusMinutes(waitPeriod); // End time for the next range
            businessDate = LocalDateTime.now().minusDays(1); // T-1 business date
            appStore.setBusinessDate(businessDate.format(businessDateFormatter));
            SimpleDateFormat dateTimeFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            List<CpaRawSecondary> cpaRawSecondaries = cpaRawConnectorRepository.findByBusinessDateAndEntityAndAppCodeAndJobStartDateTimeGreaterThanEqualAndJobEndDateTimeLessThanEqual(
                    //"20240115",
                    appStore.getBusinessDate(),
                    entity,
                    grafanaProfile,
                    startTime,
                    endTime

            );

            // Update the last run time
            lastRunTime = startTime;

            processFile(cpaRawSecondaries);
        };

        // Manually implement scheduling at fixed rate
        int delay = waitPeriod; // Delay in milliseconds (2 seconds)
        long startTime = System.currentTimeMillis();

        while (true) {
            // Execute the task
            task.run();

            // Calculate the time elapsed and sleep for the remaining time to maintain fixed rate
            long elapsedTime = System.currentTimeMillis() - startTime;
            long remainingTime = delay - (elapsedTime % delay);

            try {
                // Sleep for the remaining time before executing the task again
                Thread.sleep(remainingTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
                break;
            }
        }

    }

    @Override
    public void processFile(List<CpaRawSecondary> response) {

        List<CpaRaw> cpaRaws = CpaRawMapper.INSTANCE.toCpaRawEntity(response);

        Optional<CpaRaw> cpaRaw = cpaRaws.stream().filter(e ->
            e.getJobName().equals("JOB_NAME_1")).findFirst();

        cpaRaw.ifPresent(e -> generateCPA());

        System.out.println(cpaRaws.size());

    }

    private void saveCpaInformation(List<CpaRaw> cpaRaws) {
//        cpaRawRepository.saveAll(cpaRaws);
    }

    @Override
    public void generateCPA() {
        System.out.println("CPA generate process started");
        generatePathService.generateCritictialPath();
    }


}
