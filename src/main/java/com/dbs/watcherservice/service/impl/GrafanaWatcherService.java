package com.dbs.watcherservice.service.impl;

import com.dbs.watcherservice.JsonPrinter;
import com.dbs.watcherservice.dto.ApiResponse;
import com.dbs.watcherservice.dto.CpaRawDto;
import com.dbs.watcherservice.mapper.CpaRawMapper;
import com.dbs.watcherservice.model.CpaRaw;
import com.dbs.watcherservice.model.CpaRootNodeConfig;
import com.dbs.watcherservice.repositories.CpaRawRepository;
import com.dbs.watcherservice.repositories.CpaRootNodeConfigRepository;
import com.dbs.watcherservice.service.MonitoringService;
import jakarta.annotation.PostConstruct;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import com.fasterxml.jackson.databind.ObjectMapper;

@Profile("Grafana-Service")
@Service
public class GrafanaWatcherService implements MonitoringService {

    @Autowired
    CpaRawRepository cpaRawRepository;

    @Autowired
    CpaRootNodeConfigRepository cpaRootNodeConfigRepository;

    @Value("${grafana.system}")
    private static String grafanaProfile;

    @Value("${grafana.system.wait.period}")
    private static Long waitPeriod;

    @Value("${grafana.system.start.date}")
    private static Long startDate;

    @Value("${grafana.api.url}")
    private static String grafanaApiUrl;

    private static final OkHttpClient client = new OkHttpClient();

    private static final String API_URL = grafanaApiUrl; // Replace with your API URL
    private static final boolean conditionMet = false;
    private LocalDateTime lastRunTime = LocalDateTime.now().minusDays(startDate).minusMinutes(waitPeriod);
    private String targetJobName = "";

    @PostConstruct
    public void watchGrafana() {
        getRootNodeConfig();
        configWatchService();
    }

    public void getRootNodeConfig() {
        CpaRootNodeConfig cpaRootNodeConfig = cpaRootNodeConfigRepository.getCpaRootNodeConfigBySystem(grafanaProfile);
        targetJobName =  cpaRootNodeConfig.getJobName();
    }

    @Override
    public void configWatchService() {

        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        Runnable task = () -> {

            if (conditionMet) {
                System.out.println("Condition met! Stopping the scheduler.");
                scheduler.shutdown();
                return;
            }

            try {
//                // Make the API request
//                Request request = new Request.Builder()
//                        .url(API_URL)
//                        .header("Authorization", "Bearer YOUR_API_TOKEN") // Replace with your token
//                        .build();
//
//                try (Response response = client.newCall(request).execute()) {
//                    if (!response.isSuccessful()) {
//                        System.err.println("Request failed: " + response.code());
//                        return;
//                    }
//
//                    String responseData = response.body().string();
//                    System.out.println("API Response: " + responseData);
//
//                    // Check if the condition is met
//                    conditionMet = processFile(responseData);
//                }
                LocalDateTime startTime = lastRunTime.plusMinutes(waitPeriod).plusSeconds(1); // Start time for the next range
                LocalDateTime endTime = startTime.plusMinutes(waitPeriod); // End time for the next range

                // Format the times to a string suitable for the API
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
                String startTimeStr = startTime.format(formatter);
                String endTimeStr = endTime.format(formatter);

                // Call your API with the time range (You should replace this with your actual API call logic)
                System.out.println("Calling API with time range: " + startTimeStr + " to " + endTimeStr);

                // Update the last run time
                lastRunTime = startTime;

                ApiResponse res = getApiResponse();
                processFile(res, scheduler);
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
        long initialDelay = 0;
        scheduler.scheduleAtFixedRate(task, initialDelay, waitPeriod, TimeUnit.MINUTES);
    }

    public void processFile(ApiResponse response, ScheduledExecutorService scheduler) {
        List<CpaRawDto> cpaRawDtos = new ArrayList<>();
        ApiResponse.Data data = response.getResults().getA().getFrames().getData();

        int size = data.getValues().get(0).size();
        CpaRawDto cpaRawDto = null;
        for(int i=0;i< size;i++) {
            cpaRawDto = new CpaRawDto();
            cpaRawDto.setAppCode(data.getValues().get(0).get(i));
            cpaRawDto.setBusinessDate(data.getValues().get(1).get(i));
            cpaRawDto.setJobName(data.getValues().get(2).get(i));
            cpaRawDto.setEntity(data.getValues().get(3).get(i));
            cpaRawDto.setStartTime(data.getValues().get(4).get(i));
            cpaRawDto.setEndTime(data.getValues().get(5).get(i));
            cpaRawDto.setDependencies(data.getValues().get(6).get(i));

            cpaRawDtos.add(cpaRawDto);

            if(data.getValues().get(2).get(i).equals(targetJobName)) {
                saveCpaInformation(cpaRawDtos);
                generateCPA();
            }
        }

        if(!cpaRawDtos.isEmpty()) {
            saveCpaInformation(cpaRawDtos);
        }

        System.out.println(cpaRawDtos.size());

    }

    private void saveCpaInformation(List<CpaRawDto> cpaRawDtos) {
        List<CpaRaw> cpaRaws = CpaRawMapper.INSTANCE.toEntity(cpaRawDtos);
        cpaRawRepository.saveAll(cpaRaws);
    }

    private void generateCPA() {
        System.out.println("CPA generated");
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
}
