package com.dbs.watcherservice.repositories;

import com.dbs.watcherservice.model.CpaRootNodeConfig;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CpaRootNodeConfigRepository extends JpaRepository<CpaRootNodeConfig, Long> {

    public Optional<CpaRootNodeConfig> getCpaRootNodeConfigBySystem(String system);

}
