package com.dbs.watcherservice.repositories;

import com.dbs.watcherservice.model.CpaRootNodeConfig;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CpaRootNodeConfigRepository extends JpaRepository<CpaRootNodeConfig, Long> {

    public CpaRootNodeConfig getCpaRootNodeConfigBySystem(String system);

}
