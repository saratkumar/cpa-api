package com.dbs.watcherservice.service.impl;

import com.dbs.watcherservice.dto.FileConfigDto;
import com.dbs.watcherservice.service.EmailService;
import com.dbs.watcherservice.service.FileConfigService;
import com.dbs.watcherservice.service.MonitoringService;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.WatchService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Profile("File-Service")
@Service
public class FolderWatcherService implements MonitoringService<Path> {

    @Value("${folder.watch.path}")
    private String folderPathConfig;

    @Autowired
    EmailService emailService;

    private WatchService watchService;

    @Autowired
    FileConfigService fileConfigService;

    private static final Logger logger = LoggerFactory.getLogger(FolderWatcherService.class);

    private final Map<WatchKey, Path> watchKeys = new HashMap<>();
    // Specify the folder to watch

    @PostConstruct
    public void watchFolder() {
        try {
            // Start the folder watcher in a separate thread
            List<FileConfigDto> fileConfigDtoList =  fileConfigService.getFileConfigs();
            List<Path> paths = fileConfigDtoList.stream().map(e-> Paths.get(e.getPath())).collect(Collectors.toList());
            this.watchService = FileSystems.getDefault().newWatchService();
            for (Path path : paths) {
                registerDirectory(path);
            }


            logger.info("Initializing Folder Watcher Service...");
//        new Thread(this::configWatchService).start();
//            registerDirectory(Paths.get(folderPathConfig));
            configWatchService();
        } catch (IOException e) {
            logger.error(e.getMessage());
        }

    }

    private void registerDirectory(Path dir) throws IOException {
        // Register root folder and subfolders with WatchService for ENTRY_CREATE events
        WatchKey key = dir.register(watchService, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_MODIFY);
        watchKeys.put(key, dir);
        logger.info("Monitoring directory: {}", dir);
    }

    @Override
    public void configWatchService() {

        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        Runnable task = () -> {
            WatchKey key = null;
            try {
                key = watchService.take();  // Blocks until an event is received
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                logger.error("Folder watch service interrupted", e);
                try {
                    watchService.close();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }

            Path dir = watchKeys.get(key);
            if (dir == null) {
                logger.warn("WatchKey not recognized!");
            }

            for (WatchEvent<?> event : key.pollEvents()) {
                WatchEvent.Kind<?> kind = event.kind();

                if (kind == StandardWatchEventKinds.OVERFLOW) {
                    continue;
                }

                // Context for directory entry event is the file name
                WatchEvent<Path> ev = (WatchEvent<Path>) event;
                Path fileName = ev.context();
                Path filePath = dir.resolve(fileName);

                if (kind == StandardWatchEventKinds.ENTRY_CREATE) {
//                        if (Files.isDirectory(filePath)) {
//                            // Register new subdirectory created inside root folder
//                            try {
//                                registerDirectory(filePath);  // Automatically register the new subfolder
//                            } catch (IOException e) {
//                                logger.error("Error registering new directory: {}", filePath, e);
//                            }
//                        }
                    // Process the newly created file or directory
                    handleFileCreation(filePath);
                }
            }

            boolean valid = key.reset();
            if (!valid) {
                watchKeys.remove(key);
                if (watchKeys.isEmpty()) {
                    logger.warn("All directories are no longer accessible!");
                    try {
                        watchService.close();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        };

        // Schedule the task to run after 2 seconds
        scheduler.schedule(task, 2, TimeUnit.SECONDS);
    }

    private void handleFileCreation(Path filePath) {
        if (Files.isRegularFile(filePath)) {
            // Process the file (e.g., read content, etc.)
            processFile(filePath);

        } else {
            // Process the folder (if needed)
            logger.info("New subfolder created: {}", filePath);
        }
    }

    @Override
    public void processFile(Path filePath) {
        // Add your file processing logic here
        logger.info("Processing file: {}", filePath);

        // Example: Read the file content
        try {
            String content = Files.readString(filePath);
            logger.info("File content: {}", content);
        } catch (IOException e) {
            logger.error("Error reading file: {}", filePath, e);
        }


    }

    @Override
    public void generateCPA() {

    }
}
