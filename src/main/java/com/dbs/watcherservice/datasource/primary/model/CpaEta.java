package com.dbs.watcherservice.datasource.primary.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Time;
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

    private Time start_delay;

    private Time end_delay;

    private String jobName;

    private String entity;

    private String businessDate;

    @OneToOne
    @JoinColumn(name="id", referencedColumnName = "cpa_raw_id")
    private CpaRaw cpaRaw;

    @OneToOne
    @JoinColumn(name="id", referencedColumnName = "eta_config_id")
    private CpaEtaConfig cpaEtaConfig;
}
