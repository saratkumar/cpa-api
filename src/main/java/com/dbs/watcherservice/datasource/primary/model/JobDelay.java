package com.dbs.watcherservice.datasource.primary.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class JobDelay {

    @Id
    private String system;

    private long totalDelaySeconds;
    private String totalDelay;

}
