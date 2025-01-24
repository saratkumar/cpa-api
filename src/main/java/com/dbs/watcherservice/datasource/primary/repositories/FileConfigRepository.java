package com.dbs.watcherservice.datasource.primary.repositories;

import com.dbs.watcherservice.datasource.primary.model.FileConfig;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FileConfigRepository extends JpaRepository<FileConfig, Long> {

    public List<FileConfig> findAllByIsActive(Boolean i);
}
