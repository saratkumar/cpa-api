package com.dbs.watcherservice.repositories;

import com.dbs.watcherservice.model.FileConfig;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FileConfigRepository extends JpaRepository<FileConfig, Long> {

    public List<FileConfig> findAllByIsActive(Boolean i);
}
