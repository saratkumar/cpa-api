package com.dbs.watcherservice.datasource.primary.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name="cpa_output")
public class CriticalPathAnalysisOutputData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;

    private String sourceSystem;

    private String businessDate;

    private String target;

    private String source;

    private int duration;

    private String legs;

    private String entity;

    private String sysParam;

}
