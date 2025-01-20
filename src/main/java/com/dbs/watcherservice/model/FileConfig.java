package com.dbs.watcherservice.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class FileConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String appCode;

    private String fileSystem;

    private String pattern;

    private String path;

    @Column(columnDefinition = "BIT", nullable = false)
    private boolean isActive;

}
