package com.dbs.watcherservice.service.impl;

import com.dbs.watcherservice.JsonPrinter;
import com.dbs.watcherservice.dto.ApiResponse;
import com.dbs.watcherservice.model.CpaRaw;
import com.dbs.watcherservice.model.CpaRootNodeConfig;
import com.dbs.watcherservice.repositories.CpaRawRepository;
import com.dbs.watcherservice.repositories.CpaRootNodeConfigRepository;
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

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import com.fasterxml.jackson.databind.ObjectMapper;

@Profile("Grafana-Service")
@Service
public class GrafanaWatcherService implements MonitoringService<ApiResponse> {

    @Autowired
    CpaRawRepository cpaRawRepository;

    @Autowired
    CpaRootNodeConfigRepository cpaRootNodeConfigRepository;

    @Autowired
    GeneratePathService generatePathService;

    @Autowired
    AppStore appStore;

    @Value("${grafana.system}")
    private String grafanaProfile;

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
            LocalDateTime endTime = startTime.plusMinutes(waitPeriod); // End time for the next range
            businessDate = LocalDateTime.now().minusDays(1); // T-1 business date
            appStore.setBusinessDate(businessDate.format(businessDateFormatter));
            // Format the times to a string suitable for the API
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
            String startTimeStr = startTime.format(formatter);
            String endTimeStr = endTime.format(formatter);

            // Call your API with the time range (You should replace this with your actual API call logic)
            System.out.println("Calling API with time range: " + startTimeStr + " to " + endTimeStr);

            // Update the last run time
            lastRunTime = startTime;

            ApiResponse res = getApiResponse();
            processFile(res);
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

//        lastRunTime = LocalDateTime.now().minusDays(startDate).minusMinutes(waitPeriod);
//        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
//        Runnable task = () -> {
//
//            if (conditionMet) {
//                System.out.println("Condition met! Stopping the scheduler.");
//                scheduler.shutdown();
//                return;
//            }
//
//            try {
////                // Make the API request
////                Request request = new Request.Builder()
////                        .url(API_URL)
////                        .header("Authorization", "Bearer YOUR_API_TOKEN") // Replace with your token
////                        .build();
////
////                try (Response response = client.newCall(request).execute()) {
////                    if (!response.isSuccessful()) {
////                        System.err.println("Request failed: " + response.code());
////                        return;
////                    }
////
////                    String responseData = response.body().string();
////                    System.out.println("API Response: " + responseData);
////
////                    // Check if the condition is met
////                    conditionMet = processFile(responseData);
////                }
////                LocalDateTime startTime = lastRunTime.plusMinutes(waitPeriod).plusSeconds(1); // Start time for the next range
////                LocalDateTime endTime = startTime.plusMinutes(waitPeriod); // End time for the next range
////
////                // Format the times to a string suitable for the API
////                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
////                String startTimeStr = startTime.format(formatter);
////                String endTimeStr = endTime.format(formatter);
////
////                // Call your API with the time range (You should replace this with your actual API call logic)
////                System.out.println("Calling API with time range: " + startTimeStr + " to " + endTimeStr);
////
////                // Update the last run time
////                lastRunTime = startTime;
////
////                ApiResponse res = getApiResponse();
////                processFile(res);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        };
//        long initialDelay = 0;
//        scheduler.scheduleAtFixedRate(task, initialDelay, waitPeriod, TimeUnit.MINUTES);
    }

    private ApiResponse getApiResponse(){
        ApiResponse response = null;
        try {
            ClassPathResource resource = new ClassPathResource("/response/sample.json");
            ObjectMapper objectMapper = new ObjectMapper();
            response =  objectMapper.readValue(resource.getInputStream(), ApiResponse.class);
        } catch (Exception e) {
            System.out.println(e);
        }
        assert response != null;
        JsonPrinter.printJson(response.getResults().getA().getFrames().getData());
        return response;
    }

    @Override
    public void processFile(ApiResponse response) {

        ApiResponse.Data data = response.getResults().getA().getFrames().getData();
        List<List<String>> values = data.getValues();
        List<String> appCodes = values.get(0);
        List<String> businessDates = values.get(1);
        List<String> jobNames = values.get(2);
        List<String> entities = values.get(3);
        List<String> startTimes = values.get(4);
        List<String> endTimes = values.get(5);
        List<String> durations = values.get(6);
        List<String> successors = values.getLast();
        List<CpaRaw> cpaRaws = new ArrayList<>();
        for(int i=0;i< appCodes.size();i++) {
            // Parse start and end dates once per loop
            ZonedDateTime startZonedDateTime = ZonedDateTime.parse(startTimes.get(i), formatter);
            ZonedDateTime endZonedDateTime = ZonedDateTime.parse(endTimes.get(i), formatter);

            // Create and populate CpaRaw object
            CpaRaw cpaRaw = new CpaRaw();
            cpaRaw.setJobStartDateTime(Date.from(startZonedDateTime.toInstant()));
            cpaRaw.setJobEndDateTime(Date.from(endZonedDateTime.toInstant()));
            cpaRaw.setAppCode(appCodes.get(i));
            cpaRaw.setBusinessDate(businessDates.get(i));
            cpaRaw.setJobName(jobNames.get(i));
            cpaRaw.setEntity(entities.get(i));
            cpaRaw.setDuration(Integer.parseInt(durations.get(i)));
            cpaRaw.setSuccessorDependencies(successors.get(i));
            cpaRaws.add(cpaRaw);

            if(data.getValues().get(2).get(i).equals(targetJobName)) {
                saveCpaInformation(cpaRaws);
                cpaRaws.clear();
                generateCPA();
            }
        }

        if(!cpaRaws.isEmpty()) {
            saveCpaInformation(cpaRaws);
        }

        System.out.println(cpaRaws.size());

    }

    private void saveCpaInformation(List<CpaRaw> cpaRaws) {
        cpaRawRepository.saveAll(cpaRaws);
    }

    @Override
    public void generateCPA() {
        System.out.println("CPA generated");
        generatePathService.generateCritictialPath();
    }


}
