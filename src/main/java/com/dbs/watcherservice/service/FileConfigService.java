package com.dbs.watcherservice.service;

import com.dbs.watcherservice.dto.FileConfigDto;
//import com.dbs.watcherservice.model.FileConfig;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface FileConfigService {

    public List<FileConfigDto> getFileConfigs();

    public FileConfigDto getFileConfig();
}
