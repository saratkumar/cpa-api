package com.dbs.watcherservice.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class CpaJobStatusDto {

    private Long id;

    private String appCode;

    private String businessDate;

    private String status;

}
