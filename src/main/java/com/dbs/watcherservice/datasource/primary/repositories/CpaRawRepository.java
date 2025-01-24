package com.dbs.watcherservice.datasource.primary.repositories;

import com.dbs.watcherservice.datasource.primary.model.CpaRaw;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CpaRawRepository extends JpaRepository<CpaRaw, Long> {
}
