package com.dbs.watcherservice.datasource.primary.repositories;

import com.dbs.watcherservice.datasource.primary.model.CpaJobStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CpaJobStatusRepository extends JpaRepository<CpaJobStatus, Long> {

    List<CpaJobStatus> findByBusinessDate(String businessDate);

}
