package com.dbs.watcherservice.datasource.secondary.repositories;

import com.dbs.watcherservice.datasource.secondary.model.CpaRawCon;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

public interface CpaRawConnectorRepository extends JpaRepository<CpaRawCon, Long> {

    CpaRawCon findByJobName(String jobName);

    List<CpaRawCon> findByBusinessDateAndEntityAndAppCodeAndJobStartDateTimeGreaterThanEqualAndJobEndDateTimeLessThanEqual
            (String businessDate, String entity, String system, LocalDateTime startDateTime, LocalDateTime endDateTime);
}
