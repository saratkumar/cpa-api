package com.dbs.watcherservice.service.impl;

import com.dbs.watcherservice.service.MonitoringService;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Profile("Grafana-Service")
@Service
public class GrafanaWatcherService implements MonitoringService {
    private static final String API_URL = "https://api.example.com/endpoint"; // Replace with your API URL
    private static final OkHttpClient client = new OkHttpClient();
    private static boolean conditionMet = false;

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
                // Make the API request
                Request request = new Request.Builder()
                        .url(API_URL)
                        .header("Authorization", "Bearer YOUR_API_TOKEN") // Replace with your token
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    if (!response.isSuccessful()) {
                        System.err.println("Request failed: " + response.code());
                        return;
                    }

                    String responseData = response.body().string();
                    System.out.println("API Response: " + responseData);

                    // Check if the condition is met
                    conditionMet = processFile(responseData);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        };

        // Schedule the task to run every 5 minutes
        scheduler.scheduleAtFixedRate(task, 0, 5, TimeUnit.MINUTES);
    }

    public boolean processFile(String responseData) {
        System.out.println(responseData +"___Asdfasfd");
        return false;
    }
}
