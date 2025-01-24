package com.dbs.watcherservice.datasource.primary.model;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class CpaRootNodeConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String system;

    private String JobName;

    private String predecessorSystems;
}
