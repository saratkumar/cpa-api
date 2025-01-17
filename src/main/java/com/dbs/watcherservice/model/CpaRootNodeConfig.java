package com.dbs.watcherservice.model;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class CpaRootNodeConfig {

    @Id
    @GeneratedValue
    private long id;

    private String system;

    private String JobName;

    private String predecessorSystems;
}
