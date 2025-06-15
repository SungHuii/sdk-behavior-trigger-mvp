package com.behavior.sdk.trigger.email_log.repository;

import com.behavior.sdk.trigger.email_log.entity.EmailLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface EmailLogRepository extends JpaRepository<EmailLog, UUID> {

    List<EmailLog> findAllByVisitorIdAndDeletedAtIsNull(UUID visitorId);

    @Query("""
    SELECT COUNT(e)
      FROM EmailLog e
     WHERE e.status = com.behavior.sdk.trigger.email.enums.EmailStatus.SENT
       AND EXISTS (
            SELECT 1
              FROM SegmentVisitor sv
             WHERE sv.id.segmentId = :segmentId
               AND sv.id.visitorId = e.visitorId
          )
    """)
    long countSentBySegment(@Param("segmentId") UUID segmentId);

    @Query("""
    SELECT COUNT(e)
      FROM EmailLog e
     WHERE e.status = com.behavior.sdk.trigger.email.enums.EmailStatus.FAILED
       AND EXISTS (
            SELECT 1
              FROM SegmentVisitor sv
             WHERE sv.id.segmentId = :segmentId
               AND sv.id.visitorId = e.visitorId
          )
    """)
    long countFailedBySegment(@Param("segmentId") UUID segmentId);

    boolean existsByVisitorIdAndTemplateId(UUID visitorId, UUID templateId);

    void saveSentLog(UUID visitorId, UUID templateId, UUID batchId);

    void saveFailedLog(UUID visitorId, UUID templateId, UUID batchId, String message);

    long countByBatchIdAndStatus(UUID batchId, String sent);
}
