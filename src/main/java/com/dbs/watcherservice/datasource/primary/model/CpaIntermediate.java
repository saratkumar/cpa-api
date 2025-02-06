package com.dbs.watcherservice.datasource.primary.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class CpaIntermediate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name="system")
    private String appCode;

    private String businessDate;

    private String jobName;

    private String predecessorJob;

    private int duration;
}
