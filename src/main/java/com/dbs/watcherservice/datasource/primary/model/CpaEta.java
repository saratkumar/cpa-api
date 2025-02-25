package com.dbs.watcherservice.datasource.primary.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Setter
@Entity
public class CpaEta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name="system")
    private String appCode;

    private LocalTime startTime;

    private LocalTime endTime;

    private String jobName;

    private String entity;

    private String businessDate;
}
