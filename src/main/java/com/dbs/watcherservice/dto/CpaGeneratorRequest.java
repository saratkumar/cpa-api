package com.dbs.watcherservice.dto;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CpaGeneratorRequest {

    private String jobName;

    private String businessDate;

    private String system;

    private String entity;

    private Boolean isDefault;
}
