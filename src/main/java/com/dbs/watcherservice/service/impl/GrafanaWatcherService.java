package com.dbs.watcherservice.service.impl;

import com.dbs.watcherservice.service.MonitoringService;
import jakarta.annotation.PostConstruct;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Profile("Grafana-Service")
@Service
public class GrafanaWatcherService implements MonitoringService {
    private static final String API_URL = "https://api.example.com/endpoint"; // Replace with your API URL

    private static final OkHttpClient client = new OkHttpClient();
    private static boolean conditionMet = false;
    private static int WAIT_PERIOD = 1;
    private LocalDateTime lastRunTime = LocalDateTime.now().minusDays(7).minusMinutes(WAIT_PERIOD);

    @PostConstruct
    public void watchGrafana() {
        configWatchService();
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
                LocalDateTime startTime = lastRunTime.plusMinutes(WAIT_PERIOD).plusSeconds(1); // Start time for the next range
                LocalDateTime endTime = startTime.plusMinutes(WAIT_PERIOD); // End time for the next range

                // Format the times to a string suitable for the API
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
                String startTimeStr = startTime.format(formatter);
                String endTimeStr = endTime.format(formatter);

                // Call your API with the time range (You should replace this with your actual API call logic)
                System.out.println("Calling API with time range: " + startTimeStr + " to " + endTimeStr);

                // Update the last run time
                lastRunTime = startTime;
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
        long initialDelay = 0;
        scheduler.scheduleAtFixedRate(task, initialDelay, WAIT_PERIOD, TimeUnit.MINUTES);
        // Schedule the task to run every 5 minutes
//        scheduler.scheduleAtFixedRate(task, 0, 5, TimeUnit.MINUTES);
    }

    public boolean processFile(String responseData) {
        System.out.println(responseData +"___Asdfasfd");
        return false;
    }
}
