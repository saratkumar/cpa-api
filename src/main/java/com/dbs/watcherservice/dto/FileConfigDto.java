package com.dbs.watcherservice.dto;



import lombok.*;

@Getter
@Setter
public class FileConfigDto {

    private Long id;

    private String appCode;

    private String fileSystem;

    private String pattern;

    private String path;

    private boolean isActive;

}
