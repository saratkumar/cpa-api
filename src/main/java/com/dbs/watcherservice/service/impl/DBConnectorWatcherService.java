package com.dbs.watcherservice.service.impl;

import com.dbs.watcherservice.datasource.secondary.repositories.CpaRawConnectorRepository;
import com.dbs.watcherservice.datasource.primary.model.CpaRaw;
import com.dbs.watcherservice.datasource.secondary.model.CpaRawCon;
import com.dbs.watcherservice.mapper.CpaRawMapper;
import com.dbs.watcherservice.service.DataSourceService;
import com.dbs.watcherservice.service.MonitoringService;
import com.dbs.watcherservice.utils.AppStore;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class DBConnectorWatcherService extends ProcessPayload implements MonitoringService<List<CpaRawCon>> {
    @Autowired
    AppStore appStore;

    @Autowired
    CpaRawConnectorRepository cpaRawConnectorRepository;

    @PersistenceContext(unitName = "secondary")
    EntityManager entityManager;

    @Autowired
    DataSourceService dataSourceService;

    @Value("${connector.system}")
    private String grafanaProfile;

    @Value("${connector.entity}")
    private String entity;

    @Value("${grafana.system.wait.period}")
    private int waitPeriod;

    private LocalDateTime lastRunTime = null;
    private LocalDateTime businessDate = null;
    DateTimeFormatter businessDateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final Logger logger = LoggerFactory.getLogger(DBConnectorWatcherService.class);

    @PostConstruct
    public void watchGrafana() {
        configWatchService();
    }

    @Override
    public void configWatchService() {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        lastRunTime = LocalDateTime.now();
        Runnable task = () -> {
            try {
                LocalDateTime startTime = lastRunTime; // Start time for the next range
                LocalDateTime endTime = startTime.plusMinutes(waitPeriod); // End time for the next range
                businessDate = LocalDateTime.now().minusDays(1); // T-1 business date
                appStore.setBusinessDate(businessDate.format(businessDateFormatter));

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                LocalDateTime startDate = startTime.truncatedTo(ChronoUnit.SECONDS);
                LocalDateTime endDate = endTime.truncatedTo(ChronoUnit.SECONDS);
//                List<CpaRawSecondary> cpaRawSecondaries = cpaRawConnectorRepository.findByBusinessDateAndEntityAndAppCodeAndJobStartDateTimeGreaterThanEqualAndJobEndDateTimeLessThanEqual(
//                        appStore.getBusinessDate(),
//                        entity,
//                        grafanaProfile,
//                        startTime,
//                        endTime
//                );
                dataSourceService.switchToDataSource(grafanaProfile);
                if(cpaRootNodeConfig != null) {
                    Query query = entityManager.createQuery(cpaRootNodeConfig.getQuery(), CpaRawCon.class);
                    query.setParameter("businessDate", appStore.getBusinessDate());
                    query.setParameter("entity", entity);
                    query.setParameter("appCode", grafanaProfile);
                    query.setParameter("jobStartDateTime", startTime);
                    query.setParameter("jobEndDateTime", endTime);
                    List<CpaRawCon> cpaRawSecondaries = query.getResultList();

                    // Update the last run time
                    lastRunTime = endTime.plusSeconds(1);
                    processRawInput(cpaRawSecondaries);
                }
            } catch (Exception e) {
                logger.error("Grafana Service_"+e.getMessage());
                e.printStackTrace();
            } finally {
                dataSourceService.clearDataSource();
            }
        };
        long initialDelay = 0;
        scheduler.scheduleAtFixedRate(task, initialDelay, waitPeriod, TimeUnit.MINUTES);

    }

    @Override
    public void processRawInput(List<CpaRawCon> response) {

        List<CpaRaw> cpaRaws = CpaRawMapper.INSTANCE.toCpaRawEntity(response);

        findAndTriggerCPA(cpaRaws);

        findETA(cpaRaws);

    }

    public static String formatLocalDateTime(LocalDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return dateTime.format(formatter);  // Return the formatted string
    }
}
