package com.dbs.watcherservice.dto;


import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class CpaRawSecondaryDto {

    private String appCode;

    private String jobName;

    private String entity;

    private Date jobStartDateTime;

    private Date jobEndDateTime;

    private int duration;

    private String successorDependencies;

    private String businessDate;

}
