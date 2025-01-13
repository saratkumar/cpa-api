package com.dbs.watcherservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class FileConfig {

    @Id
    @GeneratedValue
    private Long id;

    private String appCode;

    private String fileSystem;

    private String pattern;

    private String path;

    @Column(columnDefinition = "BIT", nullable = false)
    private boolean isActive;

}
