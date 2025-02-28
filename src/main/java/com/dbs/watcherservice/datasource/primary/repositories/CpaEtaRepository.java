package com.dbs.watcherservice.datasource.primary.repositories;

import com.dbs.watcherservice.datasource.primary.model.CpaEta;
import com.dbs.watcherservice.datasource.primary.model.CpaEtaConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CpaEtaRepository extends JpaRepository<CpaEta, Long>{

    @Query("SELECT e FROM CpaEta e " +
            "WHERE e.id IN ( " +
            "   (SELECT MIN(e2.id) FROM CpaEta e2 WHERE e2.appCode = :appCode AND e2.businessDate = :businessDate AND e2.entity = :entity), " +
            "   (SELECT MAX(e2.id) FROM CpaEta e2 WHERE e2.appCode = :appCode AND e2.businessDate = :businessDate AND e2.entity = :entity) " +
            ")")
    List<CpaEta> findFirstAndLastRecords(@Param("appCode") String appCode,
                                         @Param("businessDate") String businessDate,
                                         @Param("entity") String entity);


    @Query(value = "WITH ordered_jobs AS (\n" +
            "    SELECT \n" +
            "        ce.system,\n" +
            "        ce.jobname,\n" +
            "        ce.entity,\n" +
            "        ce.startTime AS eta_start_time,\n" +
            "        ce.endTime AS eta_end_time,\n" +
            "        ce.id AS eta_config_id,\n" +
            "        cr.id AS cpa_raw_id,\n" +
            "        \n" +
            "        -- Use raw data if available; otherwise, take reference table values\n" +
            "        COALESCE(TIME(cr.job_start_date_time), ce.startTime) AS raw_start_time,\n" +
            "        COALESCE(TIME(cr.job_end_date_time), ce.endTime) AS raw_end_time,\n" +
            "        \n" +
            "        -- Calculate expected job duration\n" +
            "        TIMESTAMPDIFF(SECOND, ce.startTime, ce.endTime) AS eta_duration_seconds,\n" +
            "        TIMESTAMPDIFF(SECOND, \n" +
            "            COALESCE(TIME(cr.job_start_date_time), ce.startTime), \n" +
            "            COALESCE(TIME(cr.job_end_date_time), ce.endTime)\n" +
            "        ) AS raw_duration_seconds\n" +
            "    \n" +
            "    FROM cpa_eta_config ce\n" +
            "    LEFT JOIN cpa_raw cr \n" +
            "        ON ce.jobname = cr.job_name \n" +
            "        AND ce.system = cr.app_code \n" +
            "        AND ce.entity = cr.entity\n" +
            "        AND cr.business_date = :businessDate -- Filter for specific business date\n" +
            "        WHERE cr.job_name IS NOT NULL AND entity=:entity AND system=:appCode\n"+
            "    ORDER BY ce.system, ce.startTime\n" +
            "),\n" +
            "\n" +
            "propagated_jobs AS (\n" +
            "    SELECT \n" +
            "        `system`,\n" +
            "        jobname,\n" +
            "        entity,\n" +
            "        eta_start_time,\n" +
            "        eta_end_time,\n" +
            "        raw_start_time,\n" +
            "        raw_end_time,\n" +
            "        eta_config_id,\n" +
            "        cpa_raw_id,\n" +
            "        eta_duration_seconds,\n" +
            "        raw_duration_seconds,\n" +
            "        \n" +
            "        -- Determine actual start time: Take raw start time if available, else propagate previous job's end time\n" +
            "        TIME(\n" +
            "            GREATEST(\n" +
            "                COALESCE(raw_start_time, eta_start_time),  \n" +
            "                COALESCE(\n" +
            "                    LAG(raw_end_time) OVER (PARTITION BY `system` ORDER BY eta_start_time), \n" +
            "                    eta_start_time\n" +
            "                )\n" +
            "            )\n" +
            "        ) AS actual_start_time,\n" +
            "        \n" +
            "        -- Compute actual end time based on actual start time + raw/ETA duration\n" +
            "        TIME(\n" +
            "            ADDTIME(\n" +
            "                GREATEST(\n" +
            "                    COALESCE(raw_start_time, eta_start_time),  \n" +
            "                    COALESCE(\n" +
            "                        LAG(raw_end_time) OVER (PARTITION BY `system` ORDER BY eta_start_time), \n" +
            "                        eta_start_time\n" +
            "                    )\n" +
            "                ), \n" +
            "                SEC_TO_TIME(raw_duration_seconds)\n" +
            "            )\n" +
            "        ) AS actual_end_time\n" +
            "    \n" +
            "    FROM ordered_jobs\n" +
            ")\n" +
            "\n" +
            "SELECT \n" +
            "    -- Calculate delays for each job\n" +
            "    `system`,`jobname`, `eta_config_id`, `cpa_raw_id`,\n" +
            "    TIMEDIFF(actual_start_time, eta_start_time) AS start_delay,\n" +
            "    TIMEDIFF(actual_end_time, eta_end_time) AS end_delay\n" +
            "FROM propagated_jobs ORDER BY `system`, eta_start_time DESC LIMIT 1;", nativeQuery = true)
    List<CpaEta> getJobDelays(String businessDate, String entity, String appCode);

}
