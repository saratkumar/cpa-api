package com.dbs.watcherservice.repositories;

import com.dbs.watcherservice.model.CpaJobStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CpaJobStatusRepository extends JpaRepository<CpaJobStatus, Long> {

    List<CpaJobStatus> findByBusinessDate(String businessDate);

}
