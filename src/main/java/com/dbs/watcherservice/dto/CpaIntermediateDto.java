package com.dbs.watcherservice.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CpaIntermediateDto {

    private long id;

    private String appCode;

    private String businessDate;

    private String jobName;

    private String predecessorJob;

    private int duration;
}
