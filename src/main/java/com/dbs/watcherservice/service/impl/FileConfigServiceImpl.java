package com.dbs.watcherservice.service.impl;

import com.dbs.watcherservice.dto.FileConfigDto;
import com.dbs.watcherservice.model.FileConfig;
import com.dbs.watcherservice.repositories.FileConfigRepository;
import com.dbs.watcherservice.service.FileConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class FileConfigServiceImpl implements FileConfigService {

    @Autowired
    FileConfigRepository fileConfigRepository;

    @Override
    public List<FileConfigDto> getFileConfigs() {
        List<FileConfig> fileConfigs = fileConfigRepository.findAllByIsActive(true);
        List<FileConfigDto> fileConfigDtoList  = new ArrayList<>();
        fileConfigs.forEach(fileConfig -> {
            FileConfigDto fileConfigDto =  new FileConfigDto();
            fileConfigDto.setId(fileConfig.getId());
            fileConfigDto.setAppCode(fileConfig.getAppCode());
            fileConfigDto.setFileSystem(fileConfig.getFileSystem());
            fileConfigDto.setPattern(fileConfig.getPattern());
            fileConfigDto.setPath(fileConfig.getPath());
//            fileConfigDto.setIsActive(true);
            fileConfigDtoList.add(fileConfigDto);
        });
        return fileConfigDtoList;
    }

    @Override
    public FileConfigDto getFileConfig() {
        return null;
    }
}
