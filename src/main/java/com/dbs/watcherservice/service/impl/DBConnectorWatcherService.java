package com.dbs.watcherservice.service.impl;

import com.dbs.watcherservice.datasource.primary.repositories.CpaRawRepository;
import com.dbs.watcherservice.datasource.primary.repositories.CpaRootNodeConfigRepository;
import com.dbs.watcherservice.datasource.secondary.repositories.CpaRawConnectorRepository;
import com.dbs.watcherservice.datasource.primary.model.CpaRaw;
import com.dbs.watcherservice.datasource.secondary.model.CpaRawSecondary;
import com.dbs.watcherservice.datasource.primary.model.CpaRootNodeConfig;
import com.dbs.watcherservice.mapper.CpaRawMapper;
import com.dbs.watcherservice.service.GeneratePathService;
import com.dbs.watcherservice.service.MonitoringService;
import com.dbs.watcherservice.utils.AppStore;
import jakarta.annotation.PostConstruct;
import okhttp3.OkHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Profile("DBConnector-Service")
@Service
public class DBConnectorWatcherService implements MonitoringService<List<CpaRawSecondary>> {

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
    private static final Logger logger = LoggerFactory.getLogger(DBConnectorWatcherService.class);
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
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        lastRunTime = LocalDateTime.now().minusDays(startDate).minusMinutes(waitPeriod);
        Runnable task = () -> {

            try {
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
            } catch (Exception e) {
                logger.error("Grafana Service_"+e.getMessage());
                e.printStackTrace();
            }
        };
        long initialDelay = 0;
        scheduler.scheduleAtFixedRate(task, initialDelay, waitPeriod, TimeUnit.MINUTES);

    }

    @Override
    public void processFile(List<CpaRawSecondary> response) {

        List<CpaRaw> cpaRaws = CpaRawMapper.INSTANCE.toCpaRawEntity(response);

        Optional<CpaRaw> cpaRaw = cpaRaws.stream().filter(e ->
            e.getJobName().equals(targetJobName)).findFirst();

        cpaRaw.ifPresent(e -> generateCPA());

        System.out.println(cpaRaws.size());

    }

    private void saveCpaInformation(List<CpaRaw> cpaRaws) {
        cpaRawRepository.saveAll(cpaRaws);
    }

    @Override
    public void generateCPA() {
        System.out.println("CPA generate process started");
        generatePathService.generateCritictialPath();
    }


}
