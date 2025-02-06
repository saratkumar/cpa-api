package com.dbs.watcherservice.datasource.primary.repositories;

import com.dbs.watcherservice.datasource.primary.model.CpaIntermediate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CpaIntermediateRespository extends JpaRepository<CpaIntermediate, Long> {


}
