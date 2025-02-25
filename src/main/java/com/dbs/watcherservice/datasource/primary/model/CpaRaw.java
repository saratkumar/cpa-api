package com.dbs.watcherservice.datasource.primary.model;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Date;


@Setter
@Getter
@Entity
public class CpaRaw {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String appCode;

    private String businessDate;

    private String jobName;

    private String entity;

    private LocalDateTime jobStartDateTime;

    private LocalDateTime jobEndDateTime;

    private int duration;

    private String successorDependencies;

    private Date updatedAt;

}
