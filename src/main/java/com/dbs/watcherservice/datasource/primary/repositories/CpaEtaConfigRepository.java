package com.dbs.watcherservice.datasource.primary.repositories;

import com.dbs.watcherservice.datasource.primary.model.CpaEtaConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CpaEtaConfigRepository extends JpaRepository<CpaEtaConfig, Long>{

    List<CpaEtaConfig> findByAppCodeAndEntity(String appCode, String entity);

}
