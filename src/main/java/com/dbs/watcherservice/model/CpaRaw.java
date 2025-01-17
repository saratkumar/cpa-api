package com.dbs.watcherservice.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.sql.Date;

@Setter
@Getter
@Entity
public class CpaRaw {

    @Id
    @GeneratedValue
    private Long id;

    private String system;

    private String businessDate;

    private String jobName;

    private String entity;

    private Date jobStartDateTime;

    private Date jobEndDateTime;

    private int duration;

    private String successorDependencies;


}
