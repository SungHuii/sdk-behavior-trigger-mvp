package com.behavior.sdk.trigger.log.repository;

import com.behavior.sdk.trigger.log.entity.Log;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface LogRepository extends JpaRepository<Log, UUID> {

   @Query("""
    select l from Log l
     where (:projectId is null or l.projectId = :projectId)
       and (:visitorId is null or l.visitorId = :visitorId)
  """)
   List<Log> findAllByProjectIdAndVisitorId(@Param("projectId") UUID projectId, @Param("visitorId") UUID visitorId);
}
