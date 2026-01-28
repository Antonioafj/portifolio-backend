package dev.antonio.portifolio.repositories;

import dev.antonio.portifolio.entities.SystemMetricsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface SystemMetricsRepository extends JpaRepository<SystemMetricsEntity, Long> {

   @Modifying
   @Transactional
   @Query("UPDATE SystemMetricsEntity s SET s.cvDownloads =" +
           "s.cvDownloads + 1 WHERE s.id = 1")
    void incrementCvDownload();


    @Modifying
    @Transactional
    @Query("UPDATE SystemMetricsEntity s SET s.labAccesses =" +
            "s.labAccesses + 1 WHERE s.id = 1")
    void incrementLabAccess();



    @Modifying
    @Transactional
    @Query("UPDATE SystemMetricsEntity s SET s.apiTests =" +
            "s.apiTests + 1 WHERE s.id = 1")
    void incrementApiTest();


}
