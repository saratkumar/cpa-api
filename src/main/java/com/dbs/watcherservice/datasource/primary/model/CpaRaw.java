package com.dbs.watcherservice.datasource.primary.model;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;


@Setter
@Getter
@Entity
@Builder
@Table(name="cpa_output")
public class CpaRaw {

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

    private Date updatedAt;

}
