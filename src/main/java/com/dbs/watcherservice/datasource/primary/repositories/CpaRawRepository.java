package com.dbs.watcherservice.datasource.primary.repositories;

import com.dbs.watcherservice.datasource.primary.model.CpaRaw;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CpaRawRepository extends JpaRepository<CpaRaw, Long> {

//    @Query(name="SELECT * FROM (SELECT *,ROW_NUMBER() OVER (PARTITION BY job_name, business_date, app_code ORDER BY updated_at DESC) AS rn FROM cpa_raw where business_date=:businessDate and app_code=:appCode) t WHERE rn = 1", nativeQuery = true)
//    List<CpaRaw> getCpaRaw(@Param("businessDate") String businessDate, @Param("appCode") String appCode);

}