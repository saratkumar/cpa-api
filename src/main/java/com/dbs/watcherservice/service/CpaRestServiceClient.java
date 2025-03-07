package com.dbs.watcherservice.service;

import com.dbs.watcherservice.datasource.primary.model.CriticalPathAnalysisOutputData;
import com.dbs.watcherservice.dto.CpaGeneratorRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "cpa-rest-service", url = "${cpa.api-url}")
public interface CpaRestServiceClient {

    @PostMapping("/critical-path")
    List<CriticalPathAnalysisOutputData> generateCriticalPath(@RequestBody CpaGeneratorRequest payload);
}