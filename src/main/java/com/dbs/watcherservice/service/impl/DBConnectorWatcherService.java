package com.dbs.watcherservice.service.impl;

import com.dbs.watcherservice.datasource.primary.model.CpaRaw;
import com.dbs.watcherservice.datasource.secondary.repositories.CpaRawConnectorRepository;
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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


@Service
public class DBConnectorWatcherService extends ProcessPayload implements MonitoringService<List<Object[]>> {
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
    private static DateTimeFormatter timeStampFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");
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
                //appStore.setBusinessDate(businessDate.format(businessDateFormatter));
                appStore.setBusinessDate("20250131");
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//                LocalDateTime startDate = startTime.truncatedTo(ChronoUnit.SECONDS);
//                LocalDateTime endDate = endTime.truncatedTo(ChronoUnit.SECONDS);
                LocalDateTime startDate = startTime.minusDays(20).truncatedTo(ChronoUnit.SECONDS); // testing purpose
                LocalDateTime endDate = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);// testing purpose
                dataSourceService.switchToDataSource(grafanaProfile);
                if(cpaRootNodeConfig != null) {
                    Query query = entityManager.createNativeQuery(cpaRootNodeConfig.getQuery());
//                    query.setParameter("businessDate", appStore.getBusinessDate());
                    query.setParameter("businessDate", "20250131"); // testing purpose
                    query.setParameter("entity", entity);
                    query.setParameter("jobStartDateTime", startDate);
                    query.setParameter("jobEndDateTime", endDate);
                    List<Object[]> cpaRawSecondaries = query.getResultList();

                    // Update the last run time
                    lastRunTime = endTime.plusSeconds(1);
                    processRawInput(cpaRawSecondaries);
                }
            } catch (Exception e) {
                logger.error("DB Connector Service_"+e.getMessage());
                e.printStackTrace();
            } finally {
                dataSourceService.clearDataSource();
            }
        };
        long initialDelay = 0;
        scheduler.scheduleAtFixedRate(task, initialDelay, waitPeriod, TimeUnit.MINUTES);

    }

    @Override
    public void processRawInput(List<Object[]> response) {

        List<CpaRaw> cpaRaws = new ArrayList<>(response.size());
        for(Object[] res: response) {
            CpaRaw cpaRaw = new CpaRaw();
            cpaRaw.setAppCode((String) res[0]);
            cpaRaw.setBusinessDate((String) res[1]);
            cpaRaw.setJobName((String) res[2]);
            cpaRaw.setEntity((String) res[3]);
            cpaRaw.setJobStartDateTime(LocalDateTime.parse(res[4].toString(), timeStampFormatter));
            cpaRaw.setJobEndDateTime(LocalDateTime.parse(res[5].toString(), timeStampFormatter));
            cpaRaw.setDuration(Math.toIntExact((long) res[6]));
            cpaRaw.setSuccessorDependencies((String) res[7]);
            if(cpaRaw.getJobName() != null && cpaRaw.getEntity() != null) {
                cpaRaws.add(cpaRaw);
            }

        }

        findAndTriggerCPA(cpaRaws);

        findETA(cpaRaws);
    }
}
