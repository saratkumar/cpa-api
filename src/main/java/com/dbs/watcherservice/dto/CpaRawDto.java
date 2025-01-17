package com.dbs.watcherservice.dto;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CpaRawDto {

    String appCode;

    String jobName;

    String entity;

    String startTime;

    String endTime;

    String dependencies;

    String businessDate;

}
