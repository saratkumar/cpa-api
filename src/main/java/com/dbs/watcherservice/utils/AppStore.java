package com.dbs.watcherservice.utils;

import com.dbs.watcherservice.datasource.primary.model.CpaRootNodeConfig;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Getter
@Setter
public class AppStore {

    private String businessDate;

    private String entity;

    private List<CpaRootNodeConfig> cpaRootNodeConfigList;

}