package com.behavior.sdk.trigger.log_event.repository;

import com.behavior.sdk.trigger.log_event.entity.LogEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface LogEventRepository extends JpaRepository<LogEvent, UUID> {

   @Query(
         """
               select event
               from LogEvent event
               where (:projectId is null or event.projectId = :projectId)
               and (:visitorId is null or event.visitorId = :visitorId)
               and event.deletedAt is null
               """
   )
   List<LogEvent> findAllByProjectIdAndVisitorId(
         @Param("projectId") UUID projectId,
         @Param("visitorId") UUID visitorId);

   List<LogEvent> findAllByProjectId(UUID projectId);

   @Query(
         """
               select le.visitorId
               from LogEvent le
               where le.condition.id = :conditionId
               and le.pageUrl = :pageUrl
               group by le.visitorId
               having count(le) >= :threshold
               """
   )
   List<UUID> findVisitorIdsByCondition(@Param("conditionId") UUID conditionId,
                                        @Param("threshold") Integer threshold,
                                        @Param("pageUrl") String pageUrl);
}
