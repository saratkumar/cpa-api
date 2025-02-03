package com.dbs.watcherservice.datasource.secondary.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;


@Setter
@Getter
@Entity
public class CpaRawCon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String appCode;

    private String businessDate;

    private String jobName;

    private String entity;

    private Date jobStartDateTime;

    private Date jobEndDateTime;

    private int duration;

    private String successorDependencies;

}
