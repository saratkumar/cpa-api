package com.dbs.watcherservice.datasource.primary.repositories;

import com.dbs.watcherservice.datasource.primary.model.CpaEta;
import com.dbs.watcherservice.datasource.primary.model.JobDelay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CpaEtaRepository extends JpaRepository<JobDelay, Long >{

    @Query(value = "WITH job_durations AS (" +
            "    SELECT " +
            "        ce.jobname," +
            "        ce.system," +
            "        ce.entity," +
            "        TIMEDIFF(ce.endTime, ce.startTime) AS expected_job_duration," +
            "        TIMEDIFF(COALESCE(TIME(cr.job_end_date_time), ce.endTime), COALESCE(TIME(cr.job_start_date_time), ce.startTime)) AS curr_job_duration" +
            "    FROM cpa_eta_config ce" +
            "    LEFT JOIN cpa_raw cr" +
            "        ON ce.jobname = cr.job_name" +
            "        AND ce.system = cr.app_code" +
            "        AND ce.entity = cr.entity" +
            "        AND cr.business_date = :businessDate" +
            "), delay_calculations AS (" +
            "    SELECT " +
            "        jobname," +
            "        system," +
            "        entity," +
            "        expected_job_duration," +
            "        curr_job_duration," +
            "        CASE" +
            "            WHEN TIME_TO_SEC(curr_job_duration) > TIME_TO_SEC(expected_job_duration) THEN" +
            "                TIME(SEC_TO_TIME(TIME_TO_SEC(curr_job_duration) - TIME_TO_SEC(expected_job_duration)))" +
            "            ELSE" +
            "                '00:00:00'" +
            "        END AS delay_for_job" +
            "    FROM job_durations" +
            ")" +
            "SELECT " +
            "    system," +
            "    SUM(TIME_TO_SEC(delay_for_job)) AS total_delay_seconds," +
            "    SEC_TO_TIME(SUM(TIME_TO_SEC(delay_for_job))) AS total_delay" +
            "FROM delay_calculations" +
            "WHERE entity = :entity" +  // Dynamic value for entity
            "AND system = :appCode" +
            "GROUP BY system" +
            "ORDER BY system", nativeQuery = true)
    List<JobDelay> getJobDelays(String businessDate, String entity, String appCode);
}
